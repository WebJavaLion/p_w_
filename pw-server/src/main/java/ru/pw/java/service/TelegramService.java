package ru.pw.java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.repository.TelegramRepository;
import ru.pw.java.util.TelegramCodeUtil;

/**
 * @author Lev_S
 */

@Service
public class TelegramService {

    @Autowired
    TelegramRepository repository;

    @Autowired
    PwRequestContext pwRequestContext;

    @Value("${telegram.resolve.template}")
    private String telegramResolveTemplate;

    public String getTelegramResolveLink(boolean forAuth) {
        String key;

        if (forAuth) {
            key = TelegramCodeUtil.encodeKeyForAuthorization();
        } else {
            key = TelegramCodeUtil.encodeKeyForLinkWEBandBOT(pwRequestContext.getCurrentUser());
        }

        repository.createToken(key);

        return telegramResolveTemplate.replace("{}", key);
    }

    public boolean tokenIsActual(String token) {
        return repository.tokenIsActual(token);
    }
}
