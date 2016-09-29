package vcs.commands.impl;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AddCommand extends Command {
    @Parameter(description = "Add file contents to the index")
    private List<String> files;

    public AddCommand() {}

    public AddCommand(List<String> files) {
        this.files = files;
    }

    @Override
    public String name() {
        return "add";
    }

    /**
     * add files to list of supervised files
     */
    @Override
    protected void execImpl() {
        System.out.println("added files:");
        files = files.stream()
                .filter(f -> new File(GlobalConfig.projectDir() + f).exists())
                .collect(Collectors.toList());

        files.forEach(CommitConfig.instance::addSupervisedFile);

        files.forEach(f -> {
            try {
                CommitConfig.instance.addSupervisedFileHash(f, VcsUtils.getFileHash(GlobalConfig.projectDir() + f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        files.forEach(System.out::println);
        System.out.println("---");
    }
}
