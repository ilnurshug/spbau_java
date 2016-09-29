package vcs.commands.impl;

import com.beust.jcommander.Parameter;
import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MergeCommand extends Command {
    @Parameter(names = "-b", required = true, description = "Select branch")
    private String branch;

    public MergeCommand() {
        branch = null;
    }

    public MergeCommand(String branch) {
        this.branch = branch;
    }

    @Override
    public String name() {
        return "merge";
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
        String secondBranch = branch;

        if (!canMerge(firstBranch, secondBranch)) {
            return;
        }

        try {
            HashMap<String, String> unionFiles = getUnionFiles(firstBranch, secondBranch);
            HashMap<String, String> onlyInSecond = getFilesThatPresentOnlyInSecondBranch(firstBranch, secondBranch);

            GlobalConfig.instance.graph.merge(secondBranch);
            String mergeDir = GlobalConfig.getHeadCommitDir();
            Files.createDirectories(Paths.get(mergeDir));

            unionFiles.forEach((f, h) -> CommitConfig.instance.addSupervisedFile(f));

            for (Map.Entry<String, String> entry : onlyInSecond.entrySet()) {
                String filename = entry.getKey();
                String fileCopyAddress = entry.getValue();

                VcsUtils.copyFiles(
                        Collections.singletonList(filename),
                        fileCopyAddress,
                        GlobalConfig.projectDir(), true
                );

                CommitConfig.instance.setSupervisedFileCopyAddr(filename, fileCopyAddress);

                String hash = unionFiles.get(filename);
                CommitConfig.instance.addSupervisedFileHash(filename, hash);
            }

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
            HashMap<String, String> commonFiles = getCommonFiles(firstBranch, secondBranch);

            List<String> differentFiles = new LinkedList<>();
            for (Map.Entry<String, String> entry : commonFiles.entrySet()) {
                String fileHash = VcsUtils.getFileHash(GlobalConfig.projectDir() + entry.getKey());

                if (!entry.getValue().equals(fileHash)) {
                    differentFiles.add(entry.getKey());
                }
            }

            if (!differentFiles.isEmpty())
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
        return !CommitConfig.instance.differentFiles().isEmpty();
    }

    private HashMap<String, String> getCommonFiles(String firstBranch, String secondBranch)
            throws ClassNotFoundException, IOException
    {
        HashMap<String, String> fh = new HashMap<>();

        HashSet<String> commonFiles = new HashSet<>(CommitConfig.instance.getSupervisedFiles());

        CheckoutCommand.smallCheckout(secondBranch);

        commonFiles.retainAll(CommitConfig.instance.getSupervisedFiles());

        commonFiles.forEach(f -> fh.put(f, CommitConfig.instance.getSupervisedFileHash(f)));

        CheckoutCommand.smallCheckout(firstBranch);

        return fh;
    }

    private HashMap<String, String> getFilesThatPresentOnlyInSecondBranch(String firstBranch, String secondBranch)
            throws ClassNotFoundException, IOException
    {
        HashSet<String> files = new HashSet<>(CommitConfig.instance.getSupervisedFiles());

        CheckoutCommand.smallCheckout(secondBranch);

        HashSet<String> tmp = new HashSet<>();
        tmp.addAll(CommitConfig.instance.getSupervisedFiles());
        tmp.removeAll(files);

        HashMap<String, String> res = new HashMap<>();
        tmp.forEach(f -> res.put(f, CommitConfig.instance.getSupervisedFileCopyAddr(f)));

        CheckoutCommand.smallCheckout(firstBranch);

        return res;
    }

    private HashMap<String, String> getUnionFiles(String firstBranch, String secondBranch)
            throws ClassNotFoundException, IOException
    {
        HashMap<String, String> res = new HashMap<>();

        HashSet<String> unionFiles = new HashSet<>(CommitConfig.instance.getSupervisedFiles());
        unionFiles.forEach(f -> res.put(f, CommitConfig.instance.getSupervisedFileHash(f)));

        CheckoutCommand.smallCheckout(secondBranch);

        unionFiles.addAll(CommitConfig.instance.getSupervisedFiles());
        unionFiles.forEach(f -> {
            if (!res.containsKey(f)) {
                res.put(f, CommitConfig.instance.getSupervisedFileHash(f));
            }
        });

        CheckoutCommand.smallCheckout(firstBranch);

        return res;
    }
}
