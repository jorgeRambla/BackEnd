package es.unizar.murcy.scheduler;

import es.unizar.murcy.model.Token;
import es.unizar.murcy.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TokenCleanUpTask {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanUpTask.class);

    @Autowired
    TokenService tokenService;

    @Scheduled(cron = "0 0 0/1 ? * *")
    public void cleanExpiredTokens() {
        Date currentDate = new Date();
        List<Token> tokens = tokenService.getExpiratedTokens(currentDate);
        if(!tokens.isEmpty()) {
            log.info("-- [TAREA PROGRAMADA] [INICIO] LIMPIEZA DE TOKENS --");
            for(Token token : tokens) {
                tokenService.delete(token);
            }
            log.info("-- [TAREA PROGRAMADA] [FIN] LIMPIEZA DE TOKENS --");
        }
    }
}
