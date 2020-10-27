package json.value

import java.io.IOException

/**
 * Exception thrown when parsing an input stream, string or sequence of bytes into a json and either
 * the json is not well-formed or some unexpected exception occurs processing an input stream.
 * This is a true exception and not an error, so it must be wrapped into a Try computation.
 *
 * @param message the exception message
 */
private[json] case class InvalidJson(message: String) extends Exception(message)
{}

object InvalidJson
{

  /**
   * returns an exception pointing out that a Json object was expected instead of a
   * Json array
   *
   * @return a MalformedJson exception
   */
  def jsObjectExpected = InvalidJson(s"Json object expected. First character: [")

  /**
   * returns an exception pointing out that a Json array was expected instead of a
   * Json object
   *
   * @return a MalformedJson exception
   */
  def jsArrayExpected = InvalidJson(s"Json array expected. First character: {")


  /**
   * returns an exception if some error occurs while parsing a string into
   * a Json
   *
   * @param ex exception that took place while parsing the string
   * @return a MalformedJson exception
   */
  def errorWhileParsing(json: String,
                        ex  : IOException
                       ) = InvalidJson(s"""${ex.getMessage}|while parsing $json""".stripMargin)


  /**
   * returns an exception if some error occurs while parsing a sequence of bytes into
   * a Json
   *
   * @param ex exception that took place while parsing the sequence of bytes
   * @return a MalformedJson exception
   */
  def errorWhileParsing(bytes: Array[Byte],
                        ex   : IOException
                       ) = InvalidJson(ex.getMessage + " while parsing " + bytes)
}