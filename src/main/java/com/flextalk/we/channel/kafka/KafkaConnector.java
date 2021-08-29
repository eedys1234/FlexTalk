package com.flextalk.we.channel.kafka;

import com.flextalk.we.channel.ConnectorContext;
import com.flextalk.we.channel.PlatFormConnector;

/**
 * Kafka Producing / Consuming
 */
public class KafkaConnector implements PlatFormConnector {

    @Override
    public int send(ConnectorContext record) {
        return 0;
    }

    @Override
    public ConnectorContext receive() {
        return null;
    }
}
