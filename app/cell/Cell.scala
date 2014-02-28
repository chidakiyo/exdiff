package cell

import org.apache.poi.ss.usermodel.{ Cell => PCell }
import poi4s.Implicit._
import scala.collection.immutable.Map

abstract class Cell {
  val DIFF = "diff"
  val EQUAL = "equal"

  val typename: String
  val left: PCell
  val right: PCell

  def getLineNo(): Int = {
    left match {
      case l => l.rowNum
      case _ => right match {
        case r => r.rowNum
        case _ => 0 // TODO
      }
    }
  }

  def getCellNo(): Int = {
    left match {
      case l => l.getColumnIndex
      case _ => right match {
        case r => r.getColumnIndex
        case _ => 0 // TODO
      }
    }
  }

}

case class Diff(left: PCell, right: PCell) extends Cell {
  val typename = DIFF
}

case class Eq(left: PCell, right: PCell) extends Cell {
  val typename = EQUAL
}

object Cell {

  def diff(left: PCell, right: PCell): cell.Cell = {
    (left.text == right.text) match {
      case true => Eq(left, right)
      case false => Diff(left, right)
    }

  }
}