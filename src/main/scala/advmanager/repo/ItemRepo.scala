package advmanager.repo

import advmanager.db._
import advmanager.model._
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class ItemRepo(db: Database)(implicit ec: ExecutionContext) {
  import Tables._

  def findAll(): Future[Seq[Item]] =
    db.run(items.sortBy(_.name).result).map(_.map(r => Item(r.id, r.name, r.weight, r.description)))

  def save(i: Item): Future[Int] = i.id match {
    case Some(id) =>
      db.run(items.filter(_.id === id)
        .map(r => (r.name, r.weight, r.description))
        .update((i.name, i.weight, i.description)))
        .map(_ => id)
    case None =>
      db.run((items returning items.map(_.id)) += ItemRow(None, i.name, i.weight, i.description))
  }

  def delete(id: Int): Future[Unit] =
    db.run(items.filter(_.id === id).delete).map(_ => ())
}
