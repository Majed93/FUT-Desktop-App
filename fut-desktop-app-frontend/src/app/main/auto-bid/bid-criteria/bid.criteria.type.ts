/**
 * The Bid Criteria object.
 */
export interface BidCriteriaType {

  /**
   * The time limit of the item
   */
  timeLimit: number;

  /**
   * The minimum buy now price to search buy.
   */
  minBuyNow: number;

  /**
   * The maximum number of results before displaying a warning.
   * If 0 - then server side will default to 9
   */
  maxResults: number;

  /**
   * If true then bidding will only BIN and no BID
   */
  binOnly: boolean;

  /**
   * If true then will BIN first and then BID
   */
  binFirst: boolean;

  /**
   * If true then will only bid.
   */
  bid: boolean;
}
