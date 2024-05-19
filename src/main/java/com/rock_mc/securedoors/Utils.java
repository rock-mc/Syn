package com.rock_mc.securedoors;

public class Utils {

    public static String generateCode(String available_characters, int code_length) {
        StringBuilder verification_code = new StringBuilder();
        for (int i = 0; i < code_length; i++) {
            int randomIndex = (int) (Math.random() * available_characters.length());
            char randomChar = available_characters.charAt(randomIndex);

            verification_code.append(randomChar);
        }

        return verification_code.toString();
    }
}
