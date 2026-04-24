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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package com.cabsoft.exporters.pdf;

import com.cabsoft.rx.engine.RXPen;
import com.cabsoft.rx.engine.RXPropertiesUtil;
import com.cabsoft.rx.engine.type.LineStyleEnum;


/**
 * @author 김영관 (ykkim@cabsoftware.com)
 * @version $Id: BorderOffset.java 5088 2012-03-15 11:55:32Z teodord $
 */
public abstract class BorderOffset
{
	/**
	 * 
	 */
	public static final String PROPERTY_LEGACY_BORDER_OFFSET = RXPropertiesUtil.PROPERTY_PREFIX + "export.legacy.border.offset";
	
	/**
	 * 
	 */
	public static final BorderOffset DEFAULT = 
		new BorderOffset()
		{
			public float getValue(RXPen pen)
			{
				return 0f;
			}
		};
	
	/**
	 * 
	 */
	public static final BorderOffset LEGACY = 
		new BorderOffset()
		{
			public float getValue(RXPen pen)
			{
				float lineWidth = pen.getLineWidth().floatValue();
				LineStyleEnum lineStyle = pen.getLineStyleValue();
				
				if (
					lineWidth == 0.5f 
					&& lineStyle == LineStyleEnum.SOLID
					)
				{
					return 0.25f;
				}
				else if (
						lineWidth == 1.0f 
					&& (lineStyle == LineStyleEnum.SOLID 
						|| lineStyle == LineStyleEnum.DASHED)
					)
				{
					return 0.5f;
				}
				
				return 0f;
			}
		};
		
		
	/**
	 * 
	 */
	private static final ThreadLocal<BorderOffset> threadInstance = new ThreadLocal<BorderOffset>();
	

	/**
	 * 
	 */
	public static void setLegacy(boolean isLegacy)
	{
		threadInstance.set(isLegacy ? LEGACY : DEFAULT);
	}

	/**
	 * 
	 */
	
	@SuppressWarnings("deprecation")
	public static float getOffset(RXPen pen)
	{
		BorderOffset borderOffset = threadInstance.get();
		if (borderOffset == null)
		{
			borderOffset = 
				com.cabsoft.rx.engine.util.RXProperties.getBooleanProperty(BorderOffset.PROPERTY_LEGACY_BORDER_OFFSET)
				? LEGACY : DEFAULT;
		}
		return borderOffset.getValue(pen);
	}

	/**
	 * 
	 */
	public abstract float getValue(RXPen pen);

}
