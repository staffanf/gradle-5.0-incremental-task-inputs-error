package inctaskinputs.error


import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Unroll
class FileTreeSpec extends Specification {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder()

  private File buildFile
  private File root
  private File subDirInRoot

  def setup() {
//Create:
//  root
//  --dir
    root = tempFolder.newFolder("root")
    subDirInRoot = new File(this.root, 'dir')
    subDirInRoot.mkdir()
    buildFile = tempFolder.newFile('build.gradle')
    buildFile << """
"""
  }

  /**
   * Test table from https://github.com/gradle/gradle/issues/8374#issuecomment-459687644
   */
  def "fileTree.files should contain dirs? Gradle: #gradleVersion Pattern: #pattern Size: #size"() {
    when:
    buildFile << """
def ft = project.fileTree(dir: '$root.absolutePath', include: '$pattern')
logger.quiet ft.files.toString()
ft.visit {
  logger.quiet "Visited \$it"
}
ft.each {
  logger.quiet "Each \$it"
}
assert ft.files.size() == $size 
"""
    def result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'help')

    then:
    result.task(":help").outcome == SUCCESS

    where:
    gradleVersion | pattern       | size
    '4.10.3'      | '**'          | 1
    '5.0'         | '**'          | 1
    '4.10.3'      | 'root.txt'    | 0
    '5.0'         | 'root.txt'    | 0
    '4.10.3'      | 'dir/sub.txt' | 1
    '5.0'         | 'dir/sub.txt' | 1

  }

  def "fileTree.files does contain files. Gradle: #gradleVersion Pattern: #pattern Size: #size"() {
    setup:
    def subTxt = new File(subDirInRoot, "sub.txt")
    subTxt.text = "touch"

    when:
    buildFile << """
def ft = project.fileTree(dir: '$root.absolutePath', include: '$pattern')
logger.quiet ft.files.toString()
ft.visit {
  logger.quiet "Visited \$it"
}
ft.each {
  logger.quiet "Each \$it"
}
assert ft.files.size() == $size 
"""
    def result = TestSupport.runBuild(gradleVersion, tempFolder.root, 'help')

    then:
    result.task(":help").outcome == SUCCESS

    where:
    gradleVersion | pattern       | size
    '4.10.3'      | '**'          | 1
    '5.0'         | '**'          | 1
    '4.10.3'      | 'root.txt'    | 0
    '5.0'         | 'root.txt'    | 0
    '4.10.3'      | 'dir/sub.txt' | 1
    '5.0'         | 'dir/sub.txt' | 1
  }

}