package inctaskinputs.error

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NormalTask extends DefaultTask {

  @TaskAction
  void expectAFile() {
    inputs.each {
      logger.quiet "file: $it changed"
    }
    assert inputs.files.size() == 1
    assert inputs.files.first().isFile()

  }
}
