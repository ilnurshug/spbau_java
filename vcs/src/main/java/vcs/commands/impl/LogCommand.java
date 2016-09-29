package vcs.commands.impl;

import com.beust.jcommander.Parameters;
import vcs.commands.Command;
import vcs.config.GlobalConfig;
import vcs.graph.Commit;
import vcs.graph.CommitGraph;
import vcs.util.VcsUtils;

public class LogCommand extends Command {

    @Override
    public String name() {
        return "log";
    }

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