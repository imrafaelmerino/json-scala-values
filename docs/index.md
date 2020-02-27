 - [Persistent data structures](#pds)
 - [JsPath](#jspath)
 - [JsValue](#jsvalue)
 - [Creating Jsons](#json-creation)
   - [Json objects](#json-obj-creation)
   - [Json arrays](#json-arr-creation)
 - [Putting data in and getting data out](#data-in-out)
 - [Flattening a Json](#lazylist)  
 - [Json Spec](#spec)
 - [Json Try](#try)
 - [Json Future](#fut)
 - [Json generator](#gen)
 - [Filter, map and reduce](#fmr)  
   - [filter](#filter)  
   - [map](#map)  
   - [reduce](#reduce) 
 - [Set-theory operations](#sth)   
    - [Union](#union)  
 - [Optics](#optics)    
   - [Accessors with lenses](#lenses)    
   - [Manipulating JsValue with prisms](#prisms)    
   - [Getters with optionals](#optionals)    
 - [Performance](#performance)  
   
## <a name="pds"></a> Persistent data structures  
How do we make changes to immutable structures or values in a inexpensive way? Using persistent data structures. Copy-on-write is inefficient and the performance goes down as you produce new values.
Why don't we have a persistent Json? This is the question I asked myself when I got into functional programming. Since I found out no answer, I decided to implement a persistent Json.  

## <a name="jspath"></a> JsPath 
A _JsPath_ represents a location of a specific value within a Json. It's a sequence of _Position_, being a position
either a _Key_ or an _Index_.

```
import value.Preamble._
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

## <a name="jsvalue"></a> JsValue
Every element in a Json is a _JsValue_. There is a specific type for each value described in [json.org](https://www.json.org).
The best way of exploring that type is applying an exhaustive pattern matching:
```
val jsvalue: JsValue = ...

jsvalue match
{
  case primitive: JsPrimitive => primitive match
  {
    case JsStr(value) => println("I'm a string")
    case number: JsNumber => number match
    {
      case JsInt(value) => println("I'm an integer")
      case JsDouble(value) => println("I'm a double")
      case JsLong(value) => println("I'm a long")
      case JsBigDec(value) => println("I'm a big decimal")
      case JsBigInt(value) => println("I'm a big integer")
    }
    case JsBool(value) => println("I'm a boolean")
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
The singleton [_JsNothing_](https://www.javadoc.io/doc/com.github.imrafaelmerino/json-scala-values_2.13/latest/value/JsNothing$.html) represents nothing. It's a convenient type that makes certain functions 
that return a JsValue **total** on their arguments. For example, the Json function
```
def apply(path:JsPath):JsValue
```

is total because it returns a JsValue for every JsPath. If there is no element located at the given path, 
it returns _JsNothing_. On the other hand, inserting _JsNothing_ at a path in a Json is like removing the element located at
that path. 
 
## <a name="json-creation"></a> Creating Jsons

There are several ways of creating Jsons:
 * From maps and seqs using apply methods of companion objects.
 * Parsing an array of bytes, a string or an input stream. When possible, it's always better to work on byte level.
 If the schema of the Json is known, the fastest way is defining a spec. 
 * Creating an empty object and then using the API to insert new values.
 
### <a name="json-obj-creation"></a> Json objects
Remember that Scala’s Predef object offers an implicit conversion that lets you write maps 
with the syntax key -> value. It reads better and when possible, it's the recommended way.
```
import value.Preamble._

JsObj("age" -> 37,
      "name" -> "Rafael",
      "address" -> JsObj("location" -> JsArray(40.416775,
                                               -3.703790
                                              ),
                         "city": "Madrid"
                        )
      )
```

or from a sequence of path/value pairs:

```
import value.Preamble._

JsObj(("age", 37),
      ("name", "Rafael"),
      ("address" / "location" / 0, 40.416775),
      ("address" / "location" / 1, 40.416775),
      ("address" / "city", "Madrid")
     )
```

Parsing a string, an array of bytes or an input stream which represents a Json object with an **unknown schema**.

```
val str:String = "..."
val bytes:Array[Byte] = ...
val is:InputStream = ...

val a:Either[InvalidJson,JsObj] = JsObjParser.parsing(str)
val b:Either[InvalidJson,JsObj] = JsObjParser.parsing(bytes)
val c:Try[JsObj] = JsObjParser.parsing(is)

```

Like the previous one, but the schema of the Json object **is known**, in which case **a spec can be defined**.
```
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


Creation of a Json object from an empty Json and inserting elements with the API:

```
import value.Preamble._

JsObj.empty.inserted("a" / "b" / 0, 1)
           .inserted("a" / "b" / 1, 2)
           .inserted("a" / "c", "hi")
```

### <a name="json-arr-creation"></a> Json arrays

Creation of a Json array from a sequence of values:

```
import value.Preamble._

JsArray("a", 1, JsObj("a" -> 1), JsNull, JsArr(0,1))
```

Creation of a Json array from a sequence of pairs:

```
import value.Preamble._

JsArray((0, "a"),
        (1, 1),
        (2 / "a", 1),
        (3, JsNull),
        (4 / 0, 0),
        (4 / 1, 1)
       )
```

Parsing a string, an array of bytes or an input stream which represents a Json array with an **unknown schema**.

```
val str:String = "..."
val bytes:Array[Byte] = ...
val is:InputStream = ...

val a:Either[InvalidJson,JsArray] = JsArrayParser.parsing(str)
val b:Either[InvalidJson,JsArray] = JsArrayParser.parsing(bytes)
val c:Try[JsArray] = JsArrayParser.parsing(is)

```

Like the previous one, but the schema of the Json array **is known**, in which case **a spec can be defined**.

```
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

Creation of a Json array from an empty array and appending elements with the API:

```
JsArray.empty.appended("a")
             .appended("1")
             .appended(JsOb("a" -> 1))
             .appended(JsNull.NULL)
             .appended(JsArray(0,1))
```

## <a name="data-in-out"></a> Putting data in and getting data out

There are one function to put data in a Json specifying a path and a value:

```
JsObj   inserted(path:JsPath, value:JsValue, padWith:JsValue = JsNull):JsObj
JsArray inserted(path:JsPath, value:JsValue, padWith:JsValue = JsNull):JsArray
```

The _inserted_ function **always** inserts the value **at the specified path**, creating any needed container and padding arrays when
necessary. It's an important property that allow us to reason about the programs we write. After all, Functional
programming is all about honesty.

```
// always true: if you insert a value, you'll get it back
json.inserting(path,value)(path) == value 

JsObj.empty.inserted("a", 1) == JsObj("a" -> 1)
JsObj.empty.inserted("a" / "b", 1) == JsObj("a" -> JsObj("b" -> 1))
JsObj.empty.inserted("a" / 2, "z", pathWith="") = JsObj("a" -> JsArray("","","z"))
```

New elements can be appended and prepended to a JsArray:

```
appended(value:JsValue):JsArray

prepended(value:JsValue):JsArray

appendedAll(xs:IterableOne[JsValue]):JsArray

prependedAll(xs:IterableOne[JsValue]):JsArray
```

On the other hand, to get a JsValue out of a Json:

```
apply(path:JsPath):JsValue 

```

The function is total on its argument because it always returns a _JsValue_. As it was mentioned before, when no element
is found, _JsNothing_ is returned.

## <a name="#lazylist"></a>Flattening a Json 

A Json can be seen as a set of (JsPath,JsValue) pairs. The flatten function returns a lazy list of pairs:

```
Json flatten:LazyList[(JsPath,JsValue)]

```
Returning a lazy list decouples the consumers from the producer. No matter the number of pairs that will be consumed, the flatten implementation doesn't change.

Let's put an example:

```
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

## <a name="spec"></a> Json Spec

A Json spec specifies the structure of a Json. Specs have attractive qualities like:
 * Easy to write. You can define Specs in the same way you define a raw Json.
 * Easy to compose. You glue them together and create new ones easily.
 * Easy to extend. There are predefined specs that will cover the most common scenarios, but, any imaginable
 spec can be created from predicates.
 
Let's go straight to the point and put an example:
 
```
import value.Preamble._
import value.spec.Preamble._
import value.spec.JsObjSpec._
import value.spec.JsArraySpec._

def spec = JsObjSpec( "a" -> int,
                      "b" -> string,
                      "c" -> JsArraySpec(obj,obj),
                      "d" -> decimal(required=false),
                      "e" -> arrayOfStr(elemNullable=true),
                      "f" -> boolean(nullable=true),
                      "g" -> "constant",
                      "h" -> JsObjSpec("i" -> consts("A","B","C"),
                                       "j" -> JsArraySpec(integral,string) 
                                      ),
                      * -> any
                      )
```

I think it's self-explanatory and as it was mentioned, defining a spec is as simple as defining a Json. It's declarative and
concise, with no ceremony at all. The binding * -> any means: any value different than the specified is allowed.

Let's define the most simple spec, which specifies that a value is a constant. For example:

```
def objSpec = JsObjSpec("a" -> "hi")

def arrSpec = JsArraySpec(1, any, "a")

```
The only Json that conforms the first spec is {"a" -> "hi"}. On the other hand, the second spec 
defines an array of three elements where the first one is the constant 1, the second one is any value, and the 
third one is the constant "a". Arrays like [1,null,"a"], [1,true,"a"] or [1,JsObj.empty,"a"]
conform that spec.

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

def userWithLegalAge = user + ("age", legalAge)

def userWithAddress = user ++ JsObjSpec("address" -> address)

def userWithOptionalAddress = user ++ JsObjSpec("address" -> address.?)

```

## <a name="try"></a> Json Try
Let's compose a Json out of different functions that can fail and are modeled with a Try computation. 

```
import value.Preamble._
import value.exc.Preamble._
import value.exc.JsObjTry._
import value.exc.JsArrayTry._

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

```
val obj:JsObj = ???

val tryObj:Try[JsObj] = obj.inserted("company_location" / 0, latitude)
                           .inserted("company_location" / 1, longitude)

```

## <a name="fut"></a> Json Future
Let's conquer the future! We can define futures in the same way and mix them with Try computations!

```
import value.Preamble._
import value.future.Preamble._
import value.future.JsObjFuture._
import value.future.JsArrayFuture._

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

```
val obj:JsObj = ???

val future:Future[JsObj] = obj.inserted("company_location" / 0, latitude)
                              .inserted("company_location" / 1, longitude)

```

## <a name="gen"></a> Json generator

As you can imagine and it was pointed out in the [readme](https://github.com/imrafaelmerino/json-scala-values) of the project, defining a Json generator to do Property-Based-Testing is as simple and beautiful as the previous
examples. Defining jsons, specs, futures, tries or generators is a breeze! For further details on generators, go to the project [documentation](https://github.com/imrafaelmerino/json-scala-values-generator)

## <a name="fmr"></a> Filter, map and reduce

filterAll, filterAllKeys, mapAll, mapAllKeys and reduceAll functions **traverse the whole json recursively**. All these functions are functors.

On the other hand, the functions filter and map traverse the first level of the json.

### <a name="#filter"></a> Filter
Let's remove those keys that don't satisfy a given predicate:

```
val obj = JsObj("a" -> 1,
                "b" -> 2,
                "c" -> JsArray(true, JsObj("a" -> 3,
                                           "b" -> 4 
                                          )
                               ) 
                )

val isNotA:String => Boolean = _!="a"

obj filterAllKeys isNotA

// and the result is:

JsObj("b" -> 2,
      "c" -> JsArray(true, JsObj("b" -> 4))
     )
```


### <a name="#map"></a> Map
### <a name="#reduce"></a> Reduce
## <a name="#sth"></a> Set-theory operations
### <a name="#union"></a> Union
## <a name="#optics"></a> Optics
### <a name="#lenses"></a> Accessors with lenses   
### <a name="#prisms"></a> Manipulating JsValue with prisms   
### <a name="#optionals"></a> Getters with optionals   
## <a name="#performance"></a> Performance

Parsing a string with a spec returns a validated Json. That's why I've compared
json-values with other libraries that perform a Json validation as well:

   - [justify](https://github.com/leadpony/justify)
   - [json-schema-validator](https://github.com/java-json-tools/json-schema-validator)
    

First benchmark is deserializing a string or array of bytes into a Json:


Second benchmark is serializing a Json  into a string or array of bytes:







