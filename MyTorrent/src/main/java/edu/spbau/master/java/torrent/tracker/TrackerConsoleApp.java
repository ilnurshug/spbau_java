package edu.spbau.master.java.torrent.tracker;

import edu.spbau.master.java.torrent.shared.exception.InvalidDataStreamException;
import edu.spbau.master.java.torrent.tracker.app.QueryHandler;
import edu.spbau.master.java.torrent.tracker.data.TrackerDataHolder;
import edu.spbau.master.java.torrent.tracker.data.TrackerDataHolderImpl;
import edu.spbau.master.java.torrent.tracker.network.TrackerServer;

import java.io.*;
import java.util.Scanner;

public class TrackerConsoleApp {

    private static final String TRACKER_INFO_FILE_NAME = "TrackerInfo";

    public static void main(String[] args) throws IOException, InvalidDataStreamException {

        File file = new File(TRACKER_INFO_FILE_NAME);

        TrackerDataHolderImpl trackerDataHolder;
        if (file.exists()) {
            trackerDataHolder = TrackerDataHolderImpl.load(new BufferedInputStream(new FileInputStream(file)));
        } else {
            trackerDataHolder = TrackerDataHolderImpl.newInstance();
        }

        TrackerServer trackerServer = new TrackerServer(new QueryHandler(trackerDataHolder));
        new Thread(() -> {
            try {
                trackerServer.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        Scanner scanner = new Scanner(new BufferedInputStream(System.in));
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("exit")) {
                break;
            }
        }

        trackerServer.stop();
        trackerDataHolder.persist(new BufferedOutputStream(new FileOutputStream(file)));
    }

}
