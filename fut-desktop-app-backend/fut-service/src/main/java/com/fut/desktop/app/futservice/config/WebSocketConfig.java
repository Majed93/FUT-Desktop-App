package com.fut.desktop.app.futservice.config;

import com.fut.desktop.app.futservice.listener.SocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.annotation.PostConstruct;

/**
 * Web socket
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

    @PostConstruct
    public void init() {
        webSocketMessageBrokerStats.setLoggingPeriod(30 * 60 * 1000); // desired time in millis
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/futws")
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.configureBrokerChannel().interceptors(new SocketInterceptor());
        config.enableSimpleBroker("/test","/playerSearch", "/playerListing", "/autoBid", "/manual");
        config.setApplicationDestinationPrefixes("/fut");
    }
}
