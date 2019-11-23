package value

import java.io.IOException

private[value] case class MalformedJson(message: String) extends Exception(message)
{}

object MalformedJson
{

  private[value] def jsObjectExpected(json: String): MalformedJson = MalformedJson(s"A Json object was expected. Received: $json")

  private[value] def jsArrayExpected(json: String): MalformedJson = MalformedJson(s"A Json array was expected. Received: $json")

  private[value] def errorWhileParsing(json: String,
                                       ex  : IOException
                                      ) = MalformedJson(
    s"""${ex.getMessage}
       |while parsing $json
       |""".stripMargin
    )
}
