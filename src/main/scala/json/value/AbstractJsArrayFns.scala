package json.value

import json.value.Functions.reduceHead
import json.value.JsPath.MINUS_ONE

import scala.collection.immutable
import scala.collection.immutable.HashMap

private[json] object AbstractJsArrayFns
{
  @scala.annotation.tailrec
  def concatSets(a: JsArray,
                 b: JsArray
                ): JsArray =
  {
    if b.isEmpty
    then a
    else
    {
      val head: JsValue = b.head
      if a.seq.contains(head)
      then concatSets(a,
                      b.tail
                      )
      else concatSets(a.appended(head),
                      b.tail
                      )
    }
  }

  def concatLists(a: JsArray,
                  b: JsArray
                 ): JsArray =
  {
    val asize: Int = a.size
    val bsize: Int = b.size
    if asize == bsize || asize > bsize
    then a
    else JsArray(a.seq.appendedAll(b.seq.dropRight(asize)))
  }

  def concatMultisets(a: JsArray,
                      b: JsArray
                     ): JsArray = JsArray(a.seq.appendedAll(b.seq))

  def flatten(path: JsPath,
              seq                : immutable.Seq[JsValue]
             ): LazyList[(JsPath, JsValue)] =
  {
    if seq.isEmpty then return LazyList.empty
    val head: JsValue = seq.head
    val headPath: JsPath = path.inc
    head match
    {
      case JsArray(headSeq) =>
      {
        if headSeq.isEmpty
        then (headPath, JsArray.empty) +: flatten(headPath,
                                                  seq.tail
                                                  )
        else flatten(headPath / MINUS_ONE,
                     headSeq
                     ) ++: flatten(headPath,
                                   seq.tail
                                   )
      }

      case JsObj(headMap) =>
      {
        if headMap.isEmpty
        then (headPath, JsObj.empty) +: flatten(headPath,
                                                seq.tail
                                                )
        else AbstractJsObjFns.flatten(headPath,
                                      headMap
                                      ) ++: flatten(headPath,
                                                    seq.tail
                                                    )
      }

      case _ => (headPath, head) +: flatten(headPath,
                                            seq.tail
                                            )
    }
  }

  def filterKey(input: immutable.Seq[JsValue],
                result              : immutable.Seq[JsValue],
                p                   : String => Boolean
               ): immutable.Seq[JsValue] =
  {
    if input.isEmpty
    then result
    else input.head match
    {
      case JsObj(headMap) => filterKey(input.tail,
                                       result.appended(JsObj(AbstractJsObjFns.filterKey(headMap,
                                                                                        immutable.HashMap.empty,
                                                                                        p
                                                                                        )
                                                             )

                                                       ),
                                       p
                                       )
      case JsArray(headSeq) => filterKey(input.tail,
                                         result.appended(JsArray(filterKey(headSeq,
                                                                           Vector.empty,
                                                                           p
                                                                           )
                                                                 )
                                                         ),
                                         p
                                         )
      case head: JsValue => filterKey(input.tail,
                                      result.appended(head),
                                      p
                                      )
    }
  }

  def remove(i                   : Int,
             seq                 : immutable.Seq[JsValue]
            ): immutable.Seq[JsValue] =
  {
    if seq.isEmpty then seq
    else if i >= seq.size then seq
    else if i == -1 then seq.init
    else if i == 0 then seq.tail
    else
    {
      val (prefix, suffix): (immutable.Seq[JsValue], immutable.Seq[JsValue]) = seq.splitAt(i)
      prefix.appendedAll(suffix.tail)
    }
  }

  def reduce[V](path: JsPath,
                input: immutable.Seq[JsValue],
                acc                   : Option[V],
                p: (JsPath, JsPrimitive) => Boolean,
                m                     : (JsPath, JsPrimitive) => V,
                r                     : (V, V) => V
               ): Option[V] =
  {
    if input.isEmpty then acc
    else
    {
      val headPath: JsPath = path.inc
      val head: JsValue = input.head
      head match
      {
        case JsObj(headMap) => reduce(headPath,
                                      input.tail,
                                      Functions.reduceHead(r,
                                                           acc,
                                                           AbstractJsObjFns.reduce(headPath,
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
        case JsArray(headSeq) =>
        {
          val a: Option[V] = reduce(headPath / MINUS_ONE,
                                    headSeq,
                                    Option.empty,
                                    p,
                                    m,
                                    r
                                    )
          reduce(headPath,
                 input.tail,
                 Functions.reduceHead(r,
                                      acc,
                                      a
                                      ),
                 p,
                 m,
                 r
                 )
        }

        case value: JsPrimitive =>
        {
          if p(headPath,
               value
               )
          then reduce(headPath,
                      input.tail,
                      Functions.reduceHead(r,
                                           acc,
                                           m(headPath,
                                             value
                                             )
                                           ),
                      p,
                      m,
                      r
                      )
          else reduce(headPath,
                      input.tail,
                      acc,
                      p,
                      m,
                      r
                      )
        }

        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.reduce"
                                                                   )
      }
    }
  }

  def filterJsObj(path: JsPath,
                  input                  : immutable.Seq[JsValue],
                  result                 : immutable.Seq[JsValue],
                  p                      : (JsPath, JsObj) => Boolean
                 ): immutable.Seq[JsValue] =
  {
    if (input.isEmpty) result
    else
      val headPath: JsPath = path.inc
      input.head match
      {
        case o: JsObj =>
        {
          if p(headPath,
               o
               )
          then filterJsObj(headPath,
                           input.tail,
                           result.appended(JsObj(AbstractJsObjFns.filterJsObj(headPath,
                                                                              o.bindings,
                                                                              HashMap.empty,
                                                                              p
                                                                              )
                                                 )
                                           ),
                           p
                           )
          else filterJsObj(headPath,
                           input.tail,
                           result,
                           p
                           )
        }
        case JsArray(headSeq) => filterJsObj(headPath,
                                             input.tail,
                                             result.appended(JsArray(filterJsObj(headPath / MINUS_ONE,
                                                                                 headSeq,
                                                                                 Vector.empty,
                                                                                 p
                                                                                 )
                                                                     )
                                                             ),
                                             p
                                             )

        case head: JsValue => filterJsObj(headPath,
                                          input.tail,
                                          result.appended(head),
                                          p
                                          )
      }
  }

  def filterJsObj(input                  : immutable.Seq[JsValue],
                  result                 : immutable.Seq[JsValue],
                  p                      : JsObj => Boolean
                 ): immutable.Seq[JsValue] =
  {
    if (input.isEmpty) result
    else
    {
      input.head match
      {
        case o: JsObj =>
        {
          if p(o)
          then filterJsObj(input.tail,
                           result.appended(JsObj(AbstractJsObjFns.filterJsObj(o.bindings,
                                                                              HashMap.empty,
                                                                              p
                                                                              )
                                                 )
                                           ),
                           p
                           )
          else filterJsObj(input.tail,
                           result,
                           p
                           )
        }
        case JsArray(headSeq) => filterJsObj(input.tail,
                                             result.appended(JsArray(filterJsObj(headSeq,
                                                                                 Vector.empty,
                                                                                 p
                                                                                 )
                                                                     )
                                                             ),
                                             p
                                             )
        case head: JsValue => filterJsObj(input.tail,
                                          result.appended(head),
                                          p
                                          )
      }
    }
  }


  def filter(path: JsPath,
             input: immutable.Seq[JsValue],
             result                 : immutable.Seq[JsValue],
             p                      : (JsPath, JsPrimitive) => Boolean
            ): immutable.Seq[JsValue] =
  {
    if input.isEmpty
    then result
    else
    {
      val headPath: JsPath = path.inc
      input.head match
      {
        case JsObj(headMap) => filter(headPath,
                                      input.tail,
                                      result.appended(JsObj(AbstractJsObjFns.filter(headPath,
                                                                                    headMap,
                                                                                    immutable.HashMap.empty,
                                                                                    p
                                                                                    )
                                                            )
                                                      ),
                                      p
                                      )
        case JsArray(headSeq) => filter(headPath,
                                        input.tail,
                                        result.appended(JsArray(filter(headPath / MINUS_ONE,
                                                                       headSeq,
                                                                       Vector.empty,
                                                                       p
                                                                       )
                                                                )
                                                        ),
                                        p
                                        )
        case head: JsPrimitive =>
        {
          if p(headPath,
               head
               )
          then filter(headPath,
                      input.tail,
                      result.appended(head),
                      p
                      )
          else filter(headPath,
                      input.tail,
                      result,
                      p
                      )
        }
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.filter"
                                                                   )
      }
    }
  }

  def filter(input: immutable.Seq[JsValue],
             result                  : immutable.Seq[JsValue],
             p                       : JsPrimitive => Boolean
            ): immutable.Seq[JsValue] =
  {
    if input.isEmpty
    then result
    else
      input.head match
      {
        case JsObj(headMap) => filter(input.tail,
                                      result.appended(JsObj(AbstractJsObjFns.filter(headMap,
                                                                                    immutable.HashMap.empty,
                                                                                    p
                                                                                    )
                                                            )
                                                      ),
                                      p
                                      )
        case JsArray(headSeq) => filter(input.tail,
                                        result.appended(JsArray(filter(headSeq,
                                                                       Vector.empty,
                                                                       p
                                                                       )
                                                                )
                                                        ),
                                        p
                                        )
        case head: JsPrimitive =>
        {
          if p(head)
          then filter(input.tail,
                      result.appended(head),
                      p
                      )
          else filter(input.tail,
                      result,
                      p
                      )
        }
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.filter"
                                                                   )
      }
  }

  def map(path: JsPath,
          input: immutable.Seq[JsValue],
          result                 : immutable.Seq[JsValue],
          m                      : (JsPath, JsPrimitive) => JsValue,
          p                      : (JsPath, JsPrimitive) => Boolean
         ): immutable.Seq[JsValue] =
  {
    if input.isEmpty
    then result
    else
      val headPath: JsPath = path.inc
      input.head match
      {
        case JsObj(headMap) => map(headPath,
                                   input.tail,
                                   result.appended(JsObj(AbstractJsObjFns.map(headPath,
                                                                              headMap,
                                                                              immutable.HashMap.empty,
                                                                              m,
                                                                              p
                                                                              )
                                                         )
                                                   ),
                                   m,
                                   p
                                   )
        case JsArray(headSeq) => map(headPath,
                                     input.tail,
                                     result.appended(JsArray(map(headPath / MINUS_ONE,
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
        case head: JsPrimitive =>
        {
          if p(headPath,
               head
               )
          then map(headPath,
                   input.tail,
                   result.appended(m(headPath,
                                     head
                                     )
                                   ),
                   m,
                   p
                   )
          else map(headPath,
                   input.tail,
                   result.appended(head),
                   m,
                   p
                   )
        }
        case other => throw InternalError.typeNotExpectedInMatcher(other,
                                                                   "AbstractJsArray.map"
                                                                   )
      }
  }

  def map(input                  : immutable.Seq[JsValue],
          result                 : immutable.Seq[JsValue],
          m                      : JsPrimitive => JsValue
         ): immutable.Seq[JsValue] =
  {
    if input.isEmpty
    then result
    else
      input.head match
      {
        case JsObj(headMap) => map(input.tail,
                                   result.appended(JsObj(AbstractJsObjFns.map(headMap,
                                                                              immutable.HashMap.empty,
                                                                              m
                                                                              )
                                                         )
                                                   ),
                                   m
                                   )
        case JsArray(headSeq) => map(input.tail,
                                     result.appended(JsArray(map(headSeq,
                                                                 Vector.empty,
                                                                 m
                                                                 )
                                                             )
                                                     ),
                                     m
                                     )

        case head: JsPrimitive => map(input.tail,
                                      result.appended(m(head)),
                                      m
                                      )
        case JsNothing => throw InternalError.typeNotExpectedInMatcher(JsNothing,
                                                                       "AbstractJsArray.map"
                                                                       )
      }
  }

  def mapKey(path: JsPath,
             input                  : immutable.Seq[JsValue],
             result                 : immutable.Seq[JsValue],
             m                      : (JsPath, JsValue) => String,
             p                      : (JsPath, JsValue) => Boolean
            ): immutable.Seq[JsValue] =
  {
    if input.isEmpty then result
    else
      val headPath: JsPath = path.inc
      input.head match
      {
        case JsObj(headMap) => mapKey(headPath,
                                      input.tail,
                                      result.appended(JsObj(AbstractJsObjFns.mapKey(headPath,
                                                                                    headMap,
                                                                                    immutable.HashMap.empty,
                                                                                    m,
                                                                                    p
                                                                                    )
                                                            )
                                                      ),
                                      m,
                                      p
                                      )
        case JsArray(headSeq) => mapKey(headPath,
                                        input.tail,
                                        result.appended(JsArray(mapKey(headPath / MINUS_ONE,
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
        case head: JsValue => mapKey(headPath,
                                     input.tail,
                                     result.appended(head),
                                     m,
                                     p
                                     )
      }
  }

  def mapKey(input                  : immutable.Seq[JsValue],
             result                 : immutable.Seq[JsValue],
             m                      : String => String
            ): immutable.Seq[JsValue] =
  {
    if input.isEmpty
    then result
    else
    {
      input.head match
      {
        case JsObj(headMap) => mapKey(input.tail,
                                      result.appended(JsObj(AbstractJsObjFns.mapKey(headMap,
                                                                                    immutable.HashMap.empty,
                                                                                    m
                                                                                    )
                                                            )
                                                      ),
                                      m
                                      )
        case JsArray(headSeq) => mapKey(input.tail,
                                        result.appended(JsArray(mapKey(headSeq,
                                                                       Vector.empty,
                                                                       m
                                                                       )
                                                                )
                                                        ),
                                        m
                                        )
        case head: JsValue => mapKey(input.tail,
                                     result.appended(head),
                                     m
                                     )
      }
    }
  }

  def filterKey(path                   : JsPath,
                input                  : immutable.Seq[JsValue],
                result                 : immutable.Seq[JsValue],
                p                      : (JsPath, JsValue) => Boolean
               ): immutable.Seq[JsValue] =
  {
    if input.isEmpty then result
    else
      val headPath: JsPath = path.inc
      input.head match
      {
        case JsObj(headMap) => filterKey(headPath,
                                         input.tail,
                                         result.appended(JsObj(AbstractJsObjFns.filterKey(headPath,
                                                                                          headMap,
                                                                                          immutable.HashMap.empty,
                                                                                          p
                                                                                          )
                                                               )
                                                         ),
                                         p
                                         )
        case JsArray(headSeq) => filterKey(headPath,
                                           input.tail,
                                           result.appended(JsArray(filterKey(headPath / MINUS_ONE,
                                                                             headSeq,
                                                                             Vector.empty,
                                                                             p
                                                                             )
                                                                   ),

                                                           ),
                                           p
                                           )
        case head: JsValue => filterKey(headPath,
                                        input.tail,
                                        result.appended(head),
                                        p
                                        )
      }
  }
}
