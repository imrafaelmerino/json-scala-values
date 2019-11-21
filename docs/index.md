 - [JsPath](#jspath)
 - [JsValue](#jsvalue)
 - [Creating Jsons](#json-creation)
   - [Json objects](#json-obj-creation)
   - [Json arrays](#json-arr-creation)
 - [Putting data in and getting data out](#data-in-out)
   - [Obtaining primitive types](#obtaining-primitive-types)
   - [Obtaining Jsons](#obtaining-jsons)
   - [Putting data at any location](#putting-data-by-path)
   - [Manipulating arrays](#manipulating-arrays)
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
   - [Intersection](#union)  
   - [Difference](#union)  
   
 
## <a name="jspath"></a> JsPath 
A JsPath represents a location of a specific value within a JSON. It's a seq of Position, being a position
either a Key or an Index.

```
val x:JsPath = "a" / "b" 

val y:JsPath = 0 / 1 

val xhead:Position = x.head
xhead.isKey == true

val yhead:Position = y.head
yhead.isIndex == true

//appending paths
val z:JsPath = x // y
z.head == Key("a")
z.last == Index(1)
```

## <a name="jsvalue"></a> JsValue
Every element in a Json is a _value.JsValue_. There is a specific type for each value described in [json.org](https://www.json.org):
* _value.JsStr_ represents immutable strings.

* The singletons _value.JsBool.TRUE_ and _value.JsBool.FALSE_ represent true and false.

* The singleton _value.JsNull_ represents null.

* _value.JsObj_ is a _value.Json_ that represents an object, which is an unordered set of name/value pairs.

* _value.JsArray_ is a _value.Json_ that represents an array, which is an ordered collection of values.

* _value.JsNumber_ represents immutable numbers. There are five different specializations: 
    
    * _value.JsInt_
    
    * _value.JsLong_
    
    * _value.JsDouble_
    
    * _value.JsBigInt_
    
    * _value.JsBigDec_

* The singleton _value.JsNothing_ represents nothing. It's a convenient type that makes certain functions 
that return a JsValue **total** on their arguments. For example, the function
 _JsValue get(JsPath)_ is total because it returns a JsValue for every JsPath. If there is no 
 element located at the specified path, it returns _NOTHING_. On the other hand, given a function which returned value is inserted in a Json,
 it's possible not to insert anything just returning _NOTHING_. 
 
## <a name="json-creation"></a> Creating Jsons
There are several ways of creating Jsons:
 * From a _Map[String,JsValue]_, using Json constructors. This way, thanks to Scala implicits, turns out to be very declarative and elegant.
 * From a seq of pairs _(JsPath,JsValue)_, using Json constructors. This way is simple and convenient as  well.
 * Parsing a string. This function uses the Jackson library to parse the string into a stream of tokens and then, persistent Json objects are created. 
 * Creating an empty object and then using the API to insert values.
### <a name="json-obj-creation"></a> Json objects
Creation of a Json object from a Map:
&nbsp;
```
JsObj("age" -> 37,
      "name" -> "Rafael",
      "address" -> JsObj("location" -> JsArray(40.416775,
                                               -3.703790
                                              )
                        )
      )
```
&nbsp;
Creation of a Json object from a list of pairs:
&nbsp;
```
JsObj(("age", 37),
      ("name", "Rafael"),
      ("address" / "location" / 0, 40.416775),
      ("address" / "location" / 1, 40.416775)
     )
```

Creation of a Json object parsing a String. The result is a Try computation that may fail if the
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

Creation of a Json array from a sequence of Json values:

```
JsArray("a",1,JsObj("a" -> 1),JsNull.NULL,JsArr(0,1)
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

Creation of a Json array parsing a String. The result is a Try computation that may fail if the
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

[T<:Json[T]] inserted(path:JsPath, value:JsValue, padWith:JsValue = JsNull.NULL):T
```

Updated **never creates new containers** to accommodate the specified value. 
Inserted **always** inserts the value **at the specified path**, creating any needed container and padding arrays when
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

On the other hand, to get a JsValue out of a Json, there are four methods:

```
get(path:JsPath):Option[JsValue]
apply(path:JsPath):JsValue 

get(pos:Position):Option[JsValue]
apply(pos:Position):JsValue

```

As you can see, there are two tastes to work with not found JsValue. The _get_ functions would return Optional.empty, whereas the _apply_ functions
would return _JsNothing_.

### <a name="obtaining-primitive-types"></a> Getting out primitive types

Sometimes is more convenient to work with primitive types instead of JsValue. For those cases you can use
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
### <a name="obtaining-jsons"></a> Getting out Jsons

Analogously, instead of using get or apply an then makes the conversion to JsObj or JsArray, the following
functions can be used.

```
def obj(path: JsPath): Option[JsObj]

def array(path: JsPath): Option[JsArray] = get(path).filter(_.isArr).map(_.asJsArray)
```
### <a name="putting-data-by-path"></a> Putting data at any location

### <a name="manipulating-arrays"></a> Manipulating arrays

## <a name="#lazylist"></a> Converting a Json into a LazyList

## <a name="spec"></a> Json spec

A Json spec specifies the structure of a Json and validates it. Specs have attractive qualities like:
 * Easy to write. Specs are defined in the same way as a Json is.
 * Easy to compose. You can compose them and create new ones easily.
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

The predefined Json specs are, most of them, the established by the [Json Schema Validation](https://json-schema.org/draft/2019-09/json-schema-validation.html) specification
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

 * _int_: 32 bits precision integers
 * _long_: 64 bits precision integers
 * _integral_: arbitrary-precision integers
 * _decimal_: decimal numbers
 
It exists the parameters _minimum_ and  _maximum_ for all the above specs to specify a bounded interval. 
If the interval is unbounded, the following specs can be used:
 
 * _intGT_, _longGT_,  _integralGT_,  _decimalGT_       
 * _intLT_,  _longLT_,  _integralLT_,  _decimalLT_       
 * _intLTE_, _longLTE_, _integralLTE_, _decimalLTE_      
 * _intGTE_, _longGTE_, _integralGTE_, _decimalGTE_      
 
where:

 * GT is greater than: left-open interval 
 * LT is lower than: right-open interval
 * LTE is lower than or equal to,: right-closed interval
 * GTE is greater than or equal to: left-closed interval
 
All the numeric specs accept the optional parameter _multipleOf_.

#### <a name="spspecs"></a> Predefined JsString specs

There are two predefined string specs:

 * _string_: any kind of string literal
 * _enum_: an array of constants
 
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
 
 _dependentRequired_ specifies keys that are required if a specific other key is present. For example:
 
```
JsObjSpec("a" -> obj(dependentRequired=List(("a", List("b","c")),
                                            ("d", List("e","f"))
                                           )
                    )
         )
 ```

specifies that if _"a" / "a"_ exists, then _"a" / "b"_ and _"a" / "c"_ must exist too, 
and if _"a" / "d"_ exists, then _"a" / "e"_ and _"a" / "f"_ must exist too

#### <a name="apspecs"></a> Predefined JsArray specs

There are the following predefined specs:
 
 * _array_: array with any kind of elements
 * _arrayOfInt_: array of 32 bit integers
 * _arrayOfString_: array of literals
 * _arrayOfLong_: array of 64 bit integers
 * _arrayOfDecimal_: array of decimal
 * _arrayOfIntegral_: array of arbitrary-precision integers
 * _arrayOfNumber_: array of numbers
 
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
_c_ is conformed by arrays of one or two elements. The first element can be anything, and the second one, if it exists, has to conform _a_

### <a name="comspecs"></a> Composing specs
Reusing and composing specs is very straightforward. Composition is a good way of handling complexity. You define
little blocks and glue them together. Let's put an example

```

def legalAge = JsValueSpec((value: JsValue) => if (value.isInt(_ > 16)) Valid else Invalid("Too young"))

def address = JsObjSpec("street" -> string,
                        "number" -> int,
                        "zip_code" -> string
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






