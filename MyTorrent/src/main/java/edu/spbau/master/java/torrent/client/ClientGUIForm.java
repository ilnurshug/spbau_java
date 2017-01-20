package edu.spbau.master.java.torrent.client;

import edu.spbau.master.java.torrent.client.app.IncomingQueryHandler;
import edu.spbau.master.java.torrent.client.app.QuerySender;
import edu.spbau.master.java.torrent.client.data.ClientDataHolderImpl;
import edu.spbau.master.java.torrent.client.fs.FilePartReaderImpl;
import edu.spbau.master.java.torrent.client.fs.FilePartWriterImpl;
import edu.spbau.master.java.torrent.client.network.ClientScheduler;
import edu.spbau.master.java.torrent.client.network.ClientServer;
import edu.spbau.master.java.torrent.client.network.ClientUser;
import edu.spbau.master.java.torrent.client.network.LoadingManager;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.exception.InvalidDataStreamException;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.io.*;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.List;

@Slf4j
public class ClientGUIForm extends JFrame {

    private static final String CLIENT_INFO_FILE_NAME = "ClientInfo";

    private static final String COLUMN_NAME = "Name";

    private static final String COLUMN_SIZE = "Size";

    private static final String COLUMN_PROGRESS = "Progress";

    private JPanel rootPanel;
    private JTextField serverAddressTextField;
    private JFormattedTextField listeningPortFormattedTextField;
    private JButton startButton;
    private JList<FileInfo> availableFiles;
    private JTable loadingFiles;
    private JButton uploadFileButton;
    private JButton updateButton;

    private final DefaultTableModel tableModel = new DefaultTableModel();

    private ClientGUIForm() {
        super("Torrent client");

        setContentPane(rootPanel);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        startButton.addActionListener(e -> {

            try {
                InetAddress inetAddress = InetAddress.getByName(serverAddressTextField.getText());
                int port = (int) listeningPortFormattedTextField.getValue();

                start(port, inetAddress);
                setWorkMode(true);

            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, "Exception during start! Cause: " + e1.getMessage());
            }


        });


        setWorkMode(false);

        setVisible(true);
    }

    private File getFileFromUser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void start(int port, InetAddress serverAddress) throws IOException, InvalidDataStreamException {
        String fileName = CLIENT_INFO_FILE_NAME + port;
        File infoFile = new File(fileName);

        ClientDataHolderImpl clientDataHolder;
        if (infoFile.exists()) {
            clientDataHolder = ClientDataHolderImpl.load(new BufferedInputStream(new FileInputStream(infoFile)));
        } else {
            clientDataHolder = ClientDataHolderImpl.newInstance();
        }

        ClientServer clientServer = new ClientServer(new IncomingQueryHandler(clientDataHolder, new FilePartWriterImpl()), port);
        ClientScheduler clientScheduler = new ClientScheduler(serverAddress, port, clientDataHolder);
        LoadingManager loadingManager = new LoadingManager(
                serverAddress,
                new QuerySender(
                        new FilePartReaderImpl()
                ),
                clientDataHolder,
                partiallyLoadedFile -> {
                },
                partiallyLoadedFile -> {
                }
        );
        ClientUser clientUser = new ClientUser(
                serverAddress,
                clientDataHolder,
                loadingManager,
                new QuerySender(
                        new FilePartReaderImpl()
                )
        );

        clientScheduler.start();
        new Thread(() -> {
            try {
                clientServer.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        uploadFileButton.addActionListener(e -> {
            File selectedFile = getFileFromUser();
            if (selectedFile != null) {
                try {
                    clientUser.addFile(selectedFile);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(this, "Exception file adding! Cause: " + e1.getMessage());
                }
            }
        });

        updateButton.addActionListener(e -> {
            try {

                List<FileInfo> allTorrentFiles = clientUser.getAllTorrentFiles();
                DefaultListModel<FileInfo> model = new DefaultListModel<>();
                allTorrentFiles.forEach(model::addElement);

                availableFiles.setModel(model);

            } catch (IOException e1) {
                JOptionPane.showMessageDialog(this, "Exception torrent file list updating! Cause: " + e1.getMessage());
            }
        });


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                clientServer.stop();
                clientScheduler.stop();
                loadingManager.stop();

                clientDataHolder.persist(new BufferedOutputStream(new FileOutputStream(infoFile)));
            } catch (IOException e) {
                log.error("Exception during server closing", e);
            }
        }));

    }

    private void setWorkMode(boolean value) {
        startButton.setEnabled(!value);
        listeningPortFormattedTextField.setEnabled(!value);
        serverAddressTextField.setEnabled(!value);

        uploadFileButton.setEnabled(value);
        updateButton.setEnabled(value);
    }


    public static void main(String[] args) {
        new ClientGUIForm();
    }

    private void createUIComponents() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);

        listeningPortFormattedTextField = new JFormattedTextField(format);
    }
}
