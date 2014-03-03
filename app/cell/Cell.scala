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
      case c: Diff => s"L: $left : R:$right"
    }
  }

  private def leftOrRight(left: PCell, right: PCell)(f: PCell => Int): Int = {
    List(left, right) find (_ != null) map (f(_)) getOrElse (0) // TODO getOrElseは仮置き
  }

}

case class Diff(left: PCell, right: PCell, typename: String = DIFF) extends Cell

case class Eq(left: PCell, right: PCell, typename: String = EQUAL) extends Cell

object Cell {

  def diff(left: PCell, right: PCell): cell.Cell = {
    (left.text == right.text) match {
      case true => Eq(left, right)
      case false => Diff(left, right)
    }

  }
}