package value

import java.io.IOException

private[value] case class MalformedJson(message: String) extends Exception(message)
{}

object MalformedJson
{

  def jsObjectExpected: MalformedJson = MalformedJson(s"Json object expected. First character: [")

  def jsArrayExpected: MalformedJson = MalformedJson(s"Json array expected. First character: {")


  def errorWhileParsingInputStream(ex: IOException
                                  ): MalformedJson = MalformedJson(
    s"""${ex.getMessage}
       |while parsing an input stream""".stripMargin
    )

  def errorWhileParsing(json: String,
                        ex  : IOException
                       ) = MalformedJson(
    s"""${ex.getMessage}
       |while parsing $json""".stripMargin
    )
}
