package poi4s

import org.apache.poi.ss.usermodel._

object Implicit {

  implicit def RichSheet(sheet: Sheet): RichSheet = new RichSheet(sheet)

  implicit def RichCell(cell: Cell): RichCell = new RichCell(cell)

}