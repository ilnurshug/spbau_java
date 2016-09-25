package vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Parameters(commandNames = VcsUtils.MERGE, commandDescription = "Merge current branch with selected one")
public class MergeCommand extends Command {
    @Parameter(required = true, description = "Select branch")
    private List<String> branches;

    public MergeCommand() {
        branches = new LinkedList<>();
    }

    public MergeCommand(String branch) {
        branches.add(branch);
    }

    /**
     * merge selected branch with current branch
     * ASSUMPTION: head is pointing to the last commit on current branch
     */
    @Override
    protected void execImpl() {
        if (needToCommitBeforeMerge()) {
            System.err.println("commit changes before merge");
            return;
        }

        String firstBranch = GlobalConfig.getCurrentBranch();
        String secondBranch = branches.get(0);

        if (!canMerge(firstBranch, secondBranch)) {
            return;
        }

        try {
            List<String> unionFiles = getUnionFiles(firstBranch, secondBranch);

            String firstDir = GlobalConfig.getLastCommitDir(firstBranch);
            String secondDir = GlobalConfig.getLastCommitDir(secondBranch);

            GlobalConfig.instance.graph.merge(secondBranch);
            String mergeDir = GlobalConfig.getHeadCommitDir();

            VcsUtils.copyFiles(unionFiles, firstDir, mergeDir, true);
            VcsUtils.copyFiles(unionFiles, secondDir, mergeDir, false);

            unionFiles.forEach(CommitConfig.instance::addSupervisedFile);

            VcsUtils.copyFiles(
                    CommitConfig.instance.getSupervisedFiles(),
                    mergeDir,
                    GlobalConfig.getProjectDir(), true
            );

            serializeConfig();
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("merge failure");
            e.printStackTrace();
        }
    }

    private boolean canMerge(String firstBranch, String secondBranch) {
        if (GlobalConfig.instance.isDeletedBranch(firstBranch) || GlobalConfig.instance.isDeletedBranch(secondBranch)) {
            System.err.println("can not merge with deleted branch");
            return false;
        }

        try {
            List<String> commonFiles = getCommonFiles(firstBranch, secondBranch);

            List<String> differentFiles = VcsUtils.diffDirFilesList(
                    commonFiles,
                    GlobalConfig.getLastCommitDir(firstBranch),
                    GlobalConfig.getLastCommitDir(secondBranch));

            if (differentFiles.size() > 0)
            {
                System.out.println("merge conflict detected in following files:");
                differentFiles.forEach(System.out::println);
                return false;
            }

            return true;

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean needToCommitBeforeMerge() {
        String dir = GlobalConfig.getProjectDir();
        String commitDir = GlobalConfig.getHeadCommitDir();

        return VcsUtils.diffDirFiles(
                CommitConfig.instance.getSupervisedFiles(),
                dir,
                commitDir) > 0;
    }

    private List<String> getCommonFiles(String firstBranch, String secondBranch)
            throws ClassNotFoundException, IOException
    {
        HashSet<String> commonFiles = new HashSet<>(CommitConfig.instance.getSupervisedFiles());

        CheckoutCommand.smallCheckout(secondBranch);

        commonFiles.retainAll(CommitConfig.instance.getSupervisedFiles());

        CheckoutCommand.smallCheckout(firstBranch);

        return commonFiles.stream().collect(Collectors.toList());
    }

    private List<String> getUnionFiles(String firstBranch, String secondBranch)
            throws ClassNotFoundException, IOException
    {
        HashSet<String> unionFiles = new HashSet<>(CommitConfig.instance.getSupervisedFiles());

        CheckoutCommand.smallCheckout(secondBranch);

        unionFiles.addAll(CommitConfig.instance.getSupervisedFiles());

        CheckoutCommand.smallCheckout(firstBranch);

        return unionFiles.stream().collect(Collectors.toList());
    }
}
