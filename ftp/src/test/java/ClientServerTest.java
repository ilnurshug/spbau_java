import client.Client;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import server.Server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ClientServerTest {
    private Thread serverThread;
    private Server server;
    private Client client;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepare() {
        prepareFolder();
        runClientServer();
    }

    private void prepareFolder() {
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

    private void runClientServer() {
        try {
            server = new Server(8080);
            serverThread = new Thread(server);
            serverThread.start();

            client = new Client("127.0.0.1", 8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void shutdownServer() {
        server.shutdown();
        serverThread.interrupt();
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
            byte[] data = client.executeGet("fileA");
            byte[] expected = FileUtils.readFileToByteArray(new File(folder.getRoot().getAbsolutePath(), "fileA"));

            assertArrayEquals(expected, data);

            data = client.executeGet("fileB");
            expected = FileUtils.readFileToByteArray(new File(folder.getRoot().getAbsolutePath(), "fileB"));
            assertArrayEquals(expected, data);

            assertEquals(0, client.executeGet("NonExistingFile").length);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
