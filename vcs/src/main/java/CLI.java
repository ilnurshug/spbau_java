import com.beust.jcommander.JCommander;
import vcs.commands.*;
import vcs.util.VcsUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CLI {
    private static final JCommander jc = new JCommander();

    private static HashMap<String, Command> cmd = new HashMap<>();

    private static final CommitCommand      commit      = new CommitCommand();
    private static final AddCommand         add         = new AddCommand();
    private static final CheckoutCommand    checkout    = new CheckoutCommand();
    private static final LogCommand         log         = new LogCommand();
    private static final InitCommand        init        = new InitCommand();
    private static final MergeCommand       merge       = new MergeCommand();

    static {
        cmd.put(VcsUtils.COMMIT, commit);
        cmd.put(VcsUtils.ADD, add);
        cmd.put(VcsUtils.CHECKOUT, checkout);
        cmd.put(VcsUtils.LOG, log);
        cmd.put(VcsUtils.INIT, init);
        cmd.put(VcsUtils.MERGE, merge);

        jc.addCommand(commit);
        jc.addCommand(add);
        jc.addCommand(checkout);
        jc.addCommand(log);
        jc.addCommand(init);
        jc.addCommand(merge);
    }

    public static void main(String[] args) {

        /*
            for testing purposes only
         */
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String line;
            while ((line = br.readLine()) != null) {
                run(line.split(" "));
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void run(String[] args) {
        jc.parse(args);

        Command c = cmd.getOrDefault(jc.getParsedCommand(), null);
        if (c != null) {
            c.exec();
        }
        else {
            jc.usage();
        }
    }

}
