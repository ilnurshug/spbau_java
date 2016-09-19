package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.util.VcsUtils;

@Parameters(commandNames = VcsUtils.CHECKOUT, commandDescription = "Go to selected commit or branch")
public class CheckoutCommand extends Command {
    @Parameter(names = "-b", description = "Create new branch")
    private boolean branch = false;

    public CheckoutCommand() {}

    public CheckoutCommand(boolean branch) {
        this.branch = branch;
    }

    @Override
    public void exec() {

    }
}