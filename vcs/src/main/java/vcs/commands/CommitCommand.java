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

        List<String> diff = differentFiles();
        if (diff.isEmpty() && !GlobalConfig.instance.graph.isCreateBranch()) {
            System.out.println("nothing to commit");
            return;
        }

        GlobalConfig.instance.graph.commit(message);

        String source = GlobalConfig.getProjectDir();
        String dest = GlobalConfig.getHeadCommitDir();

        try {
            refreshSupervisedFilesList();
            diff = diff.stream()
                    .filter(f -> new File(VcsUtils.projectDir() + "/" + f).exists())
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

    private List<String> differentFiles() {
        return CommitConfig.instance.getSupervisedFiles().stream().filter(f -> {
            try {
                String current = VcsUtils.getFileHash(GlobalConfig.getProjectDir() + f);
                String old = CommitConfig.instance.getSupervisedFileHash(f);

                return !current.equals(old) || CommitConfig.instance.getSupervisedFileCopyAddr(f) == null;
            }
            catch (IOException e) {
                return true;
            }
        }).collect(Collectors.toList());
    }

    private void refreshSupervisedFilesList() {
        List<String> s = CommitConfig.instance.getSupervisedFiles()
                .stream()
                .filter(f -> new File(VcsUtils.projectDir() + "/" + f).exists())
                .collect(Collectors.toList());

        CommitConfig.instance.clearSupervisedFilesList();

        s.forEach(CommitConfig.instance::addSupervisedFile);

        s.forEach(f -> {
            try {
                CommitConfig.instance.addSupervisedFileHash(f, VcsUtils.getFileHash(VcsUtils.projectDir() + "/" + f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
