package com.fut.desktop.app.futservice.listener;

import com.fut.desktop.app.futservice.rest.HomeRestController;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * Timeout factor
     */
    @Value("${fut.timeout}")
    private Integer timeoutFactor;

    /**
     * Fut service endpoint
     */
    @Value("${fut.service.endpoint}")
    private String io;

    private static String workingDir;

    @Value("${working.dir}")
    public void setWorkingDir(String dir) {
        workingDir = dir == null ? "" : dir;
    }

    @Value("${fut.profile:#{null}}")
    private String profile;

    private final HomeRestController homeRestController;

    @Autowired
    public StartupListener(HomeRestController homeRestController) {
        this.homeRestController = homeRestController;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("working dir: " + workingDir);
        log.info("io: " + io);
        SleepUtil.setTimeoutFactor(this.timeoutFactor);
        log.info("Set timeout factor at: " + SleepUtil.getTimeoutFactor());
        homeRestController.setup();
    }
}
