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
     * Send a request to the specified URL with the supplied parameters
     *
     * If the user has been set, HTTP basic authentication will be attempted.
     *
     * @param method -- HTTP method, either GET or POST
     * @param target -- Target URL for the request, excluding parameters
     * @param params -- Parameters to encode in the body of the request (leave null if params are already in URL)
     * @return true if successful, false otherwise
     * @throws HttpClientException
     */
    public int request(String method, URL target, Map<String,String> params) throws HttpClientException {
        try {
            boolean doOutput = params != null && !params.isEmpty();
            HttpURLConnection con = getConnection(method, target, doOutput);
            if (doOutput) {
                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                addParams(out, params);
                out.close();
            }
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
                handler.handle(target.toString(), con.getInputStream(), con.getHeaderFields());
                return code;
            }
        } catch (Exception exc) {
            String message = "Error sending request to " + target.toString();
            logger.error(message, exc);
            throw new HttpClientException(message, exc);
        }
    }


    protected void addParams(OutputStreamWriter out, Map<String,String> params) throws IOException {
        if (params != null) {
            boolean first = true;
            for(String key : params.keySet()) {
                if (!first) {
                    addParam(out, AMP + encode(key) + "=" + encode(params.get(key)));
                } else {
                    addParam(out, encode(key) + "=" + encode(params.get(key)));
                    first = false;
                }
            }
        }
    }

    protected void addParam(OutputStreamWriter out, String param) throws IOException {
        logger.debug("Setting HTTP param: " + param);
        out.write(param);
    }

    protected static String readStream(InputStream in) throws IOException {
        Scanner scanner = new Scanner(in);
        StringBuilder result = new StringBuilder();
        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine());
        }
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
        public HttpClientException(String message) {
            super(message);
        }

        public HttpClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
