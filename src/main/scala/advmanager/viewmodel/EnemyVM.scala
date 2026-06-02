package advmanager.viewmodel

import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.collections.ObservableBuffer
import advmanager.model.{Enemy, Attribute}

/** Two-way-bindable wrapper around Enemy for use in edit forms. */
class EnemyVM(e: Enemy) {
  val id         = ObjectProperty[Option[Int]](e.id)
  val name       = StringProperty(e.name)
  val notes      = StringProperty(e.notes)
  val attributes: ObservableBuffer[Attribute] = ObservableBuffer.from(e.attributes)

  def toModel: Enemy = Enemy(
    id = id.value,
    name = name.value,
    notes = notes.value,
    attributes = attributes.toSeq
  )
}

object EnemyVM {
  def blank: EnemyVM = new EnemyVM(Enemy(None, "", "", Nil))
}
