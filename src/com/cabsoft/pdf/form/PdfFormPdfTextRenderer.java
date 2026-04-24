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
package com.cabsoft.pdf.form;


import com.cabsoft.rx.engine.DefaultRXReportsContext;
import com.cabsoft.rx.engine.RXPropertiesUtil;
import com.cabsoft.rx.engine.RXRuntimeException;
import com.cabsoft.rx.engine.RXReportsContext;
import com.cabsoft.rx.engine.type.RunDirectionEnum;
import com.cabsoft.rx.engine.util.RXStyledText;
import com.cabsoft.text.DocumentException;
import com.cabsoft.text.pdf.ColumnText;
import com.cabsoft.text.pdf.PdfWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: PdfTextRenderer.java 5050 2012-03-12 10:11:26Z teodord $
 */
public class PdfFormPdfTextRenderer extends PdfFormAbstractPdfTextRenderer
{
	/**
	 * @deprecated Replaced by {@link #PdfTextRenderer(RXReportsContext, boolean)}.
	 */
	public static PdfFormPdfTextRenderer getInstance()
	{
		return 
			new PdfFormPdfTextRenderer(
				DefaultRXReportsContext.getInstance(),
				RXPropertiesUtil.getInstance(DefaultRXReportsContext.getInstance()).getBooleanProperty(RXStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT)
				);
	}
	
	
	/**
	 * @deprecated Replaced by {@link #PdfTextRenderer(RXReportsContext, boolean)}. 
	 */
	public PdfFormPdfTextRenderer(boolean ignoreMissingFont)
	{
		this(DefaultRXReportsContext.getInstance(), ignoreMissingFont);
	}
	
	
	/**
	 * 
	 */
	public PdfFormPdfTextRenderer(RXReportsContext rXReportsContext, boolean ignoreMissingFont)
	{
		super(rXReportsContext, ignoreMissingFont);
	}
	
	
	/**
	 * 
	 */
	public void draw()
	{
		TabSegment segment = segments.get(segmentIndex);
		
		float advance = segment.layout.getAdvance();
		
		ColumnText colText = new ColumnText(pdfContentByte);
		colText.setSimpleColumn(
			pdfExporter.getPhrase(segment.as, segment.text, text),
			x + drawPosX + leftOffsetFactor * advance,// + leftPadding
			pdfExporter.exporterContext.getExportedReport().getPageHeight()
				- y
				- topPadding
				- verticalAlignOffset
				//- text.getLeadingOffset()
				+ lineHeight
				- drawPosY,
			x + drawPosX  + segment.layout.getAdvance() + rightOffsetFactor * advance,// + leftPadding
			pdfExporter.exporterContext.getExportedReport().getPageHeight()
				- y
				- topPadding
				- verticalAlignOffset
				//- text.getLeadingOffset()
				-400//+ lineHeight//FIXMETAB
				- drawPosY,
			0,//text.getLineSpacingFactor(),// * text.getFont().getSize(),
			horizontalAlignment
			);

		//colText.setLeading(0, text.getLineSpacingFactor());// * text.getFont().getSize());
		colText.setLeading(lineHeight);
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
	

}
