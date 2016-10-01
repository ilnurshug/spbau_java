package vcs.commands;

import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.io.IOException;

public abstract class Command {

    /**
     * execute command, and serialize result of execution
     */
    public void exec() {
        File vcsFile = new File(GlobalConfig.vcsDir());

        try {
            if (vcsFile.isDirectory()) {
                deserializeTempConfig();

                execImpl();

                serializeTempConfig();
            }
            else {
                System.out.println("repository is empty");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("serialization failure");
            e.printStackTrace();
        }
    }

    protected abstract void execImpl();

    public abstract String name();

    protected static void serializeTempConfig() throws IOException {
        serialize("config_tmp");
    }

    protected static void serializeConfig() throws IOException {
        CommitConfig.instance.clearDeletedFilesList();
        serialize("config");
    }

    protected static void deserializeTempConfig() throws IOException, ClassNotFoundException {
        deserialize("config_tmp");
    }

    protected static void deserializeConfig() throws IOException, ClassNotFoundException {
        deserialize("config");
    }

    private static void serialize(String configFile) throws IOException {
        VcsUtils.serialize(GlobalConfig.instance, GlobalConfig.globalConfigFile());
        VcsUtils.serialize(CommitConfig.instance, GlobalConfig.getHeadCommitDir() + configFile);
    }

    private static void deserialize(String configFile) throws IOException, ClassNotFoundException  {
        GlobalConfig.instance = (GlobalConfig) VcsUtils.deserialize(GlobalConfig.globalConfigFile());
        CommitConfig.instance = (CommitConfig) VcsUtils.deserialize(GlobalConfig.getHeadCommitDir() + configFile);
    }
}
