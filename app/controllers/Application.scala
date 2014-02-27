package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def upload = Action(parse.multipartFormData) { request =>

    (for {
      src <- request.body.file("srcFile")
      dst <- request.body.file("dstFile")
    } yield {
      Ok { src.filename + " : " + dst.filename }
    }).getOrElse {
      Redirect(routes.Application.index).flashing("error" -> "Missing file")
    }

    //    request.body.file("srcFile").map { srcfile =>
    //      import java.io.File
    //      val filename = srcfile.filename
    //      val contentType = srcfile.contentType
    //      srcfile.ref.moveTo(new File("/tmp/picture"))
    //      Ok("File uploaded")
    //    }.getOrElse {
    //      Redirect(routes.Application.index).flashing(
    //        "error" -> "Missing file")
    //    }
  }

}