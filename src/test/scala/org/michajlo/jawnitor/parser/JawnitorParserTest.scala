package org.michajlo.jawnitor.parser

import org.scalatest.FunSpec
import org.michajlo.jawnitor.ast.JawnitorAST._
import javax.management.ObjectName
import java.io.StringReader

class JawnitorParserTest extends FunSpec {

  describe ("for a well formed input") {
    val body = """
      mbeans {
        org.michajlo.jawnitor:name=SomeMonitor,type=* {
          someAttr1 as metric1;
          someAttr2 as metric2;
        }

        org.michajlo.jawnitor.parser:name=SomeJawn,thing=-_asf/2143 {
          badAttr as bad;
          garbageAttr as garbage;
        }
      }
      """
    val expected = List(
      MBeanDesc(new ObjectName("org.michajlo.jawnitor:name=SomeMonitor,type=*"),
        List(
          AttrDesc("someAttr1", "metric1"),
          AttrDesc("someAttr2", "metric2")
        )
      ),
      MBeanDesc(new ObjectName("org.michajlo.jawnitor.parser:name=SomeJawn,thing=-_asf/2143"),
        List(
          AttrDesc("badAttr", "bad"),
          AttrDesc("garbageAttr", "garbage")
        )
      )
    )

    it ("must properly parse from a String") {
      assert(expected === JawnitorParser.parse(body).get)
    }

    it ("must properly parse from a Reader") {
      assert(expected === JawnitorParser.parse(new StringReader(body)).get)
    }
  }
}