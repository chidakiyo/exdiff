package util

object ControlUtil {

  def defining[A, B](value: A)(f: A => B): B = f(value)
}