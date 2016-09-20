package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.CommitConfig;
import vcs.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Parameters(commandNames = VcsUtils.COMMIT)
public class CommitCommand extends Command implements Serializable {
    @Parameter(description = "Record changes to the repository")
    private List<String> files;

    @Parameter(names = "-m", description = "Commit message")
    private String message;

    public CommitCommand() {}

    public CommitCommand(List<String> files, String message) {
        this.files = files;
        this.message = message;
    }

    /**
     * @return commit message
     */
    public String getMessage() {
        return message;
    }

    /**
     * commit changes in supervised files
     */
    @Override
    protected void execImpl() {
        if (message == null || message.length() == 0) {
            VcsUtils.log("specify commit message");
            return;
        }

        if (VcsUtils.diffDirFiles(
                CommitConfig.instance.supervisedFiles.stream().collect(Collectors.toList()),
                GlobalConfig.getProjectDir(),
                GlobalConfig.getHeadCommitDir()) == 0)
        {
            VcsUtils.log("nothing to commit");
            return;
        }

        GlobalConfig.instance.graph.commit(message);

        String source = GlobalConfig.getProjectDir();
        String dest = GlobalConfig.getHeadCommitDir();

        try {
            refreshSupervisedFilesList();

            Files.createDirectories(Paths.get(dest));

            VcsUtils.copyFiles(
                    CommitConfig.instance.supervisedFiles.stream().collect(Collectors.toList()),
                    source,
                    dest, true
            );
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void refreshSupervisedFilesList() {
        CommitConfig.instance.supervisedFiles = CommitConfig.instance.supervisedFiles
                .stream()
                .filter(f -> new File(VcsUtils.PROJECT_DIR + "/" + f).exists())
                .collect(Collectors.toCollection(HashSet::new));
    }
}
