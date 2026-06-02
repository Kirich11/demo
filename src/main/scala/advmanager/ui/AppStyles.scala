package advmanager.ui

import scalafx.scene.Scene

/** Central place for CSS-related constants so views don't hardcode class names or paths. */
object AppStyles {
  /** Classpath-relative path to the stylesheet in src/main/resources/app.css */
  val stylesheetPath: String = "/app.css"

  // Style classes referenced from view/component code (defined in app.css)
  val HeaderLabel     = "header-label"
  val SubHeaderLabel  = "sub-header-label"
  val Toolbar         = "app-toolbar"
  val WarningText     = "warning-text"
  val DangerText      = "danger-text"
  val CardPane        = "card-pane"

  /** Attach the app stylesheet to a scene. Call once, right after the Scene is constructed. */
  def apply(scene: Scene): Unit =
    scene.stylesheets.add(getClass.getResource(stylesheetPath).toExternalForm)
}
