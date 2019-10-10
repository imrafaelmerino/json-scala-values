package json.immutable

import json.{JsPair, JsPath}

trait Json[T <: Json[T]]
{

  @`inline` final def +!(pair: JsPair): T = inserted(pair)

  def inserted(pair: JsPair): T

  @`inline` final def -(path: JsPath): T = removed(path)

  def removed(path: JsPath): T

  @`inline` final def +(pair: JsPair): T = updated(pair)

  def updated(pair: JsPair): T

  @`inline` final def --(xs: IterableOnce[JsPath]): T = removedAll(xs)

  def removedAll(xs: IterableOnce[JsPath]): T

}
