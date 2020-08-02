package com.fut.desktop.app.futservice.rest;

import com.fut.desktop.app.domain.LoginAuctionWrapper;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.futservice.service.base.LoginService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/login")
@RestController
@Slf4j
public class LoginRestController {

    /**
     * Handle to {@link LoginService}
     */
    private final LoginService loginService;

    /**
     * Constructor
     *
     * @param loginService Handle to {@link LoginService}
     */
    @Autowired
    public LoginRestController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * All params are encoded(not yet TODO).
     * Invoke login service to process login logic.
     *
     * @param data .
     * @return Object with loginResponse.
     */
    @PostMapping
    @ResponseBody
    public LoginAuctionWrapper login(@RequestBody String data) throws FutErrorException {
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        Integer id = jsonObject.get("id").getAsInt();
        String appVersion = jsonObject.get("appVersion").getAsString();

        //TODO : temp
        // Integer id = 1082334009; //1082334009 //44070436
        // String appVersion = "WebApp";

        return loginService.login(id, appVersion);
    }
}
