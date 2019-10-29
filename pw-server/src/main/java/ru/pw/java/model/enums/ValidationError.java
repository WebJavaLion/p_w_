package ru.pw.java.model.enums;

/**
 * @author Lev_S
 */

public enum ValidationError {

    ALREADY_EXISTS("user с таким email уже существет"),

    INVALID_EMAIL("такого email не существует"),

    INVALID_PASSWORD("Пароль должен быть не менее 8 символов, содержать, " +
            "хотя бы одну строчную и заглавную букву и хотя бы один специальный символ (@/$/% и т.д.)"),

    EMPTY_EMAIL("Введите email"),

    EMPTY_PASSWORD("Введите пароль"),

    EMPTY_TELEGRAM_ID("Введите telegramId"),

    INVALID_LOGIN("неверный пароль или логин"),

    INVALID_NAME("Укажите имя"),

    INVALID_MIXING_MODE("Укажите режим повторения"),

    INVALID_WORD("У каждого слова должен быть оригинал и перевод"),

    INVALID_CONTENT("Должен быть указан список слов или передан файл со словами"),

    INVALID_WORD_NUMBER("Список слов не может быть пустым"),

    INVALID_WORD_GROUP("Такой группы не сущесвтует"),

    INVALID_WORD_GROUP_VERIFICATION("У вас нет группы слов с таким названием"),

    INVALID_USER("Такого пользователя не сущесвтует");

    private String text;

    ValidationError(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
