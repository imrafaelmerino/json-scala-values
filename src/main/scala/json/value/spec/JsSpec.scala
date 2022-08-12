package json.value.spec
import com.github.plokhotnyuk.jsoniter_scala.core.{JsonReader, JsonWriter, ReaderConfig, readFromArray, readFromString}
import json.value.spec.codec.{JsArrayCodec, JsObjCodec}
import json.value.spec.*
import json.value.*
import json.value.spec.parser.*

import java.time.Instant
import scala.annotation.{tailrec, targetName}
sealed trait JsSpec:
  def nullable: JsSpec = this.or(IsNull)

  def parser:Parser[_]

  def or(other: JsSpec): JsSpec = new JsSpec :
    override def validate(value: JsValue): Result =
      JsSpec.this.validate(value) match
        case Valid => Valid
        case _ => other.validate(value)
    override def parser =  JsSpec.this.parser.or(other.parser)


  def validate(value: JsValue): Result

private[value] sealed trait SchemaSpec[T<:Json[T]] extends JsSpec :
  def validateAll(json: T): LazyList[(JsPath, Invalid)]

sealed trait JsObjSchema extends SchemaSpec[JsObj]:
  override def validate(value: JsValue) =
    value match
      case o:JsObj => validateAll(o).headOption match
        case Some((_, error)) => error
        case None => Valid
      case _ => Invalid(value,SpecError.OBJ_EXPECTED)

  override def parser: JsonParser[JsObj]

sealed case class IsMapOfInt(p:Int=>Boolean|String, k:String=>Boolean|String= _=>true) extends JsObjSchema:

  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head

    val errors = value match
      case JsInt(i) => p(i) match
        case x:Boolean =>
          if x then validateAll(json.tail)
          else (JsPath.root / key, Invalid(value,SpecError.INT_CONDITION_FAILED)) #:: validateAll(json.tail)
        case x:String => (JsPath.root / key, Invalid(value,SpecError(x))) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.INT_EXPECTED)) #:: validateAll(json.tail)

    k(key) match
      case x: Boolean =>
        if x then errors
        else errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError.KEY_CONDITION_FAILED)))
      case x: String => errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError(x))))

  override def parser:MapParser = MapParser(JsIntParser,toJsIntPredicate(p),k)

object IsMapOfInt extends IsMapOfInt(_=>true, _=>true)

sealed case class IsMapOfLong(p:Long => Boolean|String, k:String=>Boolean|String= _=>true) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    @inline def validateHead(i: Long,value: JsValue,key: String) = p(i) match
      case x: Boolean =>
        if x then validateAll(json.tail)
        else (JsPath.root / key, Invalid(value, SpecError.LONG_CONDITION_FAILED)) #:: validateAll(json.tail)
      case x: String => (JsPath.root / key, Invalid(value, SpecError(x))) #:: validateAll(json.tail)
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsInt(i)  => validateHead(i,value,key)
      case JsLong(i)  => validateHead(i,value,key)
      case _ => (JsPath.root / key, Invalid(value,SpecError.LONG_EXPECTED)) #:: validateAll(json.tail)

    k(key) match
      case x: Boolean =>
        if x then errors
        else errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError.KEY_CONDITION_FAILED)))
      case x: String => errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError(x))))

  override def parser:MapParser = MapParser(JsLongParser,toJsLongPredicate(p),k)

object IsMapOfLong extends IsMapOfLong(_=>true, _=>true)

sealed case class IsMapOfInstant(p:Instant=>Boolean, k:String=>Boolean= _=>true) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsInstant(i) if p(i) => validateAll(json.tail)
      case JsStr(i) if JsStr.instantPrism.exist(p).apply(i) => validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.INSTANT_EXPECTED)) #:: validateAll(json.tail)

    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))

  override def parser:MapParser = MapParser(JsInstantParser,toJsInstantPredicate(p),k)

object IsMapOfInstant extends IsMapOfInstant(_=>true, _=>true)

private class IsMapOfBool(k:String=>Boolean= _=>true) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsBool(_)  => validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.BOOLEAN_EXPECTED)) #:: validateAll(json.tail)

    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))

  override def parser:MapParser = MapParser(JsBoolParser,_=>true,k)

object IsMapOfBool extends IsMapOfBool(_=>true)

