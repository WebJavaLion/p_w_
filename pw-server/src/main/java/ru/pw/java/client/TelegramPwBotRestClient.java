package ru.pw.java.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.pw.java.model.dto.NotificationDto;

import java.net.URI;

/**
 * @author Lev_S
 */

@Component
public class TelegramPwBotRestClient {

    @Value("${telegram.notification.api}")
    private String botNotificationAPI;

    public void sendNotificationForChat(NotificationDto dto) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();

        int ONE_MINUTE = 60 * 1000;
        httpRequestFactory.setConnectionRequestTimeout(ONE_MINUTE);
        httpRequestFactory.setConnectTimeout(ONE_MINUTE);
        httpRequestFactory.setReadTimeout(ONE_MINUTE);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity httpEntity = new HttpEntity<>(dto, headers);

        URI url = URI.create(botNotificationAPI);

        restTemplate.postForEntity(url, httpEntity, Object.class);
    }
}
