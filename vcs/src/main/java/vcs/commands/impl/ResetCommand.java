package vcs.commands.impl;

import com.beust.jcommander.Parameter;
import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;

import java.io.File;

public class ResetCommand extends Command {
    @Parameter(names = "-f", required = true, description = "Select file")
    private String file;

    public ResetCommand() {
    }

    public ResetCommand(String file) {
        this.file = file;
    }

    @Override
    protected void execImpl() {
        File f = new File(GlobalConfig.projectDir() + file);
        if (f.isFile() && CommitConfig.instance.isSupervised(file)) {
            CommitConfig.instance.removeFromSupervisedList(file);
        }
    }

    @Override
    public String name() {
        return "reset";
    }
}
