package ru.pw.telegram.java.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * @author Lev_S
 */

@Component
public class ServerPwRestClient {

    @Value("${server.confirmation.api}")
    private String serverConfirmationAPI;

    public void sendConfirmationForServer(Long notificationId) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();

        int ONE_MINUTE = 60 * 1000;
        httpRequestFactory.setConnectionRequestTimeout(ONE_MINUTE);
        httpRequestFactory.setConnectTimeout(ONE_MINUTE);
        httpRequestFactory.setReadTimeout(ONE_MINUTE);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity httpEntity = new HttpEntity<>(headers);

        URI url = URI.create(serverConfirmationAPI.replace("{id}", notificationId.toString()));

        restTemplate.put(url, httpEntity);
    }
}
