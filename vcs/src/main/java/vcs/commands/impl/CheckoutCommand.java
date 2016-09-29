package vcs.commands.impl;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.graph.Commit;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CheckoutCommand extends Command {

    @Parameter(names = "-b", required = true, description = "choose branch to switch")
    private String branch;

    @Parameter(names = "-c", required = true, description = "commit id")
    private int id = -1;

    public CheckoutCommand() {}

    public CheckoutCommand(String branch, int id) {
        this.branch = branch;
        this.id = id;
    }

    @Override
    public String name() {
        return "checkout";
    }

    /**
     * switch to selected branch
     * or to selected commit on specified branch
     */
    @Override
    protected void execImpl() {
        if (!canCheckout(branch, id)) {
            System.err.println("can not checkout");
            return;
        }

        Commit headCommit = GlobalConfig.instance.graph.getHead();
        Commit destCommit = GlobalConfig.instance.graph.getCommit(branch, id);

        List<Commit> path = GlobalConfig.instance.graph.path(headCommit, destCommit);

        path.remove(0);

        try {
            Commit currentCommit = headCommit;
            for (Commit c : path) {
                if (currentCommit.getParents().contains(c)) {
                    checkoutToPrevCommit(currentCommit, c);
                }
                else if (currentCommit.getChildren().contains(c)) {
                    checkoutToNextCommit(currentCommit, c);
                }
                currentCommit = c;
            }

            serializeConfig();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean canCheckout(String branch, int commitId) {
        if (GlobalConfig.instance.isDeletedBranch(branch)) {
            System.err.println("can not switch to deleted branch");
            return false;
        }
        else {
            if (commitId == -1) {
                return GlobalConfig.instance.graph.isContains(branch);
            }
            else {
                return GlobalConfig.instance.graph.canCheckout(branch, commitId);
            }
        }
    }

    static void smallCheckout(String branch) throws ClassNotFoundException, IOException {
        GlobalConfig.instance.graph.checkout(branch);
        CommitConfig.instance = (CommitConfig) VcsUtils.deserialize(GlobalConfig.getHeadCommitDir() + "config");
    }

    static void smallCheckout(String branch, int commitId) throws ClassNotFoundException, IOException {
        GlobalConfig.instance.graph.checkout(branch, commitId);
        CommitConfig.instance = (CommitConfig) VcsUtils.deserialize(GlobalConfig.getHeadCommitDir() + "config");
    }

    private void checkoutToPrevCommit(Commit cur, Commit prev) throws ClassNotFoundException, IOException {
        List<String> sf = CommitConfig.instance.getSupervisedFiles();

        smallCheckout(prev.getBranch(), prev.getId());

        sf.stream()
                .filter(f -> !CommitConfig.instance.isSupervised(f))
                .forEach(f -> VcsUtils.deleteFiles(Collections.singletonList(f), GlobalConfig.projectDir()));

        CommitConfig.instance.getSupervisedFiles().stream()
                .filter(f -> {
                    try {
                        String a = VcsUtils.getFileHash(GlobalConfig.projectDir() + f);
                        String b = CommitConfig.instance.getSupervisedFileHash(f);
                        return !a.equals(b);
                    } catch (IOException e) {
                        return true;
                    }
                })
                .forEach(f -> {
                    VcsUtils.copyFiles(
                            Collections.singletonList(f),
                            CommitConfig.instance.getSupervisedFileCopyAddr(f),
                            GlobalConfig.projectDir(),
                            true);
                });
    }

    private void checkoutToNextCommit(Commit cur, Commit next) throws ClassNotFoundException, IOException {
        List<String> sf = CommitConfig.instance.getSupervisedFiles();

        smallCheckout(next.getBranch(), next.getId());
        String commitDir = GlobalConfig.getHeadCommitDir();

        sf.stream()
                .filter(f -> !CommitConfig.instance.isSupervised(f))
                .forEach(f -> VcsUtils.deleteFiles(Collections.singletonList(f), GlobalConfig.projectDir()));

        VcsUtils.copyFiles(
                CommitConfig.instance.getSupervisedFiles(),
                commitDir,
                GlobalConfig.projectDir(),
                true);
    }
}