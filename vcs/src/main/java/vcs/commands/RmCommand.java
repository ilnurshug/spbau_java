package vcs.commands;

import com.beust.jcommander.Parameter;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.util.Collections;

public class RmCommand extends Command {
    @Parameter(names = "-f", required = true, description = "Select file")
    private String file;

    @Override
    public String name() {
        return "rm";
    }

    @Override
    protected void execImpl() {
        if (new File(GlobalConfig.projectDir() + file).isFile()) {
            VcsUtils.deleteFiles(Collections.singletonList(file), GlobalConfig.projectDir());
        }
    }
}
