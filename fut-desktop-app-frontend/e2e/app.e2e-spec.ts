import { FutDesktopAppFrontendPage } from './app.po';

describe('fut-desktop-app-frontend App', () => {
  let page: FutDesktopAppFrontendPage;

  beforeEach(() => {
    page = new FutDesktopAppFrontendPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!!');
  });
});
