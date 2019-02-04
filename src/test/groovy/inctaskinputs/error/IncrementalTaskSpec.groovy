package inctaskinputs.error


import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

@Unroll
class IncrementalTaskSpec extends Specification {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder()

  private File buildFile
  private File inDir
  private inputFile

  def setup() {
    inDir = tempFolder.newFolder("root")
    inputFile = new File(this.inDir, 'stuff.txt')
    inputFile.text = "touch"
    buildFile = tempFolder.newFile('build.gradle')
    buildFile << """
plugins {
  id 'test.plugin' apply false
}
"""
  }

  def "incremental task can get single file from FileTree with filter. GradleVersion: #gradleVersion"() {
    when:
    buildFile << """
task fileTask(type: inctaskinputs.error.IncrementalTask) {
  inputs.files fileTree(dir: "$inDir.absolutePath", include: '$inputFile.name')  
}
"""
    def result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'fileTask')

    then:
    result.task(":fileTask").outcome == SUCCESS

    when: "Adding dir that does not match fileTree include filter"
    new File(inDir, "non.matching.dir.name").mkdir()
    result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'fileTask')

    then: "incremental task should not get the dir reported as input"
    result.task(":fileTask").outcome == UP_TO_DATE

    where:
    gradleVersion << TEST_GRADLEVERSIONS
  }

  def "normal (non-incremental) task can get single file from FileTree with filter. GradleVersion: #gradleVersion"() {
    when:
    buildFile << """
 task fileTask(type: inctaskinputs.error.NormalTask) {
  inputs.files fileTree(dir: "$inDir.absolutePath", include: '$inputFile.name')  
}
"""
    def result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'fileTask')

    then:
    result.task(":fileTask").outcome == SUCCESS

    when: "Adding dir that does not match fileTree include filter"
    new File(inDir, "non.matching.dir.name").mkdir()
    result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'fileTask')

    then: "normal task should not get the dir reported as input"
    result.task(":fileTask").outcome == SUCCESS

    where:
    gradleVersion << TEST_GRADLEVERSIONS
  }

  def "normal task would fail if dir was sent"() {
    when:
    buildFile << """
 task fileTask(type: inctaskinputs.error.NormalTask) {
  inputs.files fileTree(dir: "$inDir.absolutePath", include: '$inputFile.name')  
}
"""
    def result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'fileTask')

    then:
    result.task(":fileTask").outcome == SUCCESS

    when: "Adding dir that does not match fileTree include filter"
    new File(inDir, "non.matching.dir.name").mkdir()
    result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'fileTask')

    then: "normal task should not get the dir reported as input"
    result.task(":fileTask").outcome == SUCCESS

    where:
    gradleVersion << TEST_GRADLEVERSIONS
  }

  /** The gradle versions to run tests against */
  private static TEST_GRADLEVERSIONS = ['4.10.3', '5.0', '5.1.1']

}