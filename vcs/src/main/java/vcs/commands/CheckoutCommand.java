package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.Config;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Parameters(commandNames = VcsUtils.CHECKOUT, commandDescription = "Go to selected commit or branch")
public class CheckoutCommand extends Command {
    @Parameter(names = "-b", description = "Create new branch")
    private boolean createBranch = false;

    @Parameter(description = "choose branch to switch")
    private String branch;

    @Parameter(description = "commit id")
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

    @Override
    protected void execImpl() {
        if (!canCheckout()) {
            return;
        }

        String branch = Config.INSTANCE.graph.getHead().getBranch();
        int commitId = Config.INSTANCE.graph.getHead().getId();

        String source = VcsUtils.BRANCHES_DIR + "/" + branch + "/" + commitId + "/";
        String dest = Config.INSTANCE.dir + "/";

        try {
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

    private boolean canCheckout() {
        if (createBranch) {
            return Config.INSTANCE.graph.createBranch(branch);
        }
        else {
            if (id == -1) {
                return Config.INSTANCE.graph.checkout(branch);
            }
            else {
                return Config.INSTANCE.graph.checkout(branch, id);
            }
        }
    }
}