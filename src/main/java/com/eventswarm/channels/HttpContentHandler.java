package com.eventswarm.channels;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Interface implemented by channels etc that handle HTTP content provided by an upstream protocol/query/subscription
 * handler.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public interface HttpContentHandler {
    /**
     * Implementations must provide this method for processing content in the request or response
     * body with the specified headers.
     *
     * Implementations must internalise exceptions and should attempt to handle the content as quickly
     * as possible, since the protocol handler might wait for this method to complete.
     *
     * Note that this interface assumes the body will be string encoded. Might need to revisit if we have any
     * issues here.
     *
     * @param subs_id An identifier for the source of this content, typically the URL but sometimes a more specific subscription id
     * @param body InputStream for reading the HTTP request body
     * @param headers Map of headers
     */
    public void handle(String subs_id, InputStream body, Map<String, List<String>> headers);
}
