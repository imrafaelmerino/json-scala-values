package json.value

object AbstractJson
{

  def reduceHead[V](r: (V, V) => V,
                    acc             : Option[V],
                    head            : V
                   ): Option[V] =
  {
    acc match
    {
      case Some(accumulated) => Some(r(accumulated,
                                       head
                                       )
                                     )
      case None => Some(head)
    }
  }

  def reduceHead[V](r: (V, V) => V,
                    acc             : Option[V],
                    headOption      : Option[V]
                   ): Option[V] =
  {
    acc match
    {
      case Some(accumulated) => headOption match
      {
        case Some(head) => Some(r(accumulated,
                                  head
                                  )
                                )
        case None => Some(accumulated)
      }
      case None => headOption
    }
  }


}
