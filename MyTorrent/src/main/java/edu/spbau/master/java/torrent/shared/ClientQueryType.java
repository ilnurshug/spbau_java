package edu.spbau.master.java.torrent.shared;

public enum ClientQueryType {
    Stat(1),
    Get(2);
    public final int value;

    ClientQueryType(int value) {
        this.value = value;
    }
}
