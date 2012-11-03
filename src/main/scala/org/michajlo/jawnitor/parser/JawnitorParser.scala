package org.michajlo.jawnitor.parser

import scala.util.parsing.combinator.RegexParsers
import org.michajlo.jawnitor.ast.JawnitorAST._
import javax.management.ObjectName

/**
 * Parser defining the DSL for configuring MBeans to watch.
 *
 * An example input is as follows, for a more specific specification
 * see the source:
 *
 *     mbeans {
 *       object.name:blah=first {
 *         attrName1 as firstsFirstAttr;
 *         attrName2 as firstsSecondAttr;
 *       }
 *
 *       object.name:blah=second {
 *         attrName1 as secondsFirstAttr;
 *         attrName2 as secondsSecondAttr;
 *       }
 *     }
 *
 * Which will result in the follow alias -> attribute mappings:
 *
 *     firstsFirstAttr   -> ("object.name:blah=first", "attrName1")
 *     firstsSecondAttr  -> ("object.name:blah=first", "attrName2")
 *     secondsFirstAttr  -> ("object.name:blah=second", "attrName1")
 *     secondsSecondAttr -> ("object.name:blah=second", "attrName2")
 *
 */
object JawnitorParser extends RegexParsers {

  /**
   * Parse an MBean declaration string
   *
   * @param the string declarin the MBeans
   *
   * @return the ParseResult[List[MBeanDesc]] of the parsing.
   *         On success the result will contain a list of all
   *         MBeanDescs parsed.
   */
  def parse(str: String): ParseResult[List[MBeanDesc]] = parseAll(mbeans, str)


  private def objName: Parser[String] = "[a-zA-Z0-9.]+:[a-zA-Z][a-zA-Z0-9/;,=_.*-]+".r
  private def attrName: Parser[String] = "[a-zA-Z0-9_]+".r
  private def attrAlias: Parser[String] = "[a-z][a-zA-Z0-9_]*".r

  private def attrDesc: Parser[AttrDesc] = attrName ~ "as" ~ attrAlias ~ ";" ^? {
    case attrNameValue ~ "as" ~ attrNameAlias ~ ";" => AttrDesc(attrNameValue, attrNameAlias)
  }

  private def attrDescList: Parser[List[AttrDesc]] = rep1(attrDesc)

  private def mbeanDesc: Parser[MBeanDesc] = objName ~ "{" ~ attrDescList ~ "}" ^? {
    case objNameValue ~ "{" ~ attrDescListValue ~ "}" =>
      MBeanDesc(new ObjectName(objNameValue), attrDescListValue)
  }

  private def mbeans: Parser[List[MBeanDesc]] = ("mbeans" ~ "{") ~> rep1(mbeanDesc) <~ "}"
}