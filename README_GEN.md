[![Build Status](https://travis-ci.org/imrafaelmerino/json-scala-values-generator.svg?branch=master)](https://travis-ci.org/imrafaelmerino/json-scala-values-generator)
[![CircleCI](https://circleci.com/gh/imrafaelmerino/json-scala-values-generator/tree/master.svg)](https://circleci.com/gh/imrafaelmerino/json-scala-values-generator/tree/master)
[![codecov](https://codecov.io/gh/imrafaelmerino/json-scala-values-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/imrafaelmerino/json-scala-values-generator)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values-generator&metric=alert_status)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values-generator)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-scala-values-generator&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-scala-values-generator)

[![Gitter](https://badges.gitter.im/json-scala-values/community.svg)](https://gitter.im/json-scala-values/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

- [Introduction](#introduction)
- [What to use _json-scala-values-generator_ for](#whatfor)
- [Installation](#installation)
    - [Scala](#dotty)
    - [Dotty](#scala)
- [Defining custom Json generators](#customgens)
- [Defining random Json generators](#randomgens)
- [Composing Json generators](#composing)
- [Installation](#installation)
    - [Scala](#dotty)
    - [Dotty](#scala)

## <a name="introduction"><a/> Introduction



## <a name="whatfor"><a/> What to use _json-scala-values-generator_ for



## <a name="installation"><a/> Installation
The library is compatible with Scala 2.12, 2.13 and Dotty. Each version is maintained in a separate branch. The reason is because all
the supported versions are quite different and the library itself is different as well to embrace all the new features and idioms introduced in scala 2.13 and Dotty.

#### <a name="scala"><a/> Scala
It's built against 2.12 and 2.13 versions:

   - 2.13: [![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values-generator_2.13/1.2.1)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values-generator_2.13/1.2.1/jar)

     [It's maintained in the branch master](https://github.com/imrafaelmerino/json-scala-values-generator/tree/master)

     libraryDependencies += "com.github.imrafaelmerino" % "json-scala-values-generator_2.13" % "1.2.1" % "test"

   - 2.12:  [![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values-generator_2.12/1.2.1)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values-generator_2.12/1.2.1/jar)

     [It's maintained in the branch scala-2.12](https://github.com/imrafaelmerino/json-scala-values-generator/tree/scala-2.12)

     libraryDependencies += "com.github.imrafaelmerino" % "json-scala-values-generator_2.12" % "1.2.1" % "test"


Doubling the first % you can tell sbt that it should append the current version of Scala being used to build the library to the dependency’s name:

libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values-generator" % "1.2.1" % "test"


A Scala version of [json-values](https://github.com/imrafaelmerino/json-scala-values) is required in both cases:

libraryDependencies += "com.github.imrafaelmerino" %% "json-scala-values" % "3.0.2"

#### <a name="dotty"><a/> Dotty (0.22.0-RC1)

[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-dotty-values-generator_0.22/3.2.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-dotty-values-generator_0.22/3.2.0/jar)

libraryDependencies += "com.github.imrafaelmerino" %% "json-dotty-values-generator" % "3.2.0" % "test"

[It's maintained in the branch dotty](https://github.com/imrafaelmerino/json-scala-values-generator/tree/dotty)

A Dotty version of [json-values](https://github.com/imrafaelmerino/json-scala-values/tree/dotty) is required:

libraryDependencies += "com.github.imrafaelmerino" %% "json-dotty-values" % "3.2.0"

## <a name="customgens"><a/> Defining custom Json generators

Using **json-scala-values**, you can create Jsons in different ways. One of them
turns out to be very natural because it's  close to the Json representation itself.
It may ring a bell if you have taken the Scala courses from Martin Odersky on Coursera.

```
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

```
import jsonvalues.JsObj
import jsonvalues.Preamble.{given _}
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


```
import x.y.z.MyJson

def json.value.json.value.gen:Gen[MyJson] =  personGen.map(MyJson(_.toString))
```


Another way of creating Jsons in **json-scala-values** is from pairs of paths and values:


```
import jsonvalues.JsObj
import jsonvalues.JsPath._
import jsonvalues.Preamble.{given _}

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


 ```
import jsonvalues._
import jsonvalues.JsPath._
import jsonvalues.Preamble.{given _}
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


```
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

```
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

## <a name="randomgens"><a/> Defining random Json generators

There are times when you are only interested in generating random Jsons; after all, every
function of a Json API has to work, no matter the Json it's tested with.


```
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
 ```
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

```
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


```
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

## <a name="composing"><a/> Composing Json generators

Composing Json generators is key in order to handle complexity and reuse code avoiding repetition. There are two ways, inserting pairs into generators and
joining generators:

```
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


