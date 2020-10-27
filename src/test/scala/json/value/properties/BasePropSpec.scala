package json.value.properties

import org.scalatest.PropSpec
import org.scalatestplus.scalacheck.Checkers

private[json] class BasePropSpec extends PropSpec with Checkers
{
  val jsPathGen = JsPathGens()
  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = 10000,
                               workers = 50
                               )
}
