package com.soompyo.server.user;

import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {
    public static String generateEmail() {
        return java.util.UUID.randomUUID() + "@test.com";
    }

    public static String generatePassword() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String digits = uuid.replaceAll("\\D", "");
        StringBuilder stringBuilder = new StringBuilder(32);

        if (digits.length() >= 4) {
            stringBuilder.append(digits, 0, 4);
            stringBuilder.append(digits);
        } else {
            Random random = new Random();
            for (int i = digits.length(); i < 4; i++) {
                stringBuilder.append(random.nextInt(10));
            }
        }

        return "password" + stringBuilder + uuid;
    }
}
