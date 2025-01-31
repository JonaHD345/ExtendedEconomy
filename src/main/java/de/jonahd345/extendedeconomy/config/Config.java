package de.jonahd345.extendedeconomy.config;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Config {
    // Config
    MYSQL(false),
    STARTCOINS(1000);

    private final Object defaultValue;
    @Setter
    private Object value;

    Config(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getDefaultValueAsBoolean() {
        return Boolean.valueOf(this.defaultValue.toString());
    }

    public Boolean getValueAsBoolean() {
        return Boolean.valueOf(this.value.toString());
    }

    public Integer getDefaultValueAsInt() {
        return Integer.valueOf(this.defaultValue.toString());
    }

    public Integer getValueAsInt() {
        try { return Integer.valueOf(this.value.toString()); } catch (NumberFormatException e) { return this.getDefaultValueAsInt(); }
    }
}
