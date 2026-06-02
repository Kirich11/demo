package advmanager.ui.views

import scalafx.scene.layout.VBox
import scalafx.scene.control.Label
import scalafx.geometry.{Insets, Pos}

class MainMenuView extends VBox(12) {
  alignment = Pos.Center
  padding = Insets(40)
  children = Seq(
    new Label("Adventure Manager") { style = "-fx-font-size: 28px; -fx-font-weight: bold;" },
    new Label("Use the panel on the left to manage Characters, Enemies, and Scenes.") {
      style = "-fx-font-size: 14px;"
    }
  )
}
