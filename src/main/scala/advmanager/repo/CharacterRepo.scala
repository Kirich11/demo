package advmanager.repo

import advmanager.db._
import advmanager.model._
import slick.jdbc.SQLiteProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class CharacterRepo(db: Database)(implicit ec: ExecutionContext) {
  import Tables._

  def findAll(): Future[Seq[Character]] =
    db.run(characters.sortBy(_.name).result).flatMap(rows => Future.traverse(rows)(hydrate))

  def findById(id: Int): Future[Option[Character]] =
    db.run(characters.filter(_.id === id).result.headOption).flatMap {
      case Some(row) => hydrate(row).map(Some(_))
      case None      => Future.successful(None)
    }

  /** Load a bare row's classes/attributes/bag and assemble the full domain model. */
  private def hydrate(row: CharacterRow): Future[Character] = {
    val cid = row.id.get
    val classesQ =
      (classLinks.filter(_.characterId === cid) join classes on (_.classId === _.id))
        .map(_._2).result
    val attrsQ = attributes.filter(_.characterId === cid).result
    val bagQ =
      (bagEntries.filter(_.characterId === cid) join items on (_.itemId === _.id)).result

    db.run(classesQ zip attrsQ zip bagQ).map { case ((classRows, attrRows), bagRows) =>
      Character(
        id = row.id,
        name = row.name,
        weightLimit = row.weightLimit,
        notes = row.notes,
        classes = classRows.map(c => CharacterClass(c.id, c.name, c.description)),
        attributes = attrRows.map(a => Attribute(a.id, a.characterId, a.name, a.value)),
        bag = bagRows.map { case (entry, item) =>
          BagEntry(entry.id, Item(item.id, item.name, item.weight, item.description), entry.quantity)
        }
      )
    }
  }

  private def insertBare(c: Character): Future[Int] =
    db.run((characters returning characters.map(_.id)) +=
      CharacterRow(None, c.name, c.weightLimit, c.notes))

  private def updateBare(c: Character): Future[Unit] = {
    val id = c.id.getOrElse(throw new IllegalArgumentException("missing id"))
    db.run(characters.filter(_.id === id)
      .map(r => (r.name, r.weightLimit, r.notes))
      .update((c.name, c.weightLimit, c.notes)))
      .map(_ => ())
  }

  def setClasses(characterId: Int, classIds: Seq[Int]): Future[Unit] = {
    val action = DBIO.seq(
      classLinks.filter(_.characterId === characterId).delete,
      classLinks ++= classIds.map(cid => CharacterClassLinkRow(characterId, cid))
    ).transactionally
    db.run(action).map(_ => ())
  }

  def setAttributes(characterId: Int, attrs: Seq[(String, String)]): Future[Unit] = {
    val action = DBIO.seq(
      attributes.filter(_.characterId === characterId).delete,
      attributes ++= attrs.map { case (n, v) => AttributeRow(None, characterId, n, v) }
    ).transactionally
    db.run(action).map(_ => ())
  }

  /** entries as (itemId, quantity) pairs. */
  def setBag(characterId: Int, entries: Seq[(Int, Int)]): Future[Unit] = {
    val action = DBIO.seq(
      bagEntries.filter(_.characterId === characterId).delete,
      bagEntries ++= entries.map { case (itemId, qty) => BagEntryRow(None, characterId, itemId, qty) }
    ).transactionally
    db.run(action).map(_ => ())
  }

  /** Insert or update a character plus its classes/attributes/bag in one call. */
  def save(c: Character): Future[Int] = {
    val bagPairs = c.bag.map(b =>
      (b.item.id.getOrElse(throw new IllegalArgumentException("bag item must already exist")), b.quantity))
    val attrPairs = c.attributes.map(a => (a.name, a.value))
    val classIds  = c.classes.flatMap(_.id)

    c.id match {
      case Some(id) =>
        for {
          _ <- updateBare(c)
          _ <- setClasses(id, classIds)
          _ <- setAttributes(id, attrPairs)
          _ <- setBag(id, bagPairs)
        } yield id
      case None =>
        for {
          newId <- insertBare(c)
          _ <- setClasses(newId, classIds)
          _ <- setAttributes(newId, attrPairs)
          _ <- setBag(newId, bagPairs)
        } yield newId
    }
  }

  def delete(id: Int): Future[Unit] =
    db.run(characters.filter(_.id === id).delete).map(_ => ())
    // ON DELETE CASCADE (declared in Tables.scala foreign keys) cleans up
    // character_class_link / attribute / bag_entry rows automatically.
}
