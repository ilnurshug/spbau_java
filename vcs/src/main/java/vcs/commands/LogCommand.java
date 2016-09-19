package vcs.commands;

import com.beust.jcommander.Parameters;
import vcs.Config;
import vcs.graph.Commit;
import vcs.graph.CommitGraph;
import vcs.util.VcsUtils;

import java.io.IOException;

@Parameters(commandNames = VcsUtils.LOG, commandDescription = "Show current branch's history")
public class LogCommand extends Command {
    @Override
    protected void execImpl() {
        Commit c = Config.INSTANCE.graph.getHead();
        while (c != null) {
            System.out.println(c.getMessage());

            c = CommitGraph.getPrevCommit(c);
        }
    }
}