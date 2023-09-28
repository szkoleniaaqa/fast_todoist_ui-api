package pl.akademiaqa.tests.projects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.akademiaqa.api.request.ApiRequest;
import pl.akademiaqa.api.steps.ApiSteps;
import pl.akademiaqa.tests.BaseTest;
import pl.akademiaqa.utils.ResponseUtils;
import pl.akademiaqa.utils.StringUtils;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

class EditProjectNameTest extends BaseTest {

    private String projectId;

    @AfterEach
    void afterEachTest() {
        log.info("Deleting project with id {}", projectId);
        ApiRequest.delete(apiContext, "projects/" + projectId);
    }

    @Test
    void should_edit_project_name_test() {
        // GIVEN
        final var projectName = StringUtils.getRandomName();
        final var response = ApiSteps.createProject(apiContext, projectName);
        projectId = ResponseUtils.apiResponseToJsonObject(response).get("id").getAsString();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName))).isVisible();

        // WHEN
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName)).click();
        page.locator("span[class=simple_content]").first().click();
        page.locator("input[value=" + projectName + "]").fill(projectName + " EDIT");
        page.locator("button[type=submit]").click();

        // THEN
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName + " EDIT"))).isVisible();
    }
}
