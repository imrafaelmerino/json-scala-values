package deserializers
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, OutputTimeUnit}

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class ParsingStringIntoJsObj
{

  @Benchmark
  def parsing_string(): Unit = {
    // this method was intentionally left blank.
  }

  @Benchmark
  def parsing_string_with_spec(): Unit = {
    // this method was intentionally left blank.
  }

}
