package com.fut.desktop.app.futservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Slf4j
public class DbInit {

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initialize() {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
        } catch (SQLException e) {
            log.error("Error in SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
