package vcs.commands;

import com.beust.jcommander.Parameters;
import vcs.CommitConfig;
import vcs.util.VcsUtils;

@Parameters(commandNames = VcsUtils.STATUS)
public class StatusCommand extends Command {

    /**
     * list of all supervised files
     */
    @Override
    public void exec() {
        System.out.println("Supervised files:");
        CommitConfig.instance.supervisedFiles.forEach(System.out::println);
        System.out.println("---");
    }

    @Override
    protected void execImpl() {}
}
