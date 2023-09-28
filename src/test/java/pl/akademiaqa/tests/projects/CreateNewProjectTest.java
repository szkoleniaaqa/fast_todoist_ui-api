package pl.akademiaqa.tests.projects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.akademiaqa.api.request.ApiRequest;
import pl.akademiaqa.api.steps.ApiSteps;
import pl.akademiaqa.tests.BaseTest;
import pl.akademiaqa.utils.ResponseUtils;
import pl.akademiaqa.utils.StringUtils;


class CreateNewProjectTest extends BaseTest {

    private String projectId;

    @AfterEach
    void afterEachTest() {
        log.info("Deleting project with id {}", projectId);
        ApiRequest.delete(apiContext, "projects/" + projectId);
    }

    @Test
    void should_create_new_project_test() {
        // GIVEN
        final var projectName = StringUtils.getRandomName();

        // WHEN
        final var response = ApiSteps.createProject(apiContext, projectName);
        projectId = ResponseUtils.apiResponseToJsonObject(response).get("id").getAsString();

        // THEN
        PlaywrightAssertions.assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName))).isVisible();
    }

    @Test
    void should_create_new_project_with_one_letter_name_test() {
        // GIVEN
        final var projectName = "X";

        // WHEN
        final var response = ApiSteps.createProject(apiContext, projectName);
        projectId = ResponseUtils.apiResponseToJsonObject(response).get("id").getAsString();

        // THEN
        PlaywrightAssertions.assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName))).isVisible();
    }
}
