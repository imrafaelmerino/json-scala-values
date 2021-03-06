package json.value

import java.util.Objects.requireNonNull
import scala.collection.immutable
import scala.collection.immutable.HashMap

/**
 * abstract class to reduce class file size in subclass.
 *
 * @param bindings the map of key and values
 */
private[json] abstract class AbstractJsObj(private[json] val bindings: immutable.Map[String, JsValue])
{
  /** Throws an UserError exception
   *
   * @return Throws an UserError exception
   */
  def toJsArray: JsArray = throw UserError.toJsArrayOfJsObj

  /**
   * returns true if this is an object
   *
   * @return
   */
  def isObj: Boolean = true

  /**
   * returns true if this is an array
   *
   * @return
   */
  def isArr: Boolean = false

  /** Tests whether this json object contains a binding for a key.
   *
   * @param key the key
   * @return `true` if there is a binding for `key` in this map, `false` otherwise.
   */
  def containsKey(key: String): Boolean = bindings.contains(requireNonNull(key))


  /** Tests whether the Json object is empty.
   *
   * @return `true` if the Json object contains no elements, `false` otherwise.
   */
  def isEmpty: Boolean = bindings.isEmpty

  /** Selects the next element of the [[iterator]] of this Json object, throwing a
   * NoSuchElementException if the Json object is empty
   *
   * @return the next element of the [[iterator]] of this Json object.
   */
  def head: (String, JsValue) = bindings.head

  /** Optionally selects the next element of the [[iterator]] of this Json object.
   *
   * @return the first element of this Json object if it is nonempty.
   *         `None` if it is empty.
   */
  def headOption: Option[(String, JsValue)] = bindings.headOption

  /** Selects the last element of the iterator of this Json object, throwing a
   * NoSuchElementException if the Json object is empty
   *
   * @return the last element of the iterator of this Json object.
   */
  def last: (String, JsValue) = bindings.last

  /** Optionally selects the last element of the iterator of this Json object.
   *
   * @return the last element of the iterator of this Json object,
   *         `None` if it is empty.
   */
  def lastOption: Option[(String, JsValue)] = bindings.lastOption

  /** Collects all keys of this Json object in an iterable collection.
   *
   * @return the keys of this Json object as an iterable.
   */
  def keys: Iterable[String] = bindings.keys

  /** Retrieves the json.value which is associated with the given key. If there is no mapping
   * from the given key to a json.value, `JsNothing` is returned.
   *
   * @param  key the key
   * @return the json.value associated with the given key
   */
  def apply(key: String): JsValue = apply(Key(requireNonNull(key)))

  private[json] def apply(pos: Position): JsValue =
  {
    requireNonNull(pos) match
    {
      case Key(name) => bindings.applyOrElse(name,
                                        (_: String) => JsNothing
                                             )
      case Index(_) => JsNothing
    }
  }

  /** The size of this Json object.
   * *
   *
   * @return the number of elements in this Json object.
   */
  def size: Int = bindings.size

  /** Collects all keys of this map in a set.
   *
   * @return a set containing all keys of this map.
   */
  def keySet: Set[String] = bindings.keySet


  def init: JsObj = JsObj(bindings.init)

  def tail: JsObj = JsObj(bindings.tail)

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

  def filter(p: (String, JsValue) => Boolean): JsObj = JsObj(bindings.filter(p.tupled))

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
         ): JsObj = JsObj(bindings.map[String, JsValue](pair => if (p(pair._1, pair._2)) (pair._1, m(pair._1, pair._2)) else pair))

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
             ): JsObj = JsObj(bindings.map[String,JsValue](pair=>if(p(pair._1,pair._2)) (m(pair._1,pair._2),pair._2) else pair))

  def mapAllKeys(m: String => String): JsObj =
    JsObj(AbstractJsObj.mapKey(bindings,
                               HashMap.empty,
                               requireNonNull(m)
                               )
          )

  /** Returns an iterator of this Json object. Can be used only once
   *
   * @return an iterator
   */
  def iterator: Iterator[(String, JsValue)] = bindings.iterator

  /** Flatten this Json object into a `LazyList` of pairs of `(JsPath,JsValue)`
   * traversing recursively every noe-empty Json found along the way.
   *
   * @return a `LazyList` of pairs of `JsPath` and `JsValue`
   * */
  def flatten: LazyList[(JsPath, JsValue)] = AbstractJsObj.flatten(JsPath.empty,
                                                                   bindings
                                                                   )
}

private[json] object AbstractJsObj
{
  private[json] def flatten(path: JsPath,
                             map : immutable.Map[String, JsValue]
                            ): LazyList[(JsPath, JsValue)] =
  {
    if (map.isEmpty) return LazyList.empty
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

  private[json] def map(input : immutable.Map[String, JsValue],
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


  private[json] def filterJsObj(path  : JsPath,
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

  private[json] def filterJsObj(input : immutable.Map[String, JsValue],
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

  private[json] def map(path  : JsPath,
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


  private[json] def mapKey(path  : JsPath,
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

  private[json] def mapKey(input : immutable.Map[String, JsValue],
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
                                                        JsArray(AbstractJsArray.mapKey(arr.seq,
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


  private[json] def filterKey(path  : JsPath,
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
                                                                                                      arr.seq,
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


  private[json] def filterKey(input : immutable.Map[String, JsValue],
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
                                                                    JsArray(AbstractJsArray.filterKey(arr.seq,
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

  private[json] def reduce[V](path : JsPath,
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


  private[json] def filter(input : immutable.Map[String, JsValue],
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

  private[json] def filter(path  : JsPath,
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
