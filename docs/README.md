[![Build Status](https://travis-ci.org/imrafaelmerino/json-scala-values.svg?branch=master)](https://travis-ci.org/imrafaelmerino/json-scala-values)
[![CircleCI](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master.svg)](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master)
[![codecov](https://codecov.io/gh/imrafaelmerino/json-scala-values/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/json-scala-values)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

[![Javadocs](https://www.javadoc.io/badge/com.github.imrafaelmerino/json-scala-values_2.13.svg)](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.13/0.9.2)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values_2.13/0.9.2/jar)
[![](https://jitpack.io/v/imrafaelmerino/json-scala-values.svg)](https://jitpack.io/#imrafaelmerino/json-scala-values)


- [Introduction](#introduction)
- [Requirements](#requirements)
- [What to use _json-scala-values_ for and when to use it](#whatfor)
- [Installation](#installation)
- [Code wins arguments](#cwa)

## <a name="introduction"><a/> Introduction
Welcome to **json-scala-values**! A Json is a well-known and simple data structure, but without immutability and all the benefits 
that it brings to your code, there is still something missing. 
The Json implemented in json-scala-values uses [immutable.Map.HashMap](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/HashMap.html) and 
[immutable.Seq.Vector](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/Vector.html) as the underlying persistent data structures. 
It is the Scala version of the Java library [json-values](https://github.com/imrafaelmerino/json-values), which uses the 
same data structures. The current version **0.9.2** is a pre-release, so a lot new functionality and documentation
is coming. This early release has been published to support [json-scala-values-generator](https://github.com/imrafaelmerino/json-scala-values-generator), 
the most elegant and declarative Json generator in the whole wide world. If you like property-based testing and [ScalaCheck](https://www.scalacheck.org), 
you should take a look! If you like the library, you can let me know by starring it.

## <a name="requirements"><a/> Requirements
Scala 2.13.0

## <a name="whatfor"><a/> What to use _json-scala-values_ for and when to use it
**json-scala-values** fits like a glove to do Functional Programming. All we need to program is values and functions to manipulate them.

## <a name="installation"><a/> Installation
libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "0.9.2"


## <a name="cwa"><a/> Code wins arguments

Creation of a Json object from a Map:
```
import value.JsObj
import value.JsArray
import value.Implicits._

val person = JsObj("age" -> 37,
                   "name" -> "Rafael",
                   "gender" -> "MALE",
                   "address" -> JsObj("location" -> JsArray(40.416775,
                                                            -3.703790
                                                           )
                                     ),
                   "@type" -> "Person",
                   "registrationDate" -> "13-03-2010",
                   "book_ids" -> JsArray("00001",
                                         "00002"
                                        )
                   )
```

Creation of a Json object from a list of pairs:

```
val person = JsObj(("age", 37),
                   ("name", "Rafael"),
                   ("gender", "MALE"),
                   ("address" / "location" / 0, 40.416775),
                   ("address" / "location" / 1, 40.416775),
                   ("@type", "Person"),
                   ("registrationDate", "13-03-2010"),
                   ("books_id" / 0, "0001"),
                   ("books_id" / 1, "0002")
                  )
```

Creation of a Json object from a string, which returns a Try computation:

```
val json: Try[JsObj] = JsObj.parse(str)
```

Creation of a spec to validate that a Json object is like the defined above:

```
import value.Implicits._
import value.spec.JsStringSpecs._
import value.spec.JsArraySpecs._
import value.spec.JsNumberSpecs._
import value.spec.JsIntSpecs._
import value.spec.{JsArraySpec, JsObjSpec}

val personSpec = JsObjSpec("@type" -> "Person",
                           "age" -> int,
                           "name" -> string,
                           "gender" -> enum("MALE",
                                            "FEMALE"
                                           ),
                           "address" -> JsObjSpec("location" -> JsArraySpec(decimal,
                                                                            decimal
                                                                           )
                                                 ),
                           "height" -> decimal,
                           "registrationDate" -> string,
                           "books_id" -> arrayOfString
                          )

person.validate(personSpec) == Seq.empty  // no errors
```

We can add more restrictive specifications:

```
val personSpec = JsObjSpec("@type" -> "Person",
                           "age" -> int(minimum = 18,
                                        maximum = 100
                                       ),
                           "name" -> string(minLength = 1,
                                            maxLength = 255
                                           ),
                           "gender" -> enum("MALE",
                                            "FEMALE"
                                           ),
                           "address" -> JsObjSpec("location" -> JsArraySpec(decimal,
                                                                            decimal
                                                                            )
                                                  ),
                           "registrationDate" -> string(pattern = "\\d{2}-\\d{2}-\\d{4}".r)
                           "books_id" -> arrayOfString(maxItems = 10,
                                                       unique = true
                                                       )
                           )
```



