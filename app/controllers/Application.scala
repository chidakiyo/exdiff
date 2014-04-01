package controllers

import play.api._
import play.api.mvc._
import poi4s.Implicit._
import util.Keys.XlsType._
import util.Keys.HttpParam._
import util.Keys.SheetMap._
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
import org.apache.poi.ss.usermodel.{ Cell => PCell }
import util.ControlUtil._
import org.apache.poi.ss.usermodel.Row
import util.MaxUtil

object Application extends Controller with Logging {

  def index = Action {
    logger.info("START {}#{}", "Application", "index")
    Ok(views.html.index(""))
  }

  import play.api.data._
  import play.api.data.Forms._

  val sheetForm = Form(tuple("srcSheet" -> nonEmptyText, "dstSheet" -> nonEmptyText))

  def upload = Action(parse.multipartFormData) { implicit request =>
    logger.info("START {}#{}", "Application", "upload")

    val (sheet1, sheet2) = sheetForm.bindFromRequest.get

    (for {
      src <- request.body.file(SRC_FILE)
      dst <- request.body.file(DST_FILE)
    } yield {
      val response = for {
        srcBook <- Book.create(src)
        dstBook <- Book.create(dst)
      } yield {

        val srcSheet = srcBook.getSheetAt(SHEET_MAP.get(sheet1).get) // TODO if None return
        val dstSheet = dstBook.getSheetAt(SHEET_MAP.get(sheet2).get) // TODO if None return 

        val maxRow = MaxUtil.getLastRowNumMax(srcSheet, dstSheet)
        val maxCell = MaxUtil.getColumnNumMaxSheets(srcSheet, dstSheet)

        val result: JList[JList[String]] = new ArrayList()
        for (row <- (0 to maxRow); col <- (0 until maxCell)) {
          val getRow = (row: Row, col: Int) => { if (row != null) Some(row.getCell(col)) else None }
          val c1: Option[PCell] = getRow(srcSheet.getRow(row), col)
          val c2: Option[PCell] = getRow(dstSheet.getRow(row), col)
          if (!result.isDefinedAt(row)) { result.add(row, initRow(row)) }
          result.get(row).add(col, Cell.diff(c1, c2).getOutput)
        }
        encode(result)
      }

      Ok {
        views.html.table("Your new application is ready.")(response.orNull)
      }
    }).getOrElse {
      Redirect(routes.Application.index).flashing("error" -> "Missing file")
    }

  }

  def initRow(row: Int): ArrayList[String] = {
    val l = new ArrayList[String]
    l
  }

}