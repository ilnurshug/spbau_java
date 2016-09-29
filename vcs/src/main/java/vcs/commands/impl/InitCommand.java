package vcs.commands.impl;

import com.beust.jcommander.Parameters;
import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InitCommand extends Command {

    @Override
    public String name() {
        return "init";
    }

    /**
     * init existing repository if vcs directory is already exists
     * or empty repository
     */
    @Override
    public void exec() {
        File vcsFile = new File(GlobalConfig.vcsDir());

        try {
            if (vcsFile.isDirectory()) {
                deserializeTempConfig();
            }
            else {
                GlobalConfig.instance = new GlobalConfig();
                CommitConfig.instance = new CommitConfig();

                Files.createDirectories(Paths.get(GlobalConfig.vcsDir()));
                Files.createDirectories(Paths.get(GlobalConfig.branchesDir() + "/master/0"));

                serializeTempConfig();
                serializeConfig();

                System.out.println("initialized empty VCS repository");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("unable to init repo");
            e.printStackTrace();
        }
    }

    @Override
    protected void execImpl() {}
}
