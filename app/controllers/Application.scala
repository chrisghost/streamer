package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import playcli.CLI
import java.io.File
import scala.concurrent._
import ExecutionContext.Implicits.global

object Converters {
  val formats = List("ogg", "webm")
  val quality = List("vga","wvga","hd720")

  val pipedFFin = "ffmpeg -threads 5 -i pipe:0 "
  val pipedFFout = " pipe:1"

  def getConv(format:String, quality:String) = {
    val convstr = pipedFFin+
      (format match {
        case "ogg" => "-vcodec libtheora -acodec libvorbis -ab 192k -f ogg"
        case "webm" => "-acodec libvorbis -ab 192k -f webm"
      }) +" -s "+quality +pipedFFout

      println(convstr)
    CLI.pipe(convstr)
  }

}

object Resources {
  import play.api.Play.current

  def getVideo(name : String) : Option[File] = {
    val f = Play.getFile("videos").list.toList.filter( _.contains(name) ).headOption

    f match {
      case s:Some[String] => Some(Play.getFile("videos/"+s.get))
      case None => None
    }
  }
}

object Application extends Controller {
  import Resources._
  import Converters._

  def index(f: String, fm:String, q:String) = Action {
    import play.api.Play.current
    val fList = Play.getFile("videos").list.toList

    Ok(views.html.index(fList, formats, quality, f, fm, q))
  }

  def lst = Action {
    import play.api.Play.current
    val fList = Play.getFile("videos").list.toList

    Ok(views.html.index(fList, formats, quality, "", "", ""))
  }

  def movie(title: String, format:String, quality: String ) = Action {
    println("Looking for "+title)

    getVideo(title) match {
      case f : Some[File] => {
        val stream = Enumerator.fromFile(f.get)

        Ok.stream(stream &> getConv(format, quality)
        ).withHeaders(CONTENT_TYPE -> ("video/"+format))
      }
      case None           => NotFound("Movie not found")
    }
  }
}
