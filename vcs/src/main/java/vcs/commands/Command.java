package vcs.commands;

import vcs.Config;
import vcs.util.VcsUtils;

import java.io.IOException;

public abstract class Command {

    public void exec() {
        execImpl();

        try {
            VcsUtils.serialize(Config.INSTANCE, VcsUtils.CONFIG_FILE);
        } catch (IOException e) {
            VcsUtils.log("serialization failure");
            e.printStackTrace();
        }
    }

    protected abstract void execImpl();
}
