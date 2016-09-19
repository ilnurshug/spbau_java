package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.util.VcsUtils;

import java.util.List;

@Parameters(commandNames = VcsUtils.COMMIT)
public class CommitCommand  extends Command {
    @Parameter(description = "Record changes to the repository")
    private List<String> files;

    @Parameter(names = "-m", description = "Commit message")
    private String message;

    public CommitCommand() {}

    public CommitCommand(List<String> files, String message) {
        this.files = files;
        this.message = message;
    }

    @Override
    public void exec() {
        System.out.println(message);
    }
}
