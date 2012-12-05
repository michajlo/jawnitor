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

/**
 * Create a Jawnitor instance.  On creation a TimerTask is created to do
 * the dirty work of reading and recording JMX metrics. For the life of this
 * instance (until stop is called) it will log JMX metrics defined in the file
 * at cfgLocation to the logger at loggerName every pollIntervalMillis milliseconds.
 * 
 * Call stop to discontinue metric collection.
 * 
 * @param cfgLocation a file name, either on the filesystem or classpath, in that order,
 *        which will be used for configuring the JMX metrics which Jawnitor will keep
 *        track of
 * @param loggerName the slf4j logger name to which jawnitor should append the JMX
 *        metrics it records
 * @param pollIntervalMillis how often to read and log metrics 
 */
class Jawnitor(cfgLocation: String, loggerName: String, pollIntervalMillis: Long) {

  val metricLogger = LoggerFactory.getLogger(loggerName)

  val mbeanDescs = loadMBeanDescs()
  val pollTask = new PollTask(mbeanDescs, MBeanReader(), new Slf4jMetricOutput(metricLogger))

  val timer = new Timer
  timer.schedule(pollTask, pollIntervalMillis, pollIntervalMillis)

  /**
   * Stop this instance, cancelling the internal TimerTask used for periodic execution.
   */
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
