package edu.spbau.master.java.torrent.shared;

public enum ServerQueryType {
    List(1),
    Upload(2),
    Sources(3),
    Update(4);
    public final int value;

    ServerQueryType(int value) {
        this.value = value;
    }
}
