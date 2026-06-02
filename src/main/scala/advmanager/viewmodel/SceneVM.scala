package advmanager.viewmodel

import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.collections.ObservableBuffer
import advmanager.model.Scene

/** Two-way-bindable wrapper around Scene for use in edit forms and the scene runtime view. */
class SceneVM(s: Scene) {
  val id           = ObjectProperty[Option[Int]](s.id)
  val name         = StringProperty(s.name)
  val description  = StringProperty(s.description)
  val characterIds: ObservableBuffer[Int] = ObservableBuffer.from(s.characterIds)
  val enemyIds: ObservableBuffer[Int]     = ObservableBuffer.from(s.enemyIds)

  def toModel: Scene = Scene(
    id = id.value,
    name = name.value,
    description = description.value,
    characterIds = characterIds.toSeq,
    enemyIds = enemyIds.toSeq
  )
}

object SceneVM {
  def blank: SceneVM = new SceneVM(Scene(None, "", "", Nil, Nil))
}
