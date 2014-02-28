package controllers

import play.api._
import play.api.mvc._
import poi4s.Implicit._
import util.Keys.XlsType._
import collection.JavaConversions._
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.FileInputStream
import org.apache.poi.ss.usermodel.Sheet
import scala.collection.Seq
import scala.collection.mutable.ListBuffer
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import xls.Book
import com.typesafe.scalalogging.slf4j.Logging
import cell.Cell
import cell.Eq
import cell.Diff
import java.util.ArrayList
import net.arnx.jsonic.JSON._
import java.util.{ List => JList }

object Application extends Controller with Logging {

  def index = Action {
    logger.info("START {}#{}", "Application", "index")
    Ok(views.html.index("Your new application is ready."))
  }

  def upload = Action(parse.multipartFormData) { request =>
    logger.info("START {}#{}", "Application", "upload")

    var responseString: String = null

    (for {
      src <- request.body.file("srcFile")
      dst <- request.body.file("dstFile")
    } yield {
      for {
        srcBook <- Book.create(src)
        dstBook <- Book.create(dst)
      } {

        val srcSheet = srcBook.getSheetAt(0) // TODO
        val dstSheet = dstBook.getSheetAt(0) // TODO

        val srcCells = srcSheet toSeq
        val dstCells = dstSheet toSeq

        val checkResult = srcCells.zipAll(dstCells, null, null) map {
          case (s, d) =>
            Cell.diff(s, d)
        }

        val indexedValue = checkResult groupBy (_.getLineNo)

        val x: JList[JList[String]] = for (i <- Range(0, indexedValue.keys.max + 1)) yield {
          val value = indexedValue.get(i).get // TODO
          List((i + 1).toString) ++: (value map { c => c.getOutput }): JList[String]
        }

        responseString = encode(x)
      }

      Ok {
        views.html.table("Your new application is ready.")(responseString)
      }
    }).getOrElse {
      Redirect(routes.Application.index).flashing("error" -> "Missing file")
    }

  }

}