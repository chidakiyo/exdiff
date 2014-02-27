package controllers

import play.api._
import play.api.mvc._
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.FileInputStream
import poi4s.Implicit._
import org.apache.poi.ss.usermodel.Sheet
import scala.collection.Seq
import scala.collection.mutable.ListBuffer
import poi4s.RichCell
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object Application extends Controller {

  val HSSF_XLS = "application/vnd.ms-excel"
  val XSSF_XLS = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def createBook(file: play.api.mvc.MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]): Workbook = {
    val x = file.contentType map { typ =>
      typ match {
        case HSSF_XLS => new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(file.ref.file)))
        case XSSF_XLS => new XSSFWorkbook(new FileInputStream(file.ref.file))
      }
    }
    x.getOrElse(null)
  }

  def upload = Action(parse.multipartFormData) { request =>

    (for {
      src <- request.body.file("srcFile")
      dst <- request.body.file("dstFile")
    } yield {
      import java.io.File

      println("#################")
      println(src.contentType)

      val srcBook = createBook(src)
      val dstBook = createBook(dst)

      val srcSheet = srcBook.getSheetAt(0)
      val dstSheet = dstBook.getSheetAt(0)

      val a = new ListBuffer[RichCell]()
      val b = new ListBuffer[RichCell]()
      val srcCells = srcSheet filter { cell => a += cell; false }
      val dstCells = dstSheet filter { cell => b += cell; false }

      println("size : " + a.size)
      println("size : " + b.size)

      for ((sCell, dCell) <- a.zip(b)) {
        if (sCell.text != dCell.text) {
          println(sCell.text + " : " + dCell.text)
        }
      }

      //      val list = new ListBuffer[String]()
      //      sheet foreach { cell =>
      //        val rownum = cell.row.getRowNum
      //        list += <p>${ cell.text }</p>.toString
      //      }

      Ok {
        <div>
          <p>src : ${ src.filename } : ${ src.contentType }</p>
          <p>dst : ${ dst.filename } : ${ dst.contentType }</p>
        </div>
      }.as(HTML)
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