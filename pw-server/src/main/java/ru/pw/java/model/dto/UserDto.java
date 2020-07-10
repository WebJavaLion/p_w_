package ru.pw.java.model.dto;

public class UserDto {

    private Integer id;
    private Integer telegramUserId;
    private String  userEmail;
    private String  userPassword;
    private Integer notificationType;
    private Long    telegramChatId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTelegramUserId() {
        return telegramUserId;
    }

    public void setTelegramUserId(Integer telegramUserId) {
        this.telegramUserId = telegramUserId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }
}
