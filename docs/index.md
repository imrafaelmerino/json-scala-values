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
 - [Json spec](#spec)
   - [Predefined specs](#pspecs)
      - [Predefined JsNumber specs](npspecs)
      - [Predefined JsString specs](spspecs)
      - [Predefined JsObj specs](opspecs)
      - [Predefined JsArray specs](apspecs)
   - [Arbitrary specs](#arspecs)
   - [Composing specs](#comspecs)
   - [Examples](#exspecs)
   
 
## <a name="jspath"></a> JsPath 
## <a name="jsvalue"></a> JsValue
## <a name="json-creation"></a> Creating Jsons
### <a name="json-obj-creation"></a> Json objects
### <a name="json-arr-creation"></a> Json arrays
## <a name="data-in-out"></a> Putting data in and getting data out
### <a name="obtaining-primitive-types"></a> Obtaining primitive types
### <a name="obtaining-jsons"></a> Obtaining Jsons
### <a name="putting-data-by-path"></a> Putting data at any location
### <a name="manipulating-arrays"></a> Manipulating arrays

## <a name="spec"></a> Json spec
A Json spec specifies the structure of a Json and validates it. Specs have attractive qualities like:
 * Easy to write. Specs are defined in the same way as a Json is.
 * Easy to compose. You can compose them and create new ones easily.
 * Easy to extend. There are predefined specs and they'll cover the most common scenarios, but, any imaginable
 spec can be created using predicates.
 
 Let's go straight to the point and put an example:
 
```
def spec = JsObjSpec("a" -> int,
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

As you can see, defining a spec is as simple as defining a Json. It's declarative and
concise, with no ceremony at all. 

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

###<a name="pspecs"></a> Predefined specs
The predefined Json specs are, most of them, the established by the [Json Schema Validation](https://json-schema.org/draft/2019-09/json-schema-validation.html) specification
They will cover the most common scenarios.

Before moving on, let's define the most simple spec, which specifies that a value is a constant. For example:

```
def objSpec = JsObjSpec("a" -> "hi")

def arrSpec = JsArraySpec(1, int)

```
The only Json that conforms the first spec is JsObj("a" -> "hi"). On the other hand, the second spec defines an array of two elements where the first one is always 1, and the second one is an integer.

####<a name="npspecs"></a>Predefined JsNumber specs

There are four predefined numeric specs:

 * _int_
 * _long_
 * _integral_
 * _decimal_
 
It exists the parameters _minimum_ and  _maximum_ for all the above specs to specify an interval. 
If the interval has only an upper or lower limit, the following specs have to be used:
 
 * _intGT_,  _longGT_,  _integralGT_,  _decimalGT_       
 * _intLT_,  _longLT_,  _integralLT_,  _decimalLT_       
 * _intLTE_, _longLTE_, _integralLTE_, _decimalLTE_      
 * _intGTE_, _longGTE_, _integralGTE_, _decimalGTE_      
 
where GT is greater than, LT is lower than, LTE is lower than or equal to, and GTE is greater than or equal to.
 
All the numeric specs accept the optional parameter _multipleOf_.

####<a name="spspecs"></a> Predefined JsString specs

There are two predefined string specs:

 * _string_
 * _enum_ 
 
The _string_ spec accepts the following optional parameters:

 * _minLength:Int_
 * _maxLength:Int_
 * _pattern:Pattern_

whereas an _enum_ is just a list of possible constants. 

####<a name="opspecs"></a> JsObj predefined specs

The JsObj spec _obj_ accepts the following optional parameters:

 * _minKeys:Int_
 * _maxKeys:Int_
 * _required:Seq[String]_
 * _dependentRequired: Seq[(String, Seq[String])]_ 
 
 _dependentRequired_ specifies keys that are required if a specific other key is present. For example:
 
```
JsObjSpec("a" -> obj(dependentRequired=List(("a",List("b","c")),
                                            ("d",List("e","f"))
                                           )
                    )
         )
 ```

which means that if "a" / "a" exists, then "a" / "b" and "a" / "c" must exist too, and if "a" / "d" exists, then "a" / "e" and "a" / "f" must exist too

####<a name="apspecs"></a> Predefined JsArray specs

For arrays there are the following predefined specs:
 
 * _array_
 * _arrayOfInt_
 * _arrayOfString_
 * _arrayOfLong_
 * _arrayOfDecimal_
 * _arrayOfIntegral_
 * _arrayOfNumber_
 
All of them accept the optional parameters:
 
 * _minItems:Int_
 * _maxItems:Int_ 
 * _unique:Boolean_
  
###<a name="arspecs"></a> Arbitrary specs

 We've seen so far predefined specs. They'll be the most used for sure. There are also more generic
 specs for every type that allows to define any imaginable spec:     
           
```
//spec: a predicate to test the value
//message: the error message to be returned if the predicate is evaluated to false

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


```
def arrayOfEvenInts = arrayOf( (value:JsValue) => value.isInt(_ % 2 == 0), "An odd element found")
JsObjSpec("a" -> arrayOfEvenInts)
``` 

###<a name="comspecs"></a> Composing specs

###<a name="exspecs"></a> Examples

 





