package vcs.commands;

import java.util.HashMap;

public class CommandFactory {

    private final HashMap<String, Command> commands = new HashMap<>();

    public void registerCommand(Command cmd) {
        commands.put(cmd.name(), cmd);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }
}
