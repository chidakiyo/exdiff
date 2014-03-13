package util

import scala.collection.immutable.Map

object Keys {

  object XlsType {
    val HSSF_XLS = "application/vnd.ms-excel"
    val XSSF_XLS = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
  }

  object CellType {
    val DIFF = "diff"
    val EQUAL = "equal"
  }

  object HttpParam {
    val SRC_FILE = "srcFile"
    val DST_FILE = "dstFile"
  }

  object SheetMap {
    val SHEET_MAP = Map(
      "1" -> 1,
      "2" -> 2,
      "3" -> 3,
      "4" -> 4,
      "5" -> 5,
      "6" -> 6,
      "7" -> 7,
      "8" -> 8,
      "9" -> 9)
  }

}