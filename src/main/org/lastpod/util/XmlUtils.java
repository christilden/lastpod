/*
 * LastPod is an application used to publish one's iPod play counts to Last.fm.
 * Copyright (C) 2007  Chris Tilden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.lastpod.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Contains various XML utility functions.
 * @author Maksim Liauchuk
 */
public final class XmlUtils {
    /**
     * Cannot construct this utility class.
     */
    private XmlUtils() {
        /* Default constructor. */
    }

    public static void addChild(final Document doc, Element parent, final String childName,
        final String childValue) {
        Element child = doc.createElement(childName);
        Text text = doc.createTextNode(childValue);
        child.appendChild(text);
        parent.appendChild(child);
    }

    public static void xmlToFile(Document doc, String fileNameToWrite)
            throws Exception {
        DOMSource domSource = new DOMSource(doc);
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileNameToWrite)));
        StreamResult streamResult = new StreamResult(out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, streamResult);
    }
}
