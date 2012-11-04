package org.michajlo.jawnitor.mbean

import scala.collection.JavaConversions.setAsJavaSet
import org.michajlo.jawnitor.ast.JawnitorAST.AttrDesc
import org.michajlo.jawnitor.ast.JawnitorAST.MBeanDesc
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import javax.management.MBeanException
import javax.management.MBeanServer
import javax.management.ObjectName
import java.io.IOException
import javax.management.AttributeNotFoundException
import javax.management.InstanceNotFoundException
import javax.management.ReflectionException

class MBeanReaderTest extends FunSpec with MockitoSugar {

  it ("must return an empty list if the ObjectName isn't present") {
    val mockMBeanServer = mock[MBeanServer]
    val underTest = new MBeanReader(mockMBeanServer)
    val objName = new ObjectName("blah.blah:blah=blah")
    val mbeanDesc = MBeanDesc(objName, List(AttrDesc("someAttr", "someAlias")))

    doReturn(setAsJavaSet(Set[ObjectName]())).when(mockMBeanServer).queryNames(mbeanDesc.objName, null)

    assert(Nil === underTest.readAttributesFor(mbeanDesc))
  }

  it ("must properly map aliases to retrieved attributes' values") {
    val mockMBeanServer = mock[MBeanServer]
    val underTest = new MBeanReader(mockMBeanServer)
    val objName = new ObjectName("blah.blah:blah=blah")
    val mbeanDesc = MBeanDesc(objName,
        List(AttrDesc("someAttr1", "someAlias1"), AttrDesc("someAttr2", "someAlias2")))

    doReturn(setAsJavaSet(Set(objName))).when(mockMBeanServer).queryNames(objName, null)

    doReturn("Value1").when(mockMBeanServer).getAttribute(objName, "someAttr1")
    doReturn("Value2").when(mockMBeanServer).getAttribute(objName, "someAttr2")

    val result = underTest.readAttributesFor(mbeanDesc)
    assert(List(("someAlias1", "Value1"), ("someAlias2", "Value2")) === result.sortWith(_._1 < _._1))
  }

  it ("must omit metrics for which an exception was thrown") {
    val mockMBeanServer = mock[MBeanServer]
    val underTest = new MBeanReader(mockMBeanServer)
    val objName = new ObjectName("blah.blah:blah=blah")
    val mbeanDesc =
      MBeanDesc(objName,
        List(
          AttrDesc("failWithMBeanException", "someAlias1"),
          AttrDesc("failWithAttributeNotFoundException", "someAlias2"),
          AttrDesc("failWithInstanceNotFoundException", "someAlias3"),
          AttrDesc("failWithReflectionException", "someAlias4"),
          AttrDesc("succeedWithSomething", "someAlias5")
        )
      )

    doReturn(setAsJavaSet(Set(objName))).when(mockMBeanServer).queryNames(objName, null)

    doThrow(new MBeanException(new Exception)).when(mockMBeanServer).
        getAttribute(objName, "failWithMBeanException")
    doThrow(new AttributeNotFoundException).when(mockMBeanServer).
        getAttribute(objName, "failWithAttributeNotFoundException")
    doThrow(new InstanceNotFoundException).when(mockMBeanServer).
        getAttribute(objName, "failWithInstanceNotFoundException")
    doThrow(new ReflectionException(new Exception)).when(mockMBeanServer).
        getAttribute(objName, "failWithReflectionException")

    doReturn("SomeValue").when(mockMBeanServer).
        getAttribute(objName, "succeedWithSomething")

    val result = underTest.readAttributesFor(mbeanDesc)
    assert(List(("someAlias5", "SomeValue")) === result)
  }
}