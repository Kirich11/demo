package advmanager.ui.views

import scalafx.scene.layout.{VBox, HBox, GridPane}
import scalafx.scene.control._
import scalafx.geometry.Insets
import advmanager.model.Enemy
import advmanager.repo.EnemyRepo
import advmanager.ui.components.AttributeTable
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

class EnemyEditView(
  enemyRepo: EnemyRepo,
  existing: Option[Enemy],
  onSaved: () => Unit,
  onCancel: () => Unit
)(implicit ec: ExecutionContext) extends VBox(12) {

  padding = Insets(16)

  private val nameField = new TextField {
    text = existing.map(_.name).getOrElse("")
    promptText = "Enemy name"
  }
  private val notesArea = new TextArea {
    text = existing.map(_.notes).getOrElse("")
    prefRowCount = 3
    promptText = "Notes"
  }
  private val attributeTable = new AttributeTable(existing.map(_.attributes).getOrElse(Nil))

  private val form = new GridPane {
    hgap = 10; vgap = 8; padding = Insets(0, 0, 12, 0)
    add(new Label("Name:"), 0, 0);  add(nameField, 1, 0)
    add(new Label("Notes:"), 0, 1); add(notesArea, 1, 1)
  }

  private val saveBtn = new Button("Save") {
    onAction = _ => {
      val name = nameField.text.value.trim
      if (name.isEmpty) {
        new Alert(Alert.AlertType.Warning) { contentText = "Name is required." }.showAndWait()
      } else {
        val model = Enemy(
          id = existing.flatMap(_.id),
          name = name,
          notes = notesArea.text.value,
          attributes = attributeTable.toAttributes.map { case (n, v) => advmanager.model.Attribute(None, 0, n, v) }
        )
        this.disable = true
        Async.run(enemyRepo.save(model)) { _ =>
          this.disable = false
          onSaved()
        }
      }
    }
  }
  private val cancelBtn = new Button("Cancel") { onAction = _ => onCancel() }

  children = Seq(
    new Label(if (existing.isDefined) "Edit Enemy" else "New Enemy") { style = "-fx-font-size: 18px; -fx-font-weight: bold;" },
    form,
    attributeTable,
    new HBox(10, saveBtn, cancelBtn) { padding = Insets(12, 0, 0, 0) }
  )
}
