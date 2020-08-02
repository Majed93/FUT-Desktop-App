package com.fut.api.fut.listener;

import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartUpListener  implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * Timeout factor
     */
    @Value("${fut.timeout}")
    private Integer timeoutFactor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Load all players lists.
        SleepUtil.setTimeoutFactor(this.timeoutFactor);
        log.info("Set timeout factor at: " + SleepUtil.getTimeoutFactor());
    }
}
