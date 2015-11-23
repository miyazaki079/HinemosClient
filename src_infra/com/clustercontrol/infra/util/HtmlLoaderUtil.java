/*

Copyright (C) 2014 NTT DATA Corporation

 This program is free software; you can redistribute it and/or
 Modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation, version 2.

 This program is distributed in the hope that it will be
 useful, but WITHOUT ANY WARRANTY; without even the implied
 warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.infra.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.swt.browser.Browser;

public class HtmlLoaderUtil {
	private static final String RESEOURCE_PREFIX = "com/clustercontrol/infra/util/";

	public static void load(Browser browser, String htmlFile) {
		browser.setText(getHtmlContent(htmlFile));
	}

	private static String getHtmlContent(String url) {
		StringBuilder html = new StringBuilder();
		html.append(getTextFromResource(url, "UTF-8"));
		inlineScripts(html);
		inlineCSS(html);
		return html.toString();
	}

	private static String getTextFromResource(String resourceName,
			String charset) {
		try {
			return getTextFromResourceChecked(RESEOURCE_PREFIX + resourceName,
					charset);
		} catch (IOException exception) {
			String message = "Could not read text from resource: "
					+ resourceName;
			throw new IllegalArgumentException(message, exception);
		}
	}

	private static String getTextFromResourceChecked(String resourceName,
			String charset) throws IOException {
		InputStream inputStream = HtmlLoaderUtil.class.getClassLoader()
				.getResourceAsStream(resourceName);
		if (inputStream == null) {
			throw new IllegalArgumentException("Resource not found: "
					+ resourceName);
		}
		try {
			return getTextFromInputStream(inputStream, charset);
		} finally {
			inputStream.close();
		}
	}

	private static String getTextFromInputStream(InputStream inputStream,
			String charset) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream, charset));
		String line = reader.readLine();
		while (line != null) {
			builder.append(line);
			builder.append('\n');
			line = reader.readLine();
		}
		return builder.toString();
	}

	private static void inlineScripts(StringBuilder html) {
		String srcAttrStr = "src=\"./";
		String quotStr = "\"";
		String tagStr = "<script ";
		String closingTagStr = "</script>";
		String newTagStr = "<script type=\"text/javascript\">";
		int offset = html.length();
		while ((offset = html.lastIndexOf(tagStr, offset)) != -1) {
			int closeTag = html.indexOf(closingTagStr, offset);
			int srcAttr = html.indexOf(srcAttrStr, offset);
			if (srcAttr != -1 && srcAttr < closeTag) {
				int srcAttrStart = srcAttr + srcAttrStr.length();
				int srcAttrEnd = html.indexOf(quotStr, srcAttrStart);
				if (srcAttrEnd != -1) {
					String filename = html.substring(srcAttrStart, srcAttrEnd);
					StringBuffer newScriptTag = new StringBuffer();
					newScriptTag.append(newTagStr);
					newScriptTag.append(getTextFromResource(filename, "UTF-8"));
					newScriptTag.append(closingTagStr);
					html.replace(offset, closeTag + closingTagStr.length(),
							newScriptTag.toString());
				}
			}
			offset--;
		}
	}
	
	private static void inlineCSS(StringBuilder html) {
		String srcAttrStr = "href=\"./";
		String quotStr = "\"";
		String tagStr = "<link ";
		String closingTagStr = "</link>";
		String newTagStr = "<style type=\"text/css\">";
		String newClosingTagStr = "</style>";
		int offset = html.length();
		while ((offset = html.lastIndexOf(tagStr, offset)) != -1) {
			int closeTag = html.indexOf(closingTagStr, offset);
			int srcAttr = html.indexOf(srcAttrStr, offset);
			if (srcAttr != -1 && srcAttr < closeTag) {
				int srcAttrStart = srcAttr + srcAttrStr.length();
				int srcAttrEnd = html.indexOf(quotStr, srcAttrStart);
				if (srcAttrEnd != -1) {
					String filename = html.substring(srcAttrStart, srcAttrEnd);
					StringBuffer newScriptTag = new StringBuffer();
					newScriptTag.append(newTagStr);
					newScriptTag.append(getTextFromResource(filename, "UTF-8"));
					newScriptTag.append(newClosingTagStr);
					html.replace(offset, closeTag + closingTagStr.length(),
							newScriptTag.toString());
				}
			}
			offset--;
		}
	}
}