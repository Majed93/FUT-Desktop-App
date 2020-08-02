import {Account} from '../app/domain/account';
import {Platform} from '../app/enums/platform';

export class AccountTestUtils {

  /**
   * Return a test account
   * @returns {Account}
   */
  public static createMockAccount(): Account {
    return {
      id: 12345,
      email: 'test@test.com',
      password: 'password',
      answer: 'answer',
      secretKey: 'ABCD1234EFGH567',
      coins: 1112223,
      timeFinish: '1520198614226',
      totalSession: 0,
      watchListCount: 45,
      tradePileCount: 85,
      unassignedPileCount: 1,
      lastLogin: '1520187784371',
      deviceId: '0',
      pinCount: 0,
      platform: Platform.XboxOne,
      pile_tradePileSize: 100,
      pile_unassigned: 5,
      pile_unknownPile3: -1,
      pile_unknownPile6: 15,
      pile_unknownPile11: 100,
      pile_watchListSize: 50
    };
  }
}
