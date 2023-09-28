package pl.akademiaqa.tests.tasks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.akademiaqa.api.request.ApiRequest;
import pl.akademiaqa.api.steps.ApiSteps;
import pl.akademiaqa.tests.BaseTest;
import pl.akademiaqa.utils.StringUtils;

class CompleteTaskTest extends BaseTest {

    private String projectId;

    @AfterEach
    void afterEachTest() {
        log.info("Deleting project with id {}", projectId);
        ApiRequest.delete(apiContext, "projects/" + projectId);
    }

    @Test
    void should_be_able_to_complete_task_test() {
        // GIVEN
        final String projectName = StringUtils.getRandomName();
        final String taskName = "Napisać testy do UI i API";

        final var ids = ApiSteps.createProjectWithTask(apiContext, projectName, taskName);
        projectId = ids.get("projectId");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName)).click();

        // WHEN
        PlaywrightAssertions.assertThat(page.locator("div[class=task_content]:has-text(\"" + taskName + "\")")).isVisible();
        page.locator("button:left-of(:text(\"" + taskName + "\"))").first().click();

        // THEN
        PlaywrightAssertions.assertThat(page.locator("div[class=task_content]:has-text(\"" + taskName + "\")")).not().isVisible();
    }
}
