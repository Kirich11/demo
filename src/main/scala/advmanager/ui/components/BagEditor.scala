package advmanager.ui.components

import scalafx.Includes._
import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.beans.property.DoubleProperty
import advmanager.model.{Item, BagEntry}
import advmanager.repo.ItemRepo
import advmanager.ui.Async
import scala.concurrent.ExecutionContext

/** Lets a character's bag be edited, with a live weight-vs-limit indicator. */
class BagEditor(itemRepo: ItemRepo, initialBag: Seq[BagEntry], weightLimit: DoubleProperty)(implicit ec: ExecutionContext)
    extends VBox(8) {

  private val entries = ObservableBuffer.from(initialBag)
  private val catalog  = ObservableBuffer.empty[Item]

  private val table = new TableView[BagEntry](entries) {
    columns ++= Seq(
      new TableColumn[BagEntry, String] {
        text = "Item"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(c.value.item.name) }
        prefWidth = 160
      },
      new TableColumn[BagEntry, String] {
        text = "Qty"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(c.value.quantity.toString) }
        prefWidth = 60
      },
      new TableColumn[BagEntry, String] {
        text = "Weight"
        cellValueFactory = { c => scalafx.beans.property.StringProperty(f"${c.value.totalWeight}%.1f") }
        prefWidth = 80
      }
    )
    prefHeight = 180
  }

  private val itemPicker = new ComboBox[Item](catalog) {
    cellFactory = { _: ListView[Item] => new ListCell[Item] { item.onChange { (_, _, v) => text = Option(v).map(_.name).orNull } } }
    buttonCell = new ListCell[Item] { item.onChange { (_, _, v) => text = Option(v).map(_.name).orNull } }
  }
  private val qtySpinner = new Spinner[Int](1, 999, 1) { editable = true; prefWidth = 70 }

  private val addBtn = new Button("Add") {
    onAction = _ => Option(itemPicker.value.value).foreach { item =>
      val qty = qtySpinner.value.value
      val existingIdx = entries.indexWhere(_.item.id == item.id)
      if (existingIdx >= 0) {
        val existing = entries(existingIdx)
        entries.update(existingIdx, existing.copy(quantity = existing.quantity + qty))
      } else {
        entries += BagEntry(None, item, qty)
      }
    }
  }
  private val removeBtn = new Button("Remove Selected") {
    onAction = _ => Option(table.selectionModel.value.selectedItem.value).foreach(entries -= _)
  }

  private val newItemName   = new TextField { promptText = "New item name"; prefWidth = 140 }
  private val newItemWeight = new TextField { promptText = "Weight"; prefWidth = 70 }
  private val newItemBtn = new Button("New Item") {
    onAction = _ => {
      val n = newItemName.text.value.trim
      val w = newItemWeight.text.value.trim.toDoubleOption.getOrElse(0.0)
      if (n.nonEmpty) {
        Async.run(itemRepo.save(Item(None, n, w, ""))) { _ =>
          newItemName.clear(); newItemWeight.clear()
          reloadCatalog()
        }
      }
    }
  }

  private val weightLabel = new Label
  private def refreshWeightLabel(): Unit = {
    val w = currentWeight
    weightLabel.text = f"Bag weight: $w%.1f / ${weightLimit.value}%.1f"
    weightLabel.style =
      if (w > weightLimit.value) "-fx-text-fill: #c0392b; -fx-font-weight: bold;"
      else "-fx-text-fill: #2c3e50;"
  }
  entries.onChange { (_, _) => refreshWeightLabel() }
  weightLimit.onChange { (_, _, _) => refreshWeightLabel() }
  refreshWeightLabel()

  private def currentWeight: Double = entries.map(_.totalWeight).sum

  children = Seq(
    new Label("Bag"),
    table,
    new HBox(6, itemPicker, qtySpinner, addBtn, removeBtn) { padding = Insets(4, 0, 0, 0) },
    new HBox(6, newItemName, newItemWeight, newItemBtn) { padding = Insets(2, 0, 0, 0) },
    weightLabel
  )

  reloadCatalog()

  private def reloadCatalog(): Unit =
    Async.run(itemRepo.findAll()) { all => catalog.clear(); catalog ++= all }

  def toBag: Seq[BagEntry] = entries.toSeq
}
