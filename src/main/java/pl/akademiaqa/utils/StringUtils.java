package pl.akademiaqa.utils;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static String removeRoundBrackets(String text) {
        return text.replaceAll("[()]", "");
    }

    public static String getRandomName() {
        return new Faker().address().firstName();
    }
}
