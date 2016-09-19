package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.commons.io.FileUtils;
import vcs.Config;
import vcs.util.VcsUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public String getMessage() {
        return message;
    }

    @Override
    protected void execImpl() {
        if (message == null || message.length() == 0) {
            VcsUtils.log("specify commit message");
            return;
        }

        Config.INSTANCE.graph.commit(message);

        String branch = Config.INSTANCE.graph.getHead().getBranch();
        int commitId = Config.INSTANCE.graph.getHead().getId();

        String source = Config.INSTANCE.dir + "/";
        String dest = VcsUtils.BRANCHES_DIR + "/" + branch + "/" + commitId + "/";

        try {
            Files.createDirectories(Paths.get(dest));

            VcsUtils.copyFiles(
                    Config.INSTANCE.supervisedFiles.stream().collect(Collectors.toList()),
                    source,
                    dest
            );
        } catch (Exception e) {
            Config.rollbackCommand();
            e.printStackTrace();
        }
    }
}
