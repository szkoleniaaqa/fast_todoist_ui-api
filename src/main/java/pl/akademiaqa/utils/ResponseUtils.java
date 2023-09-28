package pl.akademiaqa.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtils {

    public static JsonObject apiResponseToJsonObject(APIResponse response) {
        return new Gson().fromJson(response.text(), JsonObject.class);
    }

    public static JsonArray apiResponseToJsonArray(APIResponse response) {
        return new Gson().fromJson(response.text(), JsonArray.class);
    }
}
