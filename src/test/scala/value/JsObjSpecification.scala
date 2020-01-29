package value

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import value.JsArraySpecification.property
import valuegen.{RandomJsArrayGen, RandomJsObjGen}
import value.Preamble._
import scala.language.implicitConversions

object JsObjSpecification extends Properties("JsObj")
{


  property("if two object are equals, they have the same hashcode") =
    forAll(RandomJsObjGen())
    { (x                                                : JsObj) =>
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
        x.map((path: JsPath, value: JsValue) =>
                  if (x(path) != value) throw new RuntimeException
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
          x.mapKeys((path: JsPath, value: JsValue) =>
                  if (x(path) != value) throw new RuntimeException
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
          x.filter((path: JsPath, value: JsValue) =>
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
          x.filterKeys((path: JsPath, value: JsValue) =>
                     if (x(path) != value) false
                     else true
                   ) == x
      }

    }

}