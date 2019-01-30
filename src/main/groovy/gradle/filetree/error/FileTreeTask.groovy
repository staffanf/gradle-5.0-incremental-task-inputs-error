package gradle.filetree.error

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class FileTreeTask extends DefaultTask {

  @TaskAction
  void expectAFile(IncrementalTaskInputs incrementalInputs) {
    assert inputs.files.size() == 1
    assert inputs.files.first().isFile()

    incrementalInputs.outOfDate { change ->
      logger.quiet "file: $change.file changed"
      assert change.file.isFile()
    }
  }
}
