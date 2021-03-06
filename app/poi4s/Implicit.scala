package poi4s

import org.apache.poi.ss.usermodel._
import scala.collection.mutable.ListBuffer

object Implicit {

  implicit class RichCell(val cell: Cell) {

    lazy val row: Row = cell.getRow

    lazy val rowNum: Int = cell.getRowIndex

    lazy val colNum: Int = cell.getColumnIndex

    lazy val cellType: Int = cell.getCellType

    lazy val hasBorderBottom: Boolean = cell.getCellStyle match {
      case null => false
      case style => hasBorder(style.getBorderBottom)
    }

    lazy val hasBorderTop: Boolean = cell.getCellStyle match {
      case null => false
      case style => hasBorder(style.getBorderTop)
    }

    lazy val hasBorderLeft: Boolean = cell.getCellStyle match {
      case null => false
      case style => hasBorder(style.getBorderLeft)
    }

    lazy val hasBorderRight: Boolean = cell.getCellStyle match {
      case null => false
      case style => hasBorder(style.getBorderRight)
    }

    def text: String = {
      if (cell == null) {
        ""
      } else {
        cell.getCellType match {
          case Cell.CELL_TYPE_BLANK => ""
          case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue.toString
          case Cell.CELL_TYPE_ERROR => String.valueOf(cell.getCellFormula())
          case Cell.CELL_TYPE_FORMULA => getCellFormulaContents(cell)
          case Cell.CELL_TYPE_NUMERIC => getNumericContents(cell)
          case Cell.CELL_TYPE_STRING => String.valueOf(cell.getStringCellValue())
          case _ => ""
        }
      }
    }

    private def hasBorder(borderStyle: Short): Boolean = {
      borderStyle match {
        case CellStyle.BORDER_NONE => false
        case CellStyle.BORDER_THIN => true
        case CellStyle.BORDER_MEDIUM => true
        case CellStyle.BORDER_DASHED => true
        case CellStyle.BORDER_DOTTED => true
        case CellStyle.BORDER_THICK => true
        case CellStyle.BORDER_DOUBLE => true
        case CellStyle.BORDER_HAIR => true
        case CellStyle.BORDER_MEDIUM_DASHED => true
        case CellStyle.BORDER_DASH_DOT => true
        case CellStyle.BORDER_MEDIUM_DASH_DOT => true
        case CellStyle.BORDER_MEDIUM_DASH_DOT_DOT => true
        case CellStyle.BORDER_SLANTED_DASH_DOT => true
        case _ => false
      }
    }

    private def convertDoubleValue(d: Double): String = {
      try {
        new java.math.BigDecimal(d).intValueExact.toString
      } catch {
        case e: ArithmeticException => d.toString
      }
    }

    private def getNumericContents(cell: Cell): String = {
      if (DateUtil.isCellDateFormatted(cell)) {
        // FIXME format string...in JExcel API standard.
        new java.text.SimpleDateFormat("yy/MM/dd").format(cell.getDateCellValue)
      } else {
        convertDoubleValue(cell.getNumericCellValue())
      }
    }

    private def getCellFormulaContents(cell: Cell): String = {
      cell.getCachedFormulaResultType match {
        case Cell.CELL_TYPE_NUMERIC => getNumericContents(cell)
        case Cell.CELL_TYPE_STRING => cell.getStringCellValue
        case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue.toString
        case Cell.CELL_TYPE_ERROR => cell.getCellFormula
        case _ => ""
      }
    }

  }

  implicit class RichSheet(sheet: Sheet) {

    lazy val name = sheet.getSheetName

    def row(rowNum: Int): Seq[RichCell] = {
      val row = sheet.getRow(rowNum)
      if (row == null) {
        Nil
      } else {
        val list = new ListBuffer[RichCell]
        for (cellNum <- row.getFirstCellNum to row.getLastCellNum) {
          val cell = row.getCell(cellNum)
          if (cell != null) {
            list :+ cell
          }
        }
        list.toSeq
      }
    }

    def column(colNum: Int): Seq[RichCell] = {
      val list = new ListBuffer[RichCell]
      for (rowNum <- sheet.getFirstRowNum to sheet.getLastRowNum) {
        val row = sheet.getRow(rowNum)
        if (row != null) {
          val cell = row.getCell(colNum)
          if (cell != null) {
            list :+ cell
          }
        }
      }
      list.toSeq
    }

    def foreach[T](f: RichCell => T): Seq[T] = {
      val list = new ListBuffer[T]
      for (rowNum <- sheet.getFirstRowNum to sheet.getLastRowNum) {
        val row = sheet.getRow(rowNum)
        if (row != null) {
          for (cellNum <- row.getFirstCellNum to row.getLastCellNum) {
            val cell = row.getCell(cellNum)
            if (cell != null) {
              list :+ f(cell)
            }
          }
        }
      }
      list.toSeq
    }

    def toSeq(): Seq[Cell] = {
      val tmp = new ListBuffer[Cell]()
      foreach { cell => tmp += cell.cell; true }
      tmp
    }

    def filter(f: RichCell => Boolean): Seq[RichCell] = foreach { cell =>
      if (f(cell)) Some(cell) else None
    }.flatten

    def find(f: RichCell => Boolean): Option[RichCell] = {
      for (rowNum <- sheet.getFirstRowNum to sheet.getLastRowNum) {
        val row = sheet.getRow(rowNum)
        if (row != null) {
          for (cellNum <- row.getFirstCellNum to row.getLastCellNum) {
            val cell = row.getCell(cellNum)
            if (cell != null) {
              if (f(cell)) {
                return Some(cell)
              }
            }
          }
        }
      }
      None
    }

  }
}