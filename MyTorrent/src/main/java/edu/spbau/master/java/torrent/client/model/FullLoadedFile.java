package edu.spbau.master.java.torrent.client.model;


import edu.spbau.master.java.torrent.model.FileInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public final class FullLoadedFile implements Serializable {
    private final FileInfo fileInfo;
    private final String path;
}
