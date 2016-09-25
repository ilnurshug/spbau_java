package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Parameters(commandNames = VcsUtils.ADD)
public class AddCommand extends Command {
    @Parameter(description = "Add file contents to the index")
    private List<String> files;

    public AddCommand() {}

    public AddCommand(List<String> files) {
        this.files = files;
    }

    /**
     * add files to list of supervised files
     */
    @Override
    protected void execImpl() {
        System.out.println("added files:");
        files = files.stream()
                .filter(f -> new File(VcsUtils.projectDir() + "/" + f).exists())
                .collect(Collectors.toList());

        files.forEach(CommitConfig.instance::addSupervisedFile);
        files.forEach(System.out::println);
        System.out.println("---");
    }
}
