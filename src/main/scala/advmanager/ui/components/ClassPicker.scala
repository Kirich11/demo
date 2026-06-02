package advmanager.ui.components

import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import advmanager.model.CharacterClass
import advmanager.repo.ClassRepo
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

/** Checkbox list letting a character be assigned to multiple classes, with a quick-add for new ones. */
class ClassPicker(classRepo: ClassRepo, initiallySelected: Seq[CharacterClass])(implicit ec: ExecutionContext)
    extends VBox(8) {

  private val selectedIds = scala.collection.mutable.Set.from(initiallySelected.flatMap(_.id))
  private val checkBoxes  = ObservableBuffer.empty[CheckBox]
  private val listBox     = new VBox(4)

  private val newClassField = new TextField { promptText = "New class name" }
  private val addBtn = new Button("Add Class") {
    onAction = _ => {
      val n = newClassField.text.value.trim
      if (n.nonEmpty) {
        Async.run(classRepo.save(CharacterClass(None, n, ""))) { _ =>
          newClassField.clear()
          reload()
        }
      }
    }
  }

  children = Seq(new Label("Classes"), listBox, new HBox(6, newClassField, addBtn) { padding = Insets(4, 0, 0, 0) })

  reload()

  private def reload(): Unit = {
    Async.run(classRepo.findAll()) { all =>
      checkBoxes.clear()
      listBox.children.clear()
      all.foreach { cls =>
        val cb = new CheckBox(cls.name) {
          selected = cls.id.exists(selectedIds.contains)
          onAction = _ => {
            cls.id.foreach { id =>
              if (selected.value) selectedIds += id else selectedIds -= id
            }
          }
          userData = cls
        }
        checkBoxes += cb
        listBox.children.add(cb)
      }
    }
  }

  def selectedClassIds: Seq[Int] = selectedIds.toSeq
}
