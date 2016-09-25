import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class VcsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private List<File> files;

    @Before
    public void before() {
        System.setProperty("user.dir", folder.getRoot().getAbsolutePath());
        files = new LinkedList<>();

        VCS.run("init");

        try {
            File a = folder.newFile("a");
            files.add(a);
            Files.append("first", a, Charset.defaultCharset());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        folder.delete();
    }

    @Test
    public void simpleTest() {
        String expected = "added files:\n" +
                "a\n" +
                "---\n" +
                "Supervised files:\n" +
                "a\n" +
                "---\n" +
                "successful commit\n" +
                "commit history of branch master\n" +
                "1: first commit\n" +
                "0: new branch - master\n" +
                "---\n" +
                "firstsecond\n" +
                "successful commit\n" +
                "commit history of branch master\n" +
                "2: second commit\n" +
                "1: first commit\n" +
                "0: new branch - master\n" +
                "---\n" +
                "first\n" +
                "commit history of branch master\n" +
                "1: first commit\n" +
                "0: new branch - master\n" +
                "---\n" +
                "firstsecond\n" +
                "commit history of branch master\n" +
                "2: second commit\n" +
                "1: first commit\n" +
                "0: new branch - master\n" +
                "---";
        try {
            File out = new File("out");
            System.setOut(new PrintStream(out));

            VCS.run("add", "a");
            VCS.run("status");
            VCS.run("commit", "-m", "\"first commit\"");
            VCS.run("log");

            File a = files.get(0);

            Files.append("second", a, Charset.defaultCharset());
            System.out.println(readFile(a));

            VCS.run("commit", "-m", "\"second commit\"");
            VCS.run("log");

            VCS.run("checkout", "-b", "master", "-c", "1");

            System.out.println(readFile(a));

            VCS.run("log");
            VCS.run("checkout", "-b", "master", "-c", "2");

            System.out.println(readFile(a));

            VCS.run("log");

            assertEquals(expected, readFile(out));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addTest() {
        try {
            VCS.run("add", "a");
            VCS.run("commit", "-m", "first");

            checkSupervisedFilesList("a");

            File b = folder.newFile("b");

            VCS.run("add", "b");
            VCS.run("commit", "-m", "second");
            checkSupervisedFilesList("a", "b");

            VCS.run("checkout", "-b", "master", "-c", "1");

            checkSupervisedFilesList("a");

            VCS.run("checkout", "-b", "master", "-c", "2");

            checkSupervisedFilesList("a", "b");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkoutTest1() {
        try {
            folder.newFile("c");

            VCS.run("add", "a");
            VCS.run("commit", "-m", "first");

            folder.newFile("b");

            VCS.run("add", "b");
            VCS.run("commit", "-m", "second");

            VCS.run("checkout", "-b", "master", "-c", "1");

            assertFilesInDir(folder.getRoot(), ".vcs", "a", "c");

            VCS.run("checkout", "-b", "master", "-c", "2");

            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkoutTest2() {
        try {
            VCS.run("add", "a");
            VCS.run("commit", "-m", "first");

            folder.newFile("b");

            VCS.run("add", "b");
            VCS.run("commit", "-m", "second");

            folder.newFile("c");

            VCS.run("branch", "-a", "1", "-b", "br");

            VCS.run("add", "c");
            VCS.run("commit", "-m", "first on br");

            VCS.run("log");

            VCS.run("checkout", "-b", "master", "-c", "-1");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b");

            VCS.run("checkout", "-b", "br", "-c", "-1");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c");

            VCS.run("checkout", "-b", "master", "-c", "-1");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b");

            folder.newFile("d");
            VCS.run("add", "d");
            VCS.run("commit", "-m", "third");

            VCS.run("checkout", "-b", "br", "-c", "-1");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c");

            VCS.run("checkout", "-b", "master", "-c", "-1");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "d");

            VCS.run("checkout", "-b", "master", "-c", "2");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mergeTest() {
        checkoutTest2();

        VCS.run("checkout", "-b", "master", "-c", "-1");
        assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "d");

        VCS.run("merge", "br");
        assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c", "d");

        VCS.run("log");
    }

    @Test
    public void branchTest() {
        checkoutTest2();

        VCS.run("checkout", "-b", "master", "-c", "-1");
        assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "d");

        VCS.run("branch", "-a", "0", "-b", "br");
        assertEquals(GlobalConfig.getCurrentBranch(), "master");
        VCS.run("checkout", "-b", "br", "-c", "-1");
        assertEquals(GlobalConfig.getCurrentBranch(), "master");
    }

    private String readFile(File f) throws IOException {
        return String.join("\n", Files.readLines(f, Charset.defaultCharset()));
    }

    private void checkSupervisedFilesList(String... files) {
        Arrays.sort(files);
        assertArrayEquals(files, CommitConfig.instance.getSupervisedFiles().stream().sorted().toArray());
    }

    /**
     * check whether all files contains in dir or not
     */
    private void assertFilesInDir(File dir, String... files) {
        Object[] filesInDir = Arrays.stream(dir.listFiles())
                .filter(Objects::nonNull)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList()).toArray();

        Arrays.sort(files);

        assertArrayEquals(filesInDir, files);
    }
}
