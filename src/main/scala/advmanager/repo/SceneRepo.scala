package advmanager.repo

import advmanager.db._
import advmanager.model._
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class SceneRepo(db: Database)(implicit ec: ExecutionContext) {
  import Tables._

  def findAll(): Future[Seq[Scene]] =
    db.run(scenes.sortBy(_.name).result).flatMap(rows => Future.traverse(rows)(hydrate))

  def findById(id: Int): Future[Option[Scene]] =
    db.run(scenes.filter(_.id === id).result.headOption).flatMap {
      case Some(r) => hydrate(r).map(Some(_))
      case None    => Future.successful(None)
    }

  private def hydrate(row: SceneRow): Future[Scene] = {
    val sid = row.id.get
    val charsQ   = sceneCharacters.filter(_.sceneId === sid).map(_.characterId).result
    val enemiesQ = sceneEnemies.filter(_.sceneId === sid).map(_.enemyId).result
    db.run(charsQ zip enemiesQ).map { case (cids, eids) =>
      Scene(row.id, row.name, row.description, cids, eids)
    }
  }

  def save(s: Scene): Future[Int] = s.id match {
    case Some(id) =>
      db.run(scenes.filter(_.id === id)
        .map(r => (r.name, r.description))
        .update((s.name, s.description)))
        .map(_ => id)
    case None =>
      db.run((scenes returning scenes.map(_.id)) += SceneRow(None, s.name, s.description))
  }

  def delete(id: Int): Future[Unit] =
    db.run(scenes.filter(_.id === id).delete).map(_ => ())

  // ---- roster queries used by the scene runtime view ----

  def getCharactersInScene(sceneId: Int): Future[Seq[Character]] = {
    val q = (sceneCharacters.filter(_.sceneId === sceneId) join characters on (_.characterId === _.id)).map(_._2)
    db.run(q.result).map(_.map(r => Character(r.id, r.name, r.weightLimit, r.notes)))
    // Lightweight (no classes/attributes/bag) — enough for the roster list; open the
    // full CharacterEditView from the character catalog if deeper detail is needed.
  }

  def getEnemiesInScene(sceneId: Int): Future[Seq[Enemy]] = {
    val q = (sceneEnemies.filter(_.sceneId === sceneId) join enemies on (_.enemyId === _.id)).map(_._2)
    db.run(q.result).map(_.map(r => Enemy(r.id, r.name, r.notes)))
  }

  def addCharacter(sceneId: Int, characterId: Int): Future[Unit] =
    db.run(sceneCharacters += SceneCharacterRow(sceneId, characterId)).map(_ => ())

  def removeCharacter(sceneId: Int, characterId: Int): Future[Unit] =
    db.run(sceneCharacters.filter(r => r.sceneId === sceneId && r.characterId === characterId).delete).map(_ => ())

  def addEnemy(sceneId: Int, enemyId: Int): Future[Unit] =
    db.run(sceneEnemies += SceneEnemyRow(sceneId, enemyId)).map(_ => ())

  def removeEnemy(sceneId: Int, enemyId: Int): Future[Unit] =
    db.run(sceneEnemies.filter(r => r.sceneId === sceneId && r.enemyId === enemyId).delete).map(_ => ())
}
