package com.fut.desktop.app.futservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry registry) {
        registry.simpDestMatchers("/fut/**").permitAll().anyMessage();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