sealed case class IsMapOfDec(p: BigDecimal =>Boolean|String, k:String=>Boolean|String= _=>true,decimalConf: DecimalConf=DecimalConf) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    @inline def validateHead(i: BigDecimal,value: JsValue,key: String) =
      p(i) match
        case x: Boolean =>
          if x then validateAll(json.tail)
          else (JsPath.root / key, Invalid(value, SpecError.DECIMAL_CONDITION_FAILED)) #:: validateAll(json.tail)
        case x: String => (JsPath.root / key, Invalid(value, SpecError(x))) #:: validateAll(json.tail)
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
        case JsInt(i)  => validateHead(i,value,key)
        case JsLong(i)  => validateHead(i,value,key)
        case JsDouble(i)  => validateHead(i,value,key)
        case JsBigDec(i)  => validateHead(i,value,key)
        case _ => (JsPath.root / key, Invalid(value,SpecError.DECIMAL_EXPECTED)) #:: validateAll(json.tail)

    k(key) match
        case x: Boolean =>
          if x then errors
          else errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError.KEY_CONDITION_FAILED)))
        case x: String => errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError(x))))
  override def parser:MapParser = MapParser(JsDecimalParser(decimalConf),toJsBigDecPredicate(p),k)

object IsMapOfDec extends IsMapOfDec(_=>true, _=>true,DecimalConf)

sealed case class IsMapOfBigInt(p: BigInt =>Boolean|String, k:String=>Boolean|String= _=>true,digitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    @inline def validateHead(i:BigInt,value:JsValue,key: String) =
      p(i) match
        case x:Boolean=>
          if x then validateAll(json.tail)
          else (JsPath.root / key, Invalid(value,SpecError.BIG_INTEGER_CONDITION_FAILED)) #:: validateAll(json.tail)
        case x:String =>
          (JsPath.root / key, Invalid(value,SpecError(x))) #:: validateAll(json.tail)
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsInt(i)  => validateHead(i,value,key)
      case JsLong(i) => validateHead(i,value,key)
      case JsBigInt(i)  => validateHead(i,value,key)
      case _ => (JsPath.root / key, Invalid(value,SpecError.BIG_INTEGER_EXPECTED)) #:: validateAll(json.tail)
    k(key) match
      case x: Boolean =>
        if x then errors
        else errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError.KEY_CONDITION_FAILED)))
      case x: String => errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError(x))))
  override def parser:MapParser = MapParser(JsBigIntParser(digitsLimit),toJsBigIntPredicate(p),k)

object IsMapOfBigInt extends IsMapOfBigInt(_=>true, _=>true,BigIntConf.DIGITS_LIMIT)

sealed case class IsMapOfStr(p:String=>Boolean|String, k:String=>Boolean|String= _=>true) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsStr(i) => p(i) match
        case x:Boolean =>
          if x then validateAll(json.tail)
          else  (JsPath.root / key, Invalid(value,SpecError.STRING_CONDITION_FAILED)) #:: validateAll(json.tail)
        case x:String =>(JsPath.root / key, Invalid(value,SpecError(x))) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.STRING_EXPECTED)) #:: validateAll(json.tail)

    k(key) match
      case x: Boolean =>
        if x then errors
        else errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError.KEY_CONDITION_FAILED)))
      case x: String => errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError(x))))
  override def parser:MapParser = MapParser(JsStrParser,toJsStrPredicate(p),k)

object IsMapOfStr extends IsMapOfStr(_=>true, _=>true)

sealed case class IsMapOfObj(p:JsObj=>Boolean|String,
                      k:String=>Boolean|String= _=>true,
                      decimalConf: DecimalConf=DecimalConf,
                      digitsLimit:Int = BigIntConf.DIGITS_LIMIT) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case o:JsObj  => p(o) match
          case x:Boolean =>
            if x then validateAll(json.tail)
            else (JsPath.root / key, Invalid(o,SpecError.OBJ_CONDITION_FAILED)) #:: validateAll(json.tail)
          case x:String=> (JsPath.root / key, Invalid(o,SpecError(x))) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.OBJ_EXPECTED)) #:: validateAll(json.tail)
    k(key) match
      case x: Boolean =>
        if x then errors
        else errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError.KEY_CONDITION_FAILED)))
      case x: String => errors.prepended((JsPath.root / key, Invalid(JsStr(key), SpecError(x))))
  override def parser:MapParser =  MapParser(JsObjParser(decimalConf,digitsLimit),toJsObjPredicate(p),k)

object IsMapOfObj extends IsMapOfObj(_=>true, _=>true,DecimalConf,BigIntConf.DIGITS_LIMIT)

