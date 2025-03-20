package de.jonahd345.extendedeconomy.config;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Message {
    PREFIX("&a&lEXTENDEDECONOMY §8» "),
    CURRENCY_NAME_PLURAL("&fDollars"),
    CURRENCY_NAME_SINGULAR("&fDollar"),
    NO_PERMISSION("&7No permission!"),
    NO_PLAYER("&7You must be a player!"),
    PLAYER_NOT_FOUND("&7Player not found!"),
    NO_NUMBER("&7You have to enter a number!"),
    NO_MONEY("&7You haven't enough money!"),
    PAY("&7You have %Player% payed %Amount%!"),
    GET_MONEY("&7You received %Amount% from %Player%!"),
    MONEY("&7You have %Amount% coins!"),
    MONEY_OTHER("&7The %Player% have %Amount% coins!"),
    PAY_EXCEPTION("&7You can't pay yourself!"),
    ECO_SET("&7You set %Player%'s balance %Amount%!"),
    ECO_ADD("&7You add %Player%'s balance %Amount%!"),
    ECO_TAKE("&7You took %Amount% from %Player%!"),
    ERROR("&4ERROR"),
    LINE("&8&m---------------------------------------");

    private final String defaultMessage;
    @Setter
    private String message;

    Message(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public static String getMessageWithPrefix(Message message) {
        return PREFIX.getMessage() + message.getMessage();
    }

    @Override
    public String toString() {
        return this.message;
    }
}
