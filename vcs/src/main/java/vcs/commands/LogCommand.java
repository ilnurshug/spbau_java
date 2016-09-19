package vcs.commands;

import com.beust.jcommander.Parameters;
import vcs.util.VcsUtils;

@Parameters(commandNames = VcsUtils.LOG, commandDescription = "Show current branch's history")
public class LogCommand extends Command {
    @Override
    public void exec() {

    }
}