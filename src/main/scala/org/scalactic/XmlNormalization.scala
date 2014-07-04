/*
 * Copyright 2001-2014 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalactic

import scala.xml.{Text, Node, Elem, NodeSeq}

trait XmlNormalization {

  private def trimTextZappingEmpty(node: Node): Seq[Node] =
    node match {
      case Text(text) if (text.trim.isEmpty) => Nil
      case Text(text) => List(Text(text.trim))
      case Elem(pre, lab, md, scp, children @ _*) =>
        Elem(pre, lab, md, scp, false, (children.flatMap(trimTextZappingEmpty)):_*)
      case _ => List(node)
    }

  implicit def compressed[T <: NodeSeq]: Uniformity[T] = {

    new Uniformity[T] {
      def normalized(nodeSeq: T): T =
        nodeSeq match {
          case Elem(pre, lab, md, scp, children @ _*) =>
            val mergedTextNodes = // Merge adjacent text nodes
              children.foldLeft(Nil: List[Node]) { (acc, ele) =>
                ele match {
                  case eleTxt: Text =>
                    acc.headOption match {
                      case Some(accTxt: Text) =>
                        Text(accTxt.text + eleTxt.text) :: acc.tail
                      case _ => ele :: acc
                    }
                  case _ => ele :: acc
                }
              }
            Elem(pre, lab, md, scp, false, (mergedTextNodes.flatMap(trimTextZappingEmpty)):_*).asInstanceOf[T]
          case _ => nodeSeq
        }

      /**
       * Returns true if the passed <code>Any</code> is a <code>Elem</code>.
       *
       * @return true if the passed <code>Any</code> is a <code>Elem</code>.
       */
      final def normalizedCanHandle(b: Any): Boolean = b.isInstanceOf[NodeSeq]
    
      /**
       * Normalizes the passed object if it is a <code>Elem</code>.
       *
       * <p>
       * This method returns either:
       * </p>
       *
       * <ul>
       * <li>if the passed object is a <code>Elem</code>, the result of passing that string to <code>normalized</code></li>
       * <li>else, the same exact object that was passed
       * </p>
       *
       * @return a normalized form of any passed <code>Elem</code>, or the same object if not a <code>Elem</code>.
       */
      final def normalizedOrSame(b: Any): Any =
        b match {
          case s: NodeSeq => XmlNormalization.compressed[NodeSeq].normalized(s)
          case _ => b
       }
    }
  }
/*
  implicit val normalizedXmlElem: Uniformity[Elem] = {

    new Uniformity[Elem] {
      def normalized(elem: Elem): Elem =
        elem match {
          case Elem(pre, lab, md, scp, children @ _*) =>
            val mergedTextNodes = // Merge adjacent text nodes
              children.foldLeft(Nil: List[Node]) { (acc, ele) =>
                ele match {
                  case eleTxt: Text =>
                    acc.headOption match {
                      case Some(accTxt: Text) =>
                        Text(accTxt.text + eleTxt.text) :: acc.tail
                      case _ => ele :: acc
                    }
                  case _ => ele :: acc
                }
              }
            Elem(pre, lab, md, scp, false, (mergedTextNodes.flatMap(trimTextZappingEmpty)):_*)
        }

      /**
       * Returns true if the passed <code>Any</code> is a <code>Elem</code>.
       *
       * @return true if the passed <code>Any</code> is a <code>Elem</code>.
       */
      final def normalizedCanHandle(b: Any): Boolean = b.isInstanceOf[Elem]
    
      /**
       * Normalizes the passed object if it is a <code>Elem</code>.
       *
       * <p>
       * This method returns either:
       * </p>
       *
       * <ul>
       * <li>if the passed object is a <code>Elem</code>, the result of passing that string to <code>normalized</code></li>
       * <li>else, the same exact object that was passed
       * </p>
       *
       * @return a normalized form of any passed <code>Elem</code>, or the same object if not a <code>Elem</code>.
       */
      final def normalizedOrSame(b: Any): Any =
        b match {
          case s: Elem => normalized(s)
          case _ => b
       }
    }
  }

  implicit val normalizedXmlNode: Uniformity[Node] = {

    new Uniformity[Node] {
      def normalized(node: Node): Node =
        node match {
          case elem: Elem => normalizedXmlElem.normalized(elem)
          case _ => node
        }

      /**
       * Returns true if the passed <code>Any</code> is a <code>Node</code>.
       *
       * @return true if the passed <code>Any</code> is a <code>Node</code>.
       */
      final def normalizedCanHandle(b: Any): Boolean = b.isInstanceOf[Node]
    
      /**
       * Normalizes the passed object if it is a <code>Node</code>.
       *
       * <p>
       * This method returns either:
       * </p>
       *
       * <ul>
       * <li>if the passed object is a <code>Node</code>, the result of passing that string to <code>normalized</code></li>
       * <li>else, the same exact object that was passed
       * </p>
       *
       * @return a normalized form of any passed <code>Node</code>, or the same object if not a <code>Node</code>.
       */
      final def normalizedOrSame(b: Any): Any =
        b match {
          case s: Node => normalized(s)
          case _ => b
       }
    }
  }

  implicit val normalizedXmlNodeSeq: Uniformity[NodeSeq] = {

    new Uniformity[NodeSeq] {
      def normalized(nodeSeq: NodeSeq): NodeSeq =
        nodeSeq match {
          case elem: Elem => normalizedXmlElem.normalized(elem)
          case _ => nodeSeq
        }

      /**
       * Returns true if the passed <code>Any</code> is a <code>NodeSeq</code>.
       *
       * @return true if the passed <code>Any</code> is a <code>NodeSeq</code>.
       */
      final def normalizedCanHandle(b: Any): Boolean = b.isInstanceOf[NodeSeq]
    
      /**
       * Normalizes the passed object if it is a <code>NodeSeq</code>.
       *
       * <p>
       * This method returns either:
       * </p>
       *
       * <ul>
       * <li>if the passed object is a <code>NodeSeq</code>, the result of passing that string to <code>normalized</code></li>
       * <li>else, the same exact object that was passed
       * </p>
       *
       * @return a normalized form of any passed <code>NodeSeq</code>, or the same object if not a <code>NodeSeq</code>.
       */
      final def normalizedOrSame(b: Any): Any =
        b match {
          case s: NodeSeq => normalized(s)
          case _ => b
       }
    }
  }
*/
}

object XmlNormalization extends XmlNormalization
