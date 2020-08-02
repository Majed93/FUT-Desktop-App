package com.fut.desktop.app.futservice.rest;

import com.fut.desktop.app.constants.FutErrorCode;
import com.fut.desktop.app.constants.Pile;
import com.fut.desktop.app.domain.AuctionDuration;
import com.fut.desktop.app.domain.AuctionInfo;
import com.fut.desktop.app.domain.AuctionResponse;
import com.fut.desktop.app.domain.FutError;
import com.fut.desktop.app.exceptions.FutErrorException;
import com.fut.desktop.app.futservice.constants.WSUrl;
import com.fut.desktop.app.futservice.service.base.AccountService;
import com.fut.desktop.app.futservice.service.base.LoginService;
import com.fut.desktop.app.futservice.service.base.StatusMessagingService;
import com.fut.desktop.app.futservice.service.base.TransferService;
import com.fut.desktop.app.futservice.utils.StringUtils;
import com.fut.desktop.app.restObjects.*;
import com.fut.desktop.app.services.base.PlayerService;
import com.fut.desktop.app.utils.PlayerUtil;
import com.fut.desktop.app.utils.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Controller to get accounts.
 */
@RequestMapping("/accounts")
@RestController
@Slf4j
public class AccountsRestController {

    /**
     * Handle to {@link StringUtils}
     */
    private final StringUtils stringUtils;

    /**
     * Handle to {@link AccountService}
     */
    private final AccountService accountService;

    /**
     * Handle to {@link PlayerService}
     */
    private final PlayerService playerService;

    private boolean stop = false;

    private boolean pause = false;

    /**
     * Handle to {@link LoginService}
     */
    private final LoginService loginService;


    /**
     * Handle to {@link StatusMessagingService}
     */
    private final StatusMessagingService messagingService;

    /**
     * Handle to {@link TransferService}
     */
    private final TransferService transferService;

    @Autowired
    public AccountsRestController(StringUtils stringUtils, AccountService accountService, PlayerService playerService, LoginService loginService, StatusMessagingService messagingService, TransferService transferService) {
        this.stringUtils = stringUtils;
        this.accountService = accountService;
        this.playerService = playerService;
        this.loginService = loginService;
        this.messagingService = messagingService;
        this.transferService = transferService;
    }

    /**
     * Get all accounts
     *
     * @return All accounts.
     */
    @GetMapping
    @ResponseBody
    public List<Account> accounts() {
        return accountService.findAll();
    }

    /**
     * @param id Id of account.
     * @return account.
     */
    @GetMapping(value = "/{id}")
    @ResponseBody
    public Account getOneAccount(@PathVariable("id") Integer id) {
        return accountService.findOne(id);
    }

    /**
     * @param account New account to add
     * @return newly added account
     * @throws Exception throws any exception
     */
    @PostMapping
    @ResponseBody
    public Account addAccount(@RequestBody Account account) throws Exception {

        if (emptyAccountFields(account)) {
            log.error("Error adding account - empty fields");
            throw new FutErrorException(new FutError("Error adding account, empty fields", FutErrorCode.InternalServerError,
                    "Error adding account, empty fields", "", ""));
        }
        return accountService.add(account);
    }

    /**
     * @param account Updated account.
     * @return updated account
     * @throws Exception handle any exception.
     */
    @PutMapping
    @ResponseBody
    public Account updateAccount(@RequestBody Account account) throws Exception {
        Account tempAccount = accountService.findOne(account.getId());

        tempAccount.setEmail(account.getEmail());
        tempAccount.setPassword(account.getPassword());
        tempAccount.setAnswer(account.getAnswer());
        tempAccount.setSecretKey(account.getSecretKey());
        tempAccount.setPlatform(account.getPlatform());

        account = tempAccount;

        if (emptyAccountFields(account)) {
            log.error("Error adding account - empty fields");
            throw new FutErrorException(new FutError("Error adding account, empty fields", FutErrorCode.InternalServerError,
                    "Error adding account, empty fields", "", ""));
        }
        return accountService.update(account);
    }

