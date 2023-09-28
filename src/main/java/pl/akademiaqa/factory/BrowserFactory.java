package pl.akademiaqa.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import static pl.akademiaqa.utils.Properties.getProperty;

public class BrowserFactory {

    private Playwright playwright;
    private Browser browser;

    public Playwright getPlaywright() {
        if (playwright == null) {
            playwright = Playwright.create();
        }
        return playwright;
    }

    public Browser getBrowser() {
        var browserName = getProperty("app.browser").toLowerCase();

        var launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(getProperty("app.browser.headless")))
                .setSlowMo(Integer.parseInt(getProperty("app.browser.slow.mo")));

        switch (browserName) {
            case "chromium" -> browser = getPlaywright().chromium().launch(launchOptions);
            case "firefox" -> browser = getPlaywright().firefox().launch(launchOptions);
            case "webkit" -> browser = getPlaywright().webkit().launch(launchOptions);
            case "chrome" -> browser = getPlaywright().chromium().launch(launchOptions.setChannel("chrome"));
            case "msedge" -> browser = getPlaywright().chromium().launch(launchOptions.setChannel("msedge"));
            default -> throw new IllegalArgumentException("Browser " + browserName + " not supported");
        }

        return browser;
    }
}
