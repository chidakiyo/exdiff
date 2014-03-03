package cell

import org.apache.poi.ss.usermodel.{ Cell => PCell }
import poi4s.Implicit._
import util.Keys.CellType._
import scala.collection.immutable.Map

abstract class Cell {

  val left: PCell
  val right: PCell

  def getLineNo(): Int = {
    leftOrRight(left, right)(_.rowNum)
  }

  def getCellNo(): Int = {
    leftOrRight(left, right)(_.getColumnIndex)
  }

  def getOutput(): String = {
    this match {
      case c: Eq => left.text
      case c: Diff => {
        val l = if (left != null) left.text else "-"
        val r = if (right != null) right.text else "-"
        s"◀: $l\n▶:$r"
      }
    }
  }

  private def leftOrRight(left: PCell, right: PCell)(f: PCell => Int): Int = {
    List(left, right) find (_ != null) map (f(_)) getOrElse (0) // TODO getOrElseは仮置き
  }

}

case class Diff(left: PCell, right: PCell, typename: String = DIFF) extends Cell

case class Eq(left: PCell, right: PCell, typename: String = EQUAL) extends Cell

object Cell {

  def diff(left: Option[PCell], right: Option[PCell]): cell.Cell = {
    (left, right) match {
      case (Some(l), Some(r)) => { println("1 " + l + " : " + r); if (l.text == r.text || (l.text == null || l.text.trim.isEmpty) && (r.text == null || r.text.trim.isEmpty)) Eq(l, r) else Diff(l, r) }
      case (Some(l), None) => { println(2); Diff(l, null) }
      case (None, Some(r)) => { println("3 " + r); Diff(null, r) }
      case (None, None) => { println(4); Diff(null, null) }
    }
  }
}