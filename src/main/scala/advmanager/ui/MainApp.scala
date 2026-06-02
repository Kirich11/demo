package advmanager.ui

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.{Button, ToolBar}
import scalafx.geometry.Orientation
import advmanager.db.AppDatabase
import advmanager.repo._
import advmanager.ui.views._

object MainApp extends JFXApp3 {

  override def start(): Unit = {
    AppDatabase.initSchema()
    implicit val ec = AppDatabase.ec
    val characterRepo = new CharacterRepo(AppDatabase.db)
    val enemyRepo      = new EnemyRepo(AppDatabase.db)
    val sceneRepo      = new SceneRepo(AppDatabase.db)
    val itemRepo       = new ItemRepo(AppDatabase.db)
    val classRepo      = new ClassRepo(AppDatabase.db)

    val root = new BorderPane
    val nav = new ToolBar {
      orientation = Orientation.Vertical
      items ++= Seq(
        new Button("Characters") { onAction = _ => root.center = new CharacterListView(characterRepo, classRepo, itemRepo) },
        new Button("Enemies")    { onAction = _ => root.center = new EnemyListView(enemyRepo) },
        new Button("Scenes")     { onAction = _ => root.center = new SceneListView(sceneRepo, characterRepo, enemyRepo) }
      )
    }
    root.left = nav
    root.center = new MainMenuView

    stage = new JFXApp3.PrimaryStage {
      title = "Adventure Manager"
      width = 1100
      height = 750
      scene = new Scene(root)
    }
  }
}