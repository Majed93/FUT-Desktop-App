export class Player {
  firstName: string;

  lastName: string;

  commonName: string;

  rating: number;

  assetId: number;

  position: string;

  lowestBin: number;

  searchPrice: number; // Used when searching to mass bid. MUST BE LOWER THAN LOWEST BIN IN MOST CASES

  maxListPrice: number;

  minListPrice: number;

  nation: number;

  club: number;

  league: number;

  customPrice: boolean;

  priceSet: boolean;

  bidAmount: number;

  minListPriceTax: number;

  maxListPriceTax: number;
}
