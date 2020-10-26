<img src="https://github.com/imrafaelmerino/json-scala-values/blob/master/logo/package_highres_if9bsyj4/black/full/black_logo_white_background.png" width="250" height="150"/>

[![Build Status](https://travis-ci.org/imrafaelmerino/json-scala-values.svg?branch=master)](https://travis-ci.org/imrafaelmerino/json-scala-values)
[![CircleCI](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master.svg)](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master)
[![codecov](https://codecov.io/gh/imrafaelmerino/json-scala-values/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/json-scala-values)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)

[![Gitter](https://badges.gitter.im/json-scala-values/community.svg)](https://gitter.im/json-scala-values/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

- [What to use _json-scala-values_ for and when to use it](#whatfor)
- [Introduction](#introduction)
   - [Specs](#specs)
   - [Future and Try monads](#futureandtry)
   - [Generators](#generators)
      - [Defining custom Json generators](#customgens)
      - [Defining random Json generators](#randomgens)
      - [Composing Json generators](#composing)
   - [Optics](#optics)
- [Installation](#installation)
    - [Scala](#scala)
    - [Dotty](#dotty)
- [Related projects](#rp)
- [Release process](#release)

## <a name="whatfor"><a/> What to use _json-scala-values_ for and when to use it

**json-scala-values** fits like a glove to do Functional Programming. All we need to program
is values and functions to manipulate them. For those architectures that work with Jsons end-to-end it's extremely safe and efficient to have a persistent Json. Think of actors sending
Json messages one to each other for example.

You can still just use json-values for testing if you do Property-Based-Testing with [ScalaCheck](https://www.scalacheck.org).
Creating Json generators with json-values is really easy.

## <a name="introduction"><a/> Introduction

Welcome to **json-scala-values**! A Json is a well-known and simple data structure, but without immutability and all the benefits
that it brings to your code, there is still something missing. The Json implemented in json-scala-values **is the first persistent Json in the JVM ever**. It uses [immutable.Map.HashMap](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/HashMap.html) and
[immutable.Seq.Vector](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/Vector.html) as the underlying persistent data structures.  No more copy-on-write!
It provides a **simple** and declarative API to manipulate Json with no ceremony.

Creation of a Json object from a Map:

```scala
import json.value.JsObj
import json.value.JsArray
import json.value.Preamble._

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

Putting data in and getting data out:

```scala

val x = JsObj.empty.inserted("a" / "b", "hi" )

x("a" / "b") == JsStr("hi")

x("a") == JsObj("b"-> "hi")


// the function inserted always insert at the specified path

val y = JsObj.empty.inserted("a" / 0 / 2, 1, padWith = 0)

y == JsObj("a" -> JsArray( JsArray(0,0,1) ))

y("a") == JsArray(0,0,1)

y("a" / 0 / 2) == JsInt(1)

y("a" / 0 / 0) == JsInt(0)

```

Manipulating Jsons with functions that traverses the whole structure recursively:

```scala
// map keys to lowercase traversing every element of the json

val toLowerCase:String => String = _.toLowerCase

json mapAllKeys toLowerCase


// trim string. Not very functional impl. We'll see a better approach
// using optics

val trimIfStr = (x: JsPrimitive) => if (x.isStr) x.toJsStr.map(_.trim) else x

array mapAll trimIfStr


// remove nulls traversing every element of the json

val isNotNull:JsPrimitive => Boolean = _.isNotNull

json filterAll isNotNull

 ```

#### <a name="specs"><a/>Specs 

We can define a **spec** to validate the structure of the above Json:

```scala
import json.value.Preamble._
import json.value.spec.Preamble._
import json.value.spec.JsObjSpec._
import json.value.spec.JsArraySpec._

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

A spec can be used to parse a String or an array of bytes into a Json directly. This way, as soon as a parsed value doesn't satisfy
a spec, the process ends with an error. On the other hand, if the parsing succeeds, we already have a validated Json.


```scala

val personParser:JsObjParser = JsObjParser(personSpec)

val bytes:Array[Byte] = ...

val result:Either[InvalidJson,JsObj] = personParser.parse(bytes)

```

#### <a name="futureandtry"><a/>Future and Try monads

Taming side effects with Future and Try monads:

```scala
import json.value.Preamble._
import json.value.future.Preamble._
import json.value.future.JsObjFuture._
import json.value.future.JsArrayFuture._

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

```scala
import json.value.Preamble._
import json.value.exc.Preamble._
import json.value.exc.JsObjTry._
import json.value.exc.JsArrayTry._

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

```scala
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

#### <a name="generators"><a/>Generators 

Let me go straight to the point. I'd argue that this is the most declarative,
concise, composable, and beautiful Json generator in the whole world! As a
functional developer, tired of spending much time manipulating mutable Jsons, I developed
[json-values](https://github.com/imrafaelmerino/json-scala-values), which is the first immutable Json
in the JVM ecosystem implemented with **persistent data structures**. To test
that library, I used property-based-testing with [ScalaCheck](https://www.scalacheck.org), and I developed several Json
generators that I decided to publish in this repo.

If you practice property-based testing and use ScalaCheck, you'll be able to design composable Json generators
very quickly and naturally, as if you were writing out a Json.

###### <a name="customgens"><a/> Defining custom Json generators
Using **json-scala-values**, you can create Jsons in different ways. One of them
turns out to be very natural because it's  close to the Json representation itself.
It may ring a bell if you have taken the Scala courses from Martin Odersky on Coursera.

```scala
import jsonvalues.{JsObj,JsArray}
import jsonvalues.Preamble.{given,_}

JsObj("@type" -> "person",
      "name" -> "Rafael Merino García",
      "birth_date" -> "13-03-1982",
      "email" -> "imrafaelmerino@gmail.com",
      "gender" -> "Male",
      "address" -> JsObj("country" -> "ES",
                         "location" -> JsArray(40.1693500,
                                               -4.2154900
                                              )
                        )
     )
```

Let's create a person generator using the same philosophy:

```scala
import jsonvalues.JsObj
import jsonvalues.Preamble.{given}
import jsonvaluesgen.Preamble.{_,given}
import jsonvaluesgen.{JsObjGen,JsArrayGen}
import org.scalacheck.Gen

def nameGen: Gen[String] = ???
def birthDateGen: Gen[String] = ???
def latitudeGen: Gen[Double] = ???
def longitudeGen: Gen[Double] = ???
def emailGen: Gen[String] = ???
def countryGen: Gen[String] = ???

def json.value.json.value.gen:Gen[JsObj] = JsObjGen("@type" -> "person",
                              "name" -> nameGen,
                              "birth_date" -> birthDateGen,
                              "email" -> emailGen,
                              "gender" -> Gen.oneOf("Male",
                                                    "Female"
                                                   ),
                              "address" -> JsObjGen("country" -> countryGen,
                                                    "location" -> JsArrayGen(latitudeGen,
                                                                             longitudeGen
                                                                            )
                                                   )
                             )
```

If you are using other Json library, you can still use this generator mapping the generated
json into its string representation, and then creating your object from that string:


```scala
import x.y.z.MyJson

def json.value.json.value.gen:Gen[MyJson] =  personGen.map(MyJson(_.toString))
```


Another way of creating Jsons in **json-scala-values** is from pairs of paths and values:


```scala
import jsonvalues.JsObj
import jsonvalues.JsPath._
import jsonvalues.Preamble.{given}

JsObj(("@type" -> "person"),
      ("name" -> "Rafael Merino García"),
      ("birth_date" -> "13-03-1982"),
      ("email" -> "imrafaelmerino@gmail.com"),
      ("gender" -> "Male"),
      ("address" / "country" -> "ES"),
      ("address" / "location" / 0 -> 40.1693500),
      ("address" / "location" / 1 -> -4.2154900)
     )
```

And again, we can create Json generators following the same approach:


 ```scala
import jsonvalues._
import jsonvalues.JsPath._
import jsonvalues.Preamble.{given}
import jsonvaluesgen._
import jsonvaluesgen.Preamble.{given,_}
import org.scalacheck.Gen

def nameGen: Gen[String] = ???
def birthDateGen: Gen[String] = ???
def latitudeGen: Gen[Double] = ???
def longitudeGen: Gen[Double] = ???
def emailGen: Gen[String] = ???
def countryGen: Gen[String] = ???

JsObjGen.fromPairs(("@type" -> "person"),
                   ("name" -> nameGen),
                   ("birth_date" -> birthDateGen),
                   ("email" -> emailGen),
                   ("gender" -> Gen.oneOf("Male",
                                          "Female"
                                         )
                   ),
                   ("address" / "country" -> countryGen),
                   ("address" / "location" / 0 -> latitudeGen),
                   ("address" / "location" / 1 -> longitudeGen)
                  )
```

A typical scenario is when we want some elements not to be always generated,
which can be easily achieved using the special json.value _JsNothing_.
Inserting _JsNothing_ in a Json at a path is like removing the element. Taking that into
account, let's create a generator that produces Jsons without the key _name_ with
a probability of 50 percent:


```scala
def nameGen: Gen[JsStr] = ???

def optNameGen: Gen[JsValue] = Gen.oneOf(JsNothing,nameGen)

JsObjGen("@type" -> "person",
         "name" -> optNameGen
        )

//syntactic sugar to do the same thing but typing less!

JsObjGen("@type" -> "person",
         "name" ->  ?(nameGen)
        )

```

And we can change that probability using the ScalaCheck function _Gen.frequencies_:

```scala
def nameGen: Gen[JsStr] = ???

def optNameGen: Gen[JsValue] = Gen.frequencies((10,JsNothing),
                                               (90,nameGen)
                                              )

JsObjGen("@type" -> "person",
         "name" ->  optNameGen,
        )

//syntactic sugar to do the same thing but typing less!

JsObjGen("@type" -> "person",
         "name" ->  ?(90,nameGen),
        )
```

###### <a name="randomgens"><a/> Defining random Json generators

There are times when you are only interested in generating random Jsons; after all, every
function of a Json API has to work, no matter the Json it's tested with.


```scala
import jsonvaluesgen.{RandomJsObjGen,RandomJsArrayGen}

//produces any imaginable Json object
def randomObjGen: Gen[JsObj] = RandomJsObjGen()

//produces any imaginable Json array
def randomArrayGen: Gen[JsArray] = RandomJsArrayGen()
```

These random generators are also customizable to some extent. The following named parameters can be passed in:

* _arrLengthGen: Gen[Int]_: to control the length of arrays.
* _objSizeGen: Gen[Int]_: to control the size of objects. Take into account that if JsNothing is generated, no
 element is inserted and the final size of the object may be lower than the returned by this generator:
 
 ```scala
 val json.value.json.value.gen = RandomJsObjGen(objSizeGen= Gen.const(5),
                          objPrimitiveGen = PrimitiveGen(strGen=Gen.oneOf(JsNothing,JsStr("a")))
                         )
```

In the previous example, for those cases where JsNothing is generated, the size of the Json object
will be four and not five.
* _keyGen: Gen[String]_: to control the name of the keys in objects.
* _arrValueFreq: ValueFreq_: to control the type of the elements generated in arrays.
 For primitive types, values are generated by the corresponding generator defined in the param _arrPrimitiveGen_.
 Nested objects can be generated, i.e. array of objects or array of arrays and so on. Nested objects have to be configured
 carefully to not blow up the stack due to recursion.
* _arrPrimitiveGen: PrimitiveGen_: to control the json.value of the primitive types generated in arrays.
* _objValueFreq: ValueFreq_: to control the type of the elements generated in objects. For primitive types, the json.value are
 generated by the generator defined in the param _objPrimitiveGen_.
* _objPrimitiveGen: PrimitiveGen_: to control the json.value of the primitive types generated in objects.
Find below the definition of the classes _PrimitiveGen_ and _ValueFreq_:

```scala
val ALPHABET: Seq[String] = "abcdefghijklmnopqrstuvwzyz".split("").toIndexedSeq

case class PrimitiveGen(strGen: Gen[String] = Gen.oneOf(ALPHABET),
                        intGen: Gen[Int] = Arbitrary.arbitrary[Int],
                        longGen: Gen[Long] = Arbitrary.arbitrary[Long],
                        doubleGen: Gen[Double] = Arbitrary.arbitrary[Double],
                        floatGen: Gen[Float] = Arbitrary.arbitrary[Float],
                        boolGen: Gen[Boolean] = Arbitrary.arbitrary[Boolean],
                        bigIntGen: Gen[BigInt] = Arbitrary.arbitrary[BigInt],
                        bigDecGen: Gen[BigDecimal] = Arbitrary.arbitrary[BigDecimal]
                       )

case class ValueFreq(obj: Int = 1,
                     arr: Int = 1,
                     str: Int = 5,
                     int: Int = 5,
                     long: Int = 5,
                     double: Int = 5,
                     bigInt: Int = 5,
                     bigDec: Int = 5,
                     bool: Int = 5,
                     `null`: Int = 5
                    )
```


As you may notice, the class ValueFreq has two params _obj_ and _arr_ that allows you to generate
nested Jsons. The default frequency assigned to them is lower than the rest, otherwise the process
can diverge and a StackOverFlowException would be thrown.

To make it clearer, let's define a JsObj generator with the following specifications:

* max size of 10
* keys of three letters from the alphabet
* values are String or Int or JsArray with the same probability, where:
    * String values are colors
    * Int values are numbers between -100 y 100
    * JsArrays are never empty, max length of 5, values are either arbitrary booleans or null with a probability of 90% and 10% respectively


```scala
def arrLengthGen:Gen[Int] = Gen.choose(1,5)
def objSizeGen:Gen[Int] = Gen.choose(0,10)
def letterGen:Gen[String] = Gen.oneOf(ALPHABET)
def keyGen: Gen[String] = for {
                                a <- letterGen
                                b <- letterGen
                                c <- letterGen
                              } yield s"$a$b$c"

def objectValueFreq:ValueFreq = ValueFreq(arr = 1,
                                          str = 1,
                                          obj = 0,
                                          int = 1,
                                          long = 0,
                                          double = 0,
                                          bigInt = 0,
                                          bigDec = 0,
                                          bool = 0,
                                          `null` = 0
                                         )

def objectPrimitiveGen:PrimitiveGen = PrimitiveGen(strGen = Gen.oneOf("blue","red","brown"),
                                                   intGen = Gen.choose(-100,100)
                                                  )

def arrayValueFreq:ValueFreq = ValueFreq(arr = 0,
                                         str = 0,
                                         obj = 0,
                                         int = 0,
                                         long = 0,
                                         double = 0,
                                         bigInt = 0,
                                         bigDec = 0,
                                         bool = 9,
                                         `null` = 1
                                        )


def jsonGen:Gen[JsObj] = RandomJsObjGen(objValueFreq = objectValueFreq,
                                        objPrimitiveGen = objectPrimitiveGen,
                                        keyGen = keyGen,
                                        objSizeGen = objSizeGen,
                                        arrValueFreq = arrayValueFreq,
                                        arrLengthGen = arrLengthGen
                                       )

```

###### <a name="composing"><a/> Composing Json generators

Composing Json generators is key in order to handle complexity and reuse code avoiding repetition. There are two ways, inserting pairs into generators and
joining generators:

```scala
def addressGen:Gen[JsObj] = JsObjGen("street" -> streetGen,
                                     "city" -> cityGen,
                                     "zip_code" -> zipCodeGen
                                    )

//let's insert location generators (JsPath,Gen[JsValue]) into our addressGen
def addressWithLocationGen:Gen[JsObj] = JsObjGen.inserted(addressGenerator,
                                                          ("location" / 0, latitudeGen),
                                                          ("location" / 1, longitudeGen)
                                                         )

def namesGen = JsObjGen("family_name" -> familyNameGen,
                        "given_name" -> givenNameGen)

def contactGen = JsObjGen("email" -> emailGen,
                          "phone" -> phoneGen,
                          "twitter_handle" -> handleGen
                         )

def clientGen = JsObjGen.concat(namesGen,
                                contactGen,
                                addressWithLocationGen
                               )

```

As you can see **defining a future, try, spec and a generator is as simple as defining a raw Json.** 


#### <a name="optics"><a/>Optics 
Optics solve a lot of very common data-manipulation problems in a composable
and concise way. json-values uses [monocle](https://julien-truffaut.github.io/Monocle)

```scala
import json.value.JsObj
import json.value.Preamble._

val obj = JsObj("name" -> "Rafael",
                "age" -> 30,
                "address" -> JsObj("city" -> "Madrid",
                                   "location" -> JsArray(49.445,38.989)
                                  )

                )
```

A **Prism** is an optic used to select part of a sum type, in our case, one of
the types of JsValue.

```scala

// JsStr.prism :: Prism[JsValue,String]

val toLowerCase = JsStr.prism.modify(_.toLowerCase)
// JsValue => JsValue

val trim = JsStr.prism.modify(_.trim)
// JsValue => JsValue

val isNotEmpty = JsStr.prism.exist(_ != "")
// JsValue => JsValue

// prism and map/filter are good friends:

obj map toLowerCase

obj map trim

obj filter isNotEmpty 

// composing prism

// monocle.std.string.stringToInt :: monocle.Prism[String,Int]

val jsStrToInt = JsStr.prism composePrism stringToInt
// monocle.Prism[JsValue,Int]

jsStrToInt.getOption(JsStr("100"))
// Option[Int] = Some(100)

```

**Lenses** focuses a single piece of data within a larger structure. In our case, 
a _JsValue_ withing a Json object or array. A Lens must never fail to get or modify that focus.
If you're an user of json-values, you may know the special type **JsNothing**. It has two properties
that make possible to define lawful lenses:
   
- When getting a value, _JsNothing_ is returned if the element is not found:

```scala
obj("c" / "d") == JsNothing
```

- If _JsNothing_ is inserted at a path where a value exists, it is removed: 

```scala
obj.inserted("name",JsNothing)("name") == JsNothing
```
Implementing accessors with lenses:

```scala 

val name = JsObj.accessor("name")                
// name: monocle.Lens[JsObj,JsValue]

val city = JsObj.accessor("address" / "city")                
// city: monocle.Lens[JsObj,JsValue] 

val latitude = JsObj.accessor("address" / "location" / 0)                
// latitude: monocle.Lens[JsObj,JsValue]

val longitude = JsObj.accessor("address" / "location" / 1)                
// longitude: monocle.Lens[JsObj,JsValue]
```

If you prefer working with more specific types than _JsValue_, an _Optional_ per type can  
be defined composing lenses and prisms. Optionals are like lenses but the element that 
the Optional focuses on may not exist. For example, getting a string  from a Json can 
fail if no element is found or it's not a string:

```scala

val name = JsObj.accessor("name") 

val maybeName = name composePrism JsStr.prism
// monocle.Optional[JsObj,String]

maybeName.getOption(obj)
// Some("Rafael")

// composing optionals

maybeName.modifyOption(_.toUpperCase).getOption(obj)
// Some("RAFAEL") 
```

Optics make data-manipulation more composable and concise. For example, the previous example:

```scala
val trimIfStr = (x: JsPrimitive) => if (x.isStr) x.toJsStr.map(_.trim) else x

obj mapAll trimIfStr
```

could have been written using a Prism:

```scala
import json.value.JsStr
// monocle.Prism[JsValue,String]

obj mapAll JsStr.prism.modify(_.trim)
```

which is more functional.

## <a name="installation"><a/> Installation
The library is compatible with Scala 2.12, 2.13 and Dotty. Each version is maintained in a separate branch. The reason is because all
the supported versions are quite different and the library itself is different as well to embrace all the new features and idioms introduced in scala 2.13 and Dotty.

#### <a name="scala"><a/> Scala
It's built against 2.12 and 2.13 versions:

   - 2.13: [![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.13/3.3.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values_2.13/3.3.0/jar)

     [It's maintained in the branch scala-2.13](https://github.com/imrafaelmerino/json-scala-values/tree/scala-2.13)

     libraryDependencies += "com.github.imrafaelmerino" % "json-scala-values_2.13" % "3.3.0" % "test"

   - 2.12:  [![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.12/3.3.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values-generator_2.12/3.3.0/jar)

     [It's maintained in the branch scala-2.12](https://github.com/imrafaelmerino/json-scala-values/tree/scala-2.12)

     libraryDependencies += "com.github.imrafaelmerino" % "json-scala-values_2.12" % "3.3.0" % "test"

Doubling the first % you can tell sbt that it should append the current version of Scala being used to build the library to the dependency’s name:

libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "3.3.0" % "test"

#### <a name="dotty"><a/> Dotty (0.22.0-RC1)

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-dotty-values_0.22/3.3.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-dotty-values_0.22/3.3.0/jar)

[It's maintained in the branch master](https://github.com/imrafaelmerino/json-scala-values/tree/master)

libraryDependencies += "com.github.imrafaelmerino" %% "json-dotty-values" % "3.3.0"

## <a name="rp"><a/> Related projects
json-values was first developed in [Java](https://github.com/imrafaelmerino/json-values).
It uses the persistent data structures from [vavr](https://www.vavr.io/), [Jackson](https://github.com/FasterXML/jackson) to parse a string/bytes into
a stream of tokens and [dsl-sjon](https://github.com/ngs-doo/dsl-json) to parse a string/bytes given a spec.

## <a name="release"><a/> Release process
Every time a tagged commit is pushed into master, a Travis CI build will be triggered automatically and 
start the release process, deploying to Maven repositories and GitHub Releases. See the Travis conf file 
**.travis.yml** for further details. On the other hand, the master branch is read-only, and all the commits 
should be pushed to master through pull requests.

If you like the library, you can let me know by starring it. It really helps. If not, much better, 
it means json-scala-values can get better, your feedback we'll be more than welcoming. 