package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.util.VcsUtils;

import java.util.List;

@Parameters(commandNames = VcsUtils.ADD)
public class AddCommand extends Command {
    @Parameter(description = "Add file contents to the index")
    private List<String> files;

    public AddCommand() {}

    public AddCommand(List<String> files) {
        this.files = files;
    }

    @Override
    public void exec() {

    }
}
