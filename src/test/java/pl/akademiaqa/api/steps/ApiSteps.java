package pl.akademiaqa.api.steps;

import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.RequestOptions;
import org.assertj.core.api.Assertions;
import pl.akademiaqa.api.request.ApiRequest;
import pl.akademiaqa.utils.ResponseUtils;

import java.util.HashMap;
import java.util.Map;

import static pl.akademiaqa.utils.ResponseUtils.apiResponseToJsonObject;

public class ApiSteps {
    protected static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApiSteps.class);

    public static APIResponse createProject(APIRequestContext apiContext, String projectName) {
        JsonObject payload = new JsonObject();
        payload.addProperty("name", projectName);

        // WHEN
        final var apiResponse = ApiRequest.post(apiContext, "projects", payload);
        log.info("Created new project with id {}", ResponseUtils.apiResponseToJsonObject(apiResponse).get("id").getAsString());
        PlaywrightAssertions.assertThat(apiResponse).isOK();

        Assertions.assertThat(apiResponseToJsonObject(apiResponse).get("id").getAsString()).isNotNull();
        Assertions.assertThat(apiResponseToJsonObject(apiResponse).get("name").getAsString()).isEqualTo(projectName);

        return apiResponse;
    }

    public static APIResponse createTask(APIRequestContext apiContext, String taskName, String projectId) {
        JsonObject taskPayload = new JsonObject();
        taskPayload.addProperty("content", taskName);
        taskPayload.addProperty("project_id", projectId);
        final var taskResponse = apiContext.post("tasks", RequestOptions.create().setData(taskPayload));

        PlaywrightAssertions.assertThat(taskResponse).isOK();
        Assertions.assertThat(ResponseUtils.apiResponseToJsonObject(taskResponse).get("id").getAsString()).isNotNull();
        Assertions.assertThat(ResponseUtils.apiResponseToJsonObject(taskResponse).get("content").getAsString()).isEqualTo(taskName);

        return taskResponse;
    }

    public static Map<String, String> createProjectWithTask(APIRequestContext apiContext, String projectName, String taskName) {
        Map<String, String> resourceIds = new HashMap<>();

        final var response = ApiSteps.createProject(apiContext, projectName);
        final var projectId = apiResponseToJsonObject(response).get("id").getAsString();
        resourceIds.put("projectId", projectId);
        log.info("Created project with id {}", projectId);

        final var taskResponse = ApiSteps.createTask(apiContext, taskName, projectId);
        String taskId = apiResponseToJsonObject(taskResponse).get("id").getAsString();
        resourceIds.put("taskId", taskId);
        log.info("Created task with id {}", taskId);

        return resourceIds;
    }
}
