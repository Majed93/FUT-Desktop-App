package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
public class AccountRelist {

    /**
     * List of accounts to relist.
     */
    List<Account> accounts;

    /**
     * If true then will only list one. If more than one in list then only first one will be done.
     */
    boolean isOnlyOne;

    /**
     * Relist request object
     */
    RelistRequest relistRequest;
}
