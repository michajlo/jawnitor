package org.michajlo.jawnitor.output

/**
 * Trait describing an output sink for found metrics.
 *
 * XXX: not sure on this one, for now it's just a sink for
 *      testing
 */
trait JawnitorMetricOutput {

  /**
   * Deliver the metric data to its final resting place,
   * wherever that may be.
   *
   * XXX: the input type will probably change here...
   *
   * @param metrics the data received from the local mbean
   *        server. The first element in each tuple is the
   *        alias, and the second the associated value
   */
  def deliver(metrics: List[(String, Any)])
}