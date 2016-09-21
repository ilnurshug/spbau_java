package vcs.commands;

import com.beust.jcommander.Parameters;
import vcs.util.VcsUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Parameters(commandNames = VcsUtils.INIT, commandDescription = "InitCommand repository in current folder")
public class InitCommand extends Command {

    /**
     * init existing repository if .vcs directory is already exists
     * or empty repository
     */
    @Override
    public void exec() {
        File vcsFile = new File(VcsUtils.VCS_DIR);

        try {
            if (vcsFile.isDirectory()) {
                deserializeTempConfig();
            }
            else {
                Files.createDirectories(Paths.get(VcsUtils.VCS_DIR));
                Files.createDirectories(Paths.get(VcsUtils.BRANCHES_DIR + "/master/0"));

                serializeTempConfig();
                serializeConfig();

                VcsUtils.log("initialized empty VCS repository");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("unable to init repo");
            e.printStackTrace();
        }
    }

    @Override
    protected void execImpl() {}
}
