/* 
 * Copyright (C) 2011-2012 ifatter.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifatter.util;

import com.ifatter.Manifest;

import dalvik.system.VMStack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;

import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ManifestParser {

    private InputStream manifestInputStream = null;

    private boolean streamConsumed = false;

    private StringBuffer manifest = new StringBuffer();

    public ManifestParser() {
    }

    public Manifest parser() {

        if (ManifestHandler.mManifest != null) {
            return ManifestHandler.mManifest;
        }

        ClassLoader loader = VMStack.getCallingClassLoader();
        String loaderToString = loader.toString();
        int start = loaderToString.indexOf('[');
        int end = loaderToString.indexOf(']', start);
        String apkPath = loaderToString.substring(start + 1, end);
        try {
            ZipFile zFile = new ZipFile(apkPath);
            ZipEntry entry = zFile.getEntry("AndroidManifest.xml");
            entry.getComment();
            entry.getCompressedSize();
            entry.getCrc();
            entry.isDirectory();
            entry.getSize();
            entry.getMethod();
            manifestInputStream = zFile.getInputStream(entry);
        } catch (IOException e) {
            e.printStackTrace();
        }

        parseManifestToString();

        ManifestHandler handler = new ManifestHandler();

        InputStream in = null;
        try {
            in = new ByteArrayInputStream(manifest.toString().getBytes());
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            parser.parse(in, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
        return ManifestHandler.mManifest;

    }

    private void parseManifestToString() {
        if (streamConsumed) {
            return;
        }
        StringBuffer buffer = manifest;
        if (manifestInputStream == null) {
            buffer.append("Usage: <binary xml file>");
        }
        try {
            AXmlResourceParser parser = new AXmlResourceParser();
            parser.open(manifestInputStream);
            StringBuilder indent = new StringBuilder(10);
            final String indentStep = " ";
            while (true) {
                int type = parser.next();
                if (type == XmlPullParser.END_DOCUMENT) {
                    break;
                }
                switch (type) {
                    case XmlPullParser.START_DOCUMENT: {
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        buffer.append(String.format("%s<%s%s", indent,
                                getNamespacePrefix(parser.getPrefix()), parser.getName()));
                        indent.append(indentStep);
                        int namespaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
                        int namespaceCount = parser.getNamespaceCount(parser.getDepth());
                        for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
                            buffer.append(String.format("%sxmlns:%s=\"%s\"", indent,
                                    parser.getNamespacePrefix(i), parser.getNamespaceUri(i)));
                        }

                        for (int i = 0; i != parser.getAttributeCount(); ++i) {
                            buffer.append(String.format("%s%s%s=\"%s\"", indent,
                                    getNamespacePrefix(parser.getAttributePrefix(i)),
                                    parser.getAttributeName(i), getAttributeValue(parser, i)));
                        }
                        buffer.append(String.format("%s>", indent));
                        if (parser.getName().equals("manifest")) {
                            buffer.toString();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        indent.setLength(indent.length() - indentStep.length());
                        buffer.append(String.format("%s</%s%s>", indent,
                                getNamespacePrefix(parser.getPrefix()), parser.getName()));
                        break;
                    }
                    case XmlPullParser.TEXT: {
                        buffer.append(String.format("%s%s", indent, parser.getText()));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                manifestInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        streamConsumed = true;
    }

    private String getNamespacePrefix(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return "";
        }
        return prefix + ":";
    }

    private String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        if (type == TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        }
        if (type == TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type == TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        }
        if (type == TypedValue.TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        }
        if (type == TypedValue.TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data))
                    + DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type == TypedValue.TYPE_FRACTION) {
            return Float.toString(complexToFloat(data))
                    + FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);
    }

    private String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }

    private float complexToFloat(int complex) {
        return (float)(complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
    }

    private static final float RADIX_MULTS[] = {
            0.00390625F, 3.051758E-005F, 1.192093E-007F, 4.656613E-010F
    };

    private static final String DIMENSION_UNITS[] = {
            "px", "dip", "sp", "pt", "in", "mm", "", ""
    };

    private static final String FRACTION_UNITS[] = {
            "%", "%p", "", "", "", "", "", ""
    };

    private static class ManifestHandler extends DefaultHandler {

        public static Manifest mManifest = null;

        public ManifestHandler() {
            super();
            mManifest = new Manifest();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if ("manifest".equals(localName)) {
                String packageName = attributes.getValue("package");
                mManifest.setPackageName(packageName);
            }
        }

    }

}
