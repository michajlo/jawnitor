package org.michajlo.jawnitor.mbean

import java.lang.management.ManagementFactory

import org.michajlo.jawnitor.ast.JawnitorAST.MBeanDesc

import javax.management.AttributeNotFoundException
import javax.management.InstanceNotFoundException
import javax.management.MBeanException
import javax.management.MBeanServer
import javax.management.ObjectName
import javax.management.ReflectionException

object MBeanReader {

  /**
   * Create an MBeanReader using the Platform MBeanServer
   */
  def apply(): MBeanReader = {
    // TODO: this may not always be the server we want...
    val mbeanServer = ManagementFactory.getPlatformMBeanServer
    new MBeanReader(mbeanServer)
  }
}

/**
 * Service class for reading metrics off of an mbeanServer
 *
 * @param mbeanServer the MBeanServer that operations will
 *        take place against for this instance
 */
class MBeanReader(mbeanServer: MBeanServer) {

  /**
   * Reads all attributes associated with mbeanDesc from the
   * resident MBeanServer and return a List of alias -> value
   * mappings for all attributes found.
   *
   * Fails silently- if an object name isn't found an empty list
   * is returned, if an getting an attribute fails that attribute
   * is omitted.
   *
   * @param mbeanDesc the MBeanDesc describing the attributes to
   *        read from the resident MBeanServer
   *
   * @return List[(String, Any)] where the first item in each tuple
   *         is the alias of the read attribute, and the second
   *         item is the value. Using a list because prepending is
   *         an O(1) operation, where not so much for Maps
   */
  def readAttributesFor(mbeanDesc: MBeanDesc): List[(String, Any)] =
    getActualObjectNameFor(mbeanDesc.objName) match {
      case Some(objName) =>
        mbeanDesc.attrs.foldLeft(List[(String, Any)]()) {
          (acc, attrDesc) => readAttribute(objName, attrDesc.name) match {
            case Some(value) => (attrDesc.alias, value) :: acc
            case None => acc
          }
        }
      case None =>
        // TODO: log inability to find
        Nil
    }

  // convert objectNames that may be a pattern to not a pattern, and verifies existence
  private def getActualObjectNameFor(objName: ObjectName): Option[ObjectName] = {
    val matchesIterator = mbeanServer.queryNames(objName, null).iterator
    if (matchesIterator.hasNext) {
      Some(matchesIterator.next())
    } else {
      None
    }
  }

  // helper to read an attribute, but return None on failure
  private def readAttribute(objName: ObjectName, attrName: String): Option[Any] =
    try {
      Some(mbeanServer.getAttribute(objName, attrName))
    } catch {
      case _: MBeanException | _: AttributeNotFoundException |
           _: InstanceNotFoundException | _: ReflectionException =>
             // TODO: log the failure
             None
    }

}