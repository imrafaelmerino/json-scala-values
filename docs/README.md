[![Build Status](https://travis-ci.org/imrafaelmerino/json-scala-values.svg?branch=master)](https://travis-ci.org/imrafaelmerino/json-scala-values)
[![CircleCI](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master.svg)](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master)
[![codecov](https://codecov.io/gh/imrafaelmerino/json-scala-values/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/json-scala-values)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)

[![Gitter](https://badges.gitter.im/json-scala-values/community.svg)](https://gitter.im/json-scala-values/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

- [Introduction](#introduction)
- [What to use _json-scala-values_ for and when to use it](#whatfor)
- [Installation](#installation)
    - [Scala](#scala)
    - [Dotty](#dotty)
- [Documentation](#doc)
- [Code wins arguments](#cwa)
- [Related projects](#rp)

## <a name="introduction"><a/> Introduction
Welcome to **json-scala-values**! A Json is a well-known and simple data structure, but without immutability and all the benefits
that it brings to your code, there is still something missing. The Json implemented in json-scala-values **is the first persistent Json in the JVM ever**. It uses [immutable.Map.HashMap](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/HashMap.html) and
[immutable.Seq.Vector](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/Vector.html) as the underlying persistent data structures.  No more copy-on-write!
It provides a **simple** and declarative API to manipulate Json with no ceremony.

## <a name="whatfor"><a/> What to use _json-scala-values_ for and when to use it
**json-scala-values** fits like a glove to do Functional Programming. All we need to program
is values and functions to manipulate them. For those architectures that work with Jsons end-to-end it's extremely safe and efficient to have a persistent Json. Think of actors sending
Json messages one to each other for example.

You can still just use json-values for testing if you do Property-Based-Testing with [ScalaCheck](https://www.scalacheck.org).
In this case you need the dependency [json-scala-values-generator](https://github.com/imrafaelmerino/json-scala-values-generator)

## <a name="installation"><a/> Installation

#### <a name="scala"><a/> Scala

It requires Scala 2.13:

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.13/3.0.2)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values_2.13/3.0.2/jar)

**libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "3.0.2"**

#### <a name="dotty"><a/> Dotty (0.22.0-RC1)

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-dotty-values_0.22/3.2.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-dotty-values_0.22/3.2.0/jar)

**libraryDependencies += "com.github.imrafaelmerino" %% "json-dotty-values" % "3.2.0"**

[It's maintained in another branch](https://github.com/imrafaelmerino/json-scala-values/tree/dotty)
## <a name="doc"><a/> Documentation
Go to the [project page](https://imrafaelmerino.github.io/json-scala-values/)

## <a name="cwa"><a/> Code wins arguments
Creation of a Json object from a Map:

```
import value.Preamble._

val person = JsObj("@type" -> "Person",
                   "age" -> 37,
                   "name" -> "Rafael",
                   "gender" -> "MALE",
                   "address" -> JsObj("location" -> JsArray(40.416775,
                                                            -3.703790
                                                           )
                                     ),
                   "book_ids" -> JsArray("00001",
                                         "00002"
                                        )
                   )

```

We can define a **spec** to validate the structure of the above Json:

```
import value.Preamble._
import value.spec.Preamble._
import value.spec.JsObjSpec._
import value.spec.JsArraySpec._

val personSpec = JsObjSpec("@type" -> "Person",
                           "age" -> int,
                           "name" -> str,
                           "gender" -> enum("MALE",
                                            "FEMALE"
                                           ),
                           "address" -> JsObjSpec("location" -> JsArraySpec(decimal,
                                                                            decimal
                                                                           )
                                                 ),
                           "books_id" -> arrayOfStr
                          )

person.validate(personSpec) == Seq.empty  // no errors

```

A spec can be used to parse into a Json directly. This way, as soon as a parsed value doesn't satisfy
a spec, the process ends with an error. On the other hand, if the parsing succeeds, we already have a validated Json.


```

val personParser:JsObjParser = JsObjParser(personSpec)

val bytes:Array[Byte] = ...

val result:Either[InvalidJson,JsObj] = personParser.parse(bytes)

```


Taming side effects with Future and Try monads:


```
import value.Preamble._
import value.future.Preamble._
import value.future.JsObjFuture._
import value.future.JsArrayFuture._

val ageFuture:Future[Int] = ???

val latitudeFuture:Future[Double] = ???

val longitudeFuture:Future[Double] = ???

val addressFuture =  JsObjFuture("location" -> JsArrayFuture(latitudeFuture,
                                                             longitudFuture
                                                            )
                                 )

val future:Future[JsOb] = JsObjFuture("@type" -> "Person",
                                      "age" -> ageFuture,
                                      "name" -> "Rafael",
                                      "gender" -> "MALE",
                                      "address" -> addressFuture
                                      )

```

```
import value.Preamble._
import value.exc.Preamble._
import value.exc.JsObjTry._
import value.exc.JsArrayTry._

val ageTry:Try[Int] = ???

val latitudeTry:Try[Double] = ???

val longitudeTry:Try[Double] = ???

val addressTry =  JsObjTry("location" -> JsArrayTry(latitudeTry,
                                                    longitudTry
                                                   )
                          )

val tryObj:Try[JsOb] = JsObjTry("@type" -> "Person",
                                "age" -> ageTry,
                                "name" -> "Rafael",
                                "gender" -> "MALE",
                                "address" -> addressTry
                               )

```

You can even mix try and future monads:

```
val ageTry:Try[Int] = ???

val latitudeFuture:Future[Double] = ???

val longitudeTry:Try[Double] = ???

val addressFuture =  JsObjFuture("location" -> JsArrayFuture(latitudeFuture,
                                                             longitudTry
                                                            )
                                )

val future:Future[JsOb] = JsObjFuture("@type" -> "Person",
                                      "age" -> ageTry,
                                      "name" -> "Rafael",
                                      "gender" -> "MALE",
                                      "address" -> addressFuture
                                      )

```

As you can see defining a future, try, spec and a [generator](https://github.com/imrafaelmerino/json-scala-values-generator) **is as simple as defining a raw Json**.

Putting data in and getting data out:

```

val x = JsObj.empty.inserted("a" / "b", "hi" )

x("a" / "b") == JsStr("hi")

x("a") == JsObj("b"-> "hi")


// inserted function always insert at the specified path

val y = JsObj.empty.inserted("a" / 0 / 2, 1, padWith = 0)

y == JsObj("a" -> JsArray( JsArray(0,0,1) ))

y("a") == JsArray(0,0,1)

y("a" / 0 / 2) == JsInt(1)

y("a" / 0 / 0) == JsInt(0)

```

Manipulating Jsons with functions that traverses the whole structure recursively:

```
// map keys to lowercase traversing every element of the json

val toLowerCase:String=>String = _.toLowerCase

json mapAllKeys toLowerCase


// trim string values. Not very functional impl. We'll see a better approach

val trimIfStr = (x: JsPrimitive) => if (x.isStr) x.toJsStr.map(_.trim) else x

array mapAll trimIfStr


// remove null values traversing every element of the json

val isNotNull:JsPrimitive => Boolean = _.isNotNull

json filterAll isNotNull

 ```

## <a name="rp"><a/> Related projects
The Json generators designed during the development of json-scala-values have been published in a different project called [json-scala-values-generator](https://github.com/imrafaelmerino/json-scala-values-generator).
If you do property-based testing with [ScalaCheck](https://www.scalacheck.org), you should take a look!
There are some optics defined in a different project [optics-json-values](https://github.com/imrafaelmerino/optics-json-values)
that makes data-manipulation more composable and concise. For example, the above example:

```
val trimIfStr = (x: JsPrimitive) => if (x.isStr) x.toJsStr.map(_.trim) else x

obj mapAll trimIfStr
```

could have been written using a Prism:

```
import value.JsStrOptics.toJsStr
// monocle.Prism[JsValue,String]

obj mapAll toJsStr.modify(_.trim)
```

which is more functional.

If you like the library, you can let me know by starring it. It really helps. If not, much better, it means json-scala-values can get better, your feedback we'll be more than welcoming.
