package com.eventswarm.channels;

import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Scanner;

/**
 * Class to execute HTTP requests against a server and pass the response body to an HttpContentHandler
 * to create events from the response.
 *
 * The intent is that a separate HttpClient will be used per-handler and per-server, since this class
 * encapsulates basic HTTP authentication for the server.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public class HttpClient {
    private String user;
    private String password;
    private HttpContentHandler handler;
    private String accept;

    public static final String ENCODING = "UTF8",
            HTTP_POST = "POST",
            HTTP_GET = "GET",
            HTTP_AUTHORIZATION = "Authorization",
            AMP = "&";

    private static final Logger logger = Logger.getLogger(HttpClient.class);

    /**
     * Encode a string for inclusion in a URL or parameter list
     *
     * @param text
     * @return
     */
    protected static String encode(String text) {
        try {
            return URLEncoder.encode(text, ENCODING);
        } catch (UnsupportedEncodingException exc) {
            // should never get here
            logger.fatal("WTF!!? " + ENCODING + " is not a valid encoding", exc);
            return null;
        }
    }

    /**
     * Decode a string included in a URL or parameter list
     *
     * @param text
     * @return
     */
    protected static String decode(String text) {
        try {
            return URLDecoder.decode(text, "UTF8");
        } catch (UnsupportedEncodingException exc) {
            // should never get here
            System.out.println("WTF!!? UTF8 is not a valid encoding. Exception: " + exc.getMessage());
            return null;
        }
    }


    /**
     * Create an HTTP client with the specified handler
     *
     * Unless user and password are specified later using the setter methods, no authentication will be attempted.
     *
     * @param handler
     */
    public HttpClient(HttpContentHandler handler) {
        this.handler = handler;
    }


    /**
     * Create an HTTP client with the specified handler and accepted content type
     *
     * Unless user and password are specified later using the setter methods, no authentication will be attempted.
     *
     * @param handler
     * @param accept MIME content type to be accepted by the handler
     */
    public HttpClient(HttpContentHandler handler, String accept) {
        this.handler = handler;
        this.accept = accept;
    }

    /**
     * Create an HTTP client with the specified handler, content type, username and password.
     *
     * @param handler
     * @param accept MIME content type to be accepted by the handler
     * @param user User name for HTTP basic authentication
     * @param password Password for HTTP basic authentication
     */
    public HttpClient(HttpContentHandler handler, String accept, String user, String password) {
        this.handler = handler;
        this.accept = accept;
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    /**
     * Set the username for the HTTP server
     *
     * @param user
     * @return self
     */
    public HttpClient setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Set the password for the HTTP server
     *
     * @param password
     * @return self
     */
    public HttpClient setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getAccept() {
        return accept;
    }

    public HttpClient setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    public HttpContentHandler getHandler() {
        return handler;
    }

    /**
     * Set the content handler for responses
     *
     * @param handler
     * @return self
     */

    public HttpClient setHandler(HttpContentHandler handler) {
        this.handler = handler;
        return this;
    }

    /**
     * Send a POST request to the specified URL with the supplied parameters as a form body
     *
     * If the user has been set, HTTP basic authentication will be attempted. If providing parameters in the URL,
     * make sure you use the encode method to encode them.
     *
     * @param target -- Target URL for the request, excluding parameters
     * @param params -- Parameters to encode in the body of the request (only works properly for POST, use URL params for GET)
     * @return true if successful, false otherwise
     * @throws HttpClientException
     */
    public int postRequest(URL target, Map<String,String> params) throws HttpClientException {
        try {
            boolean doOutput = params != null && !params.isEmpty();
            HttpURLConnection con = getConnection("POST", target, doOutput);
            if (doOutput) {
                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                out.write(makeParams(params));
                out.close();
            }
            return handleResponse(target.toString(), con);
        } catch (Exception exc) {
            String message = "Error sending request to " + target.toString();
            logger.error(message, exc);
            throw new HttpClientException(message, exc);
        }
    }

    /**
     * Send a GET request to the specified URL
     *
     * If the user has been set, HTTP basic authentication will be attempted. If providing parameters in the URL,
     * make sure you use the encode method to encode them.
     *
     * @param target -- target URL, preferably without parameters
     * @param params -- Parameters to encode in the URL
     * @return
     * @throws HttpClientException
     */
    public int getRequest(URL target, Map<String,String> params) throws HttpClientException {
        URL urlWithParams;
        try {
            if (params != null && params.size() > 0) {
                urlWithParams = new URL(target.toString() + "?" + makeParams(params));
            } else {
                urlWithParams = target;
            }
            HttpURLConnection con = getConnection("GET", urlWithParams, false);
            return handleResponse(urlWithParams.toString(), con);
        } catch (Exception exc) {
            String message = "Error sending request to " + target.toString();
            logger.error(message, exc);
            throw new HttpClientException(message, exc);
        }
    }

    private int handleResponse(String id, HttpURLConnection con) throws Exception {
        int code = con.getResponseCode();
        logger.info("Received HTTP response with code " + Integer.toString(code));
        if (code > 299) {
            // subscription and related requests should always return a 202, but this is not consistent
            // so accept any 2XX response
            InputStream in = con.getErrorStream();
            if (in == null) in = con.getInputStream(); // might have returned the wrong code
            logger.error("Error in response, content was: " + readStream(in));
            return code;
        } else {
            handler.handle(id, con.getInputStream(), con.getHeaderFields());
            return code;
        }
    }


    protected String makeParams(Map<String,String> params) throws IOException {
        if (params != null) {
            boolean first = true;
            StringBuilder builder = new StringBuilder();
            for(String key : params.keySet()) {
                if (!first) {
                    builder.append(AMP);
                }
                builder.append(encode(key));
                builder.append("=");
                builder.append(encode(params.get(key)));
                first = false;
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    protected static String readStream(InputStream in) throws IOException {
        Scanner scanner = new Scanner(in);
        StringBuilder result = new StringBuilder();
        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine());
        }
        scanner.close();
        return result.toString();
    }

    /**
     * Grab a connection to a URL with appropriate headers, including auth if defined
     *
     * @return
     * @throws IOException
     */
    protected HttpURLConnection getConnection(String method, URL url, boolean body) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        if (accept != null) {
            con.setRequestProperty("Accept", accept);
        }
        setHttpAuthorization(con);
        if (body) {
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);
        }
        con.connect();
        return con;
    }

    protected void setHttpAuthorization(HttpURLConnection con) {
        if (user != null) {
            con.setRequestProperty(HTTP_AUTHORIZATION, "Basic " +
                    DatatypeConverter.printBase64Binary((user + ":" + password).getBytes()));
        }
    }

    /**
     * Simple exception class to wrap exceptions
     */
    public static class HttpClientException extends Exception {
        private static final long serialVersionUID = 1L;

        public HttpClientException(String message) {
            super(message);
        }

        public HttpClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
