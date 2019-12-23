[![Build Status](https://travis-ci.org/imrafaelmerino/json-scala-values.svg?branch=master)](https://travis-ci.org/imrafaelmerino/json-scala-values)
[![CircleCI](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master.svg)](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master)
[![codecov](https://codecov.io/gh/imrafaelmerino/json-scala-values/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/json-scala-values)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)

[![Javadocs](https://www.javadoc.io/badge/com.github.imrafaelmerino/json-scala-values_2.13.svg)](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.13/1.0.1)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values_2.13/1.0.1/jar)
[![](https://jitpack.io/v/imrafaelmerino/json-scala-values.svg)](https://jitpack.io/#imrafaelmerino/json-scala-values)

[![Gitter](https://badges.gitter.im/json-scala-values/community.svg)](https://gitter.im/json-scala-values/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

- [Introduction](#introduction)
- [Requirements](#requirements)
- [What to use _json-scala-values_ for and when to use it](#whatfor)
- [Installation](#installation)
- [Documentation](#doc)
- [Code wins arguments](#cwa)
- [Related projects](#rp)

## <a name="introduction"><a/> Introduction
Welcome to **json-scala-values**! A Json is a well-known and simple data structure, but without immutability and all the benefits 
that it brings to your code, there is still something missing. The Json implemented in json-scala-values uses [immutable.Map.HashMap](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/HashMap.html) and 
[immutable.Seq.Vector](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/Vector.html) as the underlying persistent data structures. It provides a
rich and declarative API to manipulate Json with no ceremony.

## <a name="requirements"><a/> Requirements
Scala 2.13.0

## <a name="whatfor"><a/> What to use _json-scala-values_ for and when to use it
**json-scala-values** fits like a glove to do Functional Programming. All we need to program is values and functions to manipulate them.

## <a name="installation"><a/> Installation
libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "1.0.1"

## <a name="doc"><a/> Documentation
Go to the [project page](https://imrafaelmerino.github.io/json-scala-values/)

## <a name="cwa"><a/> Code wins arguments
Creation of a Json object from a Map:

```
val person = JsObj("@type" -> "Person",
                   "age" -> 37,
                   "name" -> "Rafael",
                   "gender" -> "MALE",
                   "address" -> JsObj("location" -> JsArray(40.416775,
                                                            -3.703790
                                                           )
                                     ),
                   "registration_date" -> "13-03-2010",
                   "book_ids" -> JsArray("00001",
                                         "00002"
                                        )
                   )
```

We can define a **spec** to validate the structure of a Json:

```
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
                           "registration_date" -> string,
                           "books_id" -> array_of_str
                          )
  
//validate: JsObjSpec => Seq[Invalid]
person.validate(personSpec) == Seq.empty  // no errors
```

Putting data in and getting data out:

```

val a = JsObj.empty.inserted("a" / "b", "hi" )
a.string("a" / "b") == "hi"
a.obj("a") == JsObj("b"-> 1)

val b = JsObj.empty.inserted("a" / 0 / 2, 1, padWith = 0)
b == JsObj("a" -> JsArray( JsArray(0,0,1) ))
b.array("a") == JsArray(0,0,1)
b.int("a" / 0 / 2) == 1
b.int("a" / 0 / 0) == 0
```

Manipulating Jsons with functions that traverses the whole Json recursively:

```
// map keys to lowercase
json.mapKeyRec((path:JsPath,_:JsValue) => path.last.asKey.name.toLowerCase)

// trim string values
json.mapRec((_: JsPath, value: JsValue) => value.asJsStr.map(_.trim),
            (_: JsPath, value: JsValue) => value.isStr
           )

// remove null values
json.filterRec((_: JsPath, value: JsValue) => value != JsNull)

 ```
 
If you like the library, you can let me know by starring it. It really helps. If not, much better, it means json-scala-values can get better, your feedback we'll be more than welcoming.
 
## <a name="rp"><a/> Related projects
This library was first developed in Java: [json-values](https://github.com/imrafaelmerino/json-values). 
The Json generators designed during the development of json-scala-values have been published in a different project called [json-scala-values-generator](https://github.com/imrafaelmerino/json-scala-values-generator). 
If you do property-based testing with [ScalaCheck](https://www.scalacheck.org), you should take a look! 
