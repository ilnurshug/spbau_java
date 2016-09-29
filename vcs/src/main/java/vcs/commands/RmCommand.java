package vcs.commands;

import com.beust.jcommander.Parameter;

public class RmCommand extends Command {
    @Parameter(names = "-f", required = true, description = "Select file")
    private String file;

    @Override
    public String name() {
        return "rm";
    }

    @Override
    protected void execImpl() {

    }
}
