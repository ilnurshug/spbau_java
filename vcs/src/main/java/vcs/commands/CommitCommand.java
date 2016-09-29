package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CommitCommand extends Command implements Serializable {

    @Parameter(names = "-m", required = true, description = "Commit message")
    private String message;

    public CommitCommand() {}

    public CommitCommand(String message) {
        this.message = message;
    }

    @Override
    public String name() {
        return "commit";
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

        List<String> diff = CommitConfig.instance.differentFiles();
        if (diff.isEmpty() && !GlobalConfig.instance.graph.isCreateBranch()) {
            System.out.println("nothing to commit");
            return;
        }

        GlobalConfig.instance.graph.commit(message);

        String source = GlobalConfig.projectDir();
        String dest = GlobalConfig.getHeadCommitDir();

        try {
            refreshSupervisedFilesList();
            diff = diff.stream()
                    .filter(f -> new File(GlobalConfig.projectDir() + f).exists())
                    .collect(Collectors.toList());

            Files.createDirectories(Paths.get(dest));

            VcsUtils.copyFiles(
                    diff,
                    source,
                    dest, true
            );

            diff.forEach(f -> {
                CommitConfig.instance.setSupervisedFileCopyAddr(f, dest);
            });

            serializeConfig();

            System.out.println("successful commit");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void refreshSupervisedFilesList() {
        List<String> s = CommitConfig.instance.getSupervisedFiles()
                .stream()
                .filter(f -> new File(GlobalConfig.projectDir() + f).exists())
                .collect(Collectors.toList());

        CommitConfig.instance.clearSupervisedFilesList();

        s.forEach(CommitConfig.instance::addSupervisedFile);

        s.forEach(f -> {
            try {
                CommitConfig.instance.addSupervisedFileHash(f, VcsUtils.getFileHash(GlobalConfig.projectDir() + f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
