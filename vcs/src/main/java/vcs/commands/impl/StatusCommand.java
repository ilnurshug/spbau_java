package vcs.commands.impl;

import com.beust.jcommander.Parameters;
import vcs.commands.Command;
import vcs.config.CommitConfig;
import vcs.config.GlobalConfig;
import vcs.util.VcsUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class StatusCommand extends Command {

    @Override
    public String name() {
        return "status";
    }

    /**
     * list of all supervised files, deleted files and unsupervised files
     */
    @Override
    public void exec() {
        System.out.println("Supervised files:");
        CommitConfig.instance.getSupervisedFiles().forEach(System.out::println);
        System.out.println("---");

        System.out.println("Deleted files:");
        CommitConfig.instance.getDeletedFiles().forEach(System.out::println);
        System.out.println("---");

        System.out.println("Unsupervised files:");
        CommitConfig.instance.getUnsupervisedFiles().forEach(System.out::println);
        System.out.println("---");
    }

    @Override
    protected void execImpl() {}
}
