package com.fut.desktop.app.futservice.config;

import com.fut.desktop.app.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
@Configuration
public class DBConfig {

    @Value("${working.dir}")
    private String workingDir;

    private final FileUtils fileUtils;

    private final String dbFileName = "fut.dat";

    public DBConfig(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    @Autowired
    public Environment environment;

    @Bean
    public DataSource dataSource() {
        deleteDatabasefile();
        Boolean createDir = fileUtils.createFolder(FileUtils.dataDir);
        log.info("Required to created directory in database setup: {}", createDir);

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");

        String url = "jdbc:sqlite:file:./data/" + dbFileName;
        // If we have a working directory (we ALWAYS should)
        if (!workingDir.isEmpty()) {
            url = "jdbc:sqlite:file:" + FileUtils.dataDir + File.separator + dbFileName;
        }
        dataSourceBuilder.url(url);
        return dataSourceBuilder.build();
    }

    private void deleteDatabasefile() {
        String intTestProfile = "INTEGRATION_TEST";
        if (environment.getActiveProfiles() != null && environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals(intTestProfile)) {
            // Delete the data folder
            File dataPath = new File(workingDir + File.separator + FileUtils.DATA);
            if (dataPath.exists()) {
//                Path pathToBeDeleted =

                try (Stream<Path> pathToBeDeleted = Files.walk(dataPath.toPath())) {
                    pathToBeDeleted.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (Exception ex) {
                    log.error("Error deleting path. {}", ex.getMessage());
                    log.error("{}", ex);
                }
            }
        }
    }
}
