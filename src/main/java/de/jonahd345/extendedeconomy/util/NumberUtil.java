package de.jonahd345.extendedeconomy.util;

import java.text.DecimalFormat;

public class NumberUtil {
    private DecimalFormat format;

    public NumberUtil() {
        //this.format = new DecimalFormat("###,###,###,###,###,###,###,###,###");
        this.format = new DecimalFormat("#,##0.00");
    }

    public String formatNumber(Object number) {
        return this.format.format(number); //.replace(",", ".");
    }

    public boolean isDouble(String argument) {
        try {
            Double.parseDouble(argument);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInt(String argument) {
        try {
            Integer.parseInt(argument);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
