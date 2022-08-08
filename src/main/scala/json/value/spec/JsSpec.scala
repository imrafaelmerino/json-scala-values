package json.value.spec
import com.github.plokhotnyuk.jsoniter_scala.core.{JsonReader, JsonWriter, ReaderConfig, readFromArray, readFromString}
import json.value.spec.codec.{JsArrayCodec, JsObjCodec}
import json.value.spec.*
import json.value.*
import json.value.spec.parser.*

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

sealed case class IsMapOfInt(p:Int=>Boolean, k:String=>Boolean= _=>true) extends JsObjSchema:

  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head

    val errors = value match
      case JsInt(i) if p(i)=> validateAll(json.tail)
      case JsInt(_) => (JsPath.root / key, Invalid(value,SpecError.INT_CONDITION_FAILED)) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.INT_EXPECTED)) #:: validateAll(json.tail)

    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))

  override def parser:MapParser = MapParser(JsIntParser,toJsIntPredicate(p),k)

object IsMapOfInt extends IsMapOfInt(_=>true, _=>true)

sealed case class IsMapOfLong(p:Long=>Boolean, k:String=>Boolean= _=>true) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsInt(i) if p(i) => validateAll(json.tail)
      case JsLong(i) if p(i) => validateAll(json.tail)
      case JsInt(_) | JsLong(_) =>
        (JsPath.root / key, Invalid(value,SpecError.LONG_CONDITION_FAILED)) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.LONG_EXPECTED)) #:: validateAll(json.tail)

    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))

  override def parser:MapParser = MapParser(JsLongParser,toJsLongPredicate(p),k)

object IsMapOfLong extends IsMapOfLong(_=>true, _=>true)

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

sealed case class IsMapOfDec(p: BigDecimal =>Boolean, k:String=>Boolean= _=>true,decimalConf: DecimalConf=DecimalConf) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsInt(i) if p(i) => validateAll(json.tail)
      case JsLong(i) if p(i) => validateAll(json.tail)
      case JsDouble(i) if p(i) => validateAll(json.tail)
      case JsBigDec(i) if p(i) => validateAll(json.tail)
      case JsInt(_) | JsLong(_) | JsDouble(_) | JsBigDec(_) =>
        (JsPath.root / key, Invalid(value,SpecError.DECIMAL_CONDITION_FAILED)) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.DECIMAL_EXPECTED)) #:: validateAll(json.tail)
    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))
  override def parser:MapParser = MapParser(JsDecimalParser(decimalConf),toJsBigDecPredicate(p),k)

object IsMapOfDec extends IsMapOfDec(_=>true, _=>true,DecimalConf)

sealed case class IsMapOfBigInt(p: BigInt =>Boolean, k:String=>Boolean= _=>true,digitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsInt(i) if p(i) => validateAll(json.tail)
      case JsLong(i) if p(i) => validateAll(json.tail)
      case JsBigInt(i) if p(i) => validateAll(json.tail)
      case JsInt(_) | JsLong(_) | JsBigInt(_)  =>
        (JsPath.root / key, Invalid(value,SpecError.BIG_INTEGER_CONDITION_FAILED)) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.BIG_INTEGER_EXPECTED)) #:: validateAll(json.tail)
    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))
  override def parser:MapParser = MapParser(JsBigIntParser(digitsLimit),toJsBigIntPredicate(p),k)

object IsMapOfBigInt extends IsMapOfBigInt(_=>true, _=>true,BigIntConf.DIGITS_LIMIT)

sealed case class IsMapOfStr(p:String=>Boolean, k:String=>Boolean= _=>true) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case JsStr(i) if p(i) => validateAll(json.tail)
      case x:JsStr=>
        (JsPath.root / key, Invalid(x,SpecError.STRING_CONDITION_FAILED)) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.STRING_EXPECTED)) #:: validateAll(json.tail)
    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))
  override def parser:MapParser = MapParser(JsStrParser,toJsStrPredicate(p),k)

object IsMapOfStr extends IsMapOfStr(_=>true, _=>true)

sealed case class IsMapOfObj(p:JsObj=>Boolean,
                      k:String=>Boolean= _=>true,
                      decimalConf: DecimalConf=DecimalConf,
                      digitsLimit:Int = BigIntConf.DIGITS_LIMIT) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case o:JsObj if p(o) => validateAll(json.tail)
      case x:JsObj=>
        (JsPath.root / key, Invalid(x,SpecError.OBJ_CONDITION_FAILED)) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.OBJ_EXPECTED)) #:: validateAll(json.tail)
    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))
  override def parser:MapParser =  MapParser(JsObjParser(decimalConf,digitsLimit),toJsObjPredicate(p),k)

object IsMapOfObj extends IsMapOfObj(_=>true, _=>true,DecimalConf,BigIntConf.DIGITS_LIMIT)

sealed case class IsMapOfArr(p:JsArray=>Boolean,
                      k:String=>Boolean= _=>true,
                      decimalConf: DecimalConf=DecimalConf,
                      bigIntDigitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsObjSchema:
  override def validateAll(json: JsObj) =
    if json.isEmpty then return LazyList.empty
    val (key,value) = json.head
    val errors = value match
      case o:JsArray if p(o) => validateAll(json.tail)
      case x:JsArray=>
        (JsPath.root / key, Invalid(x,SpecError.ARRAY_CONDITION_FAILED)) #:: validateAll(json.tail)
      case _ => (JsPath.root / key, Invalid(value,SpecError.ARRAY_EXPECTED)) #:: validateAll(json.tail)
    if k(key) then errors
    else errors.prepended((JsPath.root / key,Invalid(JsStr(key),SpecError.KEY_CONDITION_FAILED)))
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

  def setRequired(keys:String*):JsObjSpec = JsObjSpec(specs,strict,keys)
  def setOptional(keys:String*):JsObjSpec =
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


sealed case class IsAny(p:JsValue => Boolean,
                 decimalConf: DecimalConf=DecimalConf,
                 bigIntDigitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsValueSpec:
  override def validate(value: JsValue): Result =
    if p(value) then Valid else Invalid(value,SpecError.VALUE_CONDITION_FAILED)
  override def parser = JsValueParser(decimalConf,bigIntDigitsLimit).suchThat(p)


object IsNull extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsNull => Valid
    case _ => Invalid(value,SpecError.NULL_EXPECTED)
  override def parser = JsNullParser

sealed case class IsInt(p:Int=>Boolean) extends JsValueSpec :

  override def validate(value: JsValue): Result = value match
    case x:JsInt => if p(x.value) then Valid else Invalid(x,SpecError.INT_CONDITION_FAILED)
    case _ => Invalid(value,SpecError.INT_EXPECTED)
  override def parser = JsIntParser.suchThat(toJsIntPredicate(p))

object IsInt extends IsInt(_=>true)


sealed case class IsBool() extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsBool(_) => Valid
    case _ => Invalid(value,SpecError.BOOLEAN_EXPECTED)
  override def parser = JsBoolParser

object IsBool extends IsBool
sealed case class IsLong(p:Long=>Boolean) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsInt(n) => if p(n) then Valid else Invalid(value,SpecError.LONG_CONDITION_FAILED)
    case JsLong(n) => if p(n) then Valid else Invalid(value,SpecError.LONG_CONDITION_FAILED)
    case _ => Invalid(value,SpecError.LONG_EXPECTED)
  override def parser = JsLongParser.suchThat(toJsLongPredicate(p))

object IsLong extends IsLong(_=>true)

sealed case class IsStr(p:String=>Boolean) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsStr(x) => if p(x) then Valid else Invalid(value,SpecError.STRING_CONDITION_FAILED)
    case _ => Invalid(value,SpecError.STRING_EXPECTED)
  override def parser = JsStrParser.suchThat(toJsStrPredicate(p))

object IsStr extends IsStr(_=>true)


sealed case class IsDec(p:BigDecimal=>Boolean, decimalConf: DecimalConf=DecimalConf) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsInt(n) => if p(n) then Valid else Invalid(value,SpecError.DECIMAL_CONDITION_FAILED)
    case JsLong(n) => if p(n) then Valid else Invalid(value,SpecError.DECIMAL_CONDITION_FAILED)
    case JsDouble(n) => if p(n) then Valid else Invalid(value,SpecError.DECIMAL_CONDITION_FAILED)
    case JsBigDec(n) => if p(n) then Valid else Invalid(value,SpecError.DECIMAL_CONDITION_FAILED)
    case JsBigInt(n) => if p(BigDecimal(n)) then Valid else Invalid(value,SpecError.DECIMAL_CONDITION_FAILED)
    case _ => Invalid(value,SpecError.DECIMAL_EXPECTED)
  override def parser = JsDecimalParser(decimalConf).suchThat(toJsBigDecPredicate(p))

object IsDec extends IsDec(_=>true,DecimalConf)

sealed case class IsBigInt(p:BigInt=>Boolean,
                    digitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case JsInt(n) => if p(n) then Valid else Invalid(value,SpecError.BIG_INTEGER_CONDITION_FAILED)
    case JsLong(n) => if p(n) then Valid else Invalid(value,SpecError.BIG_INTEGER_CONDITION_FAILED)
    case JsBigInt(n) => if p(n) then Valid else Invalid(value,SpecError.BIG_INTEGER_CONDITION_FAILED)
    case _ => Invalid(value,SpecError.BIG_INTEGER_EXPECTED)
  override def parser = JsBigIntParser(digitsLimit).suchThat(toJsBigIntPredicate(p))

object IsBigInt extends IsBigInt(_=>true,BigIntConf.DIGITS_LIMIT)


sealed case class IsJsObj(p:JsObj=>Boolean,
                   decimalConf: DecimalConf=DecimalConf,
                   digitsLimit: Int = BigIntConf.DIGITS_LIMIT) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case n:JsObj => if p(n) then Valid else Invalid(n,SpecError.OBJ_CONDITION_FAILED)
    case _ => Invalid(value,SpecError.OBJ_EXPECTED)
  override def parser = JsObjParser(decimalConf,digitsLimit).suchThat(toJsObjPredicate(p))

object IsJsObj extends IsJsObj(_=>true,DecimalConf,BigIntConf.DIGITS_LIMIT)


sealed case class IsArray(p:JsArray => Boolean,
                          decimalConf: DecimalConf=DecimalConf,
                          bigIntDigitsLimit:Int=BigIntConf.DIGITS_LIMIT) extends JsValueSpec :
  override def validate(value: JsValue): Result = value match
    case n:JsArray => if p(n) then Valid else Invalid(n,SpecError.ARRAY_CONDITION_FAILED)
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


private[spec] def toJsIntPredicate(p:Int=>Boolean):JsValue=>Boolean =
  x =>
    x match
      case JsInt(n) => p(n)
      case _ => false


private[spec] def toJsLongPredicate(p:Long=>Boolean):JsValue=>Boolean =
 x =>
   x match
      case JsInt(n) => p(n)
      case JsLong(n) => p(n)
      case _ => false

private[spec] def toJsBigIntPredicate(p:BigInt=>Boolean):JsValue=>Boolean  =
  x =>
    x match
      case JsInt(n) => p(n)
      case JsLong(n) => p(n)
      case JsBigInt(n) => p(n)
      case _ => false

private[spec] def toJsBigDecPredicate(p:BigDecimal=>Boolean):JsValue=>Boolean  =
  x =>
    x match
      case JsInt(n) => p(n)
      case JsLong(n) => p(n)
      case JsDouble(n) => p(n)
      case JsBigDec(n) => p(n)
      case JsBigInt(n) => p(BigDecimal(n))
      case _ => false

private[spec] def toJsObjPredicate(p:JsObj=>Boolean):JsValue=>Boolean  =
  x =>
    x match
      case n:JsObj => p(n)
      case _ => false

private[spec] def toJsArrayPredicate(p:JsArray=>Boolean):JsValue=>Boolean  =
   x =>
     x match
      case n:JsArray => p(n)
      case _ => false

private[spec] def toJsStrPredicate(p:String=>Boolean):JsValue=>Boolean  =
  x =>
    x match
      case JsStr(n) => p(n)
      case _ => false