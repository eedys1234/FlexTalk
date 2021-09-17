package com.flextalk.we.channel;

/**
 * 다양한 메시징 채널로 보내기위한 추상화 인터페이스
 */
public interface PlatformConnector {

    int send(ConnectorContext record);
    ConnectorContext receive();

}
