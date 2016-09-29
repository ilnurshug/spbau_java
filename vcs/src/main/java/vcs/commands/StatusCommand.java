package vcs.commands;

import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.util.VcsUtils;

public class StatusCommand extends Command {

    @Override
    public String name() {
        return "status";
    }

    /**
     * list of all supervised files
     */
    @Override
    public void exec() {
        System.out.println("Supervised files:");
        CommitConfig.instance.getSupervisedFiles().forEach(System.out::println);
        System.out.println("---");
    }

    @Override
    protected void execImpl() {}
}
