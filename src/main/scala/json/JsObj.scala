package json

import json.JsPath./

trait JsObj extends Json[JsObj]
{


  def toLazyList: LazyList[JsPair] =
  {
    def toLazyList(obj: JsObj
                  ): LazyList[JsPair] =
    {

      if (obj.isEmpty) LazyList.empty

      else obj.head #:: toLazyList(obj.tail)
    }

    toLazyList(this)
  }

  def head: (String, JsElem)

  def toLazyListRec: LazyList[JsPair] = JsObj.toLazyList_(/,
                                                          this
                                                          )


}

object JsObj
{


  private[json] def toLazyList_(path: JsPath,
                                value: JsObj
                               ): LazyList[JsPair] =
  {
    if (value.isEmpty) return LazyList.empty
    val head = value.head

    head._2 match
    {
      case o: JsObj => if (o.isEmpty) (path / head._1, o) +: toLazyList_(path,
                                                                         value.tail
                                                                         ) else toLazyList_(path / head._1,
                                                                                            o
                                                                                            ) ++: toLazyList_(path,
                                                                                                              value.tail
                                                                                                              )
      case a: JsArray => if (a.isEmpty) (path / head._1, a) +: toLazyList_(path,
                                                                           value.tail
                                                                           ) else JsArray.toLazyList_(path / head._1 / -1,
                                                                                                      a
                                                                                                      ) ++: toLazyList_(path,
                                                                                                                        value.tail
                                                                                                                        )
      case _ => (path / head._1, head._2) +: toLazyList_(path,
                                                         value.tail
                                                         )

    }
  }


}
