package advmanager.repo

import advmanager.db._
import advmanager.model._
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class ClassRepo(db: Database)(implicit ec: ExecutionContext) {
  import Tables._

  def findAll(): Future[Seq[CharacterClass]] =
    db.run(classes.sortBy(_.name).result).map(_.map(r => CharacterClass(r.id, r.name, r.description)))

  def save(c: CharacterClass): Future[Int] = c.id match {
    case Some(id) =>
      db.run(classes.filter(_.id === id)
        .map(r => (r.name, r.description))
        .update((c.name, c.description)))
        .map(_ => id)
    case None =>
      db.run((classes returning classes.map(_.id)) += CharacterClassRow(None, c.name, c.description))
  }

  def delete(id: Int): Future[Unit] =
    db.run(classes.filter(_.id === id).delete).map(_ => ())
}
