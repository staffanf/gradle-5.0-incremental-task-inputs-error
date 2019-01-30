package gradle.filetree.error

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Unroll
class FileTreeTaskSpec extends Specification {
  File buildFile

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder()

  def setup() {
    File inDir = tempFolder.newFolder("dir1")
    def inputFile = new File(inDir, 'stuff.txt')
    inputFile.text = "touch"
    buildFile = tempFolder.newFile('build.gradle')
    buildFile << """
plugins {
  id 'test.plugin' apply false
}

import gradle.filetree.error.FileTreeTask

task fileTask(type: FileTreeTask) {
  inputs.files fileTree(dir: "$inDir.absolutePath", include: '$inputFile.name')  
}
"""
  }

  def "can get single file from FileTree with filter. GradleVersion: #gradleVersion"() {
    when:
    def result = runBuild(gradleVersion, tempFolder.root, 'fileTask')

    then:
    result.task(":fileTask").outcome == SUCCESS

    where:
    gradleVersion << TEST_GRADLEVERSIONS
  }

  /** The versions to runs tests against */
  static TEST_GRADLEVERSIONS = ['4.10.3', '5.0', '5.1.1']

  static BuildResult runBuild(String gradleVersion, File projectDir, String... extraArgs = []) {
    def args = ['-s']
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