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
package com.eventswarm.events.jdo;

import java.util.Date;
import com.eventswarm.events.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;

/**
 * Event class for JSON events received via Kafka and deserialized using the org.json JSONObject class
 *
 * Created with IntelliJ IDEA. User: andyb
 */
public class OrgJsonKafkaEvent<K, V> extends OrgJsonEvent implements KafkaEvent<K, V> {
    // maintain local pointers to parts for convenience
    protected transient ConsumerRecord<K, V> kafka;

    public OrgJsonKafkaEvent() {
        super();
    }

    /**
     * Construct an Event from the various parts required
     *
     * @param header
     * @param json
     * @param kafka
     */
    public OrgJsonKafkaEvent(Header header, JSONObject json, ConsumerRecord<K, V> kafka) {
        super(header, json);
        this.kafka = kafka;
        eventParts.put(KAFKA_PART_NAME, new JdoPartWrapper<ConsumerRecord<K, V>>(kafka));
    }

    /**
     * Create a JSON event from a kafka record using a default header, assuming we can parse the content into a JSON object
     * 
     * @param kafka
     */
    public OrgJsonKafkaEvent(JSONObject json, ConsumerRecord<K,V> kafka) {
        this(defaultHeader(kafka), json, kafka);
    }

    private static JdoHeader defaultHeader(ConsumerRecord<? extends Object, ? extends Object> kafka) {
        return new JdoHeader(
            new Date(kafka.timestamp()), 
            (int) (kafka.offset() % 1000000), /* offset trimmed to int < 1,000,000 is a good sequence number */
            new JdoSource(sourceString(kafka)), 
            kafkaId(kafka));
    }

    /**
     * Deterministically create an unique event id from Kafka metadata so we can catch duplicates
     * 
     * Incredibly small chance of creating duplicate IDs if multiple kafka clusters are used
     * 
     * @return ID string composed of kafka topic + partition id + offset
     */
    private static String kafkaId(ConsumerRecord<? extends Object, ? extends Object> kafka) {
        return(sourceString(kafka) + ":" + Integer.toString(kafka.partition()) + ":" + Long.toString(kafka.offset()));
    }

    /**
     * Deterministically create a source string from Kafka metadata, noting that same timestamp and source implies parallellism
     * 
     * Assumes only one kafka cluster is relevant (could have topic names duplicated if multiple)
     * 
     * @param kafka ConsumerRecord object
     * @return source string composed of `kafka:` prefix + topic
     */
    private static String sourceString(ConsumerRecord<? extends Object, ? extends Object> kafka) {
        return "kafka:" + kafka.topic();
    }

    public ConsumerRecord<K,V> getConsumerRecord() {
        return kafka;
    }

    public K key() {
        return this.kafka.key();
    }

    public long offset() {
        return this.kafka.offset();
    }

    public int partition() {
        return this.kafka.partition();
    }

    public long timestamp() {
        return this.kafka.timestamp();
    }

    public String topic() {
        return this.kafka.topic();
    }

    public V value() {
        return this.kafka.value();
    }
}
