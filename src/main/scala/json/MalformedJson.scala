package json

import java.io.IOException

private[json] case class MalformedJson(message: String) extends Exception(message){}

object MalformedJson
{

  def jsObjectExpected(json: String): MalformedJson = MalformedJson(s"A Json object was expected. Received: $json")

  def jsArrayExpected(json: String): MalformedJson = MalformedJson(s"A Json array was expected. Received: $json")

  def errorWhileParsing(json: String,
                        ex: IOException
                       ) = MalformedJson(
    s"""${ex.getMessage}
       |while parsing $json
       |""".stripMargin
    )
}
