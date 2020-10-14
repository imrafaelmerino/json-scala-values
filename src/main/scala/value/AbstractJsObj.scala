package value

import java.util.Objects.requireNonNull
import scala.collection.immutable
import scala.collection.immutable.HashMap

/**
 * abstract class to reduce class file size in subclass.
 *
 * @param bindings the map of key and values
 */
private[value] abstract class AbstractJsObj(private[value] val bindings: immutable.Map[String, JsValue])
{

  /** Selects all elements of this Json object  which satisfy a predicate.
   *
   * @return a new Json object consisting of all elements of this Json object that satisfy the given predicate p. The order of the elements is preserved.
   */
  def filterAll(p: (JsPath, JsPrimitive) => Boolean): JsObj =
    JsObj(AbstractJsObj.filter(JsPath.empty,
                               bindings,
                               HashMap.empty,
                               requireNonNull(p)
                               )
          )


  def filterAll(p: JsPrimitive => Boolean): JsObj =
    JsObj(AbstractJsObj.filter(bindings,
                               HashMap.empty,
                               requireNonNull(p)
                               )
          )


  def filterAllJsObj(p: (JsPath, JsObj) => Boolean): JsObj =
    JsObj(AbstractJsObj.filterJsObj(JsPath.empty,
                                    bindings,
                                    HashMap.empty,
                                    requireNonNull(p)
                                    )
          )

  def filterAllJsObj(p: JsObj => Boolean): JsObj =
    JsObj(AbstractJsObj.filterJsObj(bindings,
                                    HashMap.empty,
                                    requireNonNull(p)
                                    )
          )


  def filterAllKeys(p: (JsPath, JsValue) => Boolean): JsObj =
    JsObj(AbstractJsObj.filterKey(JsPath.empty,
                                  bindings,
                                  HashMap.empty,
                                  requireNonNull(p)
                                  )
          )

  def filterAllKeys(p: String => Boolean): JsObj =
    JsObj(AbstractJsObj.filterKey(bindings,
                                  HashMap.empty,
                                  requireNonNull(p)
                                  )
          )

  def mapAll(m: JsPrimitive => JsValue): JsObj =
    JsObj(AbstractJsObj.map(this.bindings,
                            HashMap.empty,
                            requireNonNull(m)
                            )
          )

  def mapAll(m: (JsPath, JsPrimitive) => JsValue,
             p: (JsPath, JsPrimitive) => Boolean = (_, _) => true
            ): JsObj = JsObj(AbstractJsObj.map(JsPath.empty,
                                               this.bindings,
                                               HashMap.empty,
                                               requireNonNull(m),
                                               requireNonNull(p)
                                               )
                             )

  def map(m: (String, JsValue) => JsValue,
          p: (String, JsValue) => Boolean = (_, _) => true
         ): JsObj = {
    val tuples = bindings.map(pair => if (p(pair._1,
                                            pair._2
                                            )) (pair._1, m(pair._1,
                                                           pair._2
                                                           )) else pair
                              )
    JsObj(tuples)
  }

  def reduceAll[V](p: (JsPath, JsPrimitive) => Boolean = (_, _) => true,
                m: (JsPath, JsPrimitive) => V,
                r: (V, V) => V
               ): Option[V] = AbstractJsObj.reduce(JsPath.empty,
                                                   bindings,
                                                   requireNonNull(p),
                                                   requireNonNull(m),
                                                   requireNonNull(r),
                                                   Option.empty
                                                   )


  def mapAllKeys(m: (JsPath, JsValue) => String,
                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                ): JsObj = JsObj(AbstractJsObj.mapKey(JsPath.empty,
                                                      bindings,
                                                      HashMap.empty,
                                                      requireNonNull(m),
                                                      requireNonNull(p)
                                                      )
                                 )

  def mapKeys(m: (String, JsValue) => String,
              p: (String, JsValue) => Boolean = (_, _) => true
             ): JsObj = {
    val tuples = bindings.map(pair => if (p(pair._1,
                                            pair._2
                                            )) (m(pair._1,
                                                  pair._2
                                                  ), pair._2) else pair)
    JsObj(tuples)
  }

  def mapAllKeys(m: String => String): JsObj =
    JsObj(AbstractJsObj.mapKey(bindings,
                               HashMap.empty,
                               requireNonNull(m)
                               )
          )


}

