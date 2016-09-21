package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.CommitConfig;
import vcs.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.util.stream.Collectors;

@Parameters(commandNames = VcsUtils.CHECKOUT, commandDescription = "Go to selected commit or branch")
public class CheckoutCommand extends Command {
    @Parameter(names = "-new", description = "Create new branch")
    private boolean createBranch = false;

    @Parameter(names = "-b", required = true, description = "choose branch to switch")
    private String branch;

    @Parameter(names = "-commit", description = "commit id")
    private int id = -1;

    public CheckoutCommand() {}

    public CheckoutCommand(String branch, int id) {
        this.branch = branch;
        this.id = id;
    }

    public CheckoutCommand(boolean createBranch, String branch) {
        this.createBranch = createBranch;
        this.branch = branch;
    }

    /**
     * switch to selected branch
     * or to selected commit on specified branch
     * or to newly created branch
     */
    @Override
    protected void execImpl() {
        if (!canCheckout(createBranch, branch, id)) {
            return;
        }

        String source = GlobalConfig.getHeadCommitDir();
        String dest = GlobalConfig.getProjectDir();

        try {
            if (createBranch || id == -1) {
                smallCheckout(branch);
            }
            else {
                smallCheckout(branch, id);
            }

            VcsUtils.copyFiles(
                    CommitConfig.instance.supervisedFiles.stream().collect(Collectors.toList()),
                    source,
                    dest, true
            );

            serializeConfig();
        } catch (Exception e) {
            /*GlobalConfig.rollback();
            CommitConfig.rollback();*/
            e.printStackTrace();
        }
    }

    private boolean canCheckout(boolean createBranch, String branch, int commitId) {
        if (createBranch) {
            return GlobalConfig.instance.graph.createBranch(branch);
        }
        else {
            if (commitId == -1) {
                return GlobalConfig.instance.graph.checkout(branch);
            }
            else {
                return GlobalConfig.instance.graph.checkout(branch, commitId);
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
}