    /**
     * @param id Id of account to delete.
     * @throws Exception Throw any exception.
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteAccount(@PathVariable("id") Integer id) throws Exception {
        accountService.delete(id);
    }

    /**
     * Checks if any of the account fields for NEW account are invalid/empty
     *
     * @param account Object to check
     * @return true if empty or invalid found otherwise false.
     */
    private boolean emptyAccountFields(Account account) {
        return org.springframework.util.StringUtils.isEmpty(account.getEmail()) ||
                !stringUtils.isValidEmail(account.getEmail()) ||
                org.springframework.util.StringUtils.isEmpty(account.getPassword()) ||
                org.springframework.util.StringUtils.isEmpty(account.getAnswer()) ||
                org.springframework.util.StringUtils.isEmpty(account.getSecretKey()) ||
                account.getPlatform() == null;
    }

    /**
     * Re list all accounts.
     *
     * @param requestBody Duration of auctions and if new price.
     * @param relist      true = relist, false = list for time.
     * @return true once done.
     * @throws Exception Throw any exceptions
     */
    @PostMapping(value = "/relistAll/{relist}")
    @ResponseBody
    public boolean reListAllAccounts(@RequestBody AccountRelist requestBody, @PathVariable("relist") Boolean relist) throws Exception {
        // TODO: Probably want to move this into the service tier.
        RelistRequest relistRequestFromBody = requestBody.getRelistRequest();
        stop = false;
        pause = false;
        List<Account> accountList = requestBody.getAccounts();

        if (requestBody.isOnlyOne()) {
            if (accountList.size() > 1) {
                Account onlyAccount = accountList.get(0);
                accountList = Collections.singletonList(onlyAccount);
            }
        }

        List<Player> fullPlayerList = playerService.findAllPlayers();
        AuctionDuration auctionDuration = relistRequestFromBody.getAuctionDuration();
        // Send message to say starting.
        SleepUtil.sleep(100, 200);
        messagingService.sendStatus("Starting.", WSUrl.PLAYERLISTING_STATUS);

        int counter = 0;

        for (Account account : accountList) {
            messagingService.sendStatus("Try account: " + account.getEmail(), WSUrl.PLAYERLISTING_STATUS);
            boolean loggedIn = tryLogin(account);
            boolean outBid = false;
            boolean itemWon = false;

            // Wait if paused.
            pauseCheck();

            if (stop) {
                messagingService.sendStatus("Stopping.", WSUrl.PLAYERLISTING_STATUS);
                return true;
            }

            if (loggedIn) {
                messagingService.sendStatus("Starting with account: " + account.getEmail(), WSUrl.PLAYERLISTING_STATUS);

                // get the trade pile
                AuctionResponse tradePile = transferService.tradePile(account.getId());
                messagingService.sendStatus("Getting trade pile.", WSUrl.PLAYERLISTING_STATUS);
                boolean requireAutoListing = false;
                SleepUtil.sleep(500, 750);

                if (stop) {
                    return true;
                }

                // Check if any items need to be auto listed. i.e not been listed before.
                if (tradePile.getAuctionInfo() != null) {
                    for (AuctionInfo info : tradePile.getAuctionInfo()) {
                        if (info.getTradeState() == null) {
                            requireAutoListing = true;
                        }
                    }
                } else {
                    messagingService.sendStatus("Trade pile empty", WSUrl.PLAYERLISTING_STATUS);
                }

                if (requireAutoListing) {
                    // Auto list any items which haven't been listed.
                    RelistRequest relistRequest = new RelistRequest(account.getId(), auctionDuration, tradePile, relistRequestFromBody.getNewPrice(), "tradePile");
                    tradePile = transferService.autoListUnlisted(relistRequest);
                    messagingService.sendStatus("Auto listed unlisted.", WSUrl.PLAYERLISTING_STATUS);
                }

                SleepUtil.sleep(200, 400);

                boolean sold = false;
                boolean expired = false;

                // Check for sold items now.
                if (tradePile.getAuctionInfo() != null && tradePile.getAuctionInfo().size() > 0) {
                    List<String> soldList = new ArrayList<>();
                    for (AuctionInfo info : tradePile.getAuctionInfo()) {
                        if (info.getTradeState() != null) {
                            if (info.getTradeState().equals("closed") && info.getCurrentBid() > 0) {
                                long soldPrice = info.getCurrentBid();
                                if (info.getItemData().getAssetId() != 0) {
                                    Player playerBeingListing = getPlayerBeingListing(fullPlayerList, info);
                                    String name = playerBeingListing == null ? "Unknown player" : PlayerUtil.getName(playerBeingListing);

                                    soldList.add(name + " | " + soldPrice);
                                } else {
                                    soldList.add("Non player item | " + soldPrice);
                                }
                                sold = true;
                            } else if (info.getTradeState().equals("expired")) {
                                expired = true;
                            }
                        }
                    }

                    //If there's sold items.
                    if (sold) {
                        // send request to remove sold items.
                        tradePile = transferService.removeSold(account.getId());
                        messagingService.sendStatus("Removed " + soldList.size() + " items.", WSUrl.PLAYERLISTING_STATUS);
                        soldList.forEach(s -> messagingService.sendStatus("Sold: " + s, WSUrl.PLAYERLISTING_STATUS));
                        SleepUtil.sleep(768, 1435);
                    }

                    if (tradePile.getAuctionInfo() != null && tradePile.getAuctionInfo().size() > 0 && expired) {
                        //Relist
                        messagingService.sendStatus("Relisting..", WSUrl.PLAYERLISTING_STATUS);
                        RelistRequest request = new RelistRequest(account.getId(), auctionDuration, tradePile, relistRequestFromBody.getNewPrice(), "tradePile");
                        tradePile = transferService.reListAll(request, relist);
                        messagingService.sendStatus("Done relisting", WSUrl.PLAYERLISTING_STATUS);
                        SleepUtil.sleep(350, 950);
                    } else {
                        messagingService.sendStatus("Nothing to re-list.", WSUrl.PLAYERLISTING_STATUS);
                    }
                }

                // If watch list should be checked AND and trade pile less than 100.
                // Will want to check watch list anyway incase it's from another machine.
                if (Objects.equals(relistRequestFromBody.getPile(), "watchList") && account.getTradePileCount() < 100) {
                    messagingService.sendStatus("Checking watching List", WSUrl.PLAYERLISTING_STATUS);
                    SleepUtil.sleep(350, 950);

                    AuctionResponse watchList = transferService.watchList(account.getId());
                    messagingService.sendStatus("Getting watch list.", WSUrl.PLAYERLISTING_STATUS);

                    for (AuctionInfo info : watchList.getAuctionInfo()) {

                        // Check if any items need to be removed.
                        if (info.getTradeState().equals("closed") && info.getBidState().equals("outbid")) {
                            outBid = true;
                        }

                        if (info.getTradeState().equals("closed") && info.getCurrentBid() > 0 && !info.getBidState().equals("outbid")) {
                            itemWon = true;
                        }

                        if (outBid && itemWon) {
                            break;
                        }

                    }

                    SleepUtil.sleep(200, 400);

                    // if items have been outbidded and can be removed.
                    if (outBid) {
                        WatchListRequest watchListRequest = new WatchListRequest(account.getId(), watchList, false, "watchList");
                        messagingService.sendStatus("Removing expired", WSUrl.PLAYERLISTING_STATUS);
                        watchList = transferService.removeFromWatchList(watchListRequest, 0L);
                        SleepUtil.sleep(300, 400);
                    }

                    if (stop) {
                        messagingService.sendStatus("Stopping.", WSUrl.PLAYERLISTING_STATUS);
                        return true;
                    }


                    // NOTE: Trade pile will only be saved if no moving has occured because the counts will be overridden otherwise
                    // Save the trade pile first
                    if (tradePile != null) {
                        transferService.saveListInfo(account.getId(), Pile.TradePile, tradePile);
                    }

                    // If items in watch list won.
                    if (itemWon) {
                        messagingService.sendStatus("Moving and listing items to trade", WSUrl.PLAYERLISTING_STATUS);
                        String pile = "watchList";
                        RelistRequest relistRequest = new RelistRequest(account.getId(), auctionDuration,
                                watchList, false, pile);
                        transferService.moveAndList(relistRequest);
                        messagingService.sendStatus("Moved and listed from watchlist.", WSUrl.PLAYERLISTING_STATUS);
                        SleepUtil.sleep(800, 1200);
                    }

                    // Check unassigned pile too.
                    if (account.getUnassignedPileCount() > 0) {
                        messagingService.sendStatus("Checking unassigned pile.", WSUrl.PLAYERLISTING_STATUS);
                        AuctionResponse unassignedPile = transferService.unassignedPile(account.getId());
                        RelistRequest relistRequest = new RelistRequest(account.getId(), auctionDuration,
                                unassignedPile, false, "unassignedPile");
                        transferService.moveAndList(relistRequest);
                        messagingService.sendStatus("Moved and listed from unassigned pile.", WSUrl.PLAYERLISTING_STATUS);
                        SleepUtil.sleep(800, 1200);
                    }

                    if (watchList != null) {
                        transferService.saveListInfo(account.getId(), Pile.WatchList, watchList);
                    }
                } else if (tradePile.getAuctionInfo() != null && tradePile.getAuctionInfo().size() < 1 && !Objects.equals("watchList", relistRequestFromBody.getPile()) && account.getWatchListCount() < 1) {
                    messagingService.sendStatus("No actions required on this account. Moving on.", WSUrl.PLAYERLISTING_STATUS);
                }


            } else {
                messagingService.sendStatus("Unable to login after 5 attempts for account: " + account.getEmail(), WSUrl.PLAYERLISTING_STATUS);
                return false;
            }
            transferService.stopCache();

            // If it's not the last one
            if (counter++ != accountList.size() - 1) {
                messagingService.sendStatus("Next account.", WSUrl.PLAYERLISTING_STATUS);

                if (stop) {
                    messagingService.sendStatus("Stopping.", WSUrl.PLAYERLISTING_STATUS);
                    return true;
                }

                SleepUtil.sleep(2500, 5000);
            }
        }

        transferService.stopCache();
        messagingService.sendStatus("Finished.", WSUrl.PLAYERLISTING_STATUS);
        return true;
    }

