package org.michajlo.jawnitor.ast

import javax.management.ObjectName

object JawnitorAST {

  /**
   * Describes an attribute of an MBean
   *
   * @param name the name of the attribute on the MBean
   * @param alias an alias for this attribute, which will be
   *        uesed for referencing and output
   */
  case class AttrDesc(name: String, alias: String)

  /**
   * Describe an MBean
   *
   * @param objName the ObjectName of the MBean
   * @param attrs the attributes connected to this MBean
   */
  case class MBeanDesc(objName: ObjectName, attrs: List[AttrDesc])
}