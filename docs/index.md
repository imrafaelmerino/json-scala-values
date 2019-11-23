 - [JsPath](#jspath)
 - [JsValue](#jsvalue)
 - [Creating Jsons](#json-creation)
   - [Json objects](#json-obj-creation)
   - [Json arrays](#json-arr-creation)
 - [Putting data in and getting data out](#data-in-out)
 - [Converting a Json into a LazyList](#lazylist)  
 - [Json spec](#spec)
   - [Predefined specs](#pspecs)
      - [Predefined JsNumber specs](#npspecs)
      - [Predefined JsString specs](#spspecs)
      - [Predefined JsObj specs](#opspecs)
      - [Predefined JsArray specs](#apspecs)
   - [Arbitrary specs](#arspecs)
   - [Optional specs](#optispecs)
   - [Composing specs](#comspecs)
   - [More examples](#exspecs)
 - [Filter, map and reduce](#fmr)  
   - [Filter](#filter)  
   - [Map](#map)  
   - [Reduce](#reduce)  
 - [Set-theory operations](#sto)
   - [Union](#union)  
   - [Intersection](#inter)  
   - [Difference](#diff)  
 - [Implicit conversions](#imconv)  
   
 
## <a name="jspath"></a> JsPath 
A [JsPath](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsPath.html) represents a location of a specific value within a [Json](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/Json.html). It's a sequence of [_Position_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/Position.html), being a position
either a [_Key_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/Key.html) or an [_Index_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/Index.html).

```
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

Like in Python, the index -1 points to the last element of an array.

## <a name="jsvalue"></a> JsValue
Every element in a Json is a [_JsValue_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsValue.html). There is a specific type for each value described in [json.org](https://www.json.org):
* [_JsStr_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsStr.html) represents immutable strings.

* The singletons [_TRUE_]() and [_FALSE_]() represent true and false.

* The singleton [_JsNull_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsNull$.html) represents null.

* [_JsObj_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsObj.html) is a [_Json_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/Json.html) that represents an object, which is an unordered set of name/value pairs.

* [_JsArray_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsArray.html) is a [_Json_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/Json.html) that represents an array, which is an ordered collection of values.

* [_JsNumber_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsNumber.html) represents immutable numbers. There are five different specializations: 
    
    * [_JsInt_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsInt.html)
    
    * [_JsLong_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsLong.html)
    
    * [_JsDouble_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsDouble.html)
    
    * [_JsBigInt_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsBigInt.html)
    
    * [_JsBigDec_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsBigDec.html)

* The singleton [_JsNothing_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsNothing$.html) represents nothing. It's a convenient type that makes certain functions 
that return a JsValue **total** on their arguments. For example, the Json function
```
def apply(path:JsPath):JsValue
```

is total because it returns a JsValue for every JsPath. If there is no  element located at the specified path, 
it returns _JsNothing_. On the other hand, inserting _JsNothing_ in a Json is like doing nothing:

```
json.inserted(path,value.JsNothing) == json
```
 
## <a name="json-creation"></a> Creating Jsons

There are several ways of creating Jsons:
 * From a _Map[String,JsValue]_, using Json constructors. This way, thanks to Scala implicits, turns out to be very 
 declarative and elegant.
 * From a seq of pairs _(JsPath,JsValue)_, using Json constructors. This way is simple and convenient as  well.
 * Parsing a string. This way uses the Jackson library to parse the string into a stream of tokens, and then, immutable 
 Json objects are created. 
 * Creating an empty object and then using the API to insert values.
 
### <a name="json-obj-creation"></a> Json objects
Creation of a Json object from a Map:

```
JsObj("age" -> 37,
      "name" -> "Rafael",
      "address" -> JsObj("location" -> JsArray(40.416775,
                                               -3.703790
                                              )
                        )
      )
```

Creation of a Json object from a sequence of pairs:

```
JsObj(("age", 37),
      ("name", "Rafael"),
      ("address" / "location" / 0, 40.416775),
      ("address" / "location" / 1, 40.416775)
     )
```

Creation of a Json object parsing a String. The result is a _Try_ computation that may fail if the
string is not a well-formed Json object:

```
val computation:Try[JsObj] = JsObj.parsing("{\"a\": 1, \"b\": [1,2]}")
```

Creation of a Json object from an empty Json and inserting elements with the API:

```
JsObj.empty.inserted("a" / "b" / 0, 1)
           .inserted("a" / "b" / 1, 2)
           .inserted("a" / "c", "hi")
```

### <a name="json-arr-creation"></a> Json arrays

Creation of a Json array from a sequence of JsValue:

```
JsArray("a", 1, JsObj("a" -> 1), JsNull, JsArr(0,1)
```

Creation of a Json array from a sequence of pairs:

```
JsArray((0, "a"),
        (1, 1),
        (2 / "a", 1),
        (3, JsNull),
        (4 / 0, 0),
        (4 / 1, 1)
       )
```

Creation of a Json array parsing a String. The result is a _Try_ computation that may fail if the
string is not a well-formed Json array:

```
val computation:Try[JsArray] =JsArray.parsing("[1,2,true]")
```

Creation of a Json array from an empty array and adding elements with the API:

```
JsArray.empty.appended("a")
             .appended("1")
             .appended(JsOb("a" -> 1))
             .appended(JsNull.NULL)
             .appended(JsArray(0,1))
```

## <a name="data-in-out"></a> Putting data in and getting data out

There are two functions to put data in a Json specifying a path and a value:

```
[T<:Json[T]] updated(path:JsPath, value:JsValue):T

[T<:Json[T]] inserted(path:JsPath, value:JsValue, padWith:JsValue = JsNull):T
```

The _updated_ function **never creates new containers** to accommodate the specified value. 
The _inserted_ function **always** inserts the value **at the specified path**, creating any needed container and padding arrays when
necessary.

```
JsObj.empty.updated("a", 1) == JsObj("a" -> 1)
JsObj.empty.updated("a" / "b", 1) == JsObj.empty

JsObj.empty.inserted("a", 1) == JsObj("a" -> 1)
JsObj.empty.inserted("a" / "b", 1) == JsObj("a" -> JsObj("b" -> 1))
JsObj.empty.inserted("a" / 2, 1, pathWith=0) = JsObj("a" -> JsArray(0,0,1))
```

New elements can be appended and prepended to JsArray:

```
appended(value:JsValue):JsArray

prepended(value:JsValue):JsArray

appendedAll(xs:IterableOne[JsValue]):JsArray

prependedAll(xs:IterableOne[JsValue]):JsArray
```

On the other hand, to get a JsValue out of a Json:

```
get(path:JsPath):Option[JsValue]

apply(path:JsPath):JsValue 

```

As you can see, there are two tastes to work with not-found values. The _get_ function would return Optional.empty, whereas the _apply_ function
would return _JsNothing_.

Sometimes it is more convenient to work with primitive types instead of JsValue. For those cases, you can use
the following functions to pull values out of a Json:

```
def int(path: JsPath): Option[Int]

def long(path: JsPath): Option[Long]

def bigInt(path: JsPath): Option[BigInt]

def double(path: JsPath): Option[Double]

def bigDecimal(path: JsPath): Option[BigDecimal]

def string(path: JsPath): Option[String]

def bool(path: JsPath): Option[Boolean]

```

Analogously, instead of using get or apply and then makes the conversion to JsObj or JsArray, the following
functions can be used.

```
def obj(path: JsPath): Option[JsObj]

def array(path: JsPath): Option[JsArray]
```

## <a name="#lazylist"></a> Converting a Json into a LazyList

## <a name="spec"></a> Json spec

A Json [spec](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/spec/index.html) specifies the structure of a Json and validates it. Specs have attractive qualities like:
 * Easy to write. Specs are defined in the same way as a Json is.
 * Easy to compose. You glue them together and create new ones easily.
 * Easy to extend. There are predefined specs that will cover the most common scenarios, but, any imaginable
 spec can be created from a predicate.
 
 Let's go straight to the point and put an example:
 
```
def spec = JsObjSpec( "a" -> int,
                      "b" -> string,
                      "c" -> JsArraySpec(obj,obj),
                      "d" -> decimal,
                      "e" -> arrayOfString,
                      "f" -> boolean,
                      "g" -> "constant",
                      "h" -> JsObjSpec("i" -> enum("A","B","C"),
                                       "j" -> JsArraySpec(integral,string) 
                                      )
                      )
```

As it was mentioned, defining a spec is as simple as defining a Json. It's declarative and
concise, with no ceremony at all. Let's create a more complex spec with more restrictive validations.

```
def spec =  JsObjSpec("a" -> int(minimum=0,
                                 maximum=10,
                                 multipleOf=2
                                ),
                      "b" -> string(pattern="\\d{3}".r),
                      "c" -> JsArraySpec(obj(required=List("a","b")),
                                         obj(maxKeys=5,
                                             minKeys=1
                                            )
                                        ),
                      "d" -> decimalGTE(10),
                      "e" -> arrayOfString(maxItems=10,
                                           unique=true
                                          ).?,
                      "f" -> boolean,
                      "g" -> "constant",
                      "h" -> JsObjSpec("i" -> enum("A","B","C"),
                                       "j" -> JsArraySpec(integral,
                                                          string(minLength=1,
                                                                 maxLength=255
                                                                )
                                                          ) 
                                      )
                      )
```

I'd say that the previous snippet of code is self-explanatory, if not, don't worry, we'll go over every
detail.

### <a name="pspecs"></a> Predefined specs

The predefined Json specs are, most of them, established by the [Json Schema Validation](https://json-schema.org/draft/2019-09/json-schema-validation.html) specification.
They will cover the most common scenarios. 

Before moving on, let's define the most simple spec, which specifies that a value is a constant. For example:

```
def objSpec = JsObjSpec("a" -> "hi")

def arrSpec = JsArraySpec(1, any, "a")

```
The only Json that conforms the first spec is _JsObj("a" -> "hi")_. On the other hand, the second spec 
defines an array of three elements where the first one is the constant 1, the second one is any value, and the 
third one is the constant "a". Arrays like JsArray(1,null,"a"), JsArray(1,true,"a") or JsArray(1,JsObj.empty,"a")
conform that spec.

#### <a name="npspecs"></a>Predefined JsNumber specs

There are four predefined numeric specs:

 * _int_ :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;32 bits precision integers
 * _long_ :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;64 bits precision integers
 * _integral_ :&nbsp;&nbsp;arbitrary-precision integers
 * _decimal_ :&nbsp;&nbsp;decimal numbers
 
It exists the parameters _minimum_ and _maximum_ for all the above specs to specify a bounded interval. 
If the interval is unbounded, the following specs can be used:
 
 * _intGT_&nbsp;&nbsp;&nbsp;&nbsp; _longGT_&nbsp;&nbsp;&nbsp; _integralGT_&nbsp;&nbsp;&nbsp; _decimalGT_       
 * _intLT_&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; _longLT_&nbsp;&nbsp;&nbsp;&nbsp; _integralLT_&nbsp;&nbsp;&nbsp;&nbsp; _decimalLT_       
 * _intLTE_&nbsp;&nbsp;&nbsp;&nbsp;_longLTE_&nbsp;&nbsp;&nbsp;_integralLTE_&nbsp;&nbsp;_decimalLTE_      
 * _intGTE_&nbsp;&nbsp;&nbsp;_longGTE_&nbsp;&nbsp;&nbsp;_integralGTE_&nbsp;&nbsp;_decimalGTE_      
 
where:

 * GT&nbsp;&nbsp;&nbsp;is greater than  (left-open interval) 
 * LT&nbsp;&nbsp;&nbsp;&nbsp;is lower than (right-open interval)
 * LTE&nbsp;&nbsp;is lower than or equal to (right-closed interval)
 * GTE&nbsp;&nbsp;is greater than or equal to (left-closed interval)
 
All the numeric specs accept the optional parameter _multipleOf_.

#### <a name="spspecs"></a> Predefined JsString specs

There are two predefined string specs:

 * _string_ : any kind of string literal
 * _enum_ : an array of constants
 
The _string_ spec accepts the following optional parameters:

 * _minLength:Int_
 * _maxLength:Int_
 * _pattern:Pattern_

#### <a name="opspecs"></a> JsObj predefined specs

The JsObj spec _obj_ accepts the following optional parameters:

 * _minKeys:Int_
 * _maxKeys:Int_
 * _required:Seq[String]_
 * _dependentRequired: Seq[(String, Seq[String])]_ 
 
 The parameter _dependentRequired_ specifies keys that are required if a specific other key is present. For example:
 
```
JsObjSpec("a" -> obj(dependentRequired=List(("a", List("b","c")),
                                            ("d", List("e","f"))
                                           )
                    )
         )
 ```

specifies that if _a_ exists, then _b_ and _c_ must exist too, and if _d_ exists, then _e_ and _f_ must exist too

#### <a name="apspecs"></a> Predefined JsArray specs

There are the following predefined specs:
 
 * _array_ :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;array with any kind of elements
 * _arrayOfInt_ :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;array of 32 bit integers
 * _arrayOfString_ :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;array of literals
 * _arrayOfLong_ :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;array of 64 bit integers
 * _arrayOfDecimal_ :&nbsp;&nbsp;&nbsp;array of decimal
 * _arrayOfIntegral_ :&nbsp;&nbsp;&nbsp;&nbsp;array of arbitrary-precision integers
 * _arrayOfNumber_ :&nbsp;&nbsp;&nbsp;array of numbers
 
All of them accept the optional parameters:
 
 * _minItems:Int_
 * _maxItems:Int_ 
 * _unique:Boolean_
  
### <a name="arspecs"></a> Arbitrary specs

 For those scenarios where the predefined specs are not enough, you can create any imaginable
 spec just defining a predicate and an error message:    
           
```
//spec: a predicate to test the value
//message: function that returns the error message given the value that is evaluated to false on the predicate

def int(spec: Int => Boolean, message: Int => String): JsValueSpec

def long(spec: Long => Boolean, message: Long => String): JsValueSpec

def integral(spec: BigInt => Boolean, message: BigInt => String): JsValueSpec

def decimal(spec: BigDec => Boolean, message: BigDec => String): JsValueSpec

def string(spec: String => Boolean, message: String => String): JsValueSpec

def obj(spec: JsObj => Boolean, message: JsObj => String): JsValueSpec

def array(spec: String => Boolean, message: String => String): JsValueSpec
```     

Similarly, to specify an array where all its elements has to conform an arbitrary spec:

```
//spec: is the specification that every element of the array has to conform
//message: the error message to be returned if an element of the array doesn't conform the spec

def arrayOf(spec: JsValueSpec, message: String)
``` 

### <a name="optispecs"></a> Optional specs
Given a Json spec, all its elements are mandatory. However, it's quite common to have to deal with optional elements.
In the following example

```
JsObjSpec("a" -> string, "b" -> int)
```

both _a_ and _b_ are required. Imagine we want _b_ to be optional, so that JsObj("a" -> "hi") is a valid Json:

```
JsObjSpec("a" -> string, "b" -> int.?)
```

and that's all! _"b" -> int.?_ means "if b exists, it has to be an integer"

Similarly:

```
def a = JsObjSpec(...)

def c = JsArraySpec(any, a.?)
```
The spec _c_ is conformed to JsArray of one or two elements,in which the first element can be anything, 
and the second one, if it exists, has to be a JsObj that conforms to the spec _a_.

### <a name="comspecs"></a> Composing specs
Reusing and composing specs is very straightforward. Spec composition is a good way of creating complex specs. You define
little blocks and glue them together. Let's put an example:

```

def legalAge = JsValueSpec((value: JsValue) => if (value.isInt(_ > 16)) Valid else Invalid("Too young"))

def address = JsObjSpec("street" -> string,
                        "number" -> int,
                       )

def user = JsObjSpec("name" -> string,
                     "id" -> string
                    )

def userWithAddress = user ++ JsObjSpec("address" -> address)

def userWithOptionalAddress = user ++ JsObjSpec("address" -> address.?)

def userWithOptionalAddressAndLegalAge = user ++ JsObjSpec("address" -> address.?) + ("age", legalAge)

def userWithOptionalAddressAndOptionalLegalAge = user ++ JsObjSpec("address" -> address.?) + ("age", legalAge.?)

```
### <a name="exspecs"></a> More examples

 ```
 def arrayOfEvenInts = arrayOf( (value:JsValue) => value.isInt(_ % 2 == 0), "An odd element found")
 JsObjSpec("a" -> arrayOfEvenInts)

 ```

## <a name="fmr"></a> Filter, map and reduce
### <a name="#filter"></a> Filter
### <a name="#map"></a> Map
### <a name="#reduce"></a> Reduce
## <a name="imconv"></a> Set-theory operations
### <a name="#union"></a> Union
### <a name="#inter"></a> Intersection
### <a name="#diff"></a> Difference
## <a name="imconv"></a> Implicit conversions






