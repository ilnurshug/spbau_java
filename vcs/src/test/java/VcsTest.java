import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

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
    public void checkoutTest3() {
        VCS.run("add", "a");
        VCS.run("commit", "-m", "\"first commit\"");

        try {
            folder.newFile("b");

            VCS.run("add", "b");
            VCS.run("commit", "-m", "\"second commit\"");

            folder.newFile("c");

            VCS.run("add", "c");
            VCS.run("commit", "-m", "\"third commit\"");

            VCS.run("checkout", "-b", "master", "-c", "1");
            assertFilesInDir(folder.getRoot(), ".vcs", "a");

            VCS.run("checkout", "-b", "master", "-c", "3");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c");

            VcsUtils.deleteFiles(Collections.singletonList("a"), folder.getRoot().getAbsolutePath() + "/");
            VCS.run("commit", "-m", "\"forth commit\"");

            VCS.run("checkout", "-b", "master", "-c", "3");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c");

            VCS.run("checkout", "-b", "master", "-c", "4");
            assertFilesInDir(folder.getRoot(), ".vcs", "b", "c");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mergeTest() {
        checkoutTest2();

        VCS.run("checkout", "-b", "master", "-c", "-1");
        assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "d");

        VCS.run("merge", "-b", "br");
        assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c", "d");

        VCS.run("log");
    }

    public void mergeTest2() {

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

    @Test
    public void cleanTest() {
        VCS.run("add", "a");
        VCS.run("commit", "-m", "first");

        assertFilesInDir(folder.getRoot(), ".vcs", "a");

        try {
            folder.newFile("b");

            VCS.run("add", "b");
            VCS.run("commit", "-m", "second");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b");

            folder.newFile("c");
            folder.newFolder("f");

            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "c", "f");

            VCS.run("clean");

            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b", "f");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void rmTest() {
        VCS.run("add", "a");
        VCS.run("commit", "-m", "first");

        assertFilesInDir(folder.getRoot(), ".vcs", "a");

        try {
            folder.newFile("b");

            VCS.run("add", "b");
            VCS.run("commit", "-m", "second");
            assertFilesInDir(folder.getRoot(), ".vcs", "a", "b");

            VCS.run("rm", "-f", "a");

            VCS.run("commit", "-m", "third");

            assertFilesInDir(folder.getRoot(), ".vcs", "b");

            VCS.run("checkout", "-b", "master", "-c", "1");
            assertFilesInDir(folder.getRoot(), ".vcs", "a");

            VCS.run("checkout", "-b", "master", "-c", "-1");
            assertFilesInDir(folder.getRoot(), ".vcs", "b");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void statusTest() {
        VCS.run("add", "a");
        VCS.run("commit", "-m", "first");

        try {
            folder.newFile("b");
            folder.newFile("c");

            VCS.run("add", "b");
            VCS.run("commit", "-m", "second");

            VCS.run("rm", "-f", "a");

            VCS.run("status");

            checkSupervisedFilesList("a", "b");
            checkDeletedFilesList("a");
            checkUnsupervisedFilesList("c");

            VCS.run("commit", "-m", "third");
            VCS.run("status");

            checkSupervisedFilesList("b");
            checkDeletedFilesList();
            checkUnsupervisedFilesList("c");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readFile(File f) throws IOException {
        return String.join("\n", Files.readLines(f, Charset.defaultCharset()));
    }

    private void checkSupervisedFilesList(String... files) {
        Arrays.sort(files);
        assertArrayEquals(files, CommitConfig.instance.getSupervisedFiles().stream().sorted().toArray());
    }

    private void checkDeletedFilesList(String... files) {
        Arrays.sort(files);
        assertArrayEquals(files, CommitConfig.instance.getDeletedFiles().stream().sorted().toArray());
    }

    private void checkUnsupervisedFilesList(String... files) {
        Arrays.sort(files);
        assertArrayEquals(files, CommitConfig.instance.getUnsupervisedFiles().stream().sorted().toArray());
    }

    /**
     * check whether all files contains in dir or not
     */
    private void assertFilesInDir(File dir, String... files) {
        Object[] filesInDir = Arrays.stream(dir.listFiles())
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList()).toArray();

        Arrays.sort(files);

        assertArrayEquals(filesInDir, files);
    }
}
