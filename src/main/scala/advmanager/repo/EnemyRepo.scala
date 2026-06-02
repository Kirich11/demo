package advmanager.repo

import advmanager.db._
import advmanager.model._
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class EnemyRepo(db: Database)(implicit ec: ExecutionContext) {
  import Tables._

  def findAll(): Future[Seq[Enemy]] =
    db.run(enemies.sortBy(_.name).result).flatMap(rows => Future.traverse(rows)(hydrate))

  def findById(id: Int): Future[Option[Enemy]] =
    db.run(enemies.filter(_.id === id).result.headOption).flatMap {
      case Some(r) => hydrate(r).map(Some(_))
      case None    => Future.successful(None)
    }

  private def hydrate(row: EnemyRow): Future[Enemy] = {
    val eid = row.id.get
    db.run(enemyAttributes.filter(_.enemyId === eid).result).map { attrRows =>
      Enemy(row.id, row.name, row.notes, attrRows.map(a => Attribute(a.id, a.enemyId, a.name, a.value)))
    }
  }

  def setAttributes(enemyId: Int, attrs: Seq[(String, String)]): Future[Unit] = {
    val action = DBIO.seq(
      enemyAttributes.filter(_.enemyId === enemyId).delete,
      enemyAttributes ++= attrs.map { case (n, v) => EnemyAttributeRow(None, enemyId, n, v) }
    ).transactionally
    db.run(action).map(_ => ())
  }

  def save(e: Enemy): Future[Int] = {
    val attrPairs = e.attributes.map(a => (a.name, a.value))
    e.id match {
      case Some(id) =>
        for {
          _ <- db.run(enemies.filter(_.id === id).map(r => (r.name, r.notes)).update((e.name, e.notes)))
          _ <- setAttributes(id, attrPairs)
        } yield id
      case None =>
        for {
          newId <- db.run((enemies returning enemies.map(_.id)) += EnemyRow(None, e.name, e.notes))
          _ <- setAttributes(newId, attrPairs)
        } yield newId
    }
  }

  def delete(id: Int): Future[Unit] =
    db.run(enemies.filter(_.id === id).delete).map(_ => ())
}
