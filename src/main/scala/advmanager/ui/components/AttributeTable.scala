package advmanager.ui.components

import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control._
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.StringProperty
import scalafx.geometry.Insets
import advmanager.model.Attribute

/** Row model backing the editable table — plain mutable properties for two-way binding. */
class AttrRow(initName: String, initValue: String, val id: Option[Int] = None) {
  val name  = StringProperty(initName)
  val value = StringProperty(initValue)
}

/** Editable name/value list, e.g. STR: 14, DEX: 12. Works for both character and enemy attributes. */
class AttributeTable(initial: Seq[Attribute]) extends VBox(8) {
  val rows: ObservableBuffer[AttrRow] =
    ObservableBuffer.from(initial.map(a => new AttrRow(a.name, a.value, a.id)))

  private val table = new TableView[AttrRow](rows) {
    editable = true
    columns ++= Seq(
      new TableColumn[AttrRow, String] {
        text = "Attribute"
        cellValueFactory = { _.value.name }
        onEditCommit = e => e.rowValue.name.value = e.newValue
        prefWidth = 160
      },
      new TableColumn[AttrRow, String] {
        text = "Value"
        cellValueFactory = { _.value.value }
        onEditCommit = e => e.rowValue.value.value = e.newValue
        prefWidth = 120
      }
    )
    prefHeight = 180
  }

  private val nameField  = new TextField { promptText = "Attribute name (e.g. STR)" }
  private val valueField = new TextField { promptText = "Value (e.g. 14)" }
  private val addBtn = new Button("Add") {
    onAction = _ => {
      if (nameField.text.value.trim.nonEmpty) {
        rows += new AttrRow(nameField.text.value.trim, valueField.text.value.trim)
        nameField.clear(); valueField.clear()
      }
    }
  }
  private val removeBtn = new Button("Remove Selected") {
    onAction = _ => Option(table.selectionModel.value.selectedItem.value).foreach(rows -= _)
  }

  children = Seq(
    new Label("Attributes"),
    table,
    new HBox(6, nameField, valueField, addBtn, removeBtn) { padding = Insets(4, 0, 0, 0) }
  )

  def toAttributes: Seq[(String, String)] =
    rows.toSeq.map(r => (r.name.value, r.value.value))
}
