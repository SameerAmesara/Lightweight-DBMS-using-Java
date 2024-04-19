package org.LightweightDBMS;

import java.util.Random;

public class Captcha {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CAPTCHA_LENGTH = 6;

    private final String captchaText;
    private String inputCaptcha;

    public Captcha() {
        captchaText = generateRandomCaptcha();
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public boolean validateCaptcha(String input) {
        return inputCaptcha != null && inputCaptcha.equals(input);
    }

    private String generateRandomCaptcha() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder(CAPTCHA_LENGTH);

        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        inputCaptcha = captcha.toString(); // Store the generated captcha for validation
        return captcha.toString();
    }
}
