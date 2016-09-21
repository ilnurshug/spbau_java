import com.beust.jcommander.JCommander;
import vcs.commands.*;
import vcs.util.VcsUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class VCS {
    private static HashMap<String, Command> cmd = new HashMap<>();

    private static final CommitCommand commit = new CommitCommand();
    private static final AddCommand add = new AddCommand();
    private static final CheckoutCommand checkout = new CheckoutCommand();
    private static final LogCommand log = new LogCommand();
    private static final InitCommand init = new InitCommand();
    private static final MergeCommand merge = new MergeCommand();
    private static final StatusCommand status = new StatusCommand();

    static {
        cmd.put(VcsUtils.COMMIT, commit);
        cmd.put(VcsUtils.ADD, add);
        cmd.put(VcsUtils.CHECKOUT, checkout);
        cmd.put(VcsUtils.LOG, log);
        cmd.put(VcsUtils.INIT, init);
        cmd.put(VcsUtils.MERGE, merge);
        cmd.put(VcsUtils.STATUS, status);
    }

    public static void main(String[] args) {
        String[] command = new String[args.length - 1];
        System.arraycopy(args, 1, command, 0, args.length - 1);

        run(command);
    }

    public static void run(String... args) {
        final JCommander jc = new JCommander();

        jc.addCommand(commit);
        jc.addCommand(add);
        jc.addCommand(checkout);
        jc.addCommand(log);
        jc.addCommand(init);
        jc.addCommand(merge);
        jc.addCommand(status);

        try {
            jc.parse(args);

            Command c = cmd.getOrDefault(jc.getParsedCommand(), null);
            c.exec();
        } catch (Exception e) {
            System.err.println("Unknown command. See usage");
            jc.usage();
        }
    }

}
