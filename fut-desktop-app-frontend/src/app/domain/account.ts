import {Platform} from '../enums/platform';

export class Account {
  id: number;
  email: string;
  password: string;
  answer: string;
  secretKey: string;
  coins: number;
  timeFinish: string;
  totalSession: number;
  watchListCount: number;
  tradePileCount: number;
  unassignedPileCount: number;
  lastLogin: string;
  deviceId: string;
  pinCount: number;
  platform: Platform;
  /* Pile sizes these cannot be updated by the user. */
  pile_tradePileSize: number;
  pile_watchListSize: number;
  pile_unknownPile3: number;
  pile_unknownPile6: number;
  pile_unknownPile11: number;
  pile_unassigned: number;
}
