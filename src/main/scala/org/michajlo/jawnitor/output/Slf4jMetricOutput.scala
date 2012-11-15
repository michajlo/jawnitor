package org.michajlo.jawnitor.output
import org.slf4j.LoggerFactory
import org.slf4j.Logger

/**
 * Metric output which will dump metrics to the passed in Slf4j logger
 */
class Slf4jMetricOutput(logger:  Logger) extends JawnitorMetricOutput {

  /**
   * {@inheritdoc}
   */
  def deliver(metrics: List[(String, Any)]) {
    logger.info(formatAsLine(metrics))
  }

  // TODO: escape quotes
  private def formatAsLine(metrics: List[(String, Any)]): String =
    metrics.map(kv => "%s=\"%s\"".format(kv._1, kv._2)).mkString(",")

}