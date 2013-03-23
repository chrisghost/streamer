import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "streamer"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    "fr.greweb" %% "playcli" % "0.1",
    // Add your project dependencies here,
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
