package edu.spbau.master.java.torrent.client.model;


import edu.spbau.master.java.torrent.model.FileInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public final class PartiallyLoadedFile implements Serializable {
    private final FileInfo fileInfo;
    private final String path;
    private final AtomicInteger loadedPartCount;
    private final AtomicBoolean isLoaded;
    private final boolean[] parts;

    public PartiallyLoadedFile(FileInfo fileInfo, String path) {
        this.fileInfo = fileInfo;
        this.path = path;
        loadedPartCount = new AtomicInteger();
        parts = new boolean[fileInfo.getPartCount()];
        isLoaded = new AtomicBoolean();
    }

    public synchronized void loadPart(int partNum) {
        loadedPartCount.incrementAndGet();
        parts[partNum] = true;
    }

    public synchronized boolean isLoaded() {
        return loadedPartCount.get() == parts.length;
    }
}
