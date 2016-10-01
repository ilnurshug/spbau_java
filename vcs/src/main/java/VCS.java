import com.beust.jcommander.JCommander;
import vcs.commands.*;
import vcs.commands.impl.*;

import java.util.Arrays;
import java.util.List;

public class VCS {
    private static CommandFactory factory = new CommandFactory();

    private static final List<Command> commands = Arrays.asList(
            new CommitCommand(),
            new AddCommand(),
            new CheckoutCommand(),
            new LogCommand(),
            new InitCommand(),
            new MergeCommand(),
            new StatusCommand(),
            new BranchCommand(),
            new CleanCommand(),
            new RmCommand(),
            new ResetCommand()
    );

    static {
        commands.forEach(factory::registerCommand);
    }

    public static void main(String[] args) {
        String[] command = new String[args.length - 1];
        System.arraycopy(args, 1, command, 0, args.length - 1);

        run(command);
    }

    public static void run(String... args) {
        final JCommander jc = new JCommander();

        commands.forEach(c -> jc.addCommand(c.name(), c));

        try {
            jc.parse(args);

            Command c = factory.getCommand(jc.getParsedCommand());
            c.exec();
        } catch (Exception e) {
            System.err.println("Unknown command. See usage");
            jc.usage();
        }
    }

}