    /**
     * Try login to account. Try 5 times then fail.
     *
     * @return Login state after 5 tries.
     */
    private boolean tryLogin(Account account) {
        for (int i = 0; i < 5; i++) {
            try {
                // Send login request.
                loginService.login(account.getId(), "WebApp");
                SleepUtil.sleep(758, 1143);
                // Get again because the coin count will have changed.
                account = accountService.findOne(account.getId());
                messagingService.sendStatus("Successfully logged in to " + account.getEmail(), WSUrl.PLAYERLISTING_STATUS);
                messagingService.sendStatus("Current coins: " + account.getCoins(), WSUrl.PLAYERLISTING_STATUS);
                return true;
            } catch (Exception e) {
                log.error("Failed to login. " + e.getMessage());
                messagingService.sendStatus("Failed to login. Attempts " + (i + 1) + "/5", WSUrl.PLAYERLISTING_STATUS);
            }

            pauseCheck();
            if (stop) {
                messagingService.sendStatus("Cancelling listing", WSUrl.PLAYERLISTING_STATUS);
                return true;
            }
        }

        return false;
    }

    /**
     * Stop the re-listing
     *
     * @return stop value
     */
    @GetMapping(value = "/relistAll/stop")
    @ResponseBody
    public boolean stopRelisting() {
        stop = true;
        messagingService.sendStatus("Waiting for suitable place to stop", WSUrl.PLAYERLISTING_STATUS);
        transferService.setStop(stop);
        log.info("Stopping listing.");
        return stop;
    }

    /**
     * Pause the re-listing
     *
     * @return pause value
     */
    @GetMapping(value = "/relistAll/pause")
    @ResponseBody
    public boolean pauseRelisting() {
        pause = !pause;
        messagingService.sendStatus("Waiting for suitable place to pause", WSUrl.PLAYERLISTING_STATUS);
        transferService.setPause(pause);
        log.info("Paused: " + pause);
        return pause;
    }

    /**
     * Get the player from the list which is being actioned on.
     *
     * @param players List which contains the player.
     * @param info    Auction info with asset id.
     * @return Found player.
     */
    private Player getPlayerBeingListing(List<Player> players, AuctionInfo info) {
        for (Player playa : players) {
            if (info.getItemData().getItemType().equals("player")) {
                if (playa.getAssetId().equals(info.getItemData().getAssetId())) {
                    return playa;
                }
            }
        }
        return null; // should never get here.
    }

    /**
     * Check pause status.
     */
    private void pauseCheck() {
        if (pause) {
            messagingService.sendStatus("Paused now", WSUrl.PLAYERLISTING_STATUS);
            while (pause) {
                SleepUtil.sleep(1, 2);
            }
        }
    }
}
