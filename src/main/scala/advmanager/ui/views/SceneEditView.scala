package advmanager.ui.views

import scalafx.scene.layout.{VBox, HBox, GridPane}
import scalafx.scene.control._
import scalafx.geometry.Insets
import advmanager.model.Scene
import advmanager.repo.SceneRepo
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

class SceneEditView(
  sceneRepo: SceneRepo,
  existing: Option[Scene],
  onSaved: () => Unit,
  onCancel: () => Unit
)(implicit ec: ExecutionContext) extends VBox(12) {

  padding = Insets(16)

  private val nameField = new TextField {
    text = existing.map(_.name).getOrElse("")
    promptText = "Scene name"
  }
  private val descriptionArea = new TextArea {
    text = existing.map(_.description).getOrElse("")
    prefRowCount = 5
    promptText = "Description"
  }

  private val form = new GridPane {
    hgap = 10; vgap = 8; padding = Insets(0, 0, 12, 0)
    add(new Label("Name:"), 0, 0);        add(nameField, 1, 0)
    add(new Label("Description:"), 0, 1); add(descriptionArea, 1, 1)
  }

  private val saveBtn = new Button("Save") {
    onAction = _ => {
      val name = nameField.text.value.trim
      if (name.isEmpty) {
        new Alert(Alert.AlertType.Warning) { contentText = "Name is required." }.showAndWait()
      } else {
        // Preserve existing rosters — this form only edits name/description;
        // characters/enemies are managed from the scene runtime view.
        val model = Scene(
          id = existing.flatMap(_.id),
          name = name,
          description = descriptionArea.text.value,
          characterIds = existing.map(_.characterIds).getOrElse(Nil),
          enemyIds = existing.map(_.enemyIds).getOrElse(Nil)
        )
        this.disable = true
        Async.run(sceneRepo.save(model)) { _ =>
          this.disable = false
          onSaved()
        }
      }
    }
  }
  private val cancelBtn = new Button("Cancel") { onAction = _ => onCancel() }

  children = Seq(
    new Label(if (existing.isDefined) "Edit Scene" else "New Scene") { style = "-fx-font-size: 18px; -fx-font-weight: bold;" },
    form,
    new HBox(10, saveBtn, cancelBtn) { padding = Insets(12, 0, 0, 0) }
  )
}
