package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.constants.AppVersion;
import com.fut.desktop.app.domain.Platform;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@JsonFormat
@Entity
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "accounts")
public class Account {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private String secretKey;

    @Column(nullable = false)
    private long coins;

    /**
     * Should be timestamp in epoch - not readable date
     */
    @Column(nullable = false)
    private String timeFinish;

    @Column(nullable = false)
    private int totalSession;

    @Column(nullable = false)
    private int watchListCount;

    @Column(nullable = false)
    private int tradePileCount;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int unassignedPileCount;

    @Column(nullable = false)
    private long lastLogin;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private int pinCount;

    @Column(nullable = false)
    private Platform platform;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer pile_tradePileSize;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer pile_watchListSize;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer pile_unknownPile3;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer pile_unknownPile6;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer pile_unknownPile11;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer pile_unassigned;

    @Column()
    private AppVersion lastLoggedInPlatform;
}
