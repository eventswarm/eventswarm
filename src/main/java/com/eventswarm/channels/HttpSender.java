/**
 * Copyright 2007-2014 Ensift Pty Ltd as trustee for the Avaz Trust and other contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.eventswarm.channels;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.ErrorEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.ExceptionErrorEvent;
import com.eventswarm.events.jdo.UrlConnectionErrorEvent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Output channel for sending serialized events via HTTP to a specified URL.
 *
 * Serialization is controlled by a supplied serialializer, which is a ValueRetriever&lt;byte[]&gt;.
 * The serializer should produce content that matches the supplied content type.
 *
 * This class assumes that no response is required from the server and as such, discards a success response.
 * Considering that IO is involved, however, it is recommended that output via this channel occurs in a separate
 * thread. This class uses a default connect and read timeouts of 1s: these can be changed using the setReadTimeout
 * and setConnectTimeout methods.
 *
 * Any errors are output as ErrorEvent instances, hence the AddEventTrigger interface. There are two possible
 * error events emitted: a UrlConnectionErrorEvent for server errors, and an ExceptionErrorEvent for local IO and
 * other errors.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class HttpSender implements AddEventTrigger, AddEventAction {
    private Serializer serializer;
    private String contentType;
    private URL url;
    private ValueRetriever<URL> urlRetriever;
    private Set<AddEventAction> actions;
    private HttpURLConnection connection;
    private int connectTimeout = 1000; // default connectTimeout is 1s
    private int readTimeout = 1000; // default read timeout is 1s

    private static Logger logger = Logger.getLogger(HttpSender.class);
    private static String METHOD = "POST";
    private static String CONTENT_TYPE_KEY = "Content-Type";

    public HttpSender(Serializer serializer, String contentType, URL url) {
        this.serializer = serializer;
        this.contentType = contentType;
        this.url = url;
        this.urlRetriever = new ValueRetriever<URL>() {public URL getValue(Event event) {return getUrl();}};
        this.actions = new HashSet<AddEventAction>();
        this.connection = null;
    }

    public HttpSender(Serializer serializer, String contentType, ValueRetriever<URL> urlRetriever) {
        this.serializer = serializer;
        this.contentType = contentType;
        this.urlRetriever = urlRetriever;
        this.actions = new HashSet<AddEventAction>();
        this.connection = null;
    }

    /**
     * When an event is added, serialize it and send it to the configured URL
     *
     * Note that HTTP errors can be collected by listening to the AddEventTrigger of this object.
     * If the serializer fails for the event, nothing will be output.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        try {
            connection = setupConnection(urlRetriever.getValue(event));
            byte[] data = serializer.toBytes(event);
            connection.setFixedLengthStreamingMode(data.length);
            connection.connect();
            OutputStream out = connection.getOutputStream();
            if (data != null) {
                out.write(data);
                out.flush();
                out.close();
                int code = connection.getResponseCode();
                logger.debug("HTTP POST returned with " + Integer.toString(code) + " response");
                if (code < 200 || code > 299) {
                    fire(new UrlConnectionErrorEvent(connection));
                } else {
                    // read all the data available so we can re-use the connection more easily
                    InputStream in = connection.getInputStream();
                    while (in.available() > 0) {
                        in.read();
                    }
                    in.close();
                }
            }
        } catch (ProtocolException exc) {
            // this should not happen and indicates a problem with the code
            logger.fatal("Unexpected protocol error when sending event via HTTP", exc);
            fire(new ExceptionErrorEvent(exc));
        } catch (Serializer.SerializeException exc) {
            // this will happen if serialization fails, e.g. a JSON serializer is applied to an XML event
            logger.error(exc);
            fire(new ExceptionErrorEvent(exc));
        } catch (IOException exc) {
            // create an exception error event and pass it onwards
            logger.error(exc);
            fire(new ExceptionErrorEvent(exc));
        }
    }

    /**
     * Send the supplied error event to any listeners
     *
     * @param error
     */
    protected void fire(ErrorEvent error) {
        for (AddEventAction action: actions) {
            action.execute(this, error);
        }
    }

    /**
     * Create a new connection to the supplied URL
     *
     * @param url
     * @return HttpURLConnection object
     */
    private HttpURLConnection setupConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(METHOD);
        connection.setRequestProperty(CONTENT_TYPE_KEY, contentType);
        connection.setReadTimeout(readTimeout);
        connection.setConnectTimeout(connectTimeout);
        return connection;
    }

    @Override
    public void registerAction(AddEventAction action) {
        actions.add(action);
    }

    @Override
    public void unregisterAction(AddEventAction action) {
        actions.remove(action);
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * Return the static URL used by this sender, if defined
     *
     * Note that the class uses a ValueRetriever to construct a per-event URL. This attribute will only have
     * a value if the simplified, static-url constructor was used.
     *
     * @return
     */
    public URL getUrl() {
        return url;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
