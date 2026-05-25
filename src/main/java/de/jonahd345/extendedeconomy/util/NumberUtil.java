package de.jonahd345.extendedeconomy.util;

import java.text.DecimalFormat;

public class NumberUtil {
    private static final ThreadLocal<DecimalFormat> FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("#,##0.00"));

    public static String formatNumber(Object number) {
        return FORMAT.get().format(number); //.replace(",", ".");
    }

    public static boolean isDouble(String argument) {
        try {
            Double.parseDouble(argument);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInt(String argument) {
        try {
            Integer.parseInt(argument);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
