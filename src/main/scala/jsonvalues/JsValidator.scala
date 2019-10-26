package jsonvalues

class JsValidator[T <: Json[T]]
{

  def apply(pairs: (JsPath, (T, JsValue) => JsPairValidationResult)*): T => JsValidationResult =
  {
    json =>
    {

      @scala.annotation.tailrec
      def apply(errors: Seq[JsPairError],
                pairs: (JsPath, (T, JsValue) => JsPairValidationResult)*
               ): Seq[JsPairError] =
      {
        if (pairs.isEmpty) return errors

        val (path, validation) = pairs.head

        validation.apply(json,
                         json(path)
                         ) match
        {
          case error: JsPairError => apply(errors :+ error,
                                           pairs.tail: _*
                                           )
        }

      }

      val errors: Seq[JsPairError] = apply(Vector.empty,
                                           pairs: _*
                                           )

      if (errors.isEmpty) ValidationSuccess() else ValidationFailure(errors)


    }

  }

}
