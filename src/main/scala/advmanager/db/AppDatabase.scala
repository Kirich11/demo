package advmanager.db

import slick.jdbc.SQLiteProfile.api._
import java.nio.file.{Files, Paths}
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object AppDatabase {
  private val appDataDir = Paths.get(System.getenv("APPDATA"), "AdventureManager")
  private val dbFile     = appDataDir.resolve("adventure.db")

  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val db: Database = {
    Files.createDirectories(appDataDir)
    Database.forURL(
      // foreign_keys=on is required per-connection; SQLite has it off by default.
      url = s"jdbc:sqlite:${dbFile.toAbsolutePath}?foreign_keys=on",
      driver = "org.sqlite.JDBC",
      executor = AsyncExecutor("sqlite", numThreads = 1, queueSize = 1000)
      // SQLite only supports one writer at a time; numThreads = 1 avoids
      // "database is locked" errors under concurrent access.
    )
  }

  /** Run once at startup. Blocks briefly — acceptable since it's a local file DB. */
  def initSchema(): Unit = {
    val action = Tables.schema.createIfNotExists
    Await.result(db.run(action), 10.seconds)
  }
}
