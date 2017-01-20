package edu.spbau.master.java.torrent.model;


import lombok.Data;

@Data
public final class ClientInfo {
    private final int ip;
    private final short port;

    @Override
    public String toString() {

        return "ClientInfo{" +
                String.format("%d.%d.%d.%d",
                        (ip & 0xff),
                        (ip >> 8 & 0xff),
                        (ip >> 16 & 0xff),
                        (ip >> 24 & 0xff)) +
                ":" + port +
                '}';
    }
}
