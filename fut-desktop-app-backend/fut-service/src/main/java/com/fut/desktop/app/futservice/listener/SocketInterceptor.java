package com.fut.desktop.app.futservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketInterceptor extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // TODO: change to logging System.out.println("In pre send: " + message.toString());
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // TODO: change to logging System.out.println("In post send: " + message.toString());
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        // TODO: change to logging System.out.println("In after send: " + message.toString());
    }

    public boolean preReceive(MessageChannel channel) {
        // TODO: change to logging System.out.println("In pre receive: " + channel.toString());
        return true;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        // TODO: change to logging System.out.println("In post receive: " + message.toString());
        return message;
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        // TODO: change to logging System.out.println("In after receive: " + message.toString());
    }

}
