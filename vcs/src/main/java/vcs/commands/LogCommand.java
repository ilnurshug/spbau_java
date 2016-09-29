package vcs.commands;

import com.beust.jcommander.Parameters;
import vcs.config.GlobalConfig;
import vcs.graph.Commit;
import vcs.graph.CommitGraph;
import vcs.util.VcsUtils;

@Parameters(commandNames = VcsUtils.LOG, commandDescription = "Show current branch's history")
public class LogCommand extends Command {

    /**
     * show commit history of current branch
     */
    @Override
    protected void execImpl() {
        Commit c = GlobalConfig.instance.graph.getHead();
        System.out.println("commit history of branch " + c.getBranch());
        while (c != null) {
            System.out.println(c.getId() + ": " + c.getMessage());

            c = CommitGraph.getPrevCommit(c);
        }
        System.out.println("---");
    }
}