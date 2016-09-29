package vcs.commands;

import com.beust.jcommander.Parameters;
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
        List<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(new File(GlobalConfig.projectDir()).listFiles()));

        VcsUtils.deleteFiles(
                files.stream()
                        .filter(f -> !f.isDirectory())
                        .map(File::getName)
                        .filter(f -> !CommitConfig.instance.isSupervised(f))
                        .collect(Collectors.toList()),
                GlobalConfig.projectDir()
        );

    }
}
