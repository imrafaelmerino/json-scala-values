package json.mutable

import json.JsPair


trait Json[T <: Json[T]]
{
  @`inline` final def +=(pair: JsPair): T = addOne(pair)

  def addOne(pair: JsPair): T

  def concat(xs: IterableOnce[JsPair]): T

  @`inline` final def ++=(xs: IterableOnce[JsPair]): T = addAll(xs)

  def addAll(xs: IterableOnce[JsPair]): T

  @`inline` final def --=(xs: IterableOnce[JsPair]): T = subtractAll(xs)

  def subtractAll(xs: IterableOnce[JsPair]): T

  @`inline` final def -=(pair: JsPair): T = subtractOne(pair)

  def subtractOne(pair: JsPair): T

  @`inline` final def +!(pair: JsPair): T = insertOne(pair)

  def insertOne(pair: JsPair): T

}
