//TODO:some stuff
def giturl = 'https://github.com/MNT-Lab/mntlab-dsl.git'
def branchname = 'vtarasiuk'
def testJob = freeStyleJob('DSL-Task-1-Child')

job(testJob) {
    scm {
        git (giturl, branchname)
    }
    triggers {
        scm('H/5 * * * *')
    }
    steps {
        shell(executeFileFromWorkspace('script.sh'))
    }
}



