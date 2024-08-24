package az.edu.elibrary.utils;

import az.edu.elibrary.enums.EnumCurrency;

public class CurrencyConverter {
    // This here will convert given currency to AZN,
    private static final double USD_TO_AZN = 1.70;
    private static final double EUR_TO_AZN = 1.90;

    public static double convertToAZN(double amount, EnumCurrency currency) {
        return switch (currency) { // don't know it just said, replace with expression
            case USD -> amount * USD_TO_AZN;
            case EUR -> amount * EUR_TO_AZN;
            case AZN -> amount; // Local currency return
            default -> throw new IllegalArgumentException(currency + " isn't supported");
        };
    }
}
