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


import com.cabsoft.rx.engine.DefaultRXReportsContext;
import com.cabsoft.rx.engine.RXPrintText;
import com.cabsoft.rx.engine.RXReportsContext;
import com.cabsoft.rx.engine.type.RunDirectionEnum;

import com.cabsoft.text.Element;
import com.cabsoft.text.pdf.PdfContentByte;


/**
 * @author 김영관 (ykkim@cabsoftware.com)
 * @version $Id: AbstractPdfTextRenderer.java 5180 2012-03-29 13:23:12Z teodord $
 */
public abstract class AbstractPdfTextRenderer extends AbstractTextRenderer
{
	/**
	 * 
	 */
	protected RXPdfExporter pdfExporter;
	protected PdfContentByte pdfContentByte;
	protected int horizontalAlignment;
	protected float leftOffsetFactor;
	protected float rightOffsetFactor;

	
	/**
	 * @deprecated Replaced by {@link #AbstractPdfTextRenderer(RXReportsContext, boolean)}.
	 */
	public AbstractPdfTextRenderer(boolean ignoreMissingFont)
	{
		this(DefaultRXReportsContext.getInstance(), ignoreMissingFont);
	}
	
	
	/**
	 * 
	 */
	public AbstractPdfTextRenderer(RXReportsContext rXReportsContext, boolean ignoreMissingFont)
	{
		super(rXReportsContext, false, ignoreMissingFont);
	}
	
	
	/**
	 * 
	 */
	public void initialize(
		RXPdfExporter pdfExporter, 
		PdfContentByte pdfContentByte,
		RXPrintText text,
		int offsetX,
		int offsetY
		)
	{
		this.pdfExporter = pdfExporter;
		this.pdfContentByte = pdfContentByte;
		
		horizontalAlignment = Element.ALIGN_LEFT;
		leftOffsetFactor = 0f;
		rightOffsetFactor = 0f;
		
		//FIXMETAB 0.2f was a fair approximation
		switch (text.getHorizontalAlignmentValue())
		{
			case JUSTIFIED :
			{
				horizontalAlignment = Element.ALIGN_JUSTIFIED_ALL;
				leftOffsetFactor = 0f;
				rightOffsetFactor = 0f;
				break;
			}
			case RIGHT :
			{
				if (text.getRunDirectionValue() == RunDirectionEnum.LTR)
				{
					horizontalAlignment = Element.ALIGN_RIGHT;
				}
				else
				{
					horizontalAlignment = Element.ALIGN_LEFT;
				}
				leftOffsetFactor = -0.2f;
				rightOffsetFactor = 0f;
				break;
			}
			case CENTER :
			{
				horizontalAlignment = Element.ALIGN_CENTER;
				leftOffsetFactor = -0.1f;
				rightOffsetFactor = 0.1f;
				break;
			}
			case LEFT :
			default :
			{
				if (text.getRunDirectionValue() == RunDirectionEnum.LTR)
				{
					horizontalAlignment = Element.ALIGN_LEFT;
				}
				else
				{
					horizontalAlignment = Element.ALIGN_RIGHT;
				}
				leftOffsetFactor = 0f;
				rightOffsetFactor = 0.2f;
				break;
			}
		}

		super.initialize(text, offsetX, offsetY);
	}
}
