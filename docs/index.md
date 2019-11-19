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
 - [Spec](#spec)
   
 
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

I'd say that the previous spec is self-explanatory. Just in case, we'll go over the more important
concepts.

###<a name="pspec"></a> Predefined specs
There are four predefined numeric specs:
 
 * _int_
 * _long_
 * _integral_
 * _decimal_
 
All of them accept the parameters _minimum_ and  _maximum_. To specify only an upper or lower limit, and not both of them:
 
 * intGT,  longGT,  integralGT,  decimalGT       
 * intLT,  longLT,  integralLT,  decimalLT       
 * intLTE, longLTE, integralLTE, decimalLTE      
 * intGTE, longGTE, integralGTE, decimalGTE      
 
where:
 
 * GT is greater than
 * LT is lower than
 * LTE is lower than or equal to
 * GTE is greater than or equal to.
 
All the numeric specs described above accept the optional parameter _multipleOf_.

The spec _string_ accept the optional parameters:
 
 * _minLength:Int_
 * _maxLength:Int_
 * _pattern:Pattern_

If a value is a constant, just specify that. If it has a finite number of constants, _enum_ can be used.

For arrays there are the following specs:
 
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
  
For objects, the spec _obj_ accept the optional parameters:

 * _minKeys:Int_
 * _maxKeys:Int_
 * _required:Seq[String]_
 * _dependentRequired: Seq[(String, Seq[String])]_. Specifies keys that are required if a specific other key is present

###<a name="aspec"></a> Arbitrary specs
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


 