sealed case class IsMapOfArr(p:JsArray=>Boolean|String,
                            k:String=>Boolean|String= _=>true,
                            decimalConf: DecimalConf=DecimalConf,
                            bigIntDigitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case o:JsArray  => p(o) match
        case x:Boolean =>
          if x then validateAll(json.tail)
          else  (JsPath.root / key, Invalid(value,SpecError.ARRAY_CONDITION_FAILED)) #:: validateAll(json.tail)
        case x:String => (JsPath.root / key, Invalid(value,SpecError(x))) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.ARRAY_EXPECTED)) #:: validateAll(json.tail)
    k(key) match
      case x:Boolean =>
        if x then errors
        else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))
      case x:String => errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError(x))))

  override def parser:MapParser = MapParser(JsArrayOfParser(JsValueParser(decimalConf,bigIntDigitsLimit)),toJsArrayPredicate(p),k)

object IsMapOfArr extends IsMapOfArr(_=>true, _=>true,DecimalConf,BigIntConf.DIGITS_LIMIT)

sealed case class JsObjSpec(private[spec] val specs: Map[String, JsSpec],
                     private[spec] val strict: Boolean = true,
                     private[spec] val required: Seq[String]) extends JsObjSchema :
  override def validateAll(json: JsObj): LazyList[(JsPath, Invalid)] =
    validateObjAll(JsPath.root, json, specs, strict, required)

  override def parser:JsObjSpecParser =
    JsObjSpecParser(specs.map((key,spec) => (key,spec.parser)),strict,required,null)

  def or(other:JsObjSchema) :JsObjSchema = new JsObjSchema:
    override def validateAll(json: JsObj): LazyList[(JsPath, Invalid)] =
      val errors = JsObjSpec.this.validateAll(json)
      if errors.isEmpty then LazyList.empty
      else other.validateAll(json)



    override def parser:JsonParser[JsObj] = new JsonParser[JsObj]:
      override def parse(json: String): JsObj =
        try JsObjSpec.this.parser.parse(json,ParserConf.DEFAULT_READER_CONFIG)
        catch case _ => other.parser.parse(json,ParserConf.DEFAULT_READER_CONFIG)

      override def parse(json: Array[Byte]): JsObj =
        try JsObjSpec.this.parser.parse(json,ParserConf.DEFAULT_READER_CONFIG)
        catch case _ => other.parser.parse(json,ParserConf.DEFAULT_READER_CONFIG)

      override def parse(json: String, config: ReaderConfig): JsObj =
        try JsObjSpec.this.parser.parse(json,config)
        catch case _ => other.parser.parse(json,config)

      override def parse(json: Array[Byte], config: ReaderConfig): JsObj =
        try JsObjSpec.this.parser.parse(json,config)
        catch case _ => other.parser.parse(json,config)

      override private[json] def parse(reader: JsonReader) =
        reader.setMark()
        try JsObjSpec.this.parser.parse(reader)
        catch
          case _ =>
            reader.rollbackToMark()
            other.parser.parse(reader)

  def withRequiredKeys(keys:String*):JsObjSpec = JsObjSpec(specs,strict,keys)
  def withOptKeys(keys:String*):JsObjSpec =
    JsObjSpec(specs,strict,specs.keys.toSeq.filter(!keys.contains(_)))
  

  def and(other:JsObjSpec):JsObjSpec =
    JsObjSpec(specs ++ other.specs,strict, required ++ other.required);

  def and(key:String,spec:JsSpec) =  JsObjSpec(specs.updated(key,spec), strict, required)

  def lenient = new JsObjSpec(specs,false,required):
    override def parser: JsObjSpecParser =
      JsObjSpecParser(specs.map((key,spec) => (key,spec.parser)),
                      strict,
                      required,
                      JsValueParser.DEFAULT)


  //si es lenient pueden venir numeros decimales y se pueded cambiar la forma por defecto de leerlos
  def lenient(decimalConf: DecimalConf,bigIntDigitsLimit:Int) = new JsObjSpec(specs, false, required):
    override def parser: JsObjSpecParser =
      JsObjSpecParser(specs.map((key, spec) => (key, spec.parser)),
                      strict,
                      required,
                      JsValueParser(decimalConf,bigIntDigitsLimit))



object JsObjSpec:
  def apply(pairs:(String,JsSpec)*):JsObjSpec = new JsObjSpec(pairs.toMap,true,pairs.map(_._1))

