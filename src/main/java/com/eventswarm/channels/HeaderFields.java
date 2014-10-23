package com.eventswarm.channels;

import com.eventswarm.events.Header;
import com.eventswarm.events.Sources;
import com.eventswarm.events.jdo.JdoHeader;
import java.util.Date;
import java.util.Map;

/**
 * Interface implemented by classes that know how to create EventSwarm header fields from parsed data items
 *
 * The type of the data from which headers are being extracted is T (e.g. a JSONObject or an XML Document)
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public interface HeaderFields<T> {
    /** Return a unique id for the event, preferably a URL */
    public String getId(T data);

    /** Return the name of the source of the event, preferably a domain name */
    public String getSource(T data);

    /** Return a timestamp, preferably with millisecond precision, use current time if no timestamp is available */
    public Date getTimestamp(T data);

    /** Return a sequence number to define an order over events with the same timestamp, or just 0 if this ordering is not important */
    public int getSequenceNumber(T data);


    /**
     * Simple header factory using the methods of HeaderFields
     *
     * @param <T> Type of data items from which headers are constructed
     */
    public static class HdrFactory<T> {
        HeaderFields<T> extractor;

        public HdrFactory(HeaderFields<T> extractor) {
            this.extractor = extractor;
        }

        public Header create(T data) {
            return new JdoHeader(extractor.getTimestamp(data),
                    extractor.getSequenceNumber(data),
                    Sources.cache.getSourceByName(extractor.getSource(data)),
                    extractor.getId(data));
        }
    }

    /**
     * Slightly less simple header factory that accepts a discriminator to work out which HeaderFields implementation
     * to use.
     */
    public static class DiscriminatedHdrFactory<D,T> {
        Map<D,HeaderFields<T>> extractorMap;
        Discriminator<D,T> discriminator;

        public DiscriminatedHdrFactory(Map<D, HeaderFields<T>> extractorMap) {
            this.extractorMap = extractorMap;
        }

        public Header create(T data) {
            HeaderFields<T> extractor = extractorMap.get(discriminator.getDiscriminator(data));
            return new JdoHeader(extractor.getTimestamp(data),
                    extractor.getSequenceNumber(data),
                    Sources.cache.getSourceByName(extractor.getSource(data)),
                    extractor.getId(data));
        }
    }
}
