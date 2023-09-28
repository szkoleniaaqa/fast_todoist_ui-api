package pl.akademiaqa.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import pl.akademiaqa.factory.BrowserFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static pl.akademiaqa.utils.Properties.getProperty;
import static pl.akademiaqa.utils.StringUtils.removeRoundBrackets;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    protected static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseTest.class);

    private BrowserFactory browserFactory;

    // UI
    protected Browser browser;
    protected BrowserContext uiContext;
    protected Page page;

    // API
    protected APIRequestContext apiContext;

    @BeforeAll
    void beforeAll() {
        browserFactory = new BrowserFactory();
        browser = browserFactory.getBrowser();

        // LOGOWANIE
        loginAndSaveState();
    }

    @BeforeEach
    void beforeEach() {
        if (!isLoginStateCreated()) throw new IllegalStateException("Can not find login storage file!");

        // UI CONTEXT
        uiContext = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(Paths.get(getProperty("app.login.storage.file.path"))));
        uiContext.grantPermissions(List.of("microphone"));


        // TRACING START
        if (isTracingEnabled()) {
            uiContext.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }

        page = uiContext.newPage();
        page.setViewportSize(1920, 1080);
        page.navigate(getProperty("app.ui.url"));

        // API CONTEXT
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + getProperty("app.api.token"));

        apiContext = browserFactory.getPlaywright().request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(getProperty("app.api.url"))
                .setExtraHTTPHeaders(headers));
    }

    @AfterEach
    void afterEach(TestInfo testInfo) {
        // TRACING STOP
        if (isTracingEnabled()) {
            String traceName = "traces/trace_"
                    + removeRoundBrackets(testInfo.getDisplayName())
                    + "_" + LocalDateTime.now().format(DateTimeFormatter
                    .ofPattern(getProperty("app.tracing.date.format"))) + ".zip";
            uiContext.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(traceName)));
        }

        apiContext.dispose();
        uiContext.close();
    }

    @AfterAll
    void afterAll() {
        browserFactory.getPlaywright().close();
    }

    private boolean isTracingEnabled() {
        return Boolean.parseBoolean(getProperty("app.tracing.enabled"));
    }

    private void loginAndSaveState() {
        if (!isLoginStateCreated()) {
            log.info("Loguję się do aplikacji i zapisuję stan do pliku json...");
            BrowserContext loginUiContext = browser.newContext();
            Page loginPage = loginUiContext.newPage();
            loginPage.setViewportSize(1920, 1080);

            loginPage.navigate(getProperty("app.ui.url"));
            loginPage.waitForCondition(() -> page.locator("#loading").isHidden(), new Page.WaitForConditionOptions().setTimeout(30000));
            assertThat(loginPage.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Log In"))).isVisible();

            loginPage.getByPlaceholder("Enter your email...").fill(getProperty("app.ui.username"));
            loginPage.getByPlaceholder("Enter your password...").fill(getProperty("app.ui.password"));
            loginPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in")).click();
            PlaywrightAssertions.assertThat(loginPage.getByTestId("view_header")).isVisible();

            loginPage.close();
            loginUiContext.storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get(getProperty("app.login.storage.file.path"))));
            log.info("Zalogowałem się do aplikacji i utworzyłem plik json.");
        }
    }

    private boolean isLoginStateCreated() {
        final var exists = Files.exists(Paths.get(getProperty("app.login.storage.file.path")));
        log.info("Sprawdzam czy plik login state istnieje? {}", exists);
        return exists;
    }
}
