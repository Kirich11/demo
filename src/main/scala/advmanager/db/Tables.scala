package advmanager.db

import slick.jdbc.SQLiteProfile.api._

// ---- Row types (persistence-shaped, distinct from advmanager.model classes) ----
case class CharacterClassRow(id: Option[Int], name: String, description: String)
case class CharacterRow(id: Option[Int], name: String, weightLimit: Double, notes: String)
case class CharacterClassLinkRow(characterId: Int, classId: Int)
case class AttributeRow(id: Option[Int], characterId: Int, name: String, value: String)
case class ItemRow(id: Option[Int], name: String, weight: Double, description: String)
case class BagEntryRow(id: Option[Int], characterId: Int, itemId: Int, quantity: Int)
case class EnemyRow(id: Option[Int], name: String, notes: String)
case class EnemyAttributeRow(id: Option[Int], enemyId: Int, name: String, value: String)
case class SceneRow(id: Option[Int], name: String, description: String)
case class SceneCharacterRow(sceneId: Int, characterId: Int)
case class SceneEnemyRow(sceneId: Int, enemyId: Int)

// ---- Table definitions ----
class CharacterClassTable(tag: Tag) extends Table[CharacterClassRow](tag, "character_class") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name        = column[String]("name")
  def description = column[String]("description", O.Default(""))
  def * = (id.?, name, description).mapTo[CharacterClassRow]
}

class CharacterTable(tag: Tag) extends Table[CharacterRow](tag, "character") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name        = column[String]("name")
  def weightLimit = column[Double]("weight_limit", O.Default(0))
  def notes       = column[String]("notes", O.Default(""))
  def * = (id.?, name, weightLimit, notes).mapTo[CharacterRow]
}

class CharacterClassLinkTable(tag: Tag) extends Table[CharacterClassLinkRow](tag, "character_class_link") {
  def characterId = column[Int]("character_id")
  def classId     = column[Int]("class_id")
  def pk = primaryKey("pk_char_class", (characterId, classId))
  def character = foreignKey("fk_link_character", characterId, Tables.characters)(_.id, onDelete = ForeignKeyAction.Cascade)
  def clazz     = foreignKey("fk_link_class", classId, Tables.classes)(_.id, onDelete = ForeignKeyAction.Cascade)
  def * = (characterId, classId).mapTo[CharacterClassLinkRow]
}

class AttributeTable(tag: Tag) extends Table[AttributeRow](tag, "attribute") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def characterId = column[Int]("character_id")
  def name        = column[String]("name")
  def value       = column[String]("value")
  def character = foreignKey("fk_attr_character", characterId, Tables.characters)(_.id, onDelete = ForeignKeyAction.Cascade)
  def * = (id.?, characterId, name, value).mapTo[AttributeRow]
}

class ItemTable(tag: Tag) extends Table[ItemRow](tag, "item") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name        = column[String]("name")
  def weight      = column[Double]("weight", O.Default(0))
  def description = column[String]("description", O.Default(""))
  def * = (id.?, name, weight, description).mapTo[ItemRow]
}

class BagEntryTable(tag: Tag) extends Table[BagEntryRow](tag, "bag_entry") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def characterId = column[Int]("character_id")
  def itemId      = column[Int]("item_id")
  def quantity    = column[Int]("quantity", O.Default(1))
  def character = foreignKey("fk_bag_character", characterId, Tables.characters)(_.id, onDelete = ForeignKeyAction.Cascade)
  def item      = foreignKey("fk_bag_item", itemId, Tables.items)(_.id, onDelete = ForeignKeyAction.Cascade)
  def * = (id.?, characterId, itemId, quantity).mapTo[BagEntryRow]
}

class EnemyTable(tag: Tag) extends Table[EnemyRow](tag, "enemy") {
  def id    = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name  = column[String]("name")
  def notes = column[String]("notes", O.Default(""))
  def * = (id.?, name, notes).mapTo[EnemyRow]
}

class EnemyAttributeTable(tag: Tag) extends Table[EnemyAttributeRow](tag, "enemy_attribute") {
  def id      = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def enemyId = column[Int]("enemy_id")
  def name    = column[String]("name")
  def value   = column[String]("value")
  def enemy = foreignKey("fk_enemyattr_enemy", enemyId, Tables.enemies)(_.id, onDelete = ForeignKeyAction.Cascade)
  def * = (id.?, enemyId, name, value).mapTo[EnemyAttributeRow]
}

class SceneTable(tag: Tag) extends Table[SceneRow](tag, "scene") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name        = column[String]("name")
  def description = column[String]("description", O.Default(""))
  def * = (id.?, name, description).mapTo[SceneRow]
}

class SceneCharacterTable(tag: Tag) extends Table[SceneCharacterRow](tag, "scene_character") {
  def sceneId     = column[Int]("scene_id")
  def characterId = column[Int]("character_id")
  def pk = primaryKey("pk_scene_char", (sceneId, characterId))
  def scene     = foreignKey("fk_sc_scene", sceneId, Tables.scenes)(_.id, onDelete = ForeignKeyAction.Cascade)
  def character = foreignKey("fk_sc_character", characterId, Tables.characters)(_.id, onDelete = ForeignKeyAction.Cascade)
  def * = (sceneId, characterId).mapTo[SceneCharacterRow]
}

class SceneEnemyTable(tag: Tag) extends Table[SceneEnemyRow](tag, "scene_enemy") {
  def sceneId = column[Int]("scene_id")
  def enemyId = column[Int]("enemy_id")
  def pk = primaryKey("pk_scene_enemy", (sceneId, enemyId))
  def scene = foreignKey("fk_se_scene", sceneId, Tables.scenes)(_.id, onDelete = ForeignKeyAction.Cascade)
  def enemy = foreignKey("fk_se_enemy", enemyId, Tables.enemies)(_.id, onDelete = ForeignKeyAction.Cascade)
  def * = (sceneId, enemyId).mapTo[SceneEnemyRow]
}

// ---- TableQuery handles + combined schema ----
object Tables {
  val classes         = TableQuery[CharacterClassTable]
  val characters       = TableQuery[CharacterTable]
  val classLinks       = TableQuery[CharacterClassLinkTable]
  val attributes       = TableQuery[AttributeTable]
  val items            = TableQuery[ItemTable]
  val bagEntries       = TableQuery[BagEntryTable]
  val enemies          = TableQuery[EnemyTable]
  val enemyAttributes  = TableQuery[EnemyAttributeTable]
  val scenes           = TableQuery[SceneTable]
  val sceneCharacters  = TableQuery[SceneCharacterTable]
  val sceneEnemies     = TableQuery[SceneEnemyTable]

  val schema =
    classes.schema ++ characters.schema ++ classLinks.schema ++ attributes.schema ++
    items.schema ++ bagEntries.schema ++ enemies.schema ++ enemyAttributes.schema ++
    scenes.schema ++ sceneCharacters.schema ++ sceneEnemies.schema
}
