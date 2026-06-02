package advmanager.ui.views

import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.control._
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import advmanager.model.{Scene, Character, Enemy}
import advmanager.repo.{SceneRepo, CharacterRepo, EnemyRepo}
import advmanager.ui.Async
import scala.concurrent.{ExecutionContext, Future}

class SceneRuntimeView(
  sceneRepo: SceneRepo,
  charRepo: CharacterRepo,
  enemyRepo: EnemyRepo,
  scene: Scene,
  onBack: () => Unit
)(implicit ec: ExecutionContext) extends BorderPane {

  private val sceneId = scene.id.getOrElse(throw new IllegalArgumentException("scene must be saved first"))

  private val loadedChars   = ObservableBuffer.empty[Character]
  private val loadedEnemies = ObservableBuffer.empty[Enemy]

  Async.run(sceneRepo.getCharactersInScene(sceneId)) { cs => loadedChars.clear(); loadedChars ++= cs }
  Async.run(sceneRepo.getEnemiesInScene(sceneId)) { es => loadedEnemies.clear(); loadedEnemies ++= es }

  private def rosterPane[T](
    title: String,
    loaded: ObservableBuffer[T],
    idOf: T => Int,
    allF: Future[Seq[T]],
    label: T => String,
    onAdd: T => Future[Unit],
    onRemove: T => Future[Unit]
  ): VBox = {
    val listView = new ListView[T](loaded) {
      cellFactory = { _: ListView[T] => new ListCell[T] {
        item.onChange { (_, _, v) => text = Option(v).map(label).orNull }
      }}
      prefHeight = 240
    }

    val picker = new ComboBox[T]() {
      cellFactory = { _: ListView[T] => new ListCell[T] { item.onChange { (_, _, v) => text = Option(v).map(label).orNull } } }
      buttonCell = new ListCell[T] { item.onChange { (_, _, v) => text = Option(v).map(label).orNull } }
    }
    Async.run(allF) { all => picker.items = ObservableBuffer.from(all) }

    val addBtn = new Button("Add") { onAction = _ =>
      Option(picker.value.value).foreach { v =>
        if (!loaded.exists(existing => idOf(existing) == idOf(v))) {
          Async.run(onAdd(v)) { _ => loaded += v }
        }
      }
    }
    val removeBtn = new Button("Remove") { onAction = _ =>
      Option(listView.selectionModel.value.selectedItem.value).foreach { v =>
        Async.run(onRemove(v)) { _ => loaded -= v }
      }
    }

    new VBox(8, new Label(title) { style = "-fx-font-weight: bold;" }, listView, new HBox(6, picker, addBtn, removeBtn)) {
      padding = Insets(10)
      prefWidth = 340
    }
  }

  private val charPane = rosterPane[Character](
    "Characters in Scene", loadedChars, _.id.get, charRepo.findAll(),
    label = _.name,
    onAdd = c => sceneRepo.addCharacter(sceneId, c.id.get),
    onRemove = c => sceneRepo.removeCharacter(sceneId, c.id.get)
  )

  private val enemyPane = rosterPane[Enemy](
    "Enemies in Scene", loadedEnemies, _.id.get, enemyRepo.findAll(),
    label = _.name,
    onAdd = e => sceneRepo.addEnemy(sceneId, e.id.get),
    onRemove = e => sceneRepo.removeEnemy(sceneId, e.id.get)
  )

  private val toolbar = new ToolBar {
    items ++= Seq(new Button("< Back to Scenes") { onAction = _ => onBack() })
  }

  top = toolbar
  center = new VBox(8,
    new Label(s"Scene: ${scene.name}") { style = "-fx-font-size: 18px; -fx-font-weight: bold;"; padding = Insets(10, 10, 0, 10) },
    new Label(scene.description) { padding = Insets(0, 10, 10, 10); wrapText = true },
    new HBox(20, charPane, enemyPane) { padding = Insets(10) }
  )
}
