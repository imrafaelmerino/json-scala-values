package properties

import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import value.{Index, JsPath, Key}


class JsPathProps extends BasePropSpec
{
  val objPathGen: Gen[JsPath] = JsPathGens().objPathGen
  val arrPathGen: Gen[JsPath] = JsPathGens().arrPathGen

  property("prepending and appending a key")
  {
    check(forAll(Gen.oneOf(objPathGen,
                           arrPathGen
                           )
                 )
          {
            path =>
              val a = path \ "a" / "b"
              a.head == Key("a") && a.last == Key("b")
          }
          )
  }


  property("/ is an alias for appending keys and indexes to a path")
  {
    check(forAll(Gen.oneOf(objPathGen,
                           arrPathGen
                           )
                 )
          {
            path =>
              path / "b" == path.appended("b") &&
              path / 1 == path.appended(1)

          }
          )
  }

  property("\\ is an alias for prepending keys and indexes to a path")
  {
    check(forAll(Gen.oneOf(objPathGen,
                           arrPathGen
                           )
                 )
          {
            path =>
              path \ "b" == path.prepended("b") &&
              path \ 1 == path.prepended(1)

          }
          )
  }

  property("prepending and appending an index")
  {
    check(forAll(Gen.oneOf(objPathGen,
                           arrPathGen
                           )
                 )
          {
            path =>
              val a = path \ 0 / 1
              a.head == Index(0) && a.last == Index(1)
          }
          )
  }


  property("isKey and isIndex function parameters take the right input")
  {
    check(forAll(Gen.oneOf(objPathGen,
                           arrPathGen
                           )
                 )
          {
            path =>
              val head = path.head
              head match
              {
                case Key(name) => head.isKey(_ == name) && !head.isIndex
                case Index(i) => head.isIndex(_ == i) && !head.isKey
              }
          }
          )
  }

  property("asKey and asIndex converts object of type Position into some of their impls")
  {
    check(forAll(Gen.oneOf(objPathGen,
                           arrPathGen
                           )
                 )
          {
            path =>
              val head = path.head
              head match
              {
                case key:Key => key == head.asKey
                case index:Index => index == head.asIndex
              }
          }
          )
  }
  property("mapKey returns the same key when identity function is passed in")
  {
    check(forAll(Gen.oneOf(objPathGen,
                           arrPathGen
                           )
                 )
          {
            path =>
              val head = path.head
              head match
              {
                case key:Key => key == head.mapKey((s:String) => s)
                case index:Index => index == index
              }
          }
          )
  }
}
