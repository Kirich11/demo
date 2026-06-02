ThisBuild / scalaVersion := "2.13.14"
ThisBuild / organization := "kirich"
ThisBuild / version      := "0.1.0"

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "adventure-manager",
    Compile / mainClass := Some("advmanager.Main"),

    libraryDependencies ++= Seq(
      "org.scalafx"         %% "scalafx"         % "20.0.0-R31",
      "com.typesafe.slick"  %% "slick"           % "3.5.1",
      "com.typesafe.slick"  %% "slick-hikaricp"  % "3.5.1",
      "org.xerial"           % "sqlite-jdbc"     % "3.46.0.0",
      "ch.qos.logback"       % "logback-classic" % "1.5.6",
      "org.scalatest"       %% "scalatest"       % "3.2.19" % Test
    ),
    // Pinned to "win" — this build always targets Windows, so no OS-detection needed.
    libraryDependencies ++= javaFXModules.map(m =>
      "org.openjfx" % s"javafx-$m" % "20.0.2" classifier "win"
    ),

    // Fork so the JavaFX/Slick threads run cleanly under `sbt run` too.
    Compile / run / fork := true,

    // --- jpackage: builds a Windows .exe installer from the staged app ---
    jpackageExe := {
      val stageDir = (Universal / stage).value
      val log      = streams.value.log
      val appVer   = version.value
      val destDir  = target.value / "jpackage"
      IO.createDirectory(destDir)

      // NOTE: verify this matches the actual jar name sbt generates under
      // target/universal/stage/lib after running `sbt stage` — see README.
      val mainJarName = s"${organization.value}.${name.value}-$appVer.jar"

      val iconPath = (Compile / resourceDirectory).value / "app.ico"
      val iconArgs = if (iconPath.exists()) Seq("--icon", iconPath.getPath) else Seq.empty

      val cmd = Seq(
        "jpackage",
        "--type", "exe",
        "--name", "AdventureManager",
        "--app-version", appVer,
        "--vendor", "Kirich",
        "--input", (stageDir / "lib").getPath,
        "--main-jar", mainJarName,
        "--main-class", (Compile / mainClass).value.get,
        "--win-shortcut",
        "--win-dir-chooser",
        "--win-menu",
        "--dest", destDir.getPath
      ) ++ iconArgs

      log.info(s"Running: ${cmd.mkString(" ")}")
      val exit = sys.process.Process(cmd, stageDir).!
      if (exit != 0) sys.error(s"jpackage failed with exit code $exit")
      log.info(s"Installer written to $destDir")
    }
  )

lazy val jpackageExe = taskKey[Unit]("Build a Windows .exe installer via jpackage")