private[value] object AbstractJsObj
{
  private[value] def flatten(path: JsPath,
                             map : immutable.Map[String, JsValue]
                            ): Stream[(JsPath, JsValue)] =
  {
    if (map.isEmpty) return Stream.empty
    val head = map.head

    head._2 match
    {
      case JsObj(headMap) =>
        if (headMap.isEmpty)
          (path / head._1, JsObj.empty) +: flatten(path,
                                                   map.tail
                                                   )
        else flatten(path / head._1,
                     headMap
                     ) ++: flatten(path,
                                   map.tail
                                   )
      case JsArray(headSeq) =>
        if (headSeq.isEmpty) (path / head._1, JsArray.empty) +: flatten(path,
                                                                        map.tail
                                                                        )
        else AbstractJsArray.flatten(path / head._1 / -1,
                                     headSeq
                                     ) ++: flatten(path,
                                                   map.tail
                                                   )
      case _ => (path / head._1, head._2) +: flatten(path,
                                                     map.tail
                                                     )

    }
  }

  private[value] def map(input : immutable.Map[String, JsValue],
                         result: immutable.Map[String, JsValue],
                         m     : JsPrimitive => JsValue
                        ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) =>
        map(input.tail,
            result.updated(key,
                           JsObj(map(headMap,
                                     HashMap.empty,
                                     m
                                     )
                                 )
                           ),
            m
            )
      case (key, JsArray(headSeq)) =>
        map(input.tail,
            result.updated(key,
                           JsArray(AbstractJsArray.map(headSeq,
                                                       Vector.empty,
                                                       m
                                                       )
                                   )
                           ),
            m
            )
      case (key, head: JsPrimitive) =>
        map(input.tail,
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


  private[value] def filterJsObj(path  : JsPath,
                                 input : immutable.Map[String, JsValue],
                                 result: immutable.Map[String, JsValue],
                                 p     : (JsPath, JsObj) => Boolean
                                ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) =>
        if (p(path / key,
              o
              )) filterJsObj(path,
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
      case (key, JsArray(headSeq)) =>
        filterJsObj(path,
                    input.tail,
                    result.updated(key,
                                   JsArray(AbstractJsArray.filterJsObj(path / key / -1,
                                                                       headSeq,
                                                                       Vector.empty,
                                                                       p
                                                                       )
                                           )
                                   ),
                    p
                    )
      case (key, head: JsValue) =>
        filterJsObj(path,
                    input.tail,
                    result.updated(key,
                                   head
                                   ),
                    p
                    )
    }
  }

  private[value] def filterJsObj(input : immutable.Map[String, JsValue],
                                 result: immutable.Map[String, JsValue],
                                 p     : JsObj => Boolean
                                ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(o
                                    )) filterJsObj(input.tail,
                                                   result.updated(key,
                                                                  JsObj(filterJsObj(
                                                                    o.bindings,
                                                                    HashMap.empty,
                                                                    p
                                                                    )
                                                                        )
                                                                  ),
                                                   p
                                                   ) else filterJsObj(
        input.tail,
        result,
        p
        )
      case (key, JsArray(headSeq)) => filterJsObj(input.tail,
                                                  result.updated(key,
                                                                 JsArray(AbstractJsArray.filterJsObj(
                                                                   headSeq,
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

  private[value] def map(path  : JsPath,
                         input : immutable.Map[String, JsValue],
                         result: immutable.Map[String, JsValue],
                         m     : (JsPath, JsPrimitive) => JsValue,
                         p     : (JsPath, JsPrimitive) => Boolean
                        ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
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
                                                       ),
                                        m,
                                        p
                                        )
      case (key, JsArray(headSeq)) => map(path,
                                          input.tail,
                                          result.updated(key,
                                                         JsArray(AbstractJsArray.map(path / key / -1,
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
        val headPath = path / key
        if (p(headPath,
              head
              )) map(path,
                     input.tail,
                     result.updated(key,
                                    m(headPath,
                                      head
                                      )
                                    ),
                     m,
                     p
                     ) else map(path,
                                input.tail,
                                result.updated(key,
                                               head
                                               ),
                                m,
                                p
                                )
      case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                 "AbstractJsObj.map"
                                                                 )

    }
  }


  private[value] def mapKey(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m     : (JsPath, JsValue) => String,
                            p     : (JsPath, JsValue) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) =>
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if (p(headPath,
                                    o
                                    )) m(headPath,
                                         o
                                         ) else key,
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
      case (key, arr: JsArray) =>
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if (p(headPath,
                                    arr
                                    )) m(headPath,
                                         arr
                                         ) else key,
                              JsArray(AbstractJsArray.mapKey(path / key / -1,
                                                             arr.values,
                                                             Vector.empty,
                                                             m,
                                                             p
                                                             )
                                      )
                              ),
               m,
               p
               )
      case (key, head: JsValue) =>
        val headPath = path / key
        mapKey(path,
               input.tail,
               result.updated(if (p(headPath,
                                    head
                                    )) m(headPath,
                                         head
                                         ) else key,
                              head
                              ),
               m,
               p
               )
    }

  }

  private[value] def mapKey(input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            m     : String => String
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
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
                                                        JsArray(AbstractJsArray.mapKey(arr.values,
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


  private[value] def filterKey(path  : JsPath,
                               input : immutable.Map[String, JsValue],
                               result: immutable.Map[String, JsValue],
                               p     : (JsPath, JsValue) => Boolean
                              ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(path / key,
                                    o
                                    )) filterKey(path,
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
                                                 ) else filterKey(path,
                                                                  input.tail,
                                                                  result,
                                                                  p
                                                                  )
      case (key, arr: JsArray) => if (p(path / key,
                                        arr
                                        )) filterKey(path,
                                                     input.tail,
                                                     result.updated(key,
                                                                    JsArray(AbstractJsArray.filterKey(path / key / -1,
                                                                                                      arr.values,
                                                                                                      Vector.empty,
                                                                                                      p
                                                                                                      )
                                                                            )
                                                                    ),
                                                     p
                                                     ) else filterKey(path,
                                                                      input.tail,
                                                                      result,
                                                                      p
                                                                      )
      case (key, head: JsValue) => if (p(path / key,
                                         head
                                         )) filterKey(path,
                                                      input.tail,
                                                      result.updated(key,
                                                                     head
                                                                     ),
                                                      p
                                                      ) else filterKey(path,
                                                                       input.tail,
                                                                       result,
                                                                       p
                                                                       )

    }
  }


  private[value] def filterKey(input : immutable.Map[String, JsValue],
                               result: immutable.Map[String, JsValue],
                               p     : String => Boolean
                              ): immutable.Map[String, JsValue]

  =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, o: JsObj) => if (p(key)) filterKey(input.tail,
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
      case (key, arr: JsArray) => if (p(key
                                        )) filterKey(input.tail,
                                                     result.updated(key,
                                                                    JsArray(AbstractJsArray.filterKey(arr.values,
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
      case (key, head: JsValue) => if (p(key
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

  private[value] def reduce[V](path : JsPath,
                               input: immutable.Map[String, JsValue],
                               p    : (JsPath, JsPrimitive) => Boolean,
                               m    : (JsPath, JsPrimitive) => V,
                               r    : (V, V) => V,
                               acc  : Option[V]
                              ): Option[V] =
  {

    if (input.isEmpty) acc
    else
    {
      val (key, head): (String, JsValue) = input.head
      head match
      {
        case JsObj(headMap) => reduce(path,
                                      input.tail,
                                      p,
                                      m,
                                      r,
                                      AbstractJson.reduceHead(r,
                                                              acc,
                                                              reduce(path / key,
                                                                     headMap,
                                                                     p,
                                                                     m,
                                                                     r,
                                                                     Option.empty
                                                                     )
                                                              )
                                      )
        case JsArray(headSeq) => reduce(path,
                                        input.tail,
                                        p,
                                        m,
                                        r,
                                        AbstractJson.reduceHead(r,
                                                                acc,
                                                                AbstractJsArray.reduce(path / key / -1,
                                                                                       headSeq,
                                                                                       p,
                                                                                       m,
                                                                                       r,
                                                                                       Option.empty
                                                                                       )
                                                                )
                                        )
        case value: JsPrimitive => if (p(path / key,
                                         value
                                         )) reduce(path,
                                                   input.tail,
                                                   p,
                                                   m,
                                                   r,
                                                   AbstractJson.reduceHead(r,
                                                                           acc,
                                                                           m(path / key,
                                                                             value
                                                                             )
                                                                           )
                                                   ) else reduce(path,
                                                                 input.tail,
                                                                 p,
                                                                 m,
                                                                 r,
                                                                 acc
                                                                 )
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsObj.reduce"
                                                                   )

      }
    }
  }


  private[value] def filter(input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            p     : JsPrimitive => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) =>
        filter(input.tail,
               result.updated(key,
                              JsObj(filter(headMap,
                                           HashMap.empty,
                                           p
                                           )
                                    )
                              ),
               p
               )
      case (key, JsArray(headSeq)) =>
        filter(input.tail,
               result.updated(key,
                              JsArray(AbstractJsArray.filter(headSeq,
                                                             Vector.empty,
                                                             p
                                                             )
                                      )
                              ),
               p
               )
      case (key, head: JsPrimitive) =>
        if (p(head
              )) filter(input.tail,
                        result.updated(key,
                                       head
                                       ),
                        p
                        ) else filter(input.tail,
                                      result,
                                      p
                                      )
      case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                 "AbstractJsObj.filter"
                                                                 )

    }
  }

  private[value] def filter(path  : JsPath,
                            input : immutable.Map[String, JsValue],
                            result: immutable.Map[String, JsValue],
                            p     : (JsPath, JsPrimitive) => Boolean
                           ): immutable.Map[String, JsValue] =
  {
    if (input.isEmpty) result
    else input.head match
    {
      case (key, JsObj(headMap)) =>
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
      case (key, JsArray(headSeq)) =>
        filter(path,
               input.tail,
               result.updated(key,
                              JsArray(AbstractJsArray.filter(path / key / -1,
                                                             headSeq,
                                                             Vector.empty,
                                                             p
                                                             )
                                      )
                              ),
               p
               )
      case (key, head: JsPrimitive) =>
        if (p(path / key,
              head
              )) filter(path,
                        input.tail,
                        result.updated(key,
                                       head
                                       ),
                        p
                        ) else filter(path,
                                      input.tail,
                                      result,
                                      p
                                      )
      case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                 "AbstractJsObj.filter"
                                                                 )

    }
  }
}
