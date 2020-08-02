package com.fut.desktop.app.futsimulator.rest;

import com.fut.desktop.app.models.LicenseRequest;
import com.fut.desktop.app.models.LicenseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * Mock for License server - own by me.
 */
@Getter
@Setter
@Slf4j
@RequestMapping("/mock/auth")
@RestController
@Scope(scopeName = "singleton")
public class SecurityMockController {

    // Switch to authorise user
    private boolean authorised;

    @PostMapping
    @ResponseBody
    public LicenseResponse auth(@RequestBody LicenseRequest details) throws Exception {
        String email = details.getEmail();
        String key = details.getKey();

        log.info("Email: {}", email);
        log.info("Key: {}", key);

        return new LicenseResponse(email, key, new ArrayList<>(), new ArrayList<>(), authorised);
    }

    @GetMapping("/{newValue}")
    @ResponseStatus(HttpStatus.OK)
    public void trainAuthorised(@PathVariable("newValue") boolean newValue) {
        authorised = newValue;
    }

    @GetMapping("/getAuth")
    public boolean getAuth() {
        return authorised;
    }
}
