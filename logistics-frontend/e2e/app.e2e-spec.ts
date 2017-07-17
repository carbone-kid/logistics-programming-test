import { LogisticsFrontendPage } from './app.po';

describe('logistics-frontend App', () => {
  let page: LogisticsFrontendPage;

  beforeEach(() => {
    page = new LogisticsFrontendPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
