package vcs.commands;

import vcs.CommitConfig;
import vcs.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.IOException;

public abstract class Command {

    public void exec() {
        execImpl();

        try {
            VcsUtils.serialize(GlobalConfig.instance, VcsUtils.GLOBAL_CONFIG_FILE);
            VcsUtils.serialize(CommitConfig.instance, GlobalConfig.getHeadCommitDir() + "config");
        } catch (IOException e) {
            VcsUtils.log("serialization failure");
            e.printStackTrace();
        }
    }

    protected abstract void execImpl();
}
