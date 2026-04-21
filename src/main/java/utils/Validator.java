package utils;

public class Validator {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    public static boolean isPositive(double value) {
        return value > 0;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@")
                && email.contains(".");
    }
}
