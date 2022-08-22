<img src="./logo/package_twitter_if9bsyj4/color1/full/coverphoto/color1-white_logo_dark_background.png" alt="logo"/>

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-values&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=imrafaelmerino_json-values)
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-values/11.7.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-values/11.7.0/jar)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=imrafaelmerino_json-values&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=imrafaelmerino_json-values)

“_Simplicity is a great virtue, but it requires hard work to achieve it and education to appreciate it.
And to make matters worse: complexity sells better._”
**Edsger Wybe Dijkstra**

- [Code wins arguments](#cwa)
- [Introduction](#introduction)
- [What to use _json-values_ for and when to use it](#whatfor)
- [When not to use it](#notwhatfor)
- [How-To](#how-to)
    - [JsPath](#jspath)
    - [JsValue](#jsvalue)
    - [Creating Jsons](#creatingjson)
        - [Creating JsObj](#creatingjsonobj)
        - [Creating JsArray](#creatingjsonarray)
    - [Putting data in and getting data out](#inout)
    - [Filter, map and reduce](#filtermapreduce)
    - [Specs](#specs)
    - [Generators](#gen)
    - [Optics](#optics)
- [Requirements](#requirements)
- [Installation](#installation)
- [Related projects](#rp)
- [Sponsors](#sponsor)

## <a name="cwa"><a/> Code wins arguments

**JSON creation**

```java 

JsObj.of("name",JsStr.of("Rafael"),
         "languages", JsArray.of("Java", "Scala", "Kotlin"),
         "age", JsInt.of(1),
         "address", JsObj.of("street", JsStr.of("Elm Street"),
                             "coordinates", JsArray.of(3.32, 40.4)
                            )
        );

```

**JSON validation**

```java 

JsObjSpec spec = 
        JsObjSpec.of("name", str(),
                     "languages", arrayOfStr(),
                     "age", integer(),
                     "address", JsObjSpec.of("street",str(),
                                             "coordinates", tuple(decimal(),
                                                                  decimal()
                                                                  )
                                             )
                    )
                 .setOptionals("address");
    
```   

**JSON generation**

```java 
          
Gen<JsObj> gen = 
        JsObjGen.of("name", JsStrGen.biased(0,100),
                    "languages", JsArrayGen.biased(JsStrGen.digit(),0,10),
                    "age", JsIntGen.biased(0,100),
                    "address", JsObjGen.of("street", JsStrGen.alphanumeric(0,200),
                                           "coordinates", JsTupleGen.of(JsBigDecGen.biased(),
                                                                        JsBigDecGen.biased())
                                           )
                    )
                .setAllOptional();
        
                  
Gen<JsObj> validDataGen =  gen.suchThat(spec);

Gen<JsObj> invalidDataGen = gen.suchThatNo(spec);

```

The biased generators generate, with higher probability, values that are proven
to cause more bugs in our code (zero, blank strings ...)

**JSON manipulation free of NullPointerException with optics:**

```java 


//let's craft a function using lenses and optionals

Function<JsObj,JsObj> modify = 
    ageLens.modify.apply(n -> n + 1)
           .andThen(nameLens.modify.apply(String::trim))
           .andThen(cityOpt.set.apply("Paris"))
           .andThen(latitudeLens.modify.apply(lat -> -lat))
           .andThen(languagesLens.modify.apply(lan -> lan.append(JsStr.of("Clojure"))));
           

          
JsObj updated = modify.apply(person); 

```
No if-else conditions, no null checks, and I'd say it's pretty expressive and concise.
As you may notice, each field has an associated optic defined:

- age -> ageLens
- name -> nameLens
- city -> cityOpt
- latitude  -> latitudeLens 
etc

and we just create functions, like _modify_ in the previous example, 
putting optics together (composition is key to handle complexity).

**Filter and map were never so easy!** 

```java 
          
Function<String,String> toSneakCase =  key -> {...};

json.mapKeys(toSneakCase)
    .mapValues(JsStr.prism.modify.apply(String::trim))
    .filterValues(JsValue::isNotNull);
                    
```

Performance. Did you see that!?

I've picked some json-schema implementations from https://json-schema.org/implementations.html
and then parse and validate a random JSON from a string. Find below the results of the
benchmark using [jmh](https://openjdk.java.net/projects/code-tools/jmh/)

<img src="./performance_parsing_json.png" alt="parsing string comparison"/>

You can find more details in the
class [JsDeserializers](./../benchmarking/src/main/java/jsonvalues/benchmark/JsDeserializers.java)

Disclaimer: If you know a better alternative or can improve this result, I'm more
than glad to change it.

## <a name="introduction"><a/> Introduction

Welcome to **json-values**, the first-ever JSON library in _Java_ implemented with
persistent data structures.

One of the most essential aspects of FP is immutable data structures,
better known in FP jargon as values.
It's a fact that, when possible, working with values leads to code with fewer bugs, is more
readable, and is easier to maintain. Item 17 of Effective Java states that we must minimize
mutability. Still, sometimes it's at the cost of losing performance because the
[copy-on-write](https://en.wikipedia.org/wiki/Copy-on-write)
approach is very inefficient for significant data structures. Here is where persistent data
structures come into play.

Most functional languages, like Haskell, Clojure, and Scala, implement persistent data
structures natively. Java doesn't.

The standard Java programmer finds it strange to work without objects and all the machinery
of frameworks and annotations. FP is all about functions and values; that's it. I will try
to cast some light on how we can manipulate JSON with json-values following a purely
functional approach.


## <a name="whatfor"><a/> What to use json-values for and when to use it

* You need to deal with Jsons, and you want to program following a functional style, **using just functions and values**,
  but you can't benefit from all the advantages that immutability brings to your code because **Java doesn't provide
  [Persistent Data Structures](https://en.wikipedia.org/wiki/Persistent_data_structure)**.
* For those architectures that work with JSON end-to-end, it's extremely safe and efficient to have a persistent Json. Think of actors sending JSON messages one to each other for example.
* You manipulate JSON all the time, and you'd like to do it with less ceremony. json-values is declarative and
  takes advantage of a lot of concepts from FP to define a powerful API.
* Generating JSON to do Property-Based-Testing is child's play with json-values.
* Generating specifications to validate JSON and parse strings or bytes very efficiently is a piece of cake.
* Simplicity matters and I'd argue that json-values is simple.
* As _**Pat Helland**_ said, [Immutability Changes Everything!](http://cidrdb.org/cidr2015/Papers/CIDR15_Paper16.pdf)


## <a name="how-to"><a/> How-To

### <a name="jspath"><a/>JsPath

The type _JsPath_ represents a location of a specific value within a JSON. It's a sequence of _Position_, being a position
either a _Key_ or an _Index_. Exists two different ways to create a path:

- Parsing a path-like string using the static factory method _JsPath.path_, where the path follows 
the Json Pointer specification [RFC 6901](http://tools.ietf.org/html/rfc6901)

```java   

JsPath path =  JsPath.path("/a/b/0");

Position head = path.head();

Assertions.assertEquals(head,
                        Key.of("a")
                        );

JsPath tail = path.tail();

Assertions.assertEquals(tail.head(),
                        Key.of("b")
                       );

Assertions.assertEquals(tail.last(),
                        Index.of(0)
                       );
```

- Using the static factory methods _JsPath.fromKey_ or _JsPath.fromIndex_ to create
a one-position path and then the methods _index_ or _key_ to append more keys or indexes:

```java   

JsPath.fromKey("a").key("b").index(0);

```

or

```java   

JsPath.empty().key("a").key("b").index(0);

```


### <a name="jsvalue"><a/>JsValue

Every element in a Json is a subtype of _JsValue_. There is a specific type for each value described
in [json.org](https://www.json.org): 

- String 
- Number 
- Null
- JSON object  
- JSON array

There are five number specializations: 

- Integer 
- Long 
- Double 
- BigDecimal
- BigInteger

json-values adds support for two more types:

- Instant
- Binary or array of bytes (byte[])

Instants are serialized into their string representation according to ISO-8601, 
and the binary type is serialized into a string encoded in base 64.

When it comes to the _equals_ method, json-values is data oriented, I mean, two JSON
are equals if they represent the same piece of information. For example,
the following JSONs xs and ys have values with different primitive types 
and the keys don't follow the same order. 

```java  

JsObj xs = JsObj.of("a", JsInt.of(1000),
                    "b", JsBigDec.of(BigDecimal.valueOf(100_000_000_000_000L)),
                    "c", JsInstant.of("2022-05-25T14:27:37.353Z"),
                    "d", JsStr.of("aGkh")
                    );

JsObj ys = JsObj.of("b", JsBigInt.of(BigInteger.valueOf(100_000_000_000_000L)),
                    "a", JsLong.of(1000L),
                    "d", JsBinary.of("hi!".getBytes(StandardCharsets.UTF_8)),
                    "c", JsStr.of("2022-05-25T14:27:37.353Z")
                    );  

```

Nevertheless, since both JSON represents the same piece of information:

```json   

{
  "a": 1000,
  "b": 100000000000000,
  "c": "2022-05-25T14:27:37.353Z",
  "d": "aGkh"
}

```

it makes sense that both of them are equals, and therefore they have the same hashcode.

````java  

Assertions.assertEquals(xs, ys);    
Assertions.assertEquals(xs.hashcode(), ys.hashcode());   

````

### <a name="creatingjson"><a/>Creating JSON

There are several ways of creating JSON:
* Using the static factory methods _of_.
* Parsing an array of bytes or a string. If the schema of the Json is known, 
the fastest way is to define a parser from a spec.
* Creating an empty object and then using the API to insert values.

### <a name="creatingjsonobj"><a/>Creating JsObj

Let's create the following JSON

```json

{
    "name": "Rafael",
    "surname": "Merino",
    "phoneNumber": 6666666,
    "registrationDate": "2019-01-21T05:47:26.853Z",
    "addresses": [
        {
            "coordinates": [39.8581, -4.02263],
            "city": "Toledo",
            "zipCode": "45920",
            "tags": ["homeAddress"]
        },
        {
            "coordinates": [39.8581, -4.02263],
            "city": "Madrid",
            "zipCode": "28029",
            "tags": ["workAddress"]
        }
    ]
}

```

**Using the static factory methods provided by json-values:**

```java     
 
JsObj person = 
    JsObj.of("name", JsStr.of("Rafael"),
             "surname", JsStr.of("Merino"),
             "phoneNumber", JsStr.of("6666666"),
             "registrationDate", JsInstant.of("2019-01-21T05:47:26.853Z"),
             "addresses", JsArray.of(JsObj.of("coordinates", JsArray.of(39.8581, -4.02263),
                                              "city", JsStr.of("Toledo"),
                                              "zipCode", JsStr.of("45920"),
                                              "tags", JsArray.of("workAddress")
                                             ),
                                     JsObj.of("coordinates", JsArray.of(40.4168, 3.7038),
                                              "city", JsStr.of("Madrid"),
                                              "zipCode", JsStr.of("28029"),
                                              "tags", JsArray.of("homeAddress", "amazon")
                                             )
                                    )
            );
            
```

As you can see, its definition is like raw JSON. It’s a recursive data structure.
You can nest as many JSON objects as you want. Think of any imaginable JSON, and
you can write it in no time.

Instead of keys and a nested structure, it's possible to create a JSON object
from their paths, which turns out to be really convenient as well:

```java   
     
JsObj person = 
        JsObj.of(path("/name"), JsStr.of("Rafael"),
                 path("/surname"), JsStr.of("Merino"),
                 path("/phoneNumber"), JsStr.of("6666666"),
                 path("/registrationDate"), JsInstant.of("2019-01-21T05:47:26.853Z"),
                 path("/addresses/0/coordinates/0"), JsDouble.of(39.8581),
                 path("/addresses/0/coordinates/1"), JsDouble.of(-4.02263),
                 path("/addresses/0/city"), JsStr.of("Toledo"),
                 path("/addresses/0/tags"), JsArray.of("workAddress"),
                 path("/addresses/1/coordinates/0"),  JsDouble.of(40.4168),
                 path("/addresses/1/coordinates/1"), JsDouble.of(3.7038),
                 path("/addresses/1/city"), JsStr.of("Madrid"),
                 path("/addresses/1/tags"), JsArray.of("homeAddress")
                );     
                
```


**Parsing a string and the schema of the JSON object is unknown:**

```java   

JsObj a = JsObj.parse("{...}");

JsObj b = JsObj.parseYaml("  ");

```

**Parsing a string and the schema of the JSON object is known:**

In this case, the best and fastest option is to use a spec to do the parsing.
We'll talk about this option later on when I introduce json-spec.

**Creating an empty object and adding new values with the method _set_:**

```java   

JsObj person = 
        JsObj.empty().set("name", JsStr.of("Rafael"))
                     .set("surname", JsStr.of("Merino"))
                     .set("phoneNumber", JsStr.of("6666666"));

```

Remember that a JSON is immutable, so the set method returns a brand-new value.


### <a name="creatingjsonarray"><a/>Creating JsArray

**From primitive types using the static factory method _of_ and _varargs_:**

```java   

JsArray a = JsArray.of("apple", "orange", "pear");

JsArray b = JsArray.of(1, 2, 3, 4);

```

**From JSON values using the static factory method _of_ and _varargs_:**

```java   

JsArray a = JsArray.of(JsStr.of("hi"), JsInt.of(1), JsBool.TRUE, JsNull.NULL);

```

**From an iterable of JSON values:**

```java    

List<JsValue> list = new ArrayList<>();
Set<JsValue> set = new HashSet<>();

JsArray.ofIterable(list);
JsArray.ofIterable(set);

```

**Parsing a string and the schema of the JSON array is unknown:**

```java   

JsArray a = JsArray.parse("[...]");

JsArray b = JsArray.parseYaml("[....]");

```

**Parsing a string and the schema of the JSON array is known:**

In this case, like parsing objects with a schema, the best and fastest option
is to use a spec to do the parsing. We'll also talk about this option later on when
I introduce json-spec.

**Creating an empty array and adding new values with the methods _append_ and _prepend_:**

```java   

JsArray a = JsArray.empty().append(JsInt.of(1))
                           .prepend(JsInt.of(0));

JsArray b = JsArray.empty().append(JsInt.of(3))
                           .prepend(JsInt.of(2));

Assertions.assertEquals(JsArray.of(0,1,2,3), a.appendAll(b));
Assertions.assertEquals(JsArray.of(2,3,0,1), a.prependAll(b));

```


### <a name="inout"><a/>Putting data in and getting data out

Two important methods in the API are _get_ and _set_:

```code   

Json:: JsValue get(JsPath path);

Json:: Json set(JsPath path, JsValue value);
Json:: Json set(JsPath path, JsValue value, JsValue padWith);

```

The get method always returns a JsValue, no matter what path is passed in. If there is no
element at the specified path, it returns the special value JsNothing.NOTHING.
It's a total function. Functional programmers strive for total functions.
Their signature still reflects reality. No exceptions and no surprises.

Following the same philosophy, if you set a value at a specific path,
it will always be created, creating any needed container and padding arrays when necessary.
The next line of code after setting that value, you can count on it will be at the specified
path. The following property always holds:

```code   

Assertions.assertEquals(value,
                        obj.set(path, value)
                           .get(path));

```

What do you think setting _JsNothing_ at a path does?
Well, it has to remove the value, so that _get_ returns JsNothing:

```code   

Assertions.assertEquals(JsNothing.NOTHING,
                        obj.set(path, JsNothing.NOTHING)
                           .get(path));
                        
```

FP has to do with honesty. Establishing laws makes it easier to reason about the code we write.
By the way, the set method always returns a brand-new json.
If you remember well, Jsons are immutable and implemented with persistent data
structures in json-values.

Let's put some examples:


```code   

JsObj.empty().set(path("/food/fruits/0"), 
                  JsStr.of("apple"));

{
  "food": {
    "fruits": [
      "apple"
    ]
  }
}


// pads with null by default
JsObj.empty().set(path("/food/fruits/2"), 
                  JsStr.of("apple"));

{
  "food": {
    "fruits": [
      null,
      null,
      "apple"
    ]
  }
}

// padding with empty string
JsObj obj = JsObj.empty().set(path("/food/fruits/2"), 
                              JsStr.of("apple"), 
                              JsStr.of(""));

{
  "food": {
    "fruits": [
      "",
      "",
      "apple"
    ]
  }
}

Assertions.assertEquals(JsStr.of(""),
                        obj.get(path("/food/fruits/2")));
                        
Assertions.assertEquals(JsNothing.NOTHING,
                        obj.get(path("/food/fruits/5")));                        

```

You may want to get the Java primitive types directly. In this case, if there is no element at
the specified path, the following methods returns null, unless you specify a supplier to
produce a default value:

```code          

JsArray getArray(JsPath path);
JsArray getArray(JsPath path, Supplier<JsArray> orElse);

BigDecimal getBigDec(JsPath path);
BigDecimal getBigDec(JsPath path, Supplier<BigDecimal> orElse);

BigInteger getBigInt(JsPath path);
BigInteger getBigInt(JsPath path, Supplier<BigInteger> orElse);

byte[] getBinary(JsPath path);
byte[] getBinary(JsPath path, Supplier<byte[]> orElse);

Boolean getBool(JsPath path);
Boolean getBool(JsPath path, Supplier<Boolean> orElse);

Double getDouble(JsPath path);
Double getDouble(JsPath path, Supplier<Double> orElse);

Instant getInstant(JsPath path);
Instant getInstant(JsPath path, Supplier<Instant> orElse);

Integer getInt(JsPath path);
Instant getInt(JsPath path, Supplier<Instant> orElse);

Long getLong(JsPath path);
Long getLong(JsPath path, Supplier<Long> orElse);

JsObj getObj(JsPath path);
JsObj getObj(JsPath path, Supplier<JsObj> orElse);

String getStr(JsPath path);
String getStr(JsPath path, Supplier<String> orElse);


```

To get data from the first level of a JSON, there is no need to create a path.
You can just pass in the key or the index, which is less verbose:

```java   

obj.getStr("a")

array.getStr(0)

```



### <a name="filtermapreduce"><a/>Filter, map, and reduce

Let's take a look at some very common transformations using the _map_ methods.
The map function doesn't change the structure of the JSON. This is a pattern
known in FP as a functor. Consider the following signatures:

```code   

JsObj:: JsObj mapValues( Function<JsPrimitive, JsValue> map);

JsObj:: JsObj mapKeys( Function<String, String> map);

JsObj:: JsObj mapObjs( Function<JsObj, JsValue> map);

JsArray:: JsArray mapValues( Function<JsPrimitive, JsValue> map);

//an array doesn't have any key but a JSON object contained does!
JsArray:: JsArray mapKeys( Function<String, String> map);

JsArray:: JsArray mapObjs( Function<JsObj, JsValue> map);

```

All of them traverse recursively the whole JSON.

The mapKeys function transform all the keys of JSON objects. The typical example
is when you want to pass from camel case format to snake case.

The _mapValues_ function operates on primitive types (not object or arrays)
and transforms them into another value.

If the mapping depends not only on the value but also on its position in the JSON,
you can pass the full path in the map function using the following overloaded
methods:

```java  

JsObj mapKeys( BiFunction<JsPath, JsValue, String> map); 

JsObj mapValues( BiFunction<JsPath, JsPrimitive, JsValue> map);

JsObj mapObjs( BiFunction<JsPath, JsObj, JsValue> map)

```


filter and reduce: TODO



### <a name="specs"><a/>Specs

But what about validating JSON? We can define the JSON schema following precisely
the same approach as defining JSON:

```java   

JsObjSpec personSpec =
    JsObjSpec.of("name", str(),
                 "surname", str(),
                 "phoneNumber", str(),
                 "registrationDate", instant(),
                 "addresses", arrayOfObjSpec(JsObjSpec.of("coordinates", 
                                                          tuple(decimal(),
                                                                decimal()
                                                               ),
                                                          "city", str(),
                                                          "tags", arrayOfStr(),
                                                          "zipCode", str()
                                                         )
                                                      .lenient()   
                                             )
                 );
    
Set<SpecError> errors = personSepc.test(person);   

Function<SpecError, String> toStr = 
    error -> error.value + " @ "+ error.path + " doesn't conform spec: " + error.codeCode;   

errors.forEach(pair -> System.out.println(toStr.apply(pair)));
    
```

I’d argue that it is very expressive, concise, and straightforward. I call it json-spec.
I named it after a Clojure library named [spec](https://clojure.org/guides/spec). Writing
specs feels like writing JSON. Specs by default are strict, which means that they don't allow 
keys that are not specified, whereas lenient ones do. Turning strict specs into lenient ones is as easy
as calling the method _lenient_.

The real power is that you can create specs from predicates and compose them:

```java   

BiFunction<Integer, Integer, Predicate<String>> lengthBetween =
       (min, max) -> string -> string.length() <= max && 
                               string.length() >= min;

BiFunction<Instant, Instant, Predicate<Instant>> instantBetween =
      (min, max) -> instant -> min.isBefore(instant) && 
                               max.isAfter(instant);

BiFunction<Long, Long, Predicate<BigDecimal>> decBetween =
      (min, max) -> n -> BigDecimal.valueOf(min).compareTo(n) < 0 && 
                         BigDecimal.valueOf(max).compareTo(n) > 0;
  
int MAX_NAME_LENGTH = 10;
int MAX_SURNAME_LENGTH = 10;
int MAX_PHONE_LENGTH = 10;
int MAX_CITY_LENGTH = 20;
int MAX_TAG_LENGTH = 20;
int MAX_ZIPCODE_LENGTH = 30;
int MIN_ADDRESSES_SIZE = 1;
int MAX_ADDRESSES_SIZE = 100;
int MAX_TAGS_SIZE = 10;
long LAT_MIN = -90;
long LAT_MAX = 90;
long LON_MIN = -180;
long LON_MAX = 180;    

       
Predicate<String> nameSpec = lengthBetween.apply(0, MAX_NAME_LENGTH);
       
Predicate<String> surnameSpec = lengthBetween.apply(0, MAX_SURNAME_LENGTH);
        
Predicate<String> phoneSpec = lengthBetween.apply(0, MAX_PHONE_LENGTH);
        
Predicate<Instant> registrationDateSpec = instantBetween.apply(Instant.EPOCH, Instant.MAX);

Predicate<BigDecimal> latitudeSpec = decBetween.apply(LAT_MIN, LAT_MAX);

Predicate<BigDecimal> longitudeSpec = decBetween.apply(LON_MIN, LON_MAX);

Predicate<String> citySpec = lengthBetween.apply(0, MAX_CITY_LENGTH);
        
Predicate<String> tagSpec = lengthBetween.apply(0, MAX_TAG_LENGTH);
       
Predicate<String> zipCodeSpec = lengthBetween.apply(0, MAX_ZIPCODE_LENGTH);
        

JsObjSpec personSpec =
    JsObjSpec.of("name", str(nameSpec),
                 "surname", str(surnameSpec),
                 "phoneNumber", str(phoneSpec).nullable(),
                 "registrationDate", instant(registrationDateSpec),
                 "addresses", 
                 arrayOfObjSpec(JsObjSpec.of("coordinates",
                                             tuple(decimal(latitudeSpec),
                                                   decimal(longitudeSpec)
                                                  ),
                                             "city", str(citySpec),
                                             "tags", arrayOfStr(tagSpec,
                                                                0,
                                                                MAX_TAGS_SIZE
                                                                ),
                                             "zipCode", str(zipCodeSpec)
                                            )
                                         .lenient()         
                                         .setOptionals("tags", "zipCode", "city"),
                                MIN_ADDRESSES_SIZE,
                                MAX_ADDRESSES_SIZE                
                                )
                 )
             .setOptionals("surname", "phoneNumber", "addresses");   
    
```

As you can see, the spec's structure remains the same, and it’s child’s play to define
optional and nullable fields.

Another interesting spec you can define is map, i.e, an arbitrary number of
keys and their associated values of the same type. For example a map of 
numbers or booleans etc.

``` java

JsObjSpec a = JsObjSpec.of("name", str(),
                           "grades", mapOfInteger()
                          );

Set<SpecError> errors = 
        spec.test(JsObj.of("name", JsStr.of("Rafael"),
                           "grades", JsObj.of("Maths", JsInt.of(10),
                                              "Science", JsInt.of(8),
                                              "Geography", JsInt.of(8)
                                              )
                          )
                 );                           

```

It's possible to validate a whole JsObj once parsed with the method _suchThat_.
Imagine that certain properties must be present if a given property is present in 
an object. For example, suppose we have a schema representing a customer. If you 
have their credit card number, you also want to ensure you have a billing address. 
If you don’t have their credit card number, a billing address would not be required:

```java 

 Predicate<JsObj> existsBillingIfCard = o -> {
        if (o.containsKey("credit_card") 
            && !o.containsKey("billing_address")) return false;
        return true;
    };

 JsObjSpec customerSpec =
                JsObjSpec.of("credit_card", str(),
                             "billing_address", str()
                            )
                         .setOptionals("credit_card",
                                       "billing_address")
                         .suchThat(existsBillingIfCard);

```

Another exciting thing we can do with specs is parsing strings or bytes. Instead of parsing
the whole JSON and then validating it, we can verify the schema while parsing it and
stop the process as soon as an error happens. **After all, failing fast is important as well!**

```java     
 
JsObjParser personParser = new JsObjParser(personSpec);

String string = "{...}";
    
try{

    JsObj person = personParser.parse(string);
    
}
catch(JsParserException e){
    
    System.out.println("Error parsing person JSON: " + e.getMessage())
    
}    

```

### <a name="gen"><a/>Generators

Another critical aspect of software development is data generation. It’s an essential aspect
of property-based testing, a technique for the random testing of program properties very well
known in FP. Computers are way better than humans at generating random data. You'll catch more
bugs testing your code against a lot of inputs instead of just one. Writing generators, like
specs, is as simple as writing JSON:

```java     
 
Gen<JsArray> addressGen =
    JsArrayGen.biased(JsObjGen.of("coordinates", 
                                  JsTupleGen.of(JsBigDecGen.biased(LAT_MIN, LAT_MAX),
                                                JsBigDecGen.biased(LON_MIN, LON_MAX)
                                                ),
                                  "city", JsStrGen.biased(0, MAX_CITY_LENGTH),
                                  "tags", JsArrayGen.biased(JsStrGen.biased(0, 
                                                                            MAX_TAG_LENGTH
                                                                            ),
                                                            0,
                                                            MAX_TAGS_SIZE
                                                            ),
                                  "zipCode", JsStrGen.biased(0, MAX_ZIPCODE_LENGTH)
                                  )
                              .setOptionals("tags", "zipCode", "city"),
                      MIN_ADDRESSES_SIZE, 
                      MAX_ADDRESSES_SIZE        
                      );
Gen<JsObj> personGen =
  JsObjGen.of("name", JsStrGen.biased(0, MAX_NAME_LENGTH),
              "surname", JsStrGen.biased(0, MAX_NAME_LENGTH),
              "phoneNumber", JsStrGen.biased(0,MAX_PHONE_LENGTH),
              "registrationDate", JsInstantGen.biased(0, Instant.MAX.getEpochSecond()),
              "addresses", addressGen           
              )
          .setOptionals("surname", "phoneNumber", "addresses");

```


json-values uses the generators from the library [java-fun](https://github.com/imrafaelmerino/java-fun) to 
build the JSON generator. I strongly recommend you read the readme of java-fun to get a better 
understanding of how generators work.


In our previous example, the person generator has three optional fields 
(surname, phoneNumber, addresses), whereas de address generator has another
three fields (tags, zipCode, city). The total number of combinations is

2^3  *  2^3 = 64

json-values returns every possible combination with the same probability.
Imagine ten fields instead of just three (2^10 * 2^10 = 1_048_576). Are you
going to test each case manually?!

On the other hand, imagine you want to create a generator will all the optional
fields present. It's really easy with the function _suchThat_. It takes a predicate
and discard the generated values that don't fulfill the condition:


```java 

Gen<JsObj> newPersonGen = 
        personGen.suchThat(p -> 
                               p.get("addresses").isNotNothing() && 
                               p.get("phoneNumber").isNotNothing() && 
                               p.get("surname").isNotNothing() 
                          );

```


### <a name="optics"><a/>Optics

json-values uses the optics defined in the library [java-fun](https://github.com/imrafaelmerino/java-fun). 
Take a look at its readme to understand how optics work.

I'm going to follow a top-down approach and show an example of a function crafted with optics.

```java    

Function<JsObj, JsObj> modifyPerson =
    modifyAge.apply(n -> n + 1)
             .andThen(modifyName.apply(String::trim))
             .andThen(setCity.apply("Paris"))
             .andThen(modifyLatitude.apply(lat -> -lat))
             .andThen(addLanguage.apply("Lisp"));
             
```

No if-else conditions, no null checks, and I'd say it's pretty expressive and concise.
You'll end up with such simple, readable, and maintainable code working with json-values
and optics.

It's possible to model JSON as records of paths and their associated values:

```code

Json = { path: JsValue, path1: JsValue, path2: JsValue, ... }

```

Paths represent the full location of an element in a Json. They have the type JsPath.
On the other hand, JsValue is a sum-type that represents any JSON element.


Considering the following Json:

```json 
 
{
"name": "Rafael",
"age": 37,
"languages": ["Java", "Scala"],
"address":{
"street": "Elm street",
"coordinates": [12.3, 34.5]
}

```

It can be modeled as the following record:

```code 

{
"/name": "Rafael",
"/age": 37,
"/languages/0": "Java",
"/languages/1": "Scala",
"/address/street": "Elm street",
"/address/coordinates/0": 12.3,
"/address/coordinates/1": 34.5,
*: JsNothing
}

```

As you may notice, *  represents all the paths not defined for that Json, and JsNothing is their
associated value.

From the previous paths, you can define any lenses or optionals.
Find below some examples:

```java   

Lens<JsObj,JsValue> nameLens = JsObj.lens.value("name");

Lens<JsObj,JsValue> latitudeLens = JsObj.lens.value(path("/address/coordinates/0"));

Lens<JsObj,JsValue> longitudeLens = JsObj.lens.value(path("/address/coordinates/1"));

JsStr name = JsStr.of("Rafael");

JsObj person = nameLens.set.apply(name).apply(JsObj.empty());

Assertions.assertEquals(name,
                        nameLens.get.apply(person));

Function<JsValue, JsValue> toUpper = JsStr.prism.modify.apply(String::toUpperCase);


JsObj newPerson = nameLens.modify.apply(toUpper).apply(person);

Assertions.assertEquals(JsStr.of("RAFAEL"),
                        nameLens.get.apply(newPerson));
                        
```

Every type in json-value has a Prism. Find below some of them:

```code  

JsStr.prism   :: Prism<JsValue, String>
JsInt.prism   :: Prism<JsValue, Integer>
JsLong.prism  :: Prism<JsValue, Long>
JsBool.prism  :: Prism<JsValue, Boolean>
JsObj.prism   :: Prism<JsValue, JsObj>
JsArray.prism :: Prism<JsValue, JsArray>

```

Let's put some examples:

```java 

Assertions.assertEquals(Optional.of("hi!"),
                        JsStr.prism.getOptional.apply(JsStr.of("hi!")));

// 1 is not a string, empty is returned
Assertions.assertEquals(Optional.empty(),
                        JsStr.prism.getOptional.apply(JsInt.of(1)));

Assertions.assertEquals(JsStr.of("HI!"),
                        JsStr.prism.modify.apply(String::toUpperCase)
                                          .apply(JsStr.of("hi!")));

// 1 is not a string, the same value is returned
Assertions.assertEquals(JsInt.of(1),
                        JsStr.prism.modify.apply(String::toUpperCase)
                                          .apply(JsInt.of(1)));

Assertions.assertEquals(Optional.of(2),
                        JsInt.prism.getOptional.apply(JsInt.of(2)));

Assertions.assertEquals(Optional.empty(),
                        JsInt.prism.getOptional.apply(JsStr.of("hi!")));

Assertions.assertEquals(JsInt.of(2),
                        JsInt.prism.modify.apply(n -> n  + 1)
                                   .apply(JsInt.of(1)));

Assertions.assertEquals(JsNull.NULL,
                        JsInt.prism.modify.apply(n -> n  + 1)
                                          .apply(JsNull.NULL));

Assertions.assertEquals(Optional.empty(),
                        JsInt.prism.modifyOpt.apply(n -> n  + 1)
                                             .apply(JsNull.NULL));

``` 

And finally, let's go back to the modifyPerson we defined previously and implement it 
step by step using lenses and prisms.
^6008330177634367
``` java

Lens<JsObj, JsValue> nameLens = JsObj.lens.value("name");

Lens<JsObj, JsValue> ageOpt = JsObj.lens.value("age");

Lens<JsObj, JsValue> cityLens = JsObj.lens.value(path("/address/city"));

Lens<JsObj, JsValue> lanLens = JsObj.lens.value("languages");

JsPath latPath = JsPath.path("/address/coordinates/0");
Lens<JsObj, JsValue> latLens = JsObj.lens.value(latPath);

Function<IntFunction<Integer>,Function<JsObj, JsObj>> modifyAge = 
    fn -> ageOpt.modify.apply(JsInt.prism.modify.apply(fn::apply));

Function<Function<String,String>,Function<JsObj, JsObj>> modifyName =
    fn -> nameLens.modify.apply(JsStr.prism.modify.apply(fn::apply));

Function<String, Function<JsObj, JsObj>> addLanguage =
    language -> {
                  Function<JsArray,JsArray> addLanToArr = a -> a.append(JsStr.of(language));
                  return lanLens.modify.apply(JsArray.prism.modify.apply(addLanToArr));
                };

Function<String, Function<JsObj, JsObj>> setCity = 
    city -> cityLens.set.apply(JsStr.of(city));

Function<Function<Double, Double>, Function<JsObj,JsObj>> modifyLatitude =
    fn -> latLens.modify.apply(JsDouble.prism.modify.apply(fn));

//And finally:

Function<JsObj, JsObj> modifyPerson =
    modifyAge.apply(n -> n + 1)
             .andThen(modifyName.apply(String::trim))
             .andThen(setCity.apply("Paris"))
             .andThen(modifyLatitude.apply(lat -> -lat))
             .andThen(addLanguage.apply("Lisp"));
    
```    

The takeaway is how concise, declarative, and expressive the function modifyPerson is in
the above example. Besides, it's utterly safe without writing any null check.

In the previous example, we worked with the sum-type JsValue all the time; that's why we had
to use Prisms. It's possible and convenient to work with more specific types like primitives,
json objects, and arrays, instead of JsValue. If you remember well, a lens can not fail, so
the focus must exist and has the expected type. And what happens if the focus doesn't exist?
We can then use an Optional, another kind of optic (don't confuse it with java.util.Optional).

Summing up:
- Defining a Lens<JsObj, String> is valid if the focus exists, and it's a string
- Defining an Option<JsObj, Integer> is valid if the focus is a string (it's ok if it doesn't exist).
  It's called Option and not Optional to not mix it up with java.util.Optional
- Defining a Lens<JsObj, JsValue> is valid always. It requires Prisms to manipulate the focus.

Let's rewrite the modifyPerson defining lenses with more specific types instead of JsValue. 
We validate the person Json with a spec before applying the function, which makes the operation safe.

```java   

JsObjSpec addressSpec = 
    JsObjSpec.of("street",str(),
                 "coordinates", tuple(decimal(),
                                      decimal())
                );

JsObjSpec personSpec =
    JsObjSpec.of("name", str(),
                 "languages", arrayOfStr(),
                 "age", integer(),
                 "address", addressSpec().lenient()
                )
             .setOptionals("address");

//since we know the schema of the json we'll work with lenses and primitive types instead of JsValue
//address is optional, we can't use a lens!         

Lens<JsObj, String> nameLens = JsObj.lens.str("name");

Lens<JsObj, Integer> ageLens = JsObj.lens.intNum("age");

Lens<JsObj, JsArray> lanLens = JsObj.lens.array("languages");

Option<JsObj, String> cityOpt = JsObj.optional.str(path("/address/city"));

Option<JsObj,Double> latLens = 
    JsObj.optional.doubleNum(path("/address/coordinates/0"));

Function<JsObj, JsObj> modifyPerson =
    ageLens.modify.apply(n -> n + 1)
           .andThen(nameLens.modify.apply(String::trim))
           .andThen(cityOpt.set.apply("Paris"))
           .andThen(latLens.modify.apply(lat -> -lat))
           .andThen(lanLens.modify.apply(a -> a.append(JsStr.of("Lisp"))));

Set<SpecError> errors = personSpec.test(person);
if(errors.isEmpty()) {
    //we are safe!
    JsObj newPerson = modifyPerson.apply(person);
    ....
}

```

Optics, like many other concepts in FP, can be very well explained using Category Theory.
I strongly recommend watching the talk "Beyond Scala Lenses."


## <a name="notwhatfor"><a/> When not to use it

json-values fits well in _pure_ OOP and incredibly well in FP, but NOT in _EOOP_, which stands for
Enterprise Object-Oriented Programming. Don't create yet another fancy abstraction with getters and setters
or a complex DSL over json-values. [Narcissistic Design](https://www.youtube.com/watch?v=LEZv-kQUSi4) from **Stuart
Halloway** is a
great talk that elaborates ironically on this point.

## <a name="requirements"><a/> Requirements

Java 8 or greater.

## <a name="installation"><a/> Installation

Add the following dependency to your building tool:

```xml

<dependency>
    <groupId>com.github.imrafaelmerino</groupId>
    <artifactId>json-values</artifactId>
    <version>11.7.0</version>
</dependency>
```

## <a name="rp"><a/> Related projects

“Ideas are like rabbits. You get a couple and learn how to handle them, and pretty soon you have a dozen.” – John
Steinbeck

After the development of json-values, I published some more related projects:

* [vertx-values](https://github.com/imrafaelmerino/vertx-values) Persistent data structures makes a difference in message-passing architectures like [Vert.x](https://vertx.io)
* [java-fun](https://github.com/imrafaelmerino/java-fun) json-values uses the generators and optics from java-fun
* [mongo-values](https://github.com/imrafaelmerino/mongo-values) Set of codecs to use json-values with MongoDB
* [json-scala-values](https://github.com/imrafaelmerino/json-scala-values) The Scala version

json-values uses the persistent data structures from [vavr](https://www.vavr.io/),
[Jackson](https://github.com/FasterXML/jackson) to parse a string/bytes into
a stream of tokens, and [dsl-sjon](https://github.com/ngs-doo/dsl-json) to parse a string/bytes given a spec.

## <a name="sponsor"><a/> Sponsors

IntelliJ IDEA is my preferred IDE to develop json-values. 
JetBrains supports open source projects like json-values with its products.

[![IntelliJIDEA](./IntelliJ_IDEA.png)](https://jb.gg/OpenSourceSupport)