final case class IsTuple(specs: Seq[JsSpec], strict: Boolean = true) extends SchemaSpec[JsArray] :
  override def validateAll(json: JsArray) = validateArrAll(JsPath.root / 0, json, specs, strict)
  override def parser:JsArraySpecParser = JsArraySpecParser(specs.map(_.parser),strict)
  override def validate(value: JsValue) =
    value match
      case a:JsArray => validateAll(a).headOption match
        case Some((_, error)) => error
        case None => Valid
      case _ => Invalid(value,SpecError.ARRAY_EXPECTED)

object IsTuple:
  def apply(spec: JsSpec*) = new IsTuple(spec,true)

private sealed trait JsValueSpec extends JsSpec :
  def validate(value: JsValue): Result

final case class IsArrayOf(spec: JsSpec) extends SchemaSpec[JsArray] :

  override def validateAll(json: JsArray): LazyList[(JsPath, Invalid)] =
    validateArrAll(JsPath.root / 0,json, json.seq.map(_=>spec),true)
  override def validate(value: JsValue): Result = value match
    case JsArray(seq) =>
      seq.map(it => spec.validate(it)).find(_ match
        case Valid => false
        case _ => true) match
        case Some(error) => error
        case None => Valid
    case _ => Invalid(value,SpecError.ARRAY_EXPECTED)

  override def parser:JsArrayOfParser = JsArrayOfParser(spec.parser)


object IsAny extends IsAny(_=>true,DecimalConf,BigIntConf.DIGITS_LIMIT)


