package org.michajlo.jawnitor.task

import java.util.TimerTask
import java.lang.management.ManagementFactory
import org.michajlo.jawnitor.mbean.MBeanReader
import org.michajlo.jawnitor.ast.JawnitorAST._
import org.michajlo.jawnitor.output.JawnitorMetricOutput

/**
 * TimerTask for polling the PlatformMBeanServer for the metrics
 * specified by mbeanDescs and outputting them.
 *
 * Use the companion apply instead of this.
 *
 * @param mbeanDescs MBeanDescs describing what this should
 *        poll and how it should be output
 * @param mbeanReader MBeanReader to read metrics using
 */
class PollTask(mbeanDescs: List[MBeanDesc],
               mbeanReader: MBeanReader,
               output: JawnitorMetricOutput) extends TimerTask {

  override def run() {
    val readMetrics = mbeanDescs.map(mbeanReader.readAttributesFor(_)).flatten
    output.deliver(readMetrics)
  }
}