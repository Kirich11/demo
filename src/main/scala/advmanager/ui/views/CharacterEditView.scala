package advmanager.ui.views

import scalafx.scene.layout.{VBox, HBox, GridPane}
import scalafx.scene.control._
import scalafx.geometry.Insets
import scalafx.beans.property.DoubleProperty
import advmanager.model.{Character, CharacterClass}
import advmanager.repo.{CharacterRepo, ClassRepo, ItemRepo}
import advmanager.ui.components.{AttributeTable, ClassPicker, BagEditor}
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

class CharacterEditView(
  characterRepo: CharacterRepo,
  classRepo: ClassRepo,
  itemRepo: ItemRepo,
  existing: Option[Character],
  onSaved: () => Unit,
  onCancel: () => Unit
)(implicit ec: ExecutionContext) extends VBox(12) {

  padding = Insets(16)

  private val nameField = new TextField {
    text = existing.map(_.name).getOrElse("")
    promptText = "Character name"
  }
  private val weightLimitProp = DoubleProperty(existing.map(_.weightLimit).getOrElse(50.0))
  private val weightLimitField = new TextField {
    text = weightLimitProp.value.toString
    promptText = "Weight limit"
  }
  weightLimitField.text.onChange { (_, _, v) =>
    v.toDoubleOption.foreach(weightLimitProp.value = _)
  }
  private val notesArea = new TextArea {
    text = existing.map(_.notes).getOrElse("")
    prefRowCount = 3
    promptText = "Notes"
  }

  private val classPicker = new ClassPicker(classRepo, existing.map(_.classes).getOrElse(Nil))
  private val attributeTable = new AttributeTable(existing.map(_.attributes).getOrElse(Nil))
  private val bagEditor = new BagEditor(itemRepo, existing.map(_.bag).getOrElse(Nil), weightLimitProp)

  private val form = new GridPane {
    hgap = 10; vgap = 8; padding = Insets(0, 0, 12, 0)
    add(new Label("Name:"), 0, 0);         add(nameField, 1, 0)
    add(new Label("Weight limit:"), 0, 1); add(weightLimitField, 1, 1)
    add(new Label("Notes:"), 0, 2);        add(notesArea, 1, 2)
  }

  private val saveBtn = new Button("Save") {
    onAction = _ => {
      val name = nameField.text.value.trim
      if (name.isEmpty) {
        new Alert(Alert.AlertType.Warning) { contentText = "Name is required." }.showAndWait()
      } else {
        val model = Character(
          id = existing.flatMap(_.id),
          name = name,
          weightLimit = weightLimitProp.value,
          notes = notesArea.text.value,
          classes = classPicker.selectedClassIds.map(id => CharacterClass(Some(id), "", "")),
          attributes = attributeTable.toAttributes.map { case (n, v) => advmanager.model.Attribute(None, 0, n, v) },
          bag = bagEditor.toBag
        )
        this.disable = true
        Async.run(characterRepo.save(model)) { _ =>
          this.disable = false
          onSaved()
        }
      }
    }
  }
  private val cancelBtn = new Button("Cancel") { onAction = _ => onCancel() }

  children = Seq(
    new Label(if (existing.isDefined) "Edit Character" else "New Character") { style = "-fx-font-size: 18px; -fx-font-weight: bold;" },
    form,
    new HBox(20, classPicker, attributeTable) { padding = Insets(0, 0, 8, 0) },
    bagEditor,
    new HBox(10, saveBtn, cancelBtn) { padding = Insets(12, 0, 0, 0) }
  )
}
