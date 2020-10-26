package json.value

import java.util.Objects.requireNonNull

import json.value.{AbstractJsArray, AbstractJsObj}

import scala.collection.immutable
import scala.collection.immutable.HashMap

/**
 * abstract class to reduce class file size in subclass.
 *
 * @param bindings the map of key and values
 */
abstract class AbstractJsObj(private[value] val bindings: immutable.Map[String, JsValue])
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
  def containsKey(key: String): Boolean = bindings contains requireNonNull(key)


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
   * @param key the key
   * @return the json.value associated with the given key
   */
  def apply(key: String): JsValue = apply(Key(requireNonNull(key)))

  private[value] def apply(pos: Position): JsValue =
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
    JsObj(AbstractJsObjFns.filter(JsPath.empty,
                                  bindings,
                                  HashMap.empty,
                                  requireNonNull(p)
                                  )
          )

  def filter(p: (String, JsValue) => Boolean): JsObj = JsObj(bindings.filter(p.tupled))

  def filterAll(p: JsPrimitive => Boolean): JsObj = JsObj(AbstractJsObjFns.filter(bindings,
                                                                                  HashMap.empty,
                                                                                  requireNonNull(p)
                                                                                  )
                                                          )


  def filterAllJsObj(p: (JsPath, JsObj) => Boolean): JsObj = JsObj(AbstractJsObjFns.filterJsObj(JsPath.empty,
                                                                                                bindings,
                                                                                                HashMap.empty,
                                                                                                requireNonNull(p)
                                                                                                )
                                                                   )

  def filterAllJsObj(p: JsObj => Boolean): JsObj =
    JsObj(AbstractJsObjFns.filterJsObj(bindings,
                                       HashMap.empty,
                                       requireNonNull(p)
                                       )
          )


  def filterAllKeys(p: (JsPath, JsValue) => Boolean): JsObj =
    JsObj(AbstractJsObjFns.filterKey(JsPath.empty,
                                     bindings,
                                     HashMap.empty,
                                     requireNonNull(p)
                                     )
          )

  def filterAllKeys(p: String => Boolean): JsObj =
    JsObj(AbstractJsObjFns.filterKey(bindings,
                                     HashMap.empty,
                                     requireNonNull(p)
                                     )
          )

  def mapAll(m: JsPrimitive => JsValue): JsObj =
    JsObj(AbstractJsObjFns.map(this.bindings,
                               HashMap.empty,
                               requireNonNull(m)
                               )
          )

  def mapAll(m: (JsPath, JsPrimitive) => JsValue,
             p: (JsPath, JsPrimitive) => Boolean = (_, _) => true
            ): JsObj = JsObj(AbstractJsObjFns.map(JsPath.empty,
                                                  this.bindings,
                                                  HashMap.empty,
                                                  requireNonNull(m),
                                                  requireNonNull(p)
                                                  )
                             )

  def map(m: (String, JsValue) => JsValue,
          p: (String, JsValue) => Boolean = (_, _) => true
         ): JsObj = JsObj(bindings.map[String, JsValue](pair => if p(pair._1,
                                                                     pair._2
                                                                     ) then (pair._1, m(pair._1,
                                                                                        pair._2
                                                                                        )) else pair
                                                        )
                          )

  def reduceAll[V](p: (JsPath, JsPrimitive) => Boolean = (_, _) => true,
                   m: (JsPath, JsPrimitive) => V,
                   r: (V, V) => V
                  ): Option[V] =
  {
    AbstractJsObjFns.reduce(JsPath.empty,
                            bindings,
                            Option.empty,
                            p,
                            m,
                            r
                            )
  }


  def mapAllKeys(m: (JsPath, JsValue) => String,
                 p: (JsPath, JsValue) => Boolean = (_, _) => true
                ): JsObj =
  {
    JsObj(AbstractJsObjFns.mapKey(JsPath.empty,
                                  bindings,
                                  HashMap.empty,
                                  requireNonNull(m),
                                  requireNonNull(p)
                                  )
          )
  }

  def mapKeys(m: (String, JsValue) => String,
              p: (String, JsValue) => Boolean = (_, _) => true
             ): JsObj =
  {
    JsObj(bindings.map[String, JsValue](pair =>
                                        {
                                          if p(pair._1,
                                               pair._2
                                               )
                                          then (m(pair._1,
                                                  pair._2
                                                  ), pair._2)
                                          else pair
                                          
                                        }
                                        )
          )
  }

  def mapAllKeys(m: String => String): JsObj =
    JsObj(AbstractJsObjFns.mapKey(bindings,
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
  def flatten: LazyList[(JsPath, JsValue)] = AbstractJsObjFns.flatten(JsPath.empty,
                                                                      bindings
                                                                      )
}


