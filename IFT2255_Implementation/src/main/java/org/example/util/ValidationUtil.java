package org.example.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    // Ce code provient du repertoire GitHub : ift2255-template-javalin fournit pour ce projet.
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    public static boolean isEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
