package advmanager.ui.views

import scalafx.scene.layout.BorderPane
import scalafx.scene.control._
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import advmanager.repo.{SceneRepo, CharacterRepo, EnemyRepo}
import advmanager.model.Scene
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

class SceneListView(sceneRepo: SceneRepo, characterRepo: CharacterRepo, enemyRepo: EnemyRepo)
    (implicit ec: ExecutionContext) extends BorderPane {

  private val data = ObservableBuffer.empty[Scene]

  private val table = new TableView[Scene](data) {
    columns ++= Seq(
      new TableColumn[Scene, String] {
        text = "Name"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(c.value.name) }
        prefWidth = 200
      },
      new TableColumn[Scene, String] {
        text = "Description"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(c.value.description) }
        prefWidth = 260
      },
      new TableColumn[Scene, String] {
        text = "Loaded"
        cellValueFactory = { c =>
          scalafx.beans.property.StringProperty(
            s"${c.value.characterIds.size} characters, ${c.value.enemyIds.size} enemies"
          )
        }
        prefWidth = 180
      }
    )
  }

  private val toolbar = new ToolBar {
    items ++= Seq(
      new Button("New") { onAction = _ => openEditor(None) },
      new Button("Edit") { onAction = _ =>
        Option(table.selectionModel.value.selectedItem.value).foreach(s => openEditor(Some(s)))
      },
      new Button("Open") { onAction = _ =>
        Option(table.selectionModel.value.selectedItem.value).foreach(openRuntime)
      },
      new Button("Delete") { onAction = _ =>
        Option(table.selectionModel.value.selectedItem.value).foreach { s =>
          val confirm = new Alert(Alert.AlertType.Confirmation) {
            headerText = s"Delete ${s.name}?"
            contentText = "This cannot be undone."
          }.showAndWait()
          if (confirm.contains(ButtonType.OK)) {
            Async.run(sceneRepo.delete(s.id.get)) { _ => refresh() }
          }
        }
      }
    )
  }

  top = toolbar
  center = table
  refresh()

  private def openEditor(existing: Option[Scene]): Unit = {
    center = new SceneEditView(sceneRepo, existing, onSaved = () => refresh(), onCancel = () => { center = table })
  }

  private def openRuntime(scene: Scene): Unit = {
    center = new SceneRuntimeView(sceneRepo, characterRepo, enemyRepo, scene, onBack = () => { center = table })
  }

  private def refresh(): Unit = {
    Async.run(sceneRepo.findAll()) { list =>
      data.clear(); data ++= list
      center = table
    }
  }
}
