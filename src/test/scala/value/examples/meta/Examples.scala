package value.examples.meta
object Examples

  inline def powerInlineConditional(x: Long, n: Int): Long =
     inline if n == 0 then 1L
     else inline if n % 2 == 1 then x * powerInlineConditional(x, n - 1)
     else
       val y: Long = x * x
       powerInlineConditional(y, n / 2)

  inline def power(x: Long,inline n: Int): Long =
     if n == 0 then 1L
     else if n % 2 == 1 then x * power(x, n - 1)
     else
      val y: Long = x * x
      power(y, n / 2)

  object Logger
    var indent = 0
    inline def log[T](msg: String)(thunk: => T): T =
      println(s"${"  " * indent}start $msg")
      indent += 1
      val result = thunk
      indent -= 1
      println(s"${"  " * indent}$msg = $result")
      result

  @main def main() = {
  Logger.log("123L^5"){power(123L, 5)}
}







