package com.oldbaby.oblib.util.log;

import android.text.TextUtils;
import android.util.Log;

import com.oldbaby.oblib.util.MLog;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * usage: 输出XML字符串格式化日志
 * author: kHRYSTAL
 * create time: 17/1/5
 * update time:
 * email: 723526676@qq.com
 */

public class XmlLog {

    public static void printXml(String tag, String xml, String headString) {
        if (!TextUtils.isEmpty(xml)) {
            xml = XmlLog.formatXML(xml);
            xml = headString + "\n" + xml;
        } else {
            xml = headString + MLog.NULL_TIPS;
        }
        MLogUtil.printLine(tag, true);
        String[] lines = xml.split(MLog.LINE_SEPARATOR);
        for (String line : lines) {
            if (!MLogUtil.isEmpty(line)) {
                Log.d(tag, "║ " + line);
            }
        }

        MLogUtil.printLine(tag, false);
    }

    private static String formatXML(String inputXML) {
        try {
            Source xmlInput = new StreamSource(new StringReader(inputXML));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (Exception e) {
            e.printStackTrace();
            return inputXML;
        }
    }
}
