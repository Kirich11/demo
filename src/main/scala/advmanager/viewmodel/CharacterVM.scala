package advmanager.viewmodel

import scalafx.beans.property.{StringProperty, DoubleProperty, ObjectProperty}
import advmanager.model.Character
import scalafx.collections.ObservableBuffer

class CharacterVM(c: Character) {
  val id          = ObjectProperty[Option[Int]](c.id)
  val name        = StringProperty(c.name)
  val weightLimit = DoubleProperty(c.weightLimit)
  val notes       = StringProperty(c.notes)
  val classes     = ObservableBuffer.from(c.classes)
  val attributes  = ObservableBuffer.from(c.attributes)
  val bag         = ObservableBuffer.from(c.bag)

  def bagWeight: Double = bag.map(e => e.item.weight * e.quantity).sum
  def isOverweight: Boolean = bagWeight > weightLimit.value

  def toModel: Character = Character(
    id.value, name.value, weightLimit.value, notes.value,
    classes.toSeq, attributes.toSeq, bag.toSeq
  )
}
