package advmanager.model

final case class CharacterClass(id: Option[Int], name: String, description: String)

final case class Attribute(id: Option[Int], ownerId: Int, name: String, value: String)

final case class Item(id: Option[Int], name: String, weight: Double, description: String)

final case class BagEntry(id: Option[Int], item: Item, quantity: Int) {
  def totalWeight: Double = item.weight * quantity
}

final case class Character(
  id: Option[Int],
  name: String,
  weightLimit: Double,
  notes: String,
  classes: Seq[CharacterClass] = Nil,
  attributes: Seq[Attribute] = Nil,
  bag: Seq[BagEntry] = Nil
) {
  def bagWeight: Double = bag.map(_.totalWeight).sum
  def isOverweight: Boolean = bagWeight > weightLimit
}

final case class Enemy(
  id: Option[Int],
  name: String,
  notes: String,
  attributes: Seq[Attribute] = Nil
)

final case class Scene(
  id: Option[Int],
  name: String,
  description: String,
  characterIds: Seq[Int] = Nil,
  enemyIds: Seq[Int] = Nil
)
