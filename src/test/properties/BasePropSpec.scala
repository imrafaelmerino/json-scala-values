package json.value.properties

import org.scalatest.PropSpec
import org.scalatestplus.scalacheck.Checkers

private[value] class BasePropSpec extends PropSpec with Checkers
{
  given PropertyCheckConfiguration(minSuccessful = 10000,
                                   workers = 50
                                  )
}
