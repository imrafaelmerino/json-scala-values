<img src="./logo/package_twitter_if9bsyj4/color1/full/coverphoto/color1-white_logo_dark_background.png" alt="logo"/>
[![Maven](https://img.shields.io/maven-central/v/com.github.imrafaelmerino/json-scala-values/5.0.0)](https://search.maven.org/artifact/com.github.imrafaelmerino/json-scala-values/5.0.0/jar)

<!-- TOC -->
- [Code wins arguments](#cwa)

## <a name="cwa"><a/> Code wins arguments

**JSON creation**

```scala 

JsObj("name" -> JsStr("Rafael"),
      "languages" -> JsArray("Java", "Scala", "Kotlin"),
      "age" -> JsInt(1),
      "address" -> JsObj("street" -> JsStr("Elm Street"),
                         "coordinates" -> JsArray(3.32, 40.4)
                        )
     );

```

or using conversions

```scala 
import json.value.Conversions.given

JsObj("name" -> "Rafael",
      "languages" -> JsArray("Java", "Scala", "Kotlin"),
      "age" -> 1,
      "address" -> JsObj("street" -> "Elm Street",
                         "coordinates" -> JsArray(3.32, 40.4)
                        )
     );

```


**JSON validation**

```java 

val spec = 
        JsObjSpec("name" ->  IsStr,
                  "languages" -> IsArrayOf(IsStr),
                  "age" -> IsInt,
                  "address", JsObjSpec("street" -> IsStr,
                                       "coordinates" ->  IsTuple(IsNumber,
                                                                 IsNumber
                                                                 )
                                      )
                 )
                 .withOptKeys("address");
    
```   

and customize with predicates and operators

```java 

val noneEmptyStr = IsStr(n => if n.nonEmpty then true else "empty name")
val ageSpec = IsInt(n => if n < 16 then "too young" else true)
val addressSpec = 
      JsObjSpec("coordinates" ->  IsTuple(IsNumber,IsNumber))
      .or(
      JsObjSpec("street" -> noneEmptyStr,
                "number" -> noneEmptyStr,
                "zipcode" -> noneEmptyStr
                "country" -> noneEmptyStr
               )
         )

val spec = 
        JsObjSpec("name" ->  noneEmptyStr,
                  "languages" -> IsArrayOf(noneEmptyStr),
                  "age" -> ageSpec,
                  "address", addressSpec
                 )
                 .withOptKeys("address");
    
val errors:LazyList[(JsPath, Invalid)] = spec.validateAll(json)    

// Invalid:: (JsValue, SpecError)

```   



**JSON parsing**

You can get a parser from a spec to parse a string or array of bytes into a Json.
Most of the json-schema implementations parse the whole Json and then validates it,
which is very inefficient. json-values validates each element of the Json as soon 
as it is parsed



```code   

val parser = spec.parser()

val json = parser.parse("{}")

```


**JSON generation**

```java 

val gen = 
      JsObjGen(name -> Gen.alphaStr.map(JsStr),
               languages -> JsArrayGen.of(Gen.oneOf("scala", "java", "kotlin")).distinct,
               age -> Arbitrary.arbitrary[Int].map(JsInt),
               address -> JsObjGen(street -> Gen.asciiStr.map(JsStr),
                                   coordinates -> TupleGen(Arbitrary.arbitrary[BigDecimal].map(JsBigDec),
                                                           Arbitrary.arbitrary[BigDecimal].map(JsBigDec)))
               )
        
                
```

or using conversions:

```java 
import json.value.gen.Conversions.given to avoid writting the map method:         

          
val gen = 
      JsObjGen(name -> Gen.alphaStr,
               languages -> JsArrayGen.of(Gen.oneOf("scala", "java", "kotlin")).distinct,
               age -> Arbitrary.arbitrary[Int],
               address -> JsObjGen(street -> Gen.asciiStr,
                                   coordinates -> TupleGen(Arbitrary.arbitrary[BigDecimal],
                                                           Arbitrary.arbitrary[BigDecimal]))
              )
        
                
```


When testing, it's important to generate both valid and invalid data according
to your specifications. Generators and specs can be used for this purpose:

```code 

val gen = 
      JsObjGen("name" -> Gen.alphaStr,
               "languages" -> JsArrayGen.of(Gen.oneOf("scala", "java", "kotlin")).distinct,
               "age" -> Arbitrary.arbitrary[Int],
               "address" -> JsObjGen("street" -> Gen.asciiStr,
                                     "coordinates" -> TupleGen(Arbitrary.arbitrary[BigDecimal],
                                                               Arbitrary.arbitrary[BigDecimal]
                                                               )
                                     )
                                     .withOptKeys("street","coordinates")
                                     .withNullValues("street","coordinates")
               )
               .withOptKeys("name","languages","age","address")
               .withNullValues("name","languages","age","address")
               
val (validGen, invalidGen) = gen.partition(spec)  

            
```


**JSON manipulation free of NullPointerException with optics:**

```code 

val nameLens:Lens[JsObj,String] = JsObj.lens.str("name")

val ageLens:Lens[JsObj,Int] = JsObj.lens.int("age")

val cityOpt:Optional[JsObj,String] = 
  JsObj.optional.str(JsPath.root / "address" / "city")

val latitudeOpt:Optional[JsObj,Double] = 
  JsObj.optional.double(JsPath.root / "address" / "coordinates" / "latitude")

//let's craft a function using lenses and optionals

val fn:Function[JsObj,JsObj]  = 
    ageLens.modify(_ + 1)
           .andThen(nameLens.modify(_.trim))
           .andThen(cityOpt.set("Paris"))
           .andThen(latitudeLens.modify(lat => -lat))
           
         
JsObj updated = fn(person)

```

No if-else conditions, no null checks, and I'd say it's pretty 
expressive and concise. As you may notice, each field has an 
associated optic defined, and we just create functions, like _fn_ 
in the previous example, putting them together (composition is key 
to handle complexity).

**Filter,map and reduce were never so easy!**

These functions traverse the whole Json recursively:

```code 
          
json.mapKeys(_.toLowerCase)
    .map(JsStr.prism.modify(_trim))
    .filter(_.noneNull)
    .filterKeys(!_.startsWith("$"))
                    
```




