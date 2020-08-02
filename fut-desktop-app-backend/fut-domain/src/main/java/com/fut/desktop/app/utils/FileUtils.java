package com.fut.desktop.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fut.desktop.app.extensions.DateTimeExtensions;
import com.fut.desktop.app.restObjects.Account;
import com.fut.desktop.app.restObjects.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handle file io operations.
 */
@Component
@Slf4j
public class FileUtils {

    public final static String cookieExt = ".cookie";
    public final static String dataExt = ".dat";
    public final static String jsonExt = ".json";

    //    private static String workingDir;
    public final static String DATA = "data";
    public static String path;
    public static String player_path;
    public final static String accountDetailsFile = ""; // ? Don't know what this will actually be used for..
    public final static String accountsFile = "accounts"; // might not need this either
    public final static String authFile = "auth";
    public static String dataDir;

    @Value("${working.dir}")
    public void setWorkingDir(String dir) {
        String workingDir = dir == null ? "" : dir;
        path = workingDir + File.separator + DATA;
        player_path = path + File.separator + "lists";
        dataDir = workingDir + File.separator + "data";
    }

    public boolean writeAuthInfo(String object) {
        String fileName = path + File.separator + authFile + dataExt;
        File file = new File(fileName);

        if (!file.exists()) {
            log.info("File doesn't not exist so creating one at: " + file.getAbsolutePath());
            try {
                boolean created = file.createNewFile();

                log.info("Created? " + created);
            } catch (IOException e) {
                log.error("Can't create file: " + e.getMessage());
            }
        }

        boolean writable = file.setWritable(true);
        log.info("Writable: " + writable);
        return write(file, object);
    }

    public String readAuthInfo() {
        String fileName = path + File.separator + authFile + dataExt;
        File file = new File(fileName);

        if (!file.exists()) {
            log.error("File doesn't not exist: " + file.getAbsolutePath());
            return null;
        }

        boolean writable = file.setWritable(true);
        log.info("Writable: " + writable);

        return String.valueOf(read(file));
    }

    /**
     * Write cookie to file.
     *
     * @param object .
     * @param id     .
     * @return write successful or not.
     */
    public boolean writeFile(Object object, Integer id, String ext) {
        String fileName = path + File.separator + id + ext;
        File file = new File(fileName);

        log.info("File path is: " + file.getAbsolutePath());

        if (!file.exists()) {
            log.info("File doesn't exist so creating one at: " + file.getAbsolutePath());
            try {
                boolean created = file.createNewFile();
                boolean writable = file.setWritable(true);

                log.info("Created? " + created + " & writable: " + writable);
            } catch (IOException e) {
                boolean readOnly = file.setReadOnly();
                log.info("Readonly:" + readOnly);
                log.error("Can't create file: " + e.getMessage());
            }
        }

        boolean writable = file.setWritable(true);
        log.info("Writable: " + writable);
        return write(file, object);
    }

    /**
     * Read cookie from file
     *
     * @return LoginResponse object
     */
    public Object readFile(Integer id, String ext) {
        String fileName = path + File.separator + id + ext;
        File file = new File(fileName);

        if (!file.exists()) {
            log.error("File doesn't exist: " + file.getAbsolutePath());
            return null;
        }

        boolean writable = file.setWritable(true);
        log.info("Writable: " + writable);

        return read(file);
    }

    /**
     * Read from account the lastLogin date.
     *
     * @param id Id of the user.
     * @return date of last login in LocalDateTime object.
     */
    public LocalDateTime getModifiedDate(Integer id) {
        String fileName = path + File.separator + id + dataExt;
        File file = new File(fileName);

        if (!file.exists()) {
            log.error("File doesn't exist: " + file.getAbsolutePath());
            return null;
        }

        //JSON from file to Object
        try {
            ObjectMapper mapper = new ObjectMapper();

            Account account = mapper.readValue(file, Account.class);
            boolean readOnly = file.setReadOnly();
            log.info("Readonly:" + readOnly);
            return DateTimeExtensions.FromUnixTime(String.valueOf(account.getLastLogin()));
        } catch (Exception e) {
            log.error("Can't get time of file: " + e.getMessage());
            boolean readOnly = file.setReadOnly();
            log.info("Readonly:" + readOnly);
            return null;
        }
    }

    /**
     * Create folder if doesn't exist.
     *
     * @param name name of folder.
     * @return if a need to create or not. Can return null
     */
    public Boolean createFolder(String name) {
        File directory = new File(name);

        if (!directory.exists()) {
            return directory.mkdir() ? true : null;
        }

        log.info(name + " directory exists.");
        return false;
    }

    /**
     * Create file if doesn't exist.
     *
     * @param name name of file.
     * @return if need to create or not. Can return null.
     */
    public Boolean createDataFile(String name) {
        String fileName = path + File.separator + name + dataExt;
        return createFile(fileName);
    }

    /**
     * Create file if doesn't exist.
     *
     * @param name name of file.
     * @return if need to create or not. Can return null.
     */
    public Boolean createPlayerListFile(String name) {
        String fileName = player_path + File.separator + name + jsonExt;
        return createFile(fileName);
    }

    /**
     * Create a generic file method
     *
     * @param fileName File to create
     * @return if need to create or not. Can return null.
     */
    private Boolean createFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            try {
                boolean readOnly = file.setReadOnly();
                log.info("Readonly:" + readOnly);
                return file.createNewFile() ? true : null;
            } catch (Exception e) {
                log.error("Error creating file " + e.getMessage());
                return null;
            }
        }

        log.info(fileName + " file exists.");
        return false;
    }

    /**
     * Write player list to file.
     *
     * @param playerList List of players to save
     * @param fileName   Name of file
     */
    public boolean writePlayerListToFile(List<Player> playerList, String fileName) {
        File file = new File(player_path + File.separator + fileName);

        boolean writable = file.setWritable(true);
        log.info("Writable: " + writable);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, playerList);
            return true;
        } catch (IOException e) {
            log.error("Can't process player list: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to write to file.
     *
     * @param file   File to write to.
     * @param object object to write.
     * @return If file write success.
     */
    private boolean write(File file, Object object) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
            boolean readOnly = file.setReadOnly();
            log.info("Readonly: " + readOnly);
            return true;
        } catch (Exception e) {
            boolean readOnly = file.setReadOnly();
            log.info("Readonly: " + readOnly);
            log.error("Can't write to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to read files.
     *
     * @param file File to read.
     * @return object of read data.
     */
    private Object read(File file) {
        try (FileInputStream fi = new FileInputStream(file);
             ObjectInputStream oi = new ObjectInputStream(fi)) {
            boolean readOnly = file.setReadOnly();
            log.info("Readonly: " + readOnly);
            return oi.readObject();
        } catch (Exception e) {
            boolean readOnly = file.setReadOnly();
            log.info("Readonly: " + readOnly);
            log.error("Can't read file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete file
     *
     * @param fileName Name of file to delete
     */
    public static Boolean deleteFile(String fileName) {
        File fileToDelete = new File(fileName);

        if(!fileToDelete.exists()){
            return null;
        }

        try {
            return Files.deleteIfExists(fileToDelete.toPath());
        } catch (IOException ioEx) {
            log.error("Unable to delete file {}", ioEx.getMessage());
            return false;
        }
    }
}
