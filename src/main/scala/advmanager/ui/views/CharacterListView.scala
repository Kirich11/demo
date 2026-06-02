package advmanager.ui.views

import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import advmanager.repo.{CharacterRepo, ClassRepo, ItemRepo}
import advmanager.model.Character
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

class CharacterListView(characterRepo: CharacterRepo, classRepo: ClassRepo, itemRepo: ItemRepo)
    (implicit ec: ExecutionContext) extends BorderPane {

  private val data = ObservableBuffer.empty[Character]

  private val table = new TableView[Character](data) {
    columns ++= Seq(
      new TableColumn[Character, String] {
        text = "Name"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(c.value.name) }
        prefWidth = 180
      },
      new TableColumn[Character, String] {
        text = "Classes"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(c.value.classes.map(_.name).mkString(", ")) }
        prefWidth = 160
      },
      new TableColumn[Character, String] {
        text = "Weight"
        cellValueFactory = { c =>
          scalafx.beans.property.StringProperty(f"${c.value.bagWeight}%.1f / ${c.value.weightLimit}%.1f")
        }
        prefWidth = 120
      }
    )
  }

  private val toolbar = new ToolBar {
    items ++= Seq(
      new Button("New") { onAction = _ => openEditor(None) },
      new Button("Edit") { onAction = _ =>
        Option(table.selectionModel.value.selectedItem.value).foreach(c => openEditor(Some(c)))
      },
      new Button("Delete") { onAction = _ =>
        Option(table.selectionModel.value.selectedItem.value).foreach { c =>
          val confirm = new Alert(Alert.AlertType.Confirmation) {
            headerText = s"Delete ${c.name}?"
            contentText = "This cannot be undone."
          }.showAndWait()
          if (confirm.contains(ButtonType.OK)) {
            Async.run(characterRepo.delete(c.id.get)) { _ => refresh() }
          }
        }
      }
    )
  }

  top = toolbar
  center = table
  refresh()

  private def openEditor(existing: Option[Character]): Unit = {
    center = new CharacterEditView(characterRepo, classRepo, itemRepo, existing, onSaved = () => refresh(), onCancel = () => { center = table })
  }

  private def refresh(): Unit = {
    Async.run(characterRepo.findAll()) { list =>
      data.clear(); data ++= list
      center = table
    }
  }
}
