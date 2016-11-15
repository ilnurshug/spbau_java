package utils;


public class Requests
{
    public static final int DISCONNECT = 0;
    public static final int LIST = 1;
    public static final int GET = 2;

    public static boolean validRequest(int r)
    {
        return r >= 0 && r <= 2;
    }
}