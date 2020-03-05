package value.properties
import value.Preamble.{given}
import scala.language.implicitConversions
import valuegen._
import valuegen.Preamble.{given}
import value.spec.Preamble.{given}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}
import value.spec.JsArraySpecs._
import value.spec.JsBoolSpecs.bool
import value.spec.JsNumberSpecs._
import value.spec.JsStrSpecs._
import value.spec.JsObjSpecs.objSuchThat
import value.spec.JsSpecs.any
import value.spec.JsStrSpecs.strSuchThat
import value.spec.{Invalid, JsArraySpec, JsObjSpec, Valid}
import value._
import valuegen.JsArrayGen.noneEmptyOf

object JsObjProperties extends Properties("JsObj")
{


  property("if two object are equals, they have the same hashcode") =
    forAll(RandomJsObjGen())
    { (x: JsObj) =>
      val either = JsObjParser.parse(x.toString)
      either.contains(x) && either.exists(_.hashCode() == x.hashCode())
    }

  property("an object is a set of path/value pairs") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x == JsObj(x.flatten: _*)
    }

  property("inserted function is honest: it always inserts at the specified path") =
    forAll(RandomJsObjGen())
    {
      (x: JsObj) =>

        @scala.annotation.tailrec
        def insertPairs(pairs: LazyList[(JsPath, JsValue)],
                        y    : JsObj
                       ): JsObj =
          if (pairs.isEmpty) y else
          {
            val head = pairs.head
            insertPairs(pairs.tail,
                        y.inserted(head._1,
                                   head._2
                                   )
                        )
          }

        insertPairs(x.flatten,
                    JsObj.empty
                    ) == x
    }

  property("apply function returns the element located at the specified path") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x.flatten.forall((p: (JsPath, JsValue)) => x(p._1) == p._2)
    }

  property("head + tail returns the same object") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x.tail.inserted(x.head._1,
                        x.head._2
                        ) == x
    }

  property("init + last returns the same object") =
    forAll(RandomJsObjGen(objSizeGen = Gen.choose(1,
                                                  20
                                                  )
                          )
           )
    {
      (x: JsObj) =>
        x.init.inserted(x.last._1,
                        x.last._2
                        ) == x
    }

  property("map traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.mapAll((path: JsPath, value: JsValue) =>
                  if (x(path) != value) throw  RuntimeException()
                  else value
                ) == x
      }

    }

  property("mapKeys traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.mapAllKeys((path: JsPath, value: JsValue) =>
                      if (x(path) != value) throw  RuntimeException()
                      else path.last.asKey.name
                    ) == x
      }

    }

  property("filter traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.filterAll((path: JsPath, value: JsValue) =>
                     if (x(path) != value) false
                     else true
                   ) == x
      }

    }


  property("filterKeys traverses the whole object") =
    {
      forAll(RandomJsObjGen()
             )
      {
        (x: JsObj) =>
          x.filterAllKeys((path: JsPath, value: JsValue) =>
                         if (x(path) != value) false
                         else true
                       ) == x
      }

    }



}
