/*
 * ReportExpress - Java Reporting Library.
 * Copyright (C) 2003 - 2010 Cabsoft Corporation. All rights reserved.
 * http://www.cabsoftware.com
 *
 */
package com.cabsoft.image;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cabsoft.rx.engine.RXConstants;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXImageRenderer;
import com.cabsoft.rx.engine.RXPrintImage;
import com.cabsoft.rx.engine.RXRenderable;
import com.cabsoft.rx.engine.RXWrappingSvgRenderer;
import com.cabsoft.rx.engine.ReportExpressPrint;
import com.cabsoft.rx.engine.export.RXHtmlExporter;
import com.cabsoft.rx.engine.type.ModeEnum;
import com.cabsoft.rx.engine.util.RXTypeSniffer;
import com.cabsoft.utils.SimpleQuery;
import com.cabsoft.utils.StringUtils;

/**
 * 이미지 서블릿 구현 클래스.
 * 
 * @author Cabsoft(support@cabsoftware.com)
 * @version $Id: ImageServlet.java 0001 2010-07-21 $
 */
@SuppressWarnings("deprecation")
public class ImageService extends BaseHttpService {
	private static final long serialVersionUID = RXConstants.SERIAL_VERSION_UID;
	private final Log log = LogFactory.getLog(ImageService.class);

	/**
	 *
	 */
	public final String IMAGE_NAME_REQUEST_PARAMETER = "image";

	/**
	 *
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		byte[] imageData = null;
		String imageMimeType = null;
		String imageName = null;

		String p = (String) request.getParameter("__p");
		if (!StringUtils.isEmpty(p)) {
			String pp = new String(com.cabsoft.utils.Hex.parseHexaString(p));
			SimpleQuery qry = new SimpleQuery();
			qry.setQuery(pp);
			imageName = qry.getQuery("image");
		} else {
			imageName = request.getParameter(IMAGE_NAME_REQUEST_PARAMETER);
		}

		log.debug("Image Name: " + imageName);
		if ("px".equals(imageName)) {
			try {
				RXRenderable pxRenderer = RXImageRenderer.getInstance("com/cabsoft/rx/engine/images/pixel.GIF");
				imageData = pxRenderer.getImageData();
				imageMimeType = RXRenderable.MIME_TYPE_GIF;
			} catch (RXException e) {
				log.error(e);
				throw new ServletException(e);
			}
		} else {
			List<ReportExpressPrint> rxPrintList = getReportExpressPrintList(request);

			if (rxPrintList == null) {
				log.error("HTTP 세션에서 ReportExpressPrint 도큐먼트를 찾을 수 없습니다.");
				throw new ServletException("HTTP 세션에서 ReportExpressPrint 도큐먼트를 찾을 수 없습니다.");
			}

			RXPrintImage image = RXHtmlExporter.getImage(rxPrintList, imageName);

			RXRenderable renderer = image.getRenderer();
			if (renderer.getType() == RXRenderable.TYPE_SVG) {
				renderer = new RXWrappingSvgRenderer(renderer, new Dimension(image.getWidth(), image.getHeight()), ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor() : null);
			}

			imageMimeType = RXTypeSniffer.getImageMimeType(renderer.getImageType());

			try {
				imageData = renderer.getImageData();
			} catch (RXException e) {
				log.error(e);
				throw new ServletException(e);
			}
		}

		if (imageData != null && imageData.length > 0) {
			if (imageMimeType != null) {
				response.setHeader("Content-Type", imageMimeType);
			}
			response.setContentLength(imageData.length);
			ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(imageData, 0, imageData.length);
			ouputStream.flush();
			ouputStream.close();
		}
	}
}
