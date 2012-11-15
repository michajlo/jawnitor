package org.michajlo.jawnitor.output
import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import org.slf4j.Logger
import org.mockito.Mockito._

class Slf4jMetricOutputTest extends FunSpec with MockitoSugar {

  it ("must properly output the input to its logger as key/value pairs at the info level") {
    val mockLogger = mock[Logger]
    val underTest = new Slf4jMetricOutput(mockLogger)

    val input = List(("hello", "world"), ("intval", 42), ("floatval", 1.2f))
    val expected = "hello=\"world\",intval=\"42\",floatval=\"1.2\""

    underTest.deliver(input)

    verify(mockLogger).info(expected)
  }
}