package de.jonahd345.extendedeconomy.config;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum MySql {
    HOST("127.0.0.1"),
    PORT("3306"),
    USER("root"),
    PASSWORD("iamcool"),
    DATABASE("extendedeconomy");

    private final String defaultValue;
    @Setter
    private String value;

    MySql(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
