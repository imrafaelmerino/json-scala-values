package json

import json.JsPath./

trait JsArray extends Json[JsArray]
{

  def head: JsElem

  def toLazyList: LazyList[JsPair] =
  {

    def toLazyList(i: Int,
                   arr: JsArray
                  ): LazyList[JsPair] =
    {
      if (arr.isEmpty) LazyList.empty

      else
      {
        val pair = (i, arr.head)
        pair #:: toLazyList(i + 1,
                            arr.tail
                            )
      }

    }

    toLazyList(0,
               this
               )
  }

  def toLazyListRec: LazyList[JsPair] = JsArray.toLazyList_(-1,
                                                            this
                                                            )


}

object JsArray
{
  private[json] def toLazyList_(path: JsPath,
                                value: JsArray
                               ): LazyList[JsPair] =
  {
    if (value.isEmpty) return LazyList.empty
    val head: JsElem = value.head
    val headPath: JsPath = path.inc
    head match
    {
      case a: JsArray => if (a.isEmpty) (headPath, a) +: toLazyList_(headPath,
                                                                     value.tail
                                                                     ) else toLazyList_(headPath,
                                                                                        a
                                                                                        ) ++: toLazyList_(headPath,
                                                                                                          value.tail
                                                                                                          )
      case o: JsObj => if (o.isEmpty) (headPath, o) +: toLazyList_(headPath,
                                                                   value.tail
                                                                   ) else JsObj.toLazyList_(headPath,
                                                                                            o
                                                                                            ) ++: toLazyList_(headPath,
                                                                                                              value.tail
                                                                                                              )
      case _ => (headPath, head) +: toLazyList_(headPath,
                                                value.tail
                                                )
    }

  }


}