sealed case class IsAny(p:JsValue => Boolean|String,
                 decimalConf: DecimalConf=DecimalConf,
                 bigIntDigitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsValueSpec:
  override def validate(value: JsValue): Result =
    p(value) match
      case x:Boolean => if x then Valid else Invalid(value,SpecError.VALUE_CONDITION_FAILED)
      case x:String => Invalid(value,SpecError(x))


  override def parser = JsValueParser(decimalConf,bigIntDigitsLimit).suchThat(p)


object IsNull extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsNull => Valid
    case _ => Invalid(value,SpecError.NULL_EXPECTED)
  override def parser = JsNullParser

sealed case class IsInt(p:Int=>Boolean | String) extends JsValueSpec :

  override def validate(value: JsValue): Result = value match
    case x:JsInt => p(x.value)  match
      case x:Boolean => if x then Valid else Invalid(value,SpecError.INT_CONDITION_FAILED)
      case x:String => Invalid(value,SpecError(x))
    case _ => Invalid(value,SpecError.INT_EXPECTED)
  override def parser = JsIntParser.suchThat(toJsIntPredicate(p))

object IsInt extends IsInt(_=>true)


sealed case class IsBool() extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsBool(_) => Valid
    case _ => Invalid(value,SpecError.BOOLEAN_EXPECTED)
  override def parser = JsBoolParser

object IsBool extends IsBool
sealed case class IsLong(p:Long=>Boolean|String) extends JsValueSpec :

  override def validate(value: JsValue): Result =
    def validateLong(n: Long) =
      p(n) match
        case x: Boolean => if x then Valid else Invalid(value, SpecError.LONG_CONDITION_FAILED)
        case x: String => Invalid(value, SpecError(x))
    value match
      case JsInt(n) => validateLong(n)
      case JsLong(n) => validateLong(n)
      case _ => Invalid(value,SpecError.LONG_EXPECTED)
  override def parser = JsLongParser.suchThat(toJsLongPredicate(p))

object IsLong extends IsLong(_=>true)

sealed case class IsStr(p:String=>Boolean|String) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsStr(x) => p(x) match
      case x:Boolean => if x then Valid else Invalid(value,SpecError.STRING_CONDITION_FAILED)
      case x:String => Invalid(value,SpecError(x))
    case _ => Invalid(value,SpecError.STRING_EXPECTED)
  override def parser = JsStrParser.suchThat(toJsStrPredicate(p))

object IsStr extends IsStr(_=>true)


sealed case class IsInstant(p:Instant=>Boolean|String) extends JsValueSpec :

  override def validate(value: JsValue): Result =

    def validateInstant(x: Instant) =
      p(x) match
        case x: Boolean => if x then Valid else Invalid(value, SpecError.INSTANT_CONDITION_FAILED)
        case x: String => Invalid(value, SpecError(x))
    value match
      case JsInstant(x) => validateInstant(x)
      case JsStr(x) =>
         JsStr.instantPrism.getOption(x) match
           case Some(i) => validateInstant(i)
           case None =>   Invalid(value,SpecError.INSTANT_EXPECTED)
      case _ => Invalid(value,SpecError.INSTANT_EXPECTED)
  override def parser = JsInstantParser.suchThat(toJsInstantPredicate(p))

object IsInstant extends IsInstant(_=>true)

sealed case class IsDec(p:BigDecimal=>Boolean|String, decimalConf: DecimalConf=DecimalConf) extends JsValueSpec :
  override def validate(value: JsValue): Result =
    def validateDec(dec:BigDecimal)=
      p(dec) match
        case x:Boolean => if x then Valid else Invalid(value,SpecError.DECIMAL_CONDITION_FAILED)
        case x:String => Invalid(value,SpecError(x))
    value match
      case JsInt(n) => validateDec(n)
      case JsLong(n) => validateDec(n)
      case JsDouble(n) => validateDec(n)
      case JsBigDec(n) => validateDec(n)
      case JsBigInt(n) => validateDec(BigDecimal(n))
      case _ => Invalid(value,SpecError.DECIMAL_EXPECTED)
  override def parser = JsDecimalParser(decimalConf).suchThat(toJsBigDecPredicate(p))

object IsDec extends IsDec(_=>true,DecimalConf)

sealed case class IsBigInt(p:BigInt=>Boolean|String,
                           digitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsValueSpec :

  override def validate(value: JsValue): Result =
    def validateBigInt(b: BigInt) =
      p(b) match
        case x: Boolean => if x then Valid else Invalid(value, SpecError.BIG_INTEGER_CONDITION_FAILED)
        case x: String => Invalid(value, SpecError(x))
    value match
        case JsInt(n) => validateBigInt(n)
        case JsLong(n) => validateBigInt(n)
        case JsBigInt(n) => validateBigInt(n)
        case _ => Invalid(value,SpecError.BIG_INTEGER_EXPECTED)
  override def parser = JsBigIntParser(digitsLimit).suchThat(toJsBigIntPredicate(p))

object IsBigInt extends IsBigInt(_=>true,BigIntConf.DIGITS_LIMIT)


sealed case class IsJsObj(p:JsObj=>Boolean|String,
                          decimalConf: DecimalConf=DecimalConf,
                   digitsLimit: Int = BigIntConf.DIGITS_LIMIT) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case n:JsObj =>
      p(n) match
        case x:Boolean => if x then Valid else Invalid(value,SpecError.OBJ_CONDITION_FAILED)
        case x:String => Invalid(value,SpecError(x))
    case _ => Invalid(value,SpecError.OBJ_EXPECTED)
  override def parser = JsObjParser(decimalConf,digitsLimit).suchThat(toJsObjPredicate(p))

object IsJsObj extends IsJsObj(_=>true,DecimalConf,BigIntConf.DIGITS_LIMIT)


sealed case class IsArray(p:JsArray => Boolean|String,
                          decimalConf: DecimalConf=DecimalConf,
                          bigIntDigitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case n:JsArray => p(n)  match
      case x:Boolean => if x then Valid else Invalid(value,SpecError.ARRAY_CONDITION_FAILED)
      case x:String => Invalid(value,SpecError(x))
    case _ => Invalid(value,SpecError.ARRAY_EXPECTED)
  override def parser =
    JsArrayOfParser(JsValueParser(decimalConf,bigIntDigitsLimit)).suchThat(toJsArrayPredicate(p))

object IsArray extends IsArray(_=>true,DecimalConf,BigIntConf.DIGITS_LIMIT)


private def validateObjAll(path: JsPath,
                           json: JsObj,
                           specs: Map[String, JsSpec],
                           strict: Boolean,
                           required: Seq[String]): LazyList[(JsPath, Invalid)] =
  def validateRequired(x:JsObj,path: JsPath,keys:Seq[String]):LazyList[(JsPath, Invalid)] =
    if keys.isEmpty then LazyList.empty
    else
      if x.contains(keys.head) then validateRequired(x,path,keys.tail)
      else (path / keys.head,Invalid(JsNothing,SpecError.KEY_REQUIRED)) #:: validateRequired(x,path,keys.tail)

  def validateObj(x: JsObj, path: JsPath): LazyList[(JsPath, Invalid)] =
    if x.isEmpty then return LazyList.empty
    val (key, value) = x.head
    specs.get(key) match
      case Some(spec) => spec match
        case JsObjSpec(ys, zs, r) => value match
          case o:JsObj =>  validateObj(x.tail, path) #::: validateObjAll(path / key, o, ys, zs, r)
          case _ => (path / key,Invalid(value,SpecError.OBJ_EXPECTED)) #:: validateObj(x.tail, path)
        case IsTuple(ys, zs) => value match
          case a:JsArray =>  validateObj(x.tail, path) #::: validateArrAll(path / key / 0, a, ys, zs)
          case _ => (path / key,Invalid(value,SpecError.ARRAY_EXPECTED)) #:: validateObj(x.tail, path)
        case valueSpec: JsSpec => valueSpec.validate(value) match
          case Valid => validateObj(x.tail, path)
          case error: Invalid => (path / key, error) #:: validateObj(x.tail, path)
      case None =>
        if strict then (path / key, Invalid(value,SpecError.SPEC_FOR_VALUE_NOT_DEFINED)) #:: validateObj(x.tail, path)
        else validateObj(x.tail, path)
  val errors = validateObj(json, path)
  if required.isEmpty then errors else errors #::: validateRequired(json,path,required)


private def validateArrAll(path: JsPath,
                           json: JsArray,
                           specs: Seq[JsSpec],
                           strict: Boolean): LazyList[(JsPath, Invalid)] =
  def validateArr(x: JsArray, y: Seq[JsSpec], path: JsPath): LazyList[(JsPath, Invalid)] =
    if x.isEmpty then return LazyList.empty
    if y.isEmpty && strict then (path, Invalid(x.head,SpecError.SPEC_FOR_VALUE_NOT_DEFINED)) #:: LazyList.empty
    val value = x.head
    y.head match
      case IsTuple(z, s) => value match
        case a:JsArray => validateArr(x.tail, y.tail, path.inc) #::: validateArrAll(path / 0, a, z, s)
        case _ =>  (path,Invalid(value,SpecError.ARRAY_EXPECTED)) #:: validateArr(x.tail, y.tail, path.inc)
      case JsObjSpec(z, s, r) => value match
        case o:JsObj => validateArr(x.tail, y.tail, path.inc) #::: validateObjAll(path, o, z, s, r)
        case _ => (path,Invalid(value,SpecError.OBJ_EXPECTED)) #:: validateArr(x.tail, y.tail, path.inc)
      case valueSpec: JsSpec => valueSpec.validate(value) match
        case Valid => validateArr(x.tail, y.tail, path.inc)
        case error: Invalid => (path, error) #:: validateArr(x.tail, y.tail, path.inc)
  validateArr(json, specs, path)


private[spec] def toJsIntPredicate(p:Int=>Boolean|String):JsValue=>Boolean|String =
  x =>
    x match
      case JsInt(n) => p(n)
      case _ => false


private[spec] def toJsLongPredicate(p:Long=>Boolean|String):JsValue=>Boolean|String =
 x =>
   x match
      case JsInt(n) => p(n)
      case JsLong(n) => p(n)
      case _ => false

private[spec] def toJsBigIntPredicate(p:BigInt=>Boolean|String):JsValue=>Boolean|String  =
  x =>
    x match
      case JsInt(n) => p(n)
      case JsLong(n) => p(n)
      case JsBigInt(n) => p(n)
      case _ => false

private[spec] def toJsBigDecPredicate(p:BigDecimal=>Boolean|String):JsValue=>Boolean|String  =
  x =>
    x match
      case JsInt(n) => p(n)
      case JsLong(n) => p(n)
      case JsDouble(n) => p(n)
      case JsBigDec(n) => p(n)
      case JsBigInt(n) => p(BigDecimal(n))
      case _ => false

private[spec] def toJsObjPredicate(p:JsObj=>Boolean|String):JsValue=>Boolean|String  =
  x =>
    x match
      case n:JsObj => p(n)
      case _ => false

private[spec] def toJsArrayPredicate(p:JsArray=>Boolean|String):JsValue=>Boolean|String  =
   x =>
     x match
      case n:JsArray => p(n)
      case _ => false

private[spec] def toJsStrPredicate(p:String=>Boolean|String):JsValue=>Boolean|String  =
  x =>
    x match
      case JsStr(n) => p(n)
      case _ => false

private[spec] def toJsInstantPredicate(p:Instant=>Boolean|String):JsValue=>Boolean | String  =
  x =>
    x match
      case JsInstant(n) => p(n)
      case JsStr(n) => JsStr.instantPrism.getOption(n).map(p).getOrElse(false)
      case _ => false