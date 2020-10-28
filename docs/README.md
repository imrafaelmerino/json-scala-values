<img src="https://github.com/imrafaelmerino/json-scala-values/blob/master/logo/package_highres_if9bsyj4/black/full/black_logo_white_background.png" width="250" height="150"/>

[![Build Status](https://travis-ci.org/imrafaelmerino/json-scala-values.svg?branch=master)](https://travis-ci.org/imrafaelmerino/json-scala-values)
[![CircleCI](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master.svg)](https://circleci.com/gh/imrafaelmerino/json-scala-values/tree/master)
[![codecov](https://codecov.io/gh/imrafaelmerino/json-scala-values/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/json-scala-values)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values)

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.12/3.3.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values-generator_2.12/3.3.0/jar)  **2.12**  

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values_2.13/4.0.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values_2.13/4.0.0/jar) **2.13**  

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-dotty-values_0.27/4.0.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-dotty-values_0.27/4.0.0/jar) **dotty (0.27.0-RC1)** 

- [What to use _json-values_ for and when to use it](#whatfor)
- [Introduction](#introduction)
   - [JsPath](#jspath)
   - [JsValue](#jsvalue)
   - [Creating Jsons](#creatingjson)
      - [Creating JsObj](#creatingjsonobj)
      - [Creating JsArray](#creatingjsonarray)
   - [Putting data in and getting data out](#inout)
   - [Filter, map and reduce](#filtermapreduce)
   - [Flattening a Json](#flattening)
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
- [Performance](#performance)    
- [Related projects](#rp)
- [Release process](#release)

## <a name="whatfor"><a/> What to use _json-values_ for and when to use it

**json-scala-values** fits like a glove to do Functional Programming. All we need to program
is values and functions to manipulate them. For those architectures that work with Jsons end-to-end 
it's extremely safe and efficient to have a persistent Json. Think of actors sending
Json messages one to each other for example.

You can still just use json-values for testing if you do Property-Based-Testing with [ScalaCheck](https://www.scalacheck.org).
Creating Json generators with json-values is really easy.

How do we make changes to immutable structures or values in an inexpensive way? Using persistent data structures. Copy-on-write 
is inefficient, and the performance goes down as you produce new values. Why don't we have a persistent Json? This is the question 
I asked myself when I got into functional programming. Since I found out no answer, I decided to implement a persistent Json.

## <a name="introduction"><a/> Introduction

Welcome to **json-values**! A Json is a well-known and simple data structure, but without immutability and all the benefits
that it brings to your code, there is still something missing. The Json implemented in json-values **is the first persistent 
Json in the JVM ever**. It uses [immutable.Map.HashMap](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/HashMap.html) 
and [immutable.Seq.Vector](https://www.scala-lang.org/api/2.13.1/scala/collection/immutable/Vector.html) as the underlying 
persistent data structures. It provides a **simple** and declarative API to manipulate Json with no ceremony.

#### <a name="jspath"><a/>JsPath

A _JsPath_ represents a location of a specific value within a Json. It's a sequence of _Position_, being a position
either a _Key_ or an _Index_.

```scala
import json.value.Preamble._
val a:JsPath = "a" / "b" / "c"
val b:JsPath = 0 / 1

val ahead:Position = a.head
ahead.isKey == true

val atail:JsPath = a.tail
atail.head = Key("b")
atail.last = Key("c")

val bhead:Position = b.head
bhead.isIndex == true

//appending paths
val c:JsPath = a // b
c.head == Key("a")
c.last == Index(1)

//prepending paths
val d:JsPath = x \\ y
d.head == Index(0)
d.last == Key("c")

```

The index -1 points to the last element of an array.

#### <a name="jsvalue"><a/>JsValue

Every element in a Json is a _JsValue_. There is a specific type for each value described in [json.org](https://www.json.org).
The best way of exploring that type is applying an exhaustive pattern matching:

```scala

val jsvalue: JsValue = ...

jsvalue match
{
  case primitive: JsPrimitive => primitive match
  {
    case JsStr(json.value) => println("I'm a string")
    case number: JsNumber => number match
    {
      case JsInt(json.value) => println("I'm an integer")
      case JsDouble(json.value) => println("I'm a double")
      case JsLong(json.value) => println("I'm a long")
      case JsBigDec(json.value) => println("I'm a big decimal")
      case JsBigInt(json.value) => println("I'm a big integer")
    }
    case JsBool(json.value) => println("I'm a boolean")
    case JsNull => println("I'm null")
  }
  case json: Json[_] => json match
  {
    case o: JsObj => println("I'm an object")
    case a: JsArray => println("I'm an array")
  }
  case JsNothing => println("I'm a special type!")
}

```
The singleton _JsNothing_ represents nothing. It's a convenient type that makes certain functions
that return a JsValue **total** on their arguments. For example, the Json function

```scala

def apply(path:JsPath):JsValue

```

is total because it returns a JsValue for every JsPath. If there is no element located at the given path,
it returns _JsNothing_. On the other hand, inserting _JsNothing_ at a path in a Json is like removing the 
element located at that path.


#### <a name="creatingjson"><a/>Creating Jsons 

There are several ways of creating Jsons:
 * Using apply methods of companion objects.
 * Parsing an array of bytes, a string or an input stream. When possible, it's always better to work on byte level. If the schema of the Json is known, the fastest way is defining a spec.
 * Creating an empty object and then using the API to insert values.
 
##### <a name="creatingjsonobj"><a/>Creating JsObjs
From a Map:

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
From a sequence of path/value pairs:

```scala
import json.value.Preamble._

JsObj(("type","@Person"),
      ("age", 37),
      ("name", "Rafael"),
      ("gender", "MALE"),
      ("address" / "location" / 0, 40.416775),
      ("address" / "location" / 1, 40.416775),
      ("books_ids" / 0, "00001"),
      ("books_ids" / 1, "00002"),
     )
```

Parsing a string or array of bytes, and the schema of the Json is unknown:

```scala
val str:String = ??? 
val bytes:Array[Byte] = ??? 
val is:InputStream = ??? 

val a:Either[InvalidJson,JsObj] = JsObjParser.parsing(str)
val b:Either[InvalidJson,JsObj] = JsObjParser.parsing(bytes)
val c:Try[JsObj] = JsObjParser.parsing(is)

```

Parsing a string or array of bytes, and the schema of the Json is known. We can define a spec
to define the structure of the Json object (we'll get into details later on). This way, as soon 
as a parsed value doesn't satisfy a spec, the process ends with an error. On the other hand, 
if the parsing succeeds, we already have a validated Json.

```scala
val spec:JsObjSpec = JsObjSpec("a" -> int,
                               "b" -> string,
                               "c" -> bool,
                               "d" -> JsObjSpec("e" -> long
                                                "f" -> JsArraySpec(decimal,decimal)
                                               ),
                               "e" -> arrayOfStr
                              )

val parser:JsObjParser = JsObjParser(spec) //reuse this object

val str:String = "..."
val bytes:Array[Byte] = ...
val is:InputStream = ...

val a:Either[InvalidJson,JsObj] = parser.parsing(str)
val b:Either[InvalidJson,JsObj] = parser.parsing(bytes)
val c:Try[JsObj] = parser.parsing(is)

```

With the API:

```scala

import json.value.Preamble._

JsObj.empty.inserted("a" / "b" / 0, 1)
           .inserted("a" / "b" / 1, 2)
           .inserted("a" / "c", "hi")
```

##### <a name="creatingjsonarray"><a/>Creating JsArrays
From a sequence of values:

```scala
import json.value.Preamble._

JsArray("a", 1, JsObj("a" -> 1), JsNull, JsArr(0,1))
```

From a sequence of path/value pairs:

```scala
import json.value.Preamble._

JsArray((0, "a"),
        (1, 1),
        (2 / "a", 1),
        (3, JsNull),
        (4 / 0, 0),
        (4 / 1, 1)
       )
```

Parsing a string or array of bytes, and the schema of the Json is unknown

```scala

val str:String = "..."
val bytes:Array[Byte] = ...
val is:InputStream = ...

val a:Either[InvalidJson,JsArray] = JsArrayParser.parsing(str)
val b:Either[InvalidJson,JsArray] = JsArrayParser.parsing(bytes)
val c:Try[JsArray] = JsArrayParser.parsing(is)

```

Parsing a string or array of bytes, and the schema of the Json is known. We can define a spec
to define the structure of the Json array(we'll get into details later on):

```scala
val spec:JsArraySpec = JsArraySpec(str,
                                   int,
                                   JsObjSpec("a"->str),
                                   str(nullable=true),
                                   arrOfInt
                                  )

val parser:JsArrayParser = JsArrayParser(spec) //reuse this object

val str:String = "..."
val bytes:Array[Byte] = ...
val is:InputStream = ...

val a:Either[InvalidJson,JsArray] = parser.parsing(str)
val b:Either[InvalidJson,JsArray] = parser.parsing(bytes)
val c:Try[JsArray] = parser.parsing(is)

```

With the API:

```scala

JsArray.empty.appended("a")
             .appended("1")
             .appended(JsOb("a" -> 1))
             .appended(JsNull.NULL)
             .appended(JsArray(0,1))
```

#### <a name="inout"><a/>Putting data in and getting data out
There are one function to put data in a Json specifying a path and a value:

```scala

JsObj   inserted(path:JsPath, value:JsValue, padWith:JsValue = JsNull):JsObj
JsArray inserted(path:JsPath, value:JsValue, padWith:JsValue = JsNull):JsArray

```

The _inserted_ function **always** inserts the value **at the specified path**, creating 
any needed container and padding arrays when necessary.

```scala

json.inserting(path, value)(path) == value // always true: if you insert a value, you'll get it back

JsObj.empty.inserted("a", 1) == JsObj("a" -> 1)
JsObj.empty.inserted("a" / "b", 1) == JsObj("a" -> JsObj("b" -> 1))
JsObj.empty.inserted("a" / 2, "z", pathWith="") = JsObj("a" -> JsArray("","","z"))

```

New elements can be appended and prepended to a JsArray:

```scala

appended(value:JsValue):JsArray

prepended(value:JsValue):JsArray

appendedAll(xs:IterableOne[JsValue]):JsArray

prependedAll(xs:IterableOne[JsValue]):JsArray

```


#### <a name="filtermapreduce"><a/>Filter,map and reduce

_filterAll_, _filterAllKeys_, _mapAll_, _mapAllKeys_ and _reduceAll_ functions **traverse the whole json recursively**. 
All these functions are functors (don't change the structure of the Json).

On the other hand, the functions _filter_, _filterKeys_, _map_, _mapKeys_ and _reduce_ **traverse the first level of the json**.


```scala

val toLowerCase:String => String = _.toLowerCase

json mapAllKeys toLowerCase

val trimIfStr = (x: JsPrimitive) => if (x.isStr) x.toJsStr.map(_.trim) else x

array mapAll trimIfStr

val isNotNull:JsPrimitive => Boolean = _.isNotNull

json filterAll isNotNull

 ```

#### <a name="flattening"><a/>Flattening a Json 

A Json can be seen as a set of (JsPath,JsValue) pairs. The flatten function returns a lazy list of pairs:

```scala

Json flatten:LazyList[(JsPath,JsValue)]

```
Returning a lazy list decouples the consumers from the producer. No matter the number of pairs that will be consumed, the flatten implementation doesn't change.

Let's put an example:

```scala
val obj = JsObj("a" -> 1,
                "b" -> JsArray(1,"m", JsObj("c" -> true, "d" -> JsObj.empty))
               )

obj.flatten(println) // all the pairs are consumed

// (a, 1)
// (b / 0, 1)
// (b / 1, "m")
// (b / 2 / c, true)
// (b / 2 / d, {})
```

#### <a name="specs"><a/>Specs

A Json spec specifies the structure of a Json. Specs have attractive qualities like:
 * Easy to write. You can define Specs in the same way you define a raw Json.
 * Easy to compose. You glue them together and create new ones easily.
 * Easy to extend. There are predefined specs that will cover the most common scenarios, but, any imaginable
 spec can be created from predicates.

Let's go straight to the point and put an example: 


```scala
import json.value.Preamble._
import json.value.spec.Preamble._
import json.value.spec.JsObjSpec._
import json.value.spec.JsArraySpec._

val personSpec = JsObjSpec("@type" -> "Person",
                           "age" -> int,
                           "name" -> str,
                           "gender" -> enum("MALE","FEMALE"),
                           "address" -> JsObjSpec("location" -> JsArraySpec(decimal,
                                                                            decimal
                                                                           )
                                                 ),
                           "books_id" -> arrayOfStr,
                           * -> any
                          )

person.validate(personSpec) == Seq.empty  // no errors

```

I think it's self-explanatory and as it was mentioned, defining a spec is as simple as defining a Json. 
It's declarative and concise, with no ceremony at all. The binding _* -> any_ means: any value 
different than the specified is allowed.

Consider the following specs:

```scala
def objSpec = JsObjSpec("a" -> "hi")

def arrSpec = JsArraySpec(1, any, "a")

```
The only Json that conforms the first spec is _JsObj("a" -> "hi")_. On the other hand, the second spec
defines an array of three elements where the first one is the constant 1, the second one is any value, and the
third one is the constant "a". Arrays like JsArray(1,null,"a"), JsArray(1,true,"a") or JsArray(1,JsObj.empty,"a")
conform that spec.

Reusing and composing specs is very straightforward. Spec composition is a good way of creating complex specs.
You define little blocks and glue them together. Let's put an example:

```scala

def legalAge = JsValueSpec((value: JsValue) => if (value.isInt(_ > 16)) Valid else Invalid("Too young"))

def address = JsObjSpec("street" -> string,
                        "number" -> int,
                       )

def user = JsObjSpec("name" -> string,
                     "id" -> string
                    )

def userWithAddress = user ++ JsObjSpec("address" -> address)

def userWithOptionalAddress = user ++ JsObjSpec("address" -> address.?)

```

#### <a name="futureandtry"><a/>Future and Try monads

Let's compose a Json out of different functions that can fail and are modeled with a Try computation.

```scala
import json.value.Preamble._
import json.value.exc.Preamble._
import json.value.exc.JsObjTry._
import json.value.exc.JsArrayTry._

val address:Try[JsObj] = ???
val email:Try[String] = ???
val latitude:Try[Double] = ???
val longitude:Try[Double] = ???

val person:Try[JsObj] = JsObjTry("type" -> "@Person",
                                 "name" -> "Rafael",
                                 "address" -> address,
                                 "email" -> email,
                                 "company_location" -> JsArrayTry(latitude,longitude)
                                 )

```

Or given a Json, we can create a try using the inserted function:

```scala

val obj:JsObj = ???

val tryObj:Try[JsObj] = obj.inserted("company_location" / 0, latitude)
                           .inserted("company_location" / 1, longitude)

```

Let's conquer the future! We can define futures in the same way and mix them with Try computations!

```scala 

import json.value.Preamble._
import json.value.future.Preamble._
import json.value.future.JsObjFuture._
import json.value.future.JsArrayFuture._

val address:Future[JsObj] = ???
val email:Try[String] = ???
val latitude:Future[Double] = ???
val longitude:Try[Double] = ???

val person:Future[JsObj] = JsObjFeature("type" -> "@Person",
                                        "name" -> "Rafael",
                                        "address" -> address,
                                        "email" -> email,
                                        "company_location" -> JsArrayFuture(latitude,longitude)
                                        )

```

Or given a Json, we can create a future using the inserted function:

```scala
val latitude:Future[Double] = ???
val longitude:Try[Double] = ???

val obj:JsObj = ???

val future:Future[JsObj] = obj.inserted("company_location" / 0, latitude)
                              .inserted("company_location" / 1, longitude)

```

#### <a name="generators"><a/>Generators 

Let me go straight to the point. I'd argue that this is the most declarative,
concise, composable, and beautiful Json generator in the whole wide world! I used property-based-testing with [ScalaCheck](https://www.scalacheck.org) to test
json-values. I developed several Json generators. 

If you practice property-based testing and use ScalaCheck, you'll be able to design composable Json generators
very quickly and naturally, as if you were writing out a Json.

###### <a name="customgens"><a/> Defining custom Json generators
Let's create a person generator:

```scala
import json.value.JsObj
import json.value.Preamble._
import json.value.gen.Preamble._
import json.value.gen.{JsObjGen,JsArrayGen}
import org.scalacheck.Gen

def nameGen: Gen[String] = ???
def birthDateGen: Gen[String] = ???
def latitudeGen: Gen[Double] = ???
def longitudeGen: Gen[Double] = ???
def emailGen: Gen[String] = ???
def countryGen: Gen[String] = ???

def personGen:Gen[JsObj] = JsObjGen("@type" -> "person",
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

If you are using other Json library different than json-values, you can still use this generator
mapping the generated json into its string representation, and then creating your object from that string:


```scala
import x.y.z.MyJson

def myPersonGen:Gen[MyJson] =  personGen.map(MyJson(_.toString))
```


Another way of creating Jsons in **json-values** is from pairs of paths and values:


```scala
import json.value.JsObj
import json.value.JsPath._
import json.value.Preamble._

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
import json.value._
import json.value.JsPath._
import json.value.Preamble._
import json.value.gen._
import json.value.gen.Preamble.{_, given,_}
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
which can be easily achieved using the special value _JsNothing_.
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
import json.value.gen.{RandomJsObjGen,RandomJsArrayGen}

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
 val gen = RandomJsObjGen(objSizeGen= Gen.const(5),
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
* _arrPrimitiveGen: PrimitiveGen_: to control the value of the primitive types generated in arrays.
* _objValueFreq: ValueFreq_: to control the type of the elements generated in objects. For primitive types, the value are
 generated by the generator defined in the param _objPrimitiveGen_.
* _objPrimitiveGen: PrimitiveGen_: to control the value of the primitive types generated in objects.
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

   - 2.13: [It's maintained in the branch master](https://github.com/imrafaelmerino/json-scala-values/tree/master)

     libraryDependencies += "com.github.imrafaelmerino" % "json-scala-values_2.13" % "4.0.0" % "test"

   - 2.12: [It's maintained in the branch scala-2.12](https://github.com/imrafaelmerino/json-scala-values/tree/scala-2.12) 

     libraryDependencies += "com.github.imrafaelmerino" % "json-scala-values_2.12" % "3.3.0" % "test"

Doubling the first % you can tell sbt that it should append the current version of Scala being used to build the library to the dependency’s name:

libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "4.0.0" % "test"

#### <a name="dotty"><a/> Dotty (0.27.0-RC1)

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-dotty-values_0.27/4.0.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-dotty-values_0.27/4.0.0/jar)

[It's maintained in the branch dotty](https://github.com/imrafaelmerino/json-scala-values/tree/dotty)

libraryDependencies += "com.github.imrafaelmerino" %% "json-dotty-values" % "4.0.0"


## <a name="performance"><a/> Performance 

Parsing a string with a spec returns a validated Json. That's why I've compared
json-values with other libraries that perform a Json validation as well:

   - [justify](https://github.com/leadpony/justify)
   - [json-schema-validator](https://github.com/java-json-tools/json-schema-validator)


First benchmark is deserializing a string or array of bytes into a Json: TODO


Second benchmark is serializing a Json  into a string or array of bytes: TODO

## <a name="rp"><a/> Related projects
json-values was first developed in [Java](https://github.com/imrafaelmerino/json-values).
It uses [Jackson](https://github.com/FasterXML/jackson) to parse a string/bytes into
a stream of tokens and [dsl-sjon](https://github.com/ngs-doo/dsl-json) to parse a string/bytes given a spec.

## <a name="release"><a/> Release process
Every time a tagged commit is pushed into master, a Travis CI build will be triggered automatically and 
start the release process, deploying to Maven repositories and GitHub Releases. See the Travis conf file 
**.travis.yml** for further details. On the other hand, the master branch is read-only, and all the commits 
should be pushed to master through pull requests.

If you like the library, you can let me know by starring it. It really helps. If not, much better, 
it means json-values can get better, your feedback we'll be more than welcoming. 