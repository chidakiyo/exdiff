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
      "1" -> 0,
      "2" -> 1,
      "3" -> 2,
      "4" -> 3,
      "5" -> 4,
      "6" -> 5,
      "7" -> 6,
      "8" -> 7,
      "9" -> 8)
  }

}