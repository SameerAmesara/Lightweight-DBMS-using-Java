package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.MD5Hash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MD5HashTest {
    @Test
    public void testHashPassword() {
        MD5Hash md5Hash = new MD5Hash();

        // Test with a sample password
        String hashedPassword = md5Hash.hashPassword("password123");
        assertEquals("482c811da5d5b4bc6d497ffa98491e38", hashedPassword);

        // Test with an empty password
        hashedPassword = md5Hash.hashPassword("");
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", hashedPassword);

        // Test with a different password
        hashedPassword = md5Hash.hashPassword("mysecretpassword");
        assertEquals("4cab2a2db6a3c31b01d804def28276e6", hashedPassword);
    }
}
