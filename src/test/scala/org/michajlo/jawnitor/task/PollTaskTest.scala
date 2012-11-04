package org.michajlo.jawnitor.task

import org.michajlo.jawnitor.ast.JawnitorAST.MBeanDesc
import org.michajlo.jawnitor.mbean.MBeanReader
import org.michajlo.jawnitor.output.JawnitorMetricOutput
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.ArgumentCaptor
import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec

class PollTaskTest extends FunSpec with MockitoSugar {

  it ("must deliver all read metrics to the output sink") {
    val mockMBeanReader = mock[MBeanReader]
    val mockOutput = mock[JawnitorMetricOutput]

    val mockMBeanDesc1 = mock[MBeanDesc]
    val mockMBeanDesc2 = mock[MBeanDesc]
    val mockMBeanDesc3 = mock[MBeanDesc]
    val mockMBeanDescs = List(mockMBeanDesc1, mockMBeanDesc2, mockMBeanDesc3)

    val underTest = new PollTask(mockMBeanDescs, mockMBeanReader, mockOutput)

    doReturn(List[(String, Any)](("alias1", "value1"))).when(mockMBeanReader).
        readAttributesFor(mockMBeanDesc1)
    doReturn(List[(String, Any)]()).when(mockMBeanReader).
        readAttributesFor(mockMBeanDesc2)
    doReturn(List[(String, Any)](("alias2", "value2"))).when(mockMBeanReader).
        readAttributesFor(mockMBeanDesc3)

    val captor = ArgumentCaptor.forClass(classOf[List[(String, Any)]])

    underTest.run()

    verify(mockOutput).deliver(captor.capture())

    val expected = List[(String, Any)](("alias1", "value1"), ("alias2", "value2"))
    assert(expected === captor.getValue.sortWith(_._1 < _._1))
  }
}