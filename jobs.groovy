job('EPBYMINW3088/MNTLAB-aaksionkin-DSL-build-job') {
    description 'Create child jobs.'
    parameters {
        //choiceParam(String parameterName, List<String> options, String description)
        choiceParam('BRANCH_NAME', ['aaksionkin', 'master'])
        activeChoiceParam('BUILDS_TRIGGER') {
            description('Available options')
            filterable()
            choiceType('CHECKBOX')
            groovyScript {
                script('["MNTLAB-aksionkin-child1-build-job", "MNTLAB-aksionkin-child2-build-job", "MNTLAB-aksionkin-child3-build-job", "MNTLAB-aksionkin-child4-build-job"]')
            }
        }
        triggers {
            scm('H/1 * * * *')
        }
    }
    steps {
        downstreamParameterized {
            trigger('$BUILDS_TRIGGER') {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                    git {
                        remote {
                            name('origin')
                            url('https://github.com/MNT-Lab/mntlab-dsl.git')
                        }
                    }
                    gitParam('$SelectTheBranch') {
                        description('branch selection')
                        type('BRANCH')
                        branch('~ /*')
                        defaultValue('/aaksionkin') // empty by default
                    }
                    currentBuild()
                }
            }
        }
        shell('chmod +x script.sh && ./script.sh > output.txt && cat output.txt && tar -czf ${BRANCH_NAME}_dsl_script.tar.gz output.txt')
    }
    publishers {
        archiveArtifacts('output.txt')
    }
        //creating child jobs
        ['EPBYMINW3088/MNTLAB-aksionkin-child1-build-job',
         'EPBYMINW3088/MNTLAB-aksionkin-child2-build-job',
         'EPBYMINW3088/MNTLAB-aksionkin-child3-build-job',
         'EPBYMINW3088/MNTLAB-aksionkin-child4-build-job'
        ].each {
            freeStyleJob(it) {
                description 'Echo the shell.sh.'
                scm {
                    git {
                        remote {
                            name('origin')
                            url('https://github.com/MNT-Lab/mntlab-dsl.git')
                        }
                        branch('$SelectTheBranch')
                        triggers {
                            scm 'H/5 * * * *'
                        }
                        steps {
                            shell(readFileFromWorkspace('script.sh'))
                        }
                    }
                }
            }
        }

    }



