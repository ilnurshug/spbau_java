package vcs.commands;

import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Parameters(commandNames = VcsUtils.INIT, commandDescription = "InitCommand repository in current folder")
public class InitCommand extends Command {

    /**
     * init existing repository if vcs directory is already exists
     * or empty repository
     */
    @Override
    public void exec() {
        File vcsFile = new File(VcsUtils.vcsDir());

        try {
            if (vcsFile.isDirectory()) {
                deserializeTempConfig();
            }
            else {
                GlobalConfig.instance = new GlobalConfig();
                CommitConfig.instance = new CommitConfig();

                Files.createDirectories(Paths.get(VcsUtils.vcsDir()));
                Files.createDirectories(Paths.get(VcsUtils.branchesDir() + "/master/0"));

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
