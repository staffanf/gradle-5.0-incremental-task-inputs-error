package inctaskinputs.error

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

class TestSupport {
  static BuildResult runBuild(String gradleVersion, File projectDir, String... extraArgs = []) {
    def args = ['-s', '-i']
    args.addAll(extraArgs)
    return GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments(args)
        .withGradleVersion(gradleVersion)
        .withPluginClasspath()
        .forwardOutput()
        .build()
  }

}
