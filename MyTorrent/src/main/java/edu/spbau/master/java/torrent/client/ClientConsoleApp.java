package edu.spbau.master.java.torrent.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import edu.spbau.master.java.torrent.client.app.IncomingQueryHandler;
import edu.spbau.master.java.torrent.client.app.QuerySender;
import edu.spbau.master.java.torrent.client.data.ClientDataHolderImpl;
import edu.spbau.master.java.torrent.client.fs.FilePartReaderImpl;
import edu.spbau.master.java.torrent.client.fs.FilePartWriterImpl;
import edu.spbau.master.java.torrent.client.model.PartiallyLoadedFile;
import edu.spbau.master.java.torrent.client.network.ClientScheduler;
import edu.spbau.master.java.torrent.client.network.ClientServer;
import edu.spbau.master.java.torrent.client.network.ClientUser;
import edu.spbau.master.java.torrent.client.network.LoadingManager;
import edu.spbau.master.java.torrent.model.FileInfo;
import edu.spbau.master.java.torrent.shared.exception.InvalidDataStreamException;

import java.io.*;
import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;

public class ClientConsoleApp {
    private static final String CLIENT_INFO_FILE_NAME = "ClientInfo";


    @Parameter(names = {"-p"}, description = "Port of client")
    private int port;

    @Parameter(names = {"-s"}, description = "Inet address of server")
    private String serverAddress;


    public static void main(String[] args) throws IOException, InvalidDataStreamException {
        ClientConsoleApp app = new ClientConsoleApp();
        new JCommander(app, args);

        String fileName = CLIENT_INFO_FILE_NAME + app.port;
        File infoFile = new File(fileName);

        InetAddress serverAddress = InetAddress.getByName(app.serverAddress);

        ClientDataHolderImpl clientDataHolder;
        if (infoFile.exists()) {
            clientDataHolder = ClientDataHolderImpl.load(new BufferedInputStream(new FileInputStream(infoFile)));
        } else {
            clientDataHolder = ClientDataHolderImpl.newInstance();
        }

        ClientServer clientServer = new ClientServer(new IncomingQueryHandler(clientDataHolder, new FilePartWriterImpl()), app.port);
        ClientScheduler clientScheduler = new ClientScheduler(serverAddress, app.port, clientDataHolder);
        LoadingManager loadingManager = new LoadingManager(
                serverAddress,
                new QuerySender(
                        new FilePartReaderImpl()
                ),
                clientDataHolder,
                partiallyLoadedFile -> {},
                partiallyLoadedFile -> {}
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

        for (PartiallyLoadedFile partiallyLoadedFile : clientUser.getAllPartiallyLoadedFiles()) {
            loadingManager.addToLoadQueue(partiallyLoadedFile);
        }

        Scanner scanner = new Scanner(new BufferedInputStream(System.in));
        boolean isExit = false;
        while (!isExit) {
            String line = scanner.nextLine();
            String[] words = line.split(" ");
            switch (words[0]) {
                case "exit":
                    isExit = true;
                    break;
                case "list":
                    List<FileInfo> torrentFiles = clientUser.getAllTorrentFiles();
                    for (FileInfo torrentFile : torrentFiles) {
                        System.out.println(torrentFile);
                    }
                    break;
                case "upload":
                    clientUser.addFile(new File(words[1]));
                    break;
                case "download":
                    String filePath = words[1];
                    int fileId = Integer.parseInt(words[2]);
                    clientUser.addToLoadingQueue(filePath, fileId);
                    break;
                default:
                    System.out.println("Invalid command");
            }
        }

        clientServer.stop();
        clientScheduler.stop();
        loadingManager.stop();

        clientDataHolder.persist(new BufferedOutputStream(new FileOutputStream(infoFile)));
    }

}
