package com.fut.desktop.app.futservice.rest;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.futservice.utils.RolePrivilegeMapper;
import com.fut.desktop.app.futservice.utils.StringUtils;
import com.fut.desktop.app.models.LicenseRequest;
import com.fut.desktop.app.models.LicenseResponse;
import com.fut.desktop.app.models.RoleLevel;
import com.fut.desktop.app.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Verify users license key here
 */
@RequestMapping("/auth")
@RestController
@Slf4j
public class SecurityRestController {


    private final RolePrivilegeMapper rolePrivilegeMapper;

    private final StringUtils stringUtils;

    private final FileUtils fileUtils;

    private final RestTemplate restTemplate;

    private final Environment environment;

    @Value("${license.server.endpoint}")
    private String licenseServerEndpoint;

    @Autowired
    public SecurityRestController(RolePrivilegeMapper rolePrivilegeMapper,
                                  StringUtils stringUtils, FileUtils fileUtils,
                                  RestTemplate restTemplate, Environment environment) {
        this.rolePrivilegeMapper = rolePrivilegeMapper;
        this.stringUtils = stringUtils;
        this.fileUtils = fileUtils;
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    @PostMapping
    @ResponseBody
    public boolean auth(@RequestBody String details) throws Exception {
        String[] detailsSplit = org.springframework.util.StringUtils.split(details, "||");

        String email = detailsSplit[0];
        String key = detailsSplit[1];

        boolean validEmail = stringUtils.isValidEmail(email);
        boolean authorised = authorise(email, key); //TODO: this will be from the LS

        log.info("Key provided: " + key);

        if (validEmail && authorised) {
            List<GrantedAuthority> authorityList = rolePrivilegeMapper.getPrivelges(RoleLevel.ROLE_BRONZE);
            //TODO: get the privilege level and parse it.

            Authentication authentication = new UsernamePasswordAuthenticationToken(email, key, authorityList);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } else {
            throw new FutErrorException(new FutError("Invalid credentials", FutErrorCode.PermissionDenied,
                    "Invalid credentials", "", ""));
        }
    }

    /**
     * Check if the user is authorized.
     *
     * @param email Email
     * @param key   Key
     * @return True if authorised otherwise false
     */
    private boolean authorise(String email, String key) {
        // TODO: Remove after license server obtained
        if (licenseServerEndpoint == null || licenseServerEndpoint.contains("null") || licenseServerEndpoint.isEmpty()) {
            return true;
        }

        if (!licenseServerEndpoint.contains("http")) {
            licenseServerEndpoint = "http://localhost:" + environment.getProperty("local.server.port") + licenseServerEndpoint;
        }

        LicenseRequest licenseRequest = LicenseRequest.builder().email(email).key(key).build();
        HttpEntity<LicenseRequest> entity = new HttpEntity<>(licenseRequest, new HttpHeaders());
        ResponseEntity<LicenseResponse> licenseResponse = restTemplate.exchange(licenseServerEndpoint, HttpMethod.POST, entity, LicenseResponse.class);

        // TODO: Construct roles and privileges from this.
        return licenseResponse.getBody().isAuthorized();
    }


    /**
     * Save auth of app info to file.
     *
     * @param file This should be a JSON format.
     * @return 201 if successfully created. Otherwise error.
     */
    @PostMapping(value = "/saveAuth")
    @ResponseBody
    public boolean postSaveAuth(@RequestBody String file) throws Exception {

        boolean authInfo = fileUtils.writeAuthInfo(file);

        if (!authInfo) {
            throw new FutErrorException(new FutError("Unable to save", FutErrorCode.NotFound, "Unable to save", "", ""));
        }

        return true;

    }

    @GetMapping(value = "/getAuth")
    @ResponseBody
    public String getGetAuth() throws Exception {
        String authInfo = fileUtils.readAuthInfo();

        if (authInfo == null) {
            throw new FutErrorException(new FutError("Unable to get file, maybe none exists?", FutErrorCode.NotFound, "No file found", "", ""));
        }

        return authInfo;
    }

}
