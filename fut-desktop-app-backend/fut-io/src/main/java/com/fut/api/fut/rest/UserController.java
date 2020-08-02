package com.fut.api.fut.rest;

import com.fut.api.fut.client.IFutClient;
import com.fut.desktop.app.domain.CreditsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handle user operations
 */
@RequestMapping("/io/user")
@RestController
@Slf4j
public class UserController {

    private IFutClient futClient;

    @GetMapping(value = "/credits")
    @ResponseBody
    public CreditsResponse credits() throws Exception {
        return futClient.getCredits().get();
    }
}
