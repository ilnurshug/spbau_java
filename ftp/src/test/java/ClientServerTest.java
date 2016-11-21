import client.Client;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import server.Server;
import client.requests.Get.ByteStream;
import utils.Requests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClientServerTest {
    private static Thread serverThread;
    private static Server server;
    private static Client client;

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void prepare() {
        prepareFolder();
        runClientServer();
    }

    private static void prepareFolder() {
        System.setProperty("user.dir", folder.getRoot().getAbsolutePath());
        try {
            folder.newFolder("folderA");
            File a = folder.newFile("fileA");
            folder.newFolder("folderB");
            File b = folder.newFile("fileB");

            FileUtils.write(a, "file a", Charset.defaultCharset());
            FileUtils.write(b, "file b", Charset.defaultCharset());
        }
        catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    private static void runClientServer() {
        try {
            server = new Server(8080);
            serverThread = new Thread(server);
            serverThread.start();

            client = new Client("127.0.0.1", 8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void shutdownServer() {
        if (server != null) {
            server.shutdown();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
        try {
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void executeListTest() {
        try {
            List<String> items = new ArrayList<>();
            List<Boolean> isDir = new ArrayList<>();
            client.executeList("", items, isDir);

            File[] itemList = new File(folder.getRoot().getAbsolutePath(), "").listFiles();
            for (int i = 0; i < itemList.length; i++) {
                assertEquals(itemList[i].getName(), items.get(i));
                assertEquals(itemList[i].isDirectory(), isDir.get(i));
            }

            assertEquals(0, client.executeList("NonExistingFolder", items, isDir));
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void executeGetTest() {
        try {
            ByteStream bs = client.executeGet("fileA");
            check(bs, new File(folder.getRoot().getAbsolutePath(), "fileA").getAbsolutePath());

            bs = client.executeGet("fileB");
            check(bs, new File(folder.getRoot().getAbsolutePath(), "fileB").getAbsolutePath());

            bs = client.executeGet("NonExistingFile");
            bs.readChunk();
            assertEquals(0L, bs.getFileLength());
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void multipleClientsTest() {
        try {
            final Client client2 = new Client("127.0.0.1", 8080);

            ByteStream bs = client.executeGet("fileA");
            check(bs, new File(folder.getRoot().getAbsolutePath(), "fileA").getAbsolutePath());

            bs = client2.executeGet("fileB");
            check(bs, new File(folder.getRoot().getAbsolutePath(), "fileB").getAbsolutePath());

            client2.closeConnection();
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    private boolean check(ByteStream bs, String path) throws IOException {
        long len = 0;
        byte[] buffer = new byte[Requests.BUFFER_SIZE];
        int bytesRead = 0;

        FileInputStream in = new FileInputStream(path);

        boolean f = true;
        while ((bytesRead = in.read(buffer)) != -1)
        {
            len += bytesRead;

            List<Byte> b = bs.readChunk();
            for (int i = 0; i < bytesRead; i++)
            {
                f &= (b.get(i) == buffer[i]);
            }
        }

        f &= (len == bs.getFileLength() && len == new File(path).length());
        return f;
    }
}
