package org.michajlo.jawnitor.parser

import java.io.Reader

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
   * @param the string declaring the MBeans
   *
   * @return the ParseResult[List[MBeanDesc]] of the parsing.
   *         On success the result will contain a list of all
   *         MBeanDescs parsed.
   */
  def parse(str: String): List[MBeanDesc] =
    parseAll(mbeans, str) match {
      case Success(result, _) => result
      case failure: Failure =>
        // TODO: perhaps a more descriptive type of exception?
        throw new IllegalArgumentException(failure.toString)
    }

  /**
   * Parse an MBean declaration file
   *
   * @param the reader for the resource declaring the MBeans
   *
   * @return the ParseResult[List[MBeanDesc]] of the parsing.
   *         On success the result will contain a list of all
   *         MBeanDescs parsed.
   */
  def parse(reader: Reader): List[MBeanDesc] =
    parseAll(mbeans, reader) match {
      case Success(result, _) => result
      case failure: Failure =>
        // TODO: perhaps a more descriptive type of exception?
        throw new IllegalArgumentException(failure.toString)
    }

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