/**
 * Copyright 2020 Andrew Berry 
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
package com.eventswarm.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Event interface for Kafka events allowing us to retrieve Kafka metadata,
 * normally combined with a payload event type (e.g. JsonEvent).
 *
 * Essentially wraps the methods from ConsumerRecord
 */
public interface KafkaEvent<K,V> extends Event {

    /** default part name for the KafkaPart of a KafkaEvent */
    public static String KAFKA_PART_NAME="KAFKA";
    
    public String topic();
    public int partition();
    public long offset();
    public long timestamp();
    public K key();
    public V value();

    /**
     * Deterministically create an unique event id from Kafka metadata so we can catch duplicates
     * 
     * Incredibly small chance of creating duplicate IDs if multiple kafka clusters are used
     * 
     * @return ID string composed of kafka topic + partition id + offset
     */
    public static String kafkaId(ConsumerRecord<? extends Object, ? extends Object> kafka) {
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
    public static String sourceString(ConsumerRecord<? extends Object, ? extends Object> kafka) {
        return "kafka:" + kafka.topic();
    }
}
