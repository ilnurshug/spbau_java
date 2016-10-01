package vcs.commands.impl;

import com.beust.jcommander.Parameters;
import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CleanCommand extends Command {
    @Override
    public String name() {
        return "clean";
    }

    @Override
    protected void execImpl() {
        List<String> files = new LinkedList<>();
        files.addAll(CommitConfig.instance.getUnsupervisedFiles());

        VcsUtils.deleteFiles(
                files.stream()
                        .filter(f -> !CommitConfig.instance.isSupervised(f))
                        .collect(Collectors.toList()),
                GlobalConfig.projectDir()
        );

    }
}
