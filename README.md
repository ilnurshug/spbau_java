# spbau_java

VCS
---
Supported commands
  
    commit      
      Usage: commit [options]
        Options:
        * -m
             Commit message

    add      
      Usage: add file {,file} Add file contents to the index

    checkout      Go to selected commit or branch
      Usage: checkout [options]
        Options:
        * -b
             choose branch to switch
        * -c
             commit id or
             -1 to go to the last commit on selected branch

    log      Show current branch's history
      Usage: log

    init      InitCommand repository in current folder
      Usage: init

    merge      Merge current branch with selected one
      Usage: merge [branchname] Select branch

    status     Show list of supervised files
      Usage: status 

    branch      Create or delete selected branch
      Usage: branch [options]
        Options:
        * -a
             Create(1) or delete(0) new branch
             Default: 1
        * -b
             choose branch to switch
 
