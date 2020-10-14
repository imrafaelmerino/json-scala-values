package value

import java.io.ByteArrayOutputStream

private object Functions
{


  /** Returns the string representation of this Json
   *
   * @return the string representation of this Json
   */
  def toString(json:Json[_]): String =
  {
    val baos = new ByteArrayOutputStream
    dslJson.serialize(json,
                      baos
                      )
    baos.toString("UTF-8")

  }

}
