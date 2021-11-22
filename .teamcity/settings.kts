import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.ideaRunner
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {

    vcsRoot(HttpsGithubComManturovDanTestsConfigurationCIRefsHeadsMain)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    params {
        param("system.path.macro.MAVEN.REPOSITORY", "%env.HOME%/.m2/repository")
        param("env.JDK_1_8", "/opt/java/openjdk")
    }

    vcs {
        root(HttpsGithubComManturovDanTestsConfigurationCIRefsHeadsMain)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "status"
            scriptContent = """
                git status
                echo %teamcity.pullRequest.source.branch%
            """.trimIndent()
        }
        step {
            name = "premerge"
            type = "premergeRunner"

            conditions {
                exists("teamcity.pullRequest.target.branch")
            }
            param("tar.br", "%teamcity.pullRequest.target.branch%")
        }
        ideaRunner {
            pathToProject = ""
            jdk {
                name = "1.8"
                path = "%env.JDK_1_8%"
                patterns("jre/lib/*.jar", "jre/lib/ext/jfxrt.jar")
                extAnnotationPatterns("%teamcity.tool.idea%/lib/jdkAnnotations.jar")
            }
            pathvars {
                variable("MAVEN_REPOSITORY", "%system.path.macro.MAVEN.REPOSITORY%")
            }
            jvmArgs = "-Xmx256m"
            runConfigurations = "All in tests"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        pullRequests {
            vcsRootExtId = "${HttpsGithubComManturovDanTestsConfigurationCIRefsHeadsMain.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:f693d045-555b-4dbf-9341-1cfa8678a798"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})

object HttpsGithubComManturovDanTestsConfigurationCIRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/manturovDan/TestsConfigurationCI#refs/heads/main"
    url = "https://github.com/manturovDan/TestsConfigurationCI"
    branch = "refs/heads/main"
    authMethod = password {
        userName = "manturovDan"
        password = "credentialsJSON:f693d045-555b-4dbf-9341-1cfa8678a798"
    }
})

object HttpsGithubComManturovDanTestsConfigurationCIRefsHeadsMain2 : GitVcsRoot({
    name = "https://github.com/manturovDan/TestsConfigurationCI#refs/heads/main2"
    url = "https://github.com/manturovDan/TestsConfigurationCI"
    branch = "refs/heads/main"
    branchSpec = "+:refs/heads/*"
    authMethod = password {
        userName = "manturovDan"
        password = "credentialsJSON:f693d045-555b-4dbf-9341-1cfa8678a798"
    }
})
