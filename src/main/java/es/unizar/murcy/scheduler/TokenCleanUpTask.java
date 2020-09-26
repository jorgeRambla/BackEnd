package es.unizar.murcy.scheduler;

import es.unizar.murcy.model.Token;
import es.unizar.murcy.service.TokenService;
import es.unizar.murcy.service.UserService;
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

    @Autowired
    UserService userService;

    @Scheduled(cron = "0 0/30 0 * * ?")
    public void cleanExpiredTokens() {
        List<Token> tokens = tokenService.getExpiredTokens(new Date());
        if(!tokens.isEmpty()) {
            log.info("-- [TAREA PROGRAMADA] [INICIO] LIMPIEZA DE TOKENS [{}] --", tokens.size());
            for(Token token : tokens) {
                tokenService.delete(token);
                userService.deleteUser(token.getUser());
            }
            log.info("-- [TAREA PROGRAMADA] [FIN] LIMPIEZA DE TOKENS --");
        }
    }
}
