package com.flextalk.we.channel.kafka;

import com.flextalk.we.channel.MessagingContext;
import com.flextalk.we.channel.PlatFormMessagingChannel;

/**
 * Kafka Producing / Consuming
 */
public class KafkaMessagingChannel implements PlatFormMessagingChannel {

    @Override
    public int send(MessagingContext record) {
        return 0;
    }

    @Override
    public MessagingContext receive() {
        return null;
    }
}
