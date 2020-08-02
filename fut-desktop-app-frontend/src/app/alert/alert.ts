import {AlertInterface} from './alert.interface';

export class Alert implements AlertInterface {
  static nextId = 0;


  id: number = (Alert.incrementNextId());
  text = 'default text';
  cssClass = '';

  static incrementNextId(): number {
    return Alert.nextId++;
  }

  /**
   * Constructor
   *
   * @param {string} text Text for alert
   * @param {string} cssClass CSS Style of alert to correspond with BS.
   */
  constructor(text?: string, cssClass?: string) {
    this.text = text;
    this.cssClass = cssClass;
  }
}
