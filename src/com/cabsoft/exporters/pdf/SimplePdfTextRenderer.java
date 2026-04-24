/*
 * ReportExpress - Free Java Reporting Library.
 * Copyright (C) 2001 - 2012 CABSOFTWARE.COM. All rights reserved.
 * http://www.cabsoftware.com
 *
 * Unless you have purchased a commercial license agreement from CABSOFTWARE,
 * the following license terms apply:
 *
 * This program is part of ReportExpress.
 *
 * ReportExpress is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ReportExpress is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ReportExpress. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cabsoft.exporters.pdf;

import java.text.AttributedString;


import com.cabsoft.rx.engine.DefaultRXReportsContext;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXPropertiesUtil;
import com.cabsoft.rx.engine.RXRuntimeException;
import com.cabsoft.rx.engine.RXReportsContext;
import com.cabsoft.rx.engine.type.RunDirectionEnum;
import com.cabsoft.rx.engine.util.RXStyledText;
import com.cabsoft.text.DocumentException;
import com.cabsoft.text.Element;
import com.cabsoft.text.Phrase;
import com.cabsoft.text.pdf.ColumnText;
import com.cabsoft.text.pdf.PdfWriter;


/**
 * @author 김영관 (ykkim@cabsoftware.com)
 * @version $Id: SimplePdfTextRenderer.java 5180 2012-03-29 13:23:12Z teodord $
 */
public class SimplePdfTextRenderer extends AbstractPdfTextRenderer
{
	/**
	 * @deprecated Replaced by {@link #SimplePdfTextRenderer(RXReportsContext, boolean)}.
	 */
	public static SimplePdfTextRenderer getInstance()
	{
		return 
			new SimplePdfTextRenderer(
				DefaultRXReportsContext.getInstance(),
				RXPropertiesUtil.getInstance(DefaultRXReportsContext.getInstance()).getBooleanProperty(RXStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT)
				);
	}
	
	
	/**
	 * @deprecated Replaced by {@link #SimplePdfTextRenderer(RXReportsContext, boolean)}.
	 */
	public SimplePdfTextRenderer(boolean ignoreMissingFont)
	{
		this(DefaultRXReportsContext.getInstance(), ignoreMissingFont);
	}
	
	
	/**
	 * 
	 */
	public SimplePdfTextRenderer(RXReportsContext rXReportsContext, boolean ignoreMissingFont)
	{
		super(rXReportsContext, ignoreMissingFont);
	}
	
	
	/**
	 *
	 */
	protected Phrase getPhrase(RXStyledText styledText, RXPrintText textElement)
	{
		String text = styledText.getText();

		AttributedString as = styledText.getAttributedString();

		return pdfExporter.getPhrase(as, text, textElement);
	}

	
	/**
	 * 
	 */
	public void render()
	{
		ColumnText colText = new ColumnText(pdfContentByte);
		colText.setSimpleColumn(
			getPhrase(styledText, text),
			x + leftPadding,
			pdfExporter.exporterContext.getExportedReport().getPageHeight()
				- y
				- topPadding
				- verticalAlignOffset
				- text.getLeadingOffset(),
				//+ text.getLineSpacingFactor() * text.getFont().getSize(),
			x + width - rightPadding,
			pdfExporter.exporterContext.getExportedReport().getPageHeight()
				- y
				- height
				+ bottomPadding,
			0,//text.getLineSpacingFactor(),// * text.getFont().getSize(),
			horizontalAlignment == Element.ALIGN_JUSTIFIED_ALL ? Element.ALIGN_JUSTIFIED : horizontalAlignment
			);

		colText.setLeading(0, text.getLineSpacingFactor());// * text.getFont().getSize());
		colText.setRunDirection(
			text.getRunDirectionValue() == RunDirectionEnum.LTR
			? PdfWriter.RUN_DIRECTION_LTR : PdfWriter.RUN_DIRECTION_RTL
			);

		try
		{
			colText.go();
		}
		catch (DocumentException e)
		{
			throw new RXRuntimeException(e);
		}
	}


	/**
	 * 
	 */
	public void draw()
	{
		//nothing to do
	}
}
