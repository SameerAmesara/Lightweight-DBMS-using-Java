package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.Captcha;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CaptchaTest {

    @Test
    public void testCaptchaTextIsGenerated() {
        Captcha captcha = new Captcha();
        String captchaText = captcha.getCaptchaText();

        assertNotNull(captchaText);
        assertEquals(6, captchaText.length());
    }

    @Test
    public void testValidateCaptcha() {
        Captcha captcha = new Captcha();
        String captchaText = captcha.getCaptchaText();

        assertTrue(captcha.validateCaptcha(captchaText));
        assertFalse(captcha.validateCaptcha("InvalidCaptcha"));
        assertFalse(captcha.validateCaptcha(null));
    }

    @Test
    public void testCaptchaTextUnchangedAfterValidation() {
        Captcha captcha = new Captcha();
        String captchaText = captcha.getCaptchaText();
        String input = "ValidCaptcha";

        captcha.validateCaptcha(input);

        assertEquals(captchaText, captcha.getCaptchaText());
    }
}