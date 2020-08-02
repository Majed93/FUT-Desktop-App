export class ProfitMargin {

  /**
   * Key of the max amount for this filter to apply on.
   */
  key: number;

  /**
   * Minimum profit margin
   */
  minProfit: number;

  /**
   * Maximum profit margin
   */
  maxProfit: number;

  /**
   * Choice of how to set the prices
   * <p>
   * 0 - Less than lowest bin.
   * 1 - Between lowest bin.
   * 2 - On and greater than lowest bin.
   * </p>
   */
  priceSetChoice: number;

  // Calculated fields

  exampleBin: number;
}
