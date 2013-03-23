package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import playcli.CLI
import java.io.File

object Converters {

  val toAVI = CLI.pipe("ffmpeg -i pipe:0 -f avi pipe:1")
  val smallPrev = CLI.pipe("ffmpeg -i pipe:0 -vf scale=iw/4:-1 -f avi pipe:1")

}

object Resources {
  import play.api.Play.current

  val videoFile = Play.getFile("viselalune.flv")

}

object Application extends Controller {
  import Resources._
  import Converters._

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def prev = Action {
    println("Starting smallPreview....")
    val stream = Enumerator.fromFile(videoFile)
    Ok.stream(stream &> smallPrev)
      .withHeaders(CONTENT_TYPE -> "video/avi")
  }

  def movie(title: String) = Action {
    import play.api.Play.current
    println("Looking for "+title)

    val fIn = Play.getFile(title)
    if(!fIn.exists()) {
      NotFound("Movie not found")
    } else {
      val stream =  Enumerator.fromFile(fIn)

      Ok.stream( stream &> toAVI )
        .withHeaders(CONTENT_TYPE -> "video/avi")

    }
  }
}
