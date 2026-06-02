package advmanager.ui

import scalafx.application.Platform
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/** Runs a Future and marshals the result (or an error dialog) back onto the JavaFX thread. */
object Async {
  def run[T](f: Future[T])(onSuccess: T => Unit)(implicit ec: ExecutionContext): Unit =
    f.onComplete {
      case Success(v) => Platform.runLater(onSuccess(v))
      case Failure(ex) => Platform.runLater {
        new Alert(AlertType.Error) {
          headerText = "Something went wrong"
          contentText = ex.getMessage
        }.showAndWait()
      }
    }
}
