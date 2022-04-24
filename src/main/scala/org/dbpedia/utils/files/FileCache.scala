package org.dbpedia.utils.files

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.Date

class FileCache {

  def main(): Unit = {
    val file: File = Paths.get("./test.file").toFile

    while (true) {
      val timestamp = file.lastModified()
      val datetime = new Date(timestamp)
      println(datetime)
      Thread.sleep(1000)
    }
  }

  def checkFileIsOpen(file: File): Unit = {
    import scala.collection.JavaConverters._
    val plsof = new ProcessBuilder(List("lsof", "|", "grep", file.getAbsolutePath).asJava).start()
    plsof
  }
}
