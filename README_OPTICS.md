[![Build Status](https://travis-ci.org/imrafaelmerino/optics-json-values.svg?branch=master)](https://travis-ci.org/imrafaelmerino/optics-json-values)
[![Gitter](https://badges.gitter.im/optics-json-values/community.svg)](https://gitter.im/optics-json-values/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

- [Introduction](#introduction)
- [Installation](#inst)
    [Scala](#scala)
    [Dotty](#dotty)
- [Code wins arguments](#cwa)

## <a name="introduction"><a/> Introduction
This is an optional dependency of [json-values](https://github.com/imrafaelmerino/optics-json-values) to work with optics.
Optics solve a lot of very common data-manipulation problems in a composable
and concise way. This library uses [monocle](https://julien-truffaut.github.io/Monocle) library and I didn't want to be optionated
about anything in json-values, that's why I decided to maintain all the predefined optics
in another repo and library. 

## <a name="inst"><a/> Installation

#### <a name="scala"><a/> Scala

It requires Scala 2.13:

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/optics-json-values_2.13/0.4)](https://search.maven.org/artifact/com.github.imrafaelmerino/optics-json-values_2.13/0.4/jar)

**libraryDependencies += "com.github.imrafaelmerino" % "optics-json-values" % "0.4"**

#### <a name="dotty"><a/> Dotty

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/optics-dotty-json-values_0.21/0.21.0-RC1)](https://search.maven.org/artifact/com.github.imrafaelmerino/optics-dotty-json-values_0.21/0.21.0-RC1/jar)

**libraryDependencies += "com.github.imrafaelmerino" %% "optics-dotty-json-values" % "0.21.0-RC1"**

## <a name="cwa"><a/> Code wins arguments 
```
import JsObj
import json.value.Preamble.{given _}

val obj = JsObj("name" -> "Rafael",
                "age" -> 30,
                "address" -> JsObj("city" -> "Madrid",
                                   "location" -> JsArray(49.445,38.989)
                                  )

                )
```

A **Prism** is an optic used to select part of a Sum type, in our case, one of
the types of JsValue.

```
import JsStrOptics.toJsStr
// toJsStr :: Prism[JsValue,String]

val toLowerCase = toJsStr.modify(_.toLowerCase)
// JsValue => JsValue

val trim = toJsStr.modify(_.trim)
// JsValue => JsValue

val isNotEmpty = toJsStr.exist(_ != "")
// JsValue => JsValue

// prism and map/filter are good friends:

obj map toLowerCase

obj map trim

obj filter isNotEmpty 

// composing prism

import monocle.std.string.stringToInt
// monocle.Prism[String,Int]

val jsStrToInt = toJsStr composePrism stringToInt
// monocle.Prism[JsValue,Int]

jsStrToInt.getOption(JsStr("100"))
// Option[Int] = Some(100)

```

**Lenses** focuses a single piece of data within a larger structure. In our case, 
a JsValue withing a Json object or array. A Lens must never fail to get or modify that focus.
If you're an user of json-values, you may know the special type **JsNothing**. It has two properties
that make possible to define lawful lenses:
   
- When getting a json.value, JsNothing is returned if the element is not found:
```
obj("c" / "d") == JsNothing
```
- If JsNothing is inserted at a path where a json.value exists, it is removed: 
```
obj.inserted("name",JsNothing)("name") == JsNothing
```
Implementing accessors with lenses:

```     
import JsObjOptics.accesor          

val name = accessor("name")                
// name: monocle.Lens[JsObj,JsValue]

val city = accessor("address" / "city")                
// city: monocle.Lens[JsObj,JsValue] 

val latitude = accessor("address" / "location" / 0)                
// latitude: monocle.Lens[JsObj,JsValue]

val longitude = accessor("address" / "location" / 1)                
// longitude: monocle.Lens[JsObj,JsValue]
```

If you prefer working with more specific types than JsValue, an Optional per type can  
be defined composing lenses and prisms. Optionals are like lenses but the element that 
the Optional focuses on may not exist. For example, getting a string  from a Json can 
fail if no element is found or it's not a string:

```
import json.value.JsStrOptics.toJsStr
import json.value JsObjOptics.accessor

val name = accessor("name") 

val maybeName = name composePrism toJsStr
// monocle.Optional[JsObj,String]

maybeName.getOption(obj)
// Some("Rafael")

// composing optionals

maybeName.modifyOption(_.toUpperCase).getOption(obj)
// Some("RAFAEL") 
```

