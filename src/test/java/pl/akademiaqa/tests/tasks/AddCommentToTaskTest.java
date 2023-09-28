package pl.akademiaqa.tests.tasks;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.akademiaqa.api.request.ApiRequest;
import pl.akademiaqa.tests.BaseTest;
import pl.akademiaqa.utils.StringUtils;

import java.nio.file.Paths;

import static pl.akademiaqa.api.steps.ApiSteps.createProjectWithTask;


class AddCommentToTaskTest extends BaseTest {

    private String projectId;

    @AfterEach
    void afterEachTest() {
        log.info("Deleting project with id {}", projectId);
        ApiRequest.delete(apiContext, "projects/" + projectId);
    }

    @Test
    void should_be_able_to_add_a_comment_with_an_image_attachment_to_a_task_test() {
        // GIVEN
        final String projectName = StringUtils.getRandomName();
        final String taskName = StringUtils.getRandomName();
        final var ids = createProjectWithTask(apiContext, projectName, taskName);
        projectId = ids.get("projectId");

        // WHEN
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName)).click();
        page.locator("div[class=task_content]").filter(new Locator.FilterOptions().setHasText(taskName)).click();

        page.getByTestId("open-comment-editor-button").click();
        page.getByRole(AriaRole.PARAGRAPH).fill("Dodaję zdjęcie ze spotkania");

        FileChooser fileChooser = page.waitForFileChooser(() -> page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Dodaj plik")).click());
        fileChooser.setFiles(Paths.get("upload/api.png"));

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Skomentuj")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Zamknij okno")).click();

        // THEN
        PlaywrightAssertions.assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("1 komentarz"))).isVisible();
    }

    @Test
    void should_be_able_to_add_a_comment_with_audio_attachment_to_a_task_test() {
        // GIVEN
        final var projectName = StringUtils.getRandomName();
        final var taskName = StringUtils.getRandomName();
        final var responseMap = createProjectWithTask(apiContext, projectName, taskName);
        projectId = responseMap.get("projectId");

        // WHEN
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(projectName)).click();
        page.locator("div[class=task_content]").filter(new Locator.FilterOptions().setHasText(taskName)).click();
        page.getByTestId("open-comment-editor-button").click();
        page.getByRole(AriaRole.PARAGRAPH).fill("Dodaję nagranie ze spotkania.");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Nagraj dźwięk")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Nagraj").setExact(true)).click();
        page.waitForTimeout(1000);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Zatrzymaj")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Załącz")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Skomentuj")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Zamknij okno")).click();

        // THEN
        PlaywrightAssertions.assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("1 komentarz"))).isVisible();
    }

}
