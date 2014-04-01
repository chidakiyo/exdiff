package util

import org.apache.poi.ss.usermodel.Sheet
import collection.JavaConversions._

object MaxUtil {

  /**
   * sheetｓの最終行の最大値を取得する
   */
  def getLastRowNumMax(sheets: Sheet*): Int = {
    sheets filter (_ != null) map (_.getLastRowNum) max
  }

  /**
   * 複数のsheetsにおいて最大のcolumnを取得する
   */
  def getColumnNumMaxSheets(sheets: Sheet*): Int = {
    sheets filter (s => s != null) map (getColumnNumMax(_)) max
  }

  /**
   * 単体のsheetにおいて最大のcolumnを取得する
   */
  private def getColumnNumMax(sheet: Sheet): Int = {
    val cells = sheet.iterator.toStream map (_.getLastCellNum.toInt)
    if (cells != null && !cells.isEmpty) { cells max } else { 0 }
  }

}