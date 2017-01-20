package edu.spbau.master.java.torrent.model;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public final class FileInfo implements Serializable {
    public static final int PART_SIZE = 1024 * 1024 * 10;
    private final int id;
    private final String name;
    private final long size;

    public int getPartCount() {
        return (int) ((size + PART_SIZE - 1) / PART_SIZE);
    }

    public static long getOffset(int partNum) {
        return ((long) partNum) * PART_SIZE;
    }
}
