package advmanager.ui.views

import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import advmanager.repo.EnemyRepo
import advmanager.model.Enemy
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

class EnemyListView(enemyRepo: EnemyRepo)(implicit ec: ExecutionContext) extends BorderPane {

  private val data = ObservableBuffer.empty[Enemy]

  private val table = new TableView[Enemy](data) {
    columns ++= Seq(
      new TableColumn[Enemy, String] {
        text = "Name"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(c.value.name) }
        prefWidth = 200
      },
      new TableColumn[Enemy, String] {
        text = "Attributes"
        cellValueFactory = { c =>
          scalafx.beans.property.StringProperty(c.value.attributes.map(a => s"${a.name}:${a.value}").mkString(", "))
        }
        prefWidth = 260
      }
    )
  }

  private val toolbar = new ToolBar {
    items ++= Seq(
      new Button("New") { onAction = _ => openEditor(None) },
      new Button("Edit") { onAction = _ =>
        Option(table.selectionModel.value.selectedItem.value).foreach(e => openEditor(Some(e)))
      },
      new Button("Delete") { onAction = _ =>
        Option(table.selectionModel.value.selectedItem.value).foreach { e =>
          val confirm = new Alert(Alert.AlertType.Confirmation) {
            headerText = s"Delete ${e.name}?"
            contentText = "This cannot be undone."
          }.showAndWait()
          if (confirm.contains(ButtonType.OK)) {
            Async.run(enemyRepo.delete(e.id.get)) { _ => refresh() }
          }
        }
      }
    )
  }

  top = toolbar
  center = table
  refresh()

  private def openEditor(existing: Option[Enemy]): Unit = {
    center = new EnemyEditView(enemyRepo, existing, onSaved = () => refresh(), onCancel = () => { center = table })
  }

  private def refresh(): Unit = {
    Async.run(enemyRepo.findAll()) { list =>
      data.clear(); data ++= list
      center = table
    }
  }
}
