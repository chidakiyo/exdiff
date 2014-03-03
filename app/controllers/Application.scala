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
import util.Keys.HttpParam._
import org.apache.poi.ss.usermodel.{ Cell => PCell }

object Application extends Controller with Logging {

  def index = Action {
    logger.info("START {}#{}", "Application", "index")
    Ok(views.html.index("Your new application is ready."))
  }

  def upload = Action(parse.multipartFormData) { request =>
    logger.info("START {}#{}", "Application", "upload")

    var responseString: String = null

    (for {
      src <- request.body.file(SRC_FILE)
      dst <- request.body.file(DST_FILE)
    } yield {
      for {
        srcBook <- Book.create(src)
        dstBook <- Book.create(dst)
      } {

        val srcSheet = srcBook.getSheetAt(0) // TODO
        val dstSheet = dstBook.getSheetAt(0) // TODO

        val maxRow = List(srcSheet, dstSheet) filter (_ != null) map (_.getLastRowNum) max: Int
        val maxCell = List(srcSheet, dstSheet) filter (_ != null) map (m => m.iterator map (_.getLastCellNum) max) max

        val result: JList[JList[String]] = new ArrayList()
        for (row <- (0 to maxRow); col <- (0 until maxCell)) yield {
          val s1 = srcSheet.getRow(row)
          val s2 = dstSheet.getRow(row)
          val c1: Option[PCell] = if (s1 != null) Some(s1.getCell(col)) else None
          val c2: Option[PCell] = if (s2 != null) Some(s2.getCell(col)) else None
          println(s"ROW: $row, COL: $col, C1: $c1, C2: $c2")
          if (!result.isDefinedAt(row)) {
            result.add(row, new ArrayList)
          }
          val tmpRow = result.get(row);
          tmpRow.add(col, Cell.diff(c1, c2).getOutput)
        }

        responseString = encode(result)
      }

      Ok {
        views.html.table("Your new application is ready.")(responseString)
      }
    }).getOrElse {
      Redirect(routes.Application.index).flashing("error" -> "Missing file")
    }

  }

}