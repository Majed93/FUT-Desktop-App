/**
 * Model of Status message received from websocket.
 */
export class StatusMessage {
  message: string;

  /**
   * Constructor.
   *
   * @param msg The message to set.
   */
  constructor(msg: any) {
    this.message = msg;
  }

  /**
   * Parse the json message.
   *
   * @param msg The message.
   */
  public static transform(msg: string) {
    return JSON.parse(msg);
  }
}
