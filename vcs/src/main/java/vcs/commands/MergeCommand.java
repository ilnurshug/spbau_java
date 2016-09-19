package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.util.VcsUtils;

@Parameters(commandNames = VcsUtils.MERGE, commandDescription = "Merge current branch with selected one")
public class MergeCommand extends Command {
    @Parameter(description = "Select branch")
    private String branch;

    public MergeCommand() {}

    public MergeCommand(String branch) {
        this.branch = branch;
    }

    @Override
    public void exec() {

    }
}
