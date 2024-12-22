package com.engdiarytoon.server.user;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final String CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@$!%*?&";
    private static final String ALL_CHARACTERS = CHAR + NUMBERS + SPECIAL_CHARACTERS;
    private static final int PASSWORD_LENGTH = 10; // Set desired length here (between 8-20)

    public static String generateRandomPassword() {
        PasswordGenerator random = new PasswordGenerator();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Ensure at least one of each required type
        password.append(CHAR.charAt(random.nextInt(CHAR.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the rest of the password with random characters
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the characters to make the password unpredictable
        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        PasswordGenerator random = new PasswordGenerator();
        for (int i = characters.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = characters[index];
            characters[index] = characters[i];
            characters[i] = temp;
        }
        return new String(characters);
    }
}
