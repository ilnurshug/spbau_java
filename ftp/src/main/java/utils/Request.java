package utils;

public enum Request {
    DISCONNECT(0),
    LIST(1),
    GET(2);

    public final int type;

    Request(int type) {
        this.type = type;
    }

    public static Request fromInt(int type) throws Exception {
        for (Request r : Request.values()) {
            if (r.type == type) {
                return r;
            }
        }

        throw new Exception("unknown request type " + type);
    }
}
