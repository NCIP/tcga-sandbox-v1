/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HttpServletBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet definition that renders resource from a url. The code comes from the spring
 * ResourceServlet which allows for resolving of resources (static content) in jar files.
 * This servlet has been modified and reduced to the need of allowing a default rendering of
 * static resources in a spring environment when a dispatcher servlet has mapping to *.htm files for example.
 * Use this servelt to render any resources as-is by your application server.
 * Configuration resides in the application web.xml just like any servlets.
 * This class has dependencies with spring libraries
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class StaticContentServlet extends HttpServletBean{

	private static final String HTTP_CONTENT_LENGTH_HEADER = "Content-Length";

	private static final String HTTP_LAST_MODIFIED_HEADER = "Last-Modified";

	private static final String HTTP_EXPIRES_HEADER = "Expires";

	private static final String HTTP_CACHE_CONTROL_HEADER = "Cache-Control";

	private static final Log log = LogFactory.getLog(StaticContentServlet.class);

	private Map defaultMimeTypes = new HashMap();
	{
		defaultMimeTypes.put(".css", "text/css");
		defaultMimeTypes.put(".gif", "image/gif");
		defaultMimeTypes.put(".ico", "image/vnd.microsoft.icon");
		defaultMimeTypes.put(".jpeg", "image/jpeg");
		defaultMimeTypes.put(".jpg", "image/jpeg");
		defaultMimeTypes.put(".js", "text/javascript");
		defaultMimeTypes.put(".png", "image/png");
        defaultMimeTypes.put(".htm", "text/html");
        defaultMimeTypes.put(".html", "text/html");
	}

    //Set the cache to a day (in seconds) - quite high because static content is not supposed to change often
    // and could be big like images ...
	private int cacheTimeout = 8640;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String rawResourcePath = request.getServletPath()+request.getPathInfo();

		if (log.isDebugEnabled()) {
			log.debug("Attempting to GET resource: " + rawResourcePath);
		}
		final URL[] resources = getRequestResourceURLs(request);
		if (resources == null || resources.length == 0) {
			if (log.isDebugEnabled()) {
				log.debug("Resource not found: " + rawResourcePath);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND); //Error 404
			return;
		}
        //Render the resource as-is with a stream
		prepareResponse(response, resources, rawResourcePath);
		final OutputStream out = response.getOutputStream();
		try {
			for (int i = 0; i < resources.length; i++) {
				final URLConnection resourceConn = resources[i].openConnection();
				final InputStream in = resourceConn.getInputStream();
				try {
					byte[] buffer = new byte[1024];
					int bytesRead = -1;
					while ((bytesRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
					}
				} finally {
					in.close();
				}
			}
		} finally {
			out.close();
		}
	}

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,response);
    }

	private void prepareResponse(HttpServletResponse response, URL[] resources, String rawResourcePath)
			throws IOException {
        long lastModified = -1;
        int contentLength = 0;
        String mimeType = null;
        for (int i = 0; i < resources.length; i++) {
            final URLConnection resourceConn = resources[i].openConnection();
            //Setting the last modified http for the requested resource
            if (resourceConn.getLastModified() > lastModified) {
                lastModified = resourceConn.getLastModified();
            }
            //Setting the mime type of the resource requested
            String currentMimeType = getServletContext().getMimeType(resources[i].getPath());
            if (log.isDebugEnabled()) {
                log.debug("Current MimeType: " + currentMimeType);
            }
            if (currentMimeType == null) {
                int index = resources[i].getPath().lastIndexOf('.');
                if (index > 0) {
                    String extension = resources[i].getPath().substring(resources[i].getPath().lastIndexOf('.'));
                    currentMimeType = (String) defaultMimeTypes.get(extension);
                } else {
                    currentMimeType = "text/html"; //html will be the default mime.
                }
            }
            if (mimeType == null) {
                mimeType = currentMimeType;
            } else if (!mimeType.equals(currentMimeType)) {
                //This does not apply to us yet since we don't use combined resource url but maybe in the future ...
                throw new MalformedURLException("Combined resource path: " + rawResourcePath
                        + " is invalid. All resources in a combined resource path must be of the same mime type.");
            }
            contentLength += resourceConn.getContentLength();
        }
        response.setContentType(mimeType);
        response.setHeader(HTTP_CONTENT_LENGTH_HEADER, Long.toString(contentLength));
        response.setDateHeader(HTTP_LAST_MODIFIED_HEADER, lastModified);
        if (cacheTimeout > 0) {
            configureCaching(response, cacheTimeout);
        }
    }

    //Setting up the last modified attribute of an http request.
	protected long getLastModified(HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("Checking last modified of resource: " + request.getServletPath()+request.getPathInfo());
        }
        URL[] resources;
        try {
            resources = getRequestResourceURLs(request);
        } catch (MalformedURLException e) {
            return -1;
        }
        if (resources == null || resources.length == 0) {
            return -1;
        }
        long lastModified = -1;
        for (int i = 0; i < resources.length; i++) {
            URLConnection resourceConn;
            try {
                resourceConn = resources[i].openConnection();
            } catch (IOException e) {
                return -1;
            }
            if (resourceConn.getLastModified() > lastModified) {
                lastModified = resourceConn.getLastModified();
            }
        }
        return lastModified;
    }


	private URL[] getRequestResourceURLs(HttpServletRequest request) throws MalformedURLException {

		final String rawResourcePath = request.getServletPath()+request.getPathInfo();
		//The Spring ResourceServlet allowed for combined resource url delimited by ',' as you can see
        //below but we're not using it since we have one call to the server for each resource by default.
        //Still I left that code there in case in the future we want to use the feature.
        final String[] localResourcePaths = StringUtils.delimitedListToStringArray(rawResourcePath, ",");
		final URL[] resources = new URL[localResourcePaths.length];
		for (int i = 0; i < localResourcePaths.length; i++) {
			final URL resource = getServletContext().getResource(localResourcePaths[i]);
			if (resource == null) {
                return null;
            } else {
				resources[i] = resource;
			}
		}
		return resources;
	}

	/**
	 * Set HTTP headers to allow caching for the given number of seconds.
	 * @param seconds number of seconds into the future that the response should be cacheable for
	 */
	private void configureCaching(HttpServletResponse response, int seconds) {
		// HTTP 1.0 header
		response.setDateHeader(HTTP_EXPIRES_HEADER, System.currentTimeMillis() + seconds * 1000L);
		// HTTP 1.1 header
		response.setHeader(HTTP_CACHE_CONTROL_HEADER, "max-age=" + seconds);
	}

}//End of Class
