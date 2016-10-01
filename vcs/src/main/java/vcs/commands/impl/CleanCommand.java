package vcs.commands.impl;

import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

public class CleanCommand extends Command {
    @Override
    public String name() {
        return "clean";
    }

    @Override
    protected void execImpl() {
        VcsUtils.deleteFiles(CommitConfig.instance.getUnsupervisedFiles(), GlobalConfig.projectDir());
    }
}
