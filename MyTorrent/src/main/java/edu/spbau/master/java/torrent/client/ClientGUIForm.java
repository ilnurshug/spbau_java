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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ClientGUIForm extends JFrame {

    private static final String CLIENT_INFO_FILE_NAME = "ClientInfo";

    private static final String COLUMN_NAME = "Name";

    private static final String COLUMN_SIZE = "Size";

    private static final String COLUMN_PROGRESS = "Progress";

    private static final int COLUMN_PROGRESS_ID = 2;

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
                log.error("Exception during start! Cause: " + e1.getMessage(), e);
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

        ConcurrentMap<FileInfo, Integer> fileInfoToRowNumMap = new ConcurrentHashMap<>();

        loadingFiles.setModel(tableModel);
        tableModel.addColumn(COLUMN_NAME);
        tableModel.addColumn(COLUMN_SIZE);
        tableModel.addColumn(COLUMN_PROGRESS);

        loadingFiles.getColumn(COLUMN_PROGRESS).setCellRenderer(new ProgressCellRender());

        ClientServer clientServer = new ClientServer(
                new IncomingQueryHandler(
                        clientDataHolder,
                        new FilePartWriterImpl()
                ),
                port
        );
        ClientScheduler clientScheduler = new ClientScheduler(
                serverAddress,
                port,
                clientDataHolder
        );

        LoadingManager loadingManager = new LoadingManager(
                serverAddress,
                new QuerySender(
                        new FilePartReaderImpl()
                ),
                clientDataHolder,
                partiallyLoadedFile -> {
                    int progress = (partiallyLoadedFile.getLoadedPartCount().get() * 100) / partiallyLoadedFile.getFileInfo().getPartCount();
                    int rowNum =
                            fileInfoToRowNumMap.getOrDefault(partiallyLoadedFile.getFileInfo(), -1);
                    if (rowNum != -1) {
                        tableModel.setValueAt(progress, fileInfoToRowNumMap.get(partiallyLoadedFile.getFileInfo()), COLUMN_PROGRESS_ID);
                    } else {
                        log.warn("Can't find row num for file info {}", partiallyLoadedFile.getFileInfo());
                    }
                },
                partiallyLoadedFile -> {
                    int progress = (partiallyLoadedFile.getLoadedPartCount().get() * 100) / partiallyLoadedFile.getFileInfo().getPartCount();
                    int rowNum =
                            fileInfoToRowNumMap.getOrDefault(partiallyLoadedFile.getFileInfo(), -1);
                    if (rowNum != -1) {
                        tableModel.setValueAt(progress, fileInfoToRowNumMap.get(partiallyLoadedFile.getFileInfo()), COLUMN_PROGRESS_ID);
                    } else {
                        log.warn("Can't find row num for file info {}", partiallyLoadedFile.getFileInfo());
                    }
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

        new Thread(() -> {
            try {
                clientServer.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        clientScheduler.start();

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

        availableFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    log.info("Add new file to loading files.");
                    FileInfo fileInfo = availableFiles.getModel().getElementAt(availableFiles.locationToIndex(e.getPoint()));
                    File destination = getFileFromUser();
                    if (destination != null) {
                        try {
                            int rowNum = tableModel.getRowCount();
                            fileInfoToRowNumMap.putIfAbsent(fileInfo, rowNum);
                            clientUser.addToLoadingQueue(destination.getPath(), fileInfo.getId());
                            tableModel.addRow(new Object[]{fileInfo.getName(), fileInfo.getSize(), 0});
                        } catch (IOException e1) {
                            log.error("Exception while file loading.", e1);
                            JOptionPane.showMessageDialog(ClientGUIForm.this, "Exception while file loading! Cause: " + e1.getMessage());
                        }

                    }
                }
                super.mouseClicked(e);
            }
        });


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Stop application.");
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
        listeningPortFormattedTextField.setValue(1488);
    }

    public static class ProgressCellRender extends JProgressBar implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setValue((int) value);
            return this;
        }
    }
}
