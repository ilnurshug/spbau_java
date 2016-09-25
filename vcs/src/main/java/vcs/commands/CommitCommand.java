package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Parameters(commandNames = VcsUtils.COMMIT)
public class CommitCommand extends Command implements Serializable {

    @Parameter(names = "-m", required = true, description = "Commit message")
    private String message;

    public CommitCommand() {}

    public CommitCommand(String message) {
        this.message = message;
    }

    /**
     * commit changes in supervised files
     */
    @Override
    protected void execImpl() {
        if (message == null || message.length() == 0) {
            System.out.println("specify commit message");
            return;
        }

        if (VcsUtils.diffDirFiles(
                CommitConfig.instance.getSupervisedFiles(),
                GlobalConfig.getProjectDir(),
                GlobalConfig.getHeadCommitDir()) == 0 && !GlobalConfig.instance.graph.isCreateBranch())
        {
            System.out.println("nothing to commit");
            return;
        }

        GlobalConfig.instance.graph.commit(message);

        String source = GlobalConfig.getProjectDir();
        String dest = GlobalConfig.getHeadCommitDir();

        try {
            refreshSupervisedFilesList();

            Files.createDirectories(Paths.get(dest));

            VcsUtils.copyFiles(
                    CommitConfig.instance.getSupervisedFiles(),
                    source,
                    dest, true
            );

            serializeConfig();

            System.out.println("successful commit");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void refreshSupervisedFilesList() {
        Stream<String> s = CommitConfig.instance.getSupervisedFiles()
                .stream()
                .filter(f -> new File(VcsUtils.projectDir() + "/" + f).exists());

        CommitConfig.instance.clearSupervisedFilesList();

        s.forEach(CommitConfig.instance::addSupervisedFile);
    }
}
