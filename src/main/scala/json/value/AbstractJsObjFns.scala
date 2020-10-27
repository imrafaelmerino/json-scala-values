package json.value

import scala.collection.immutable
import scala.collection.immutable.HashMap

private[json] object AbstractJsObjFns
{
  def flatten(path: JsPath,
              map                : immutable.Map[String, JsValue]
             ): LazyList[(JsPath, JsValue)] =
  {
    if map.isEmpty then return LazyList.empty
    val head = map.head
    head._2 match
    {
      case JsObj(headMap) =>
      {
        if headMap.isEmpty
        then (path / head._1,
               JsObj.empty
             ) +: flatten(path,
                          map.tail
                          )
        else flatten(path / head._1,
                     headMap
                     ) ++: flatten(path,
                                   map.tail
                                   )
      }
      case JsArray(headSeq) =>
      {
        if headSeq.isEmpty
        then (path / head._1,
               JsArray.empty
             ) +: flatten(path,
                          map.tail
                          )
        else AbstractJsArrayFns.flatten(path / head._1 / -1,
                                        headSeq
                                        ) ++: flatten(path,
                                                      map.tail
                                                      )
      }
      case _ => (path / head._1, head._2) +: flatten(path,
                                                     map.tail
                                                     )
    }

  }

  def map(input: immutable.Map[String, JsValue],
          result: immutable.Map[String, JsValue],
          m                    : JsPrimitive => JsValue
         ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty then result
    else input.head match
    {
      case (key, JsObj(headMap)) => map(input.tail,
                                        result.updated(key,
                                                       JsObj(map(headMap,
                                                                 HashMap.empty,
                                                                 m
                                                                 )
                                                             )
                                                       ),
                                        m
                                        )
      case (key, JsArray(headSeq)) => map(input.tail,
                                          result.updated(key,
                                                         JsArray(AbstractJsArrayFns.map(headSeq,
                                                                                        Vector.empty,
                                                                                        m
                                                                                        )
                                                                 )
                                                         ),
                                          m
                                          )
      case (key, head: JsPrimitive) => map(input.tail,
                                           result.updated(key,
                                                          m(head)
                                                          ),
                                           m
                                           )
      case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                 "AbstractJsObj.map"
                                                                 )
    }
  }

  def filterJsObj(path: JsPath,
                  input                : immutable.Map[String, JsValue],
                  result               : immutable.Map[String, JsValue],
                  p                    : (JsPath, JsObj) => Boolean
                 ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty then result
    else input.head match
    {
      case (key, o: JsObj) =>
      {
        if p(path / key,
             o
             ) then filterJsObj(path,
                                input.tail,
                                result.updated(key,
                                               JsObj(filterJsObj(path / key,
                                                                 o.bindings,
                                                                 HashMap.empty,
                                                                 p
                                                                 )
                                                     )
                                               ),
                                p
                                ) else filterJsObj(path,
                                                   input.tail,
                                                   result,
                                                   p
                                                   )
      }
      case (key, JsArray(headSeq)) => filterJsObj(path,
                                                  input.tail,
                                                  result.updated(key,
                                                                 JsArray(AbstractJsArrayFns.filterJsObj(path / key / -1,
                                                                                                        headSeq,
                                                                                                        Vector.empty,
                                                                                                        p
                                                                                                        )
                                                                         )
                                                                 ),
                                                  p
                                                  )
      case (key, head: JsValue) => filterJsObj(path,
                                               input.tail,
                                               result.updated(key,
                                                              head
                                                              ),
                                               p
                                               )
    }
  }

  def filterJsObj(input: immutable.Map[String, JsValue],
                  result               : immutable.Map[String, JsValue],
                  p                    : JsObj => Boolean
                 ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, o: JsObj) => if p(o) then filterJsObj(input.tail,
                                                       result.updated(key,
                                                                      JsObj(filterJsObj(o.bindings,
                                                                                        HashMap.empty,
                                                                                        p
                                                                                        )
                                                                            )
                                                                      ),
                                                       p
                                                       ) else filterJsObj(input.tail,
                                                                          result,
                                                                          p
                                                                          )

      case (key, JsArray(headSeq)) => filterJsObj(input.tail,
                                                  result.updated(key,
                                                                 JsArray(AbstractJsArrayFns.filterJsObj(headSeq,
                                                                                                        Vector.empty,
                                                                                                        p
                                                                                                        )
                                                                         )
                                                                 ),
                                                  p
                                                  )

      case (key, head: JsValue) => filterJsObj(input.tail,
                                               result.updated(key,
                                                              head
                                                              ),
                                               p
                                               )
    }
  }

  def map(path: JsPath,
          input: immutable.Map[String, JsValue],
          result: immutable.Map[String, JsValue],
          m: (JsPath, JsPrimitive) => JsValue,
          p: (JsPath, JsPrimitive) => Boolean
         ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, JsObj(headMap)) => map(path,
                                        input.tail,
                                        result.updated(key,
                                                       JsObj(map(path / key,
                                                                 headMap,
                                                                 HashMap.empty,
                                                                 m,
                                                                 p
                                                                 )
                                                             )
                                                       )
                                        ,
                                        m,
                                        p
                                        )
      case (key, JsArray(headSeq)) => map(path,
                                          input.tail,
                                          result.updated(key,
                                                         JsArray(AbstractJsArrayFns.map(path / key / -1,
                                                                                        headSeq,
                                                                                        Vector.empty,
                                                                                        m,
                                                                                        p
                                                                                        )
                                                                 )
                                                         ),
                                          m,
                                          p
                                          )
      case (key, head: JsPrimitive) =>
      {
        val headPath = path / key
        if p(headPath,
             head
             )
        then map(path,
                 input.tail,
                 result.updated(key,
                                m(headPath,
                                  head
                                  )
                                ),
                 m,
                 p
                 )
        else map(path,
                 input.tail,
                 result.updated(key,
                                head
                                ),
                 m,
                 p
                 )
      }
      case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                 "AbstractJsObj.map"
                                                                 )
    }

  }

  def mapKey(path                 : JsPath,
             input: immutable.Map[String, JsValue],
             result               : immutable.Map[String, JsValue],
             m                    : (JsPath, JsValue) => String,
             p                    : (JsPath, JsValue) => Boolean
            ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, o: JsObj) =>
      {
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if p(headPath,
                                   o
                                   )
                              then m(headPath,
                                     o
                                     )
                              else key,
                              JsObj(mapKey(headPath,
                                           o.bindings,
                                           HashMap.empty,
                                           m,
                                           p
                                           )
                                    )
                              ),
               m,
               p
               )
      }
      case (key, arr: JsArray) =>
      {
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if p(headPath,
                                   arr
                                   )
                              then m(headPath,
                                     arr
                                     )
                              else key,
                              JsArray(AbstractJsArrayFns.mapKey(path / key / -1,
                                                                arr.seq,
                                                                Vector.empty,
                                                                m,
                                                                p
                                                                )
                                      )
                              ),
               m,
               p
               )
      }
      case (key, head: JsValue) =>
      {
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if p(headPath,
                                   head
                                   )
                              then m(headPath,
                                     head
                                     )
                              else key,
                              head
                              ),
               m,
               p
               )
      }
    }
  }

  def mapKey(input                : immutable.Map[String, JsValue],
             result               : immutable.Map[String, JsValue],
             m                    : String => String
            ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, o: JsObj) => mapKey(input.tail,
                                     result.updated(m(key),
                                                    JsObj(mapKey(o.bindings,
                                                                 HashMap.empty,
                                                                 m
                                                                 )
                                                          )
                                                    ),
                                     m
                                     )

      case (key, arr: JsArray) => mapKey(input.tail,
                                         result.updated(m(key),
                                                        JsArray(AbstractJsArrayFns.mapKey(arr.seq,
                                                                                          Vector.empty,
                                                                                          m
                                                                                          )
                                                                )
                                                        ),
                                         m
                                         )

      case (key, head: JsValue) => mapKey(input.tail,
                                          result.updated(m(key),
                                                         head
                                                         ),
                                          m
                                          )

    }
  }

  def filterKey(path: JsPath,
                input: immutable.Map[String, JsValue],
                result: immutable.Map[String, JsValue],
                p: (JsPath, JsValue) => Boolean
               ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, o: JsObj) =>
      {
        if p(path / key,
             o
             )
        then filterKey(path,
                       input.tail,
                       result.updated(key,
                                      JsObj(filterKey(path / key,
                                                      o.bindings,
                                                      HashMap.empty,
                                                      p
                                                      )
                                            )
                                      ),
                       p
                       )
        else filterKey(path,
                       input.tail,
                       result,
                       p
                       )
      }
      case (key, arr: JsArray) =>
      {
        if p(path / key,
             arr
             )
        then filterKey(path,
                       input.tail,
                       result.updated(key,
                                      JsArray(AbstractJsArrayFns.filterKey(path / key / -1,
                                                                           arr.seq,
                                                                           Vector.empty,
                                                                           p
                                                                           )
                                              )
                                      ),
                       p
                       )
        else filterKey(path,
                       input.tail,
                       result,
                       p
                       )
      }
      case (key, head: JsValue) =>
      {
        if p(path / key,
             head
             )
        then filterKey(path,
                       input.tail,
                       result.updated(key,
                                      head
                                      ),
                       p
                       )
        else filterKey(path,
                       input.tail,
                       result,
                       p
                       )
      }

    }
  }

  def filterKey(input                 : immutable.Map[String, JsValue],
                result                : immutable.Map[String, JsValue],
                p                     : String => Boolean
               ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, o: JsObj) =>
      {
        if (p(key)) filterKey(input.tail,
                              result.updated(key,
                                             JsObj(filterKey(o.bindings,
                                                             HashMap.empty,
                                                             p
                                                             )
                                                   )
                                             ),
                              p
                              ) else filterKey(input.tail,
                                               result,
                                               p
                                               )
      }
      case (key, arr: JsArray) =>
      {
        if (p(key)) filterKey(input.tail,
                              result.updated(key,
                                             JsArray(AbstractJsArrayFns.filterKey(arr.seq,
                                                                                  Vector.empty,
                                                                                  p
                                                                                  )
                                                     )
                                             ),
                              p
                              ) else filterKey(input.tail,
                                               result,
                                               p
                                               )
      }
      case (key, head: JsValue) =>
      {
        if (p(key
              )) filterKey(input.tail,
                           result.updated(key,
                                          head
                                          ),
                           p
                           ) else filterKey(input.tail,
                                            result,
                                            p
                                            )
      }
    }
  }

  def reduce[V](path: JsPath,
                input: immutable.Map[String, JsValue],
                acc             : Option[V],
                p: (JsPath, JsPrimitive) => Boolean,
                m               : (JsPath, JsPrimitive) => V,
                r               : (V, V) => V
               ): Option[V] =
  {
    if input.isEmpty
    then acc
    else
    {
      val (key, head): (String, JsValue) = input.head
      head match
      {
        case JsObj(headMap) => reduce(path,
                                      input.tail,
                                      Functions.reduceHead(r,
                                                           acc,
                                                           reduce[V](path / key,
                                                                     headMap,
                                                                     Option.empty,
                                                                     p,
                                                                     m,
                                                                     r
                                                                     )
                                                           ),
                                      p,
                                      m,
                                      r
                                      )
        case JsArray(headSeq) => reduce(path,
                                        input.tail,
                                        Functions.reduceHead(r,
                                                             acc,
                                                             AbstractJsArrayFns.reduce(path / key / -1,
                                                                                       headSeq,
                                                                                       Option.empty,
                                                                                       p,
                                                                                       m,
                                                                                       r
                                                                                       )
                                                             ),
                                        p,
                                        m,
                                        r
                                        )
        case value: JsPrimitive =>
        {
          if p(path / key,
               value
               )
          then reduce(path,
                      input.tail,
                      Functions.reduceHead(r,
                                           acc,
                                           m(path / key,
                                             value
                                             )
                                           ),
                      p,
                      m,
                      r
                      )
          else reduce(path,
                      input.tail,
                      acc,
                      p,
                      m,
                      r
                      )
        }
        case other =>
        {
          throw InternalError.typeNotExpectedInMatcher(other,
                                                       "AbstractJsObj.reduce"
                                                       )
        }
      }
    }
  }


  def filter(input                : immutable.Map[String, JsValue],
             result               : immutable.Map[String, JsValue],
             p                    : JsPrimitive => Boolean
            ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, JsObj(headMap)) => filter(input.tail,
                                           result.updated(key,
                                                          JsObj(filter(headMap,
                                                                       HashMap.empty,
                                                                       p
                                                                       )
                                                                )
                                                          ),
                                           p
                                           )
      case (key, JsArray(headSeq)) => filter(input.tail,
                                             result.updated(key,
                                                            JsArray(AbstractJsArrayFns.filter(headSeq,
                                                                                              Vector.empty,
                                                                                              p
                                                                                              )
                                                                    )
                                                            ),
                                             p
                                             )
      case (key, head: JsPrimitive) =>
      {
        if p(head)
        then filter(input.tail,
                    result.updated(key,
                                   head
                                   ),
                    p
                    )
        else filter(input.tail,
                    result,
                    p
                    )
      }
      case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                 "AbstractJsObj.filter"
                                                                 )
    }
  }

  def filter(path             : JsPath,
             input            : immutable.Map[String, JsValue],
             result           : immutable.Map[String, JsValue],
             p                : (JsPath, JsPrimitive) => Boolean
            ): immutable.Map[String, JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case (key, JsObj(headMap)) =>
      {
        filter(path,
               input.tail,
               result.updated(key,
                              JsObj(filter(path / key,
                                           headMap,
                                           HashMap.empty,
                                           p
                                           )
                                    )
                              ),
               p
               )
      }
      case (key, JsArray(headSeq)) =>
      {
        filter(path,
               input.tail,
               result.updated(key,
                              JsArray(AbstractJsArrayFns.filter(path / key / -1,
                                                                headSeq,
                                                                Vector.empty,
                                                                p
                                                                )
                                      )
                              ),
               p
               )
      }
      case (key, head: JsPrimitive) =>
      {
        if p(path / key,
             head
             )
        then filter(path,
                    input.tail,
                    result.updated(key,
                                   head
                                   ),
                    p
                    )
        else filter(path,
                    input.tail,
                    result,
                    p
                    )
      }
      case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                 "AbstractJsObj.filter"
                                                                 )
    }
  }
}
