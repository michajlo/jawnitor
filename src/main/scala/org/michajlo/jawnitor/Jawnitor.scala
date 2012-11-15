package org.michajlo.jawnitor

import java.io.FileReader
import org.michajlo.jawnitor.ast.JawnitorAST.MBeanDesc
import org.michajlo.jawnitor.util.InputUtil
import org.michajlo.jawnitor.parser.JawnitorParser
import org.michajlo.jawnitor.task.PollTask
import org.michajlo.jawnitor.mbean.MBeanReader
import org.slf4j.LoggerFactory
import org.michajlo.jawnitor.output.Slf4jMetricOutput
import java.util.Timer


class Jawnitor(cfgLocation: String, loggerName: String, pollIntervalMillis: Long) {

  val metricLogger = LoggerFactory.getLogger(loggerName)

  val mbeanDescs = loadMBeanDescs()
  val pollTask = new PollTask(mbeanDescs, MBeanReader(), new Slf4jMetricOutput(metricLogger))

  val timer = new Timer
  timer.schedule(pollTask, pollIntervalMillis, pollIntervalMillis)

  def stop() {
    timer.cancel()
  }

  private def loadMBeanDescs(): List[MBeanDesc] =
    InputUtil.getReaderFor(cfgLocation) match {
      case Some(input) => JawnitorParser.parse(input)
      case None =>
        throw new IllegalArgumentException("Could not locate " + cfgLocation)
    }
}
