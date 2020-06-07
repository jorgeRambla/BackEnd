package es.unizar.murcy.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.rules.ExternalResource;
import javax.mail.internet.MimeMessage;

public class MailServiceRule extends ExternalResource {

    private GreenMail server;

    public MailServiceRule() {
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        server = new GreenMail(ServerSetupTest.SMTP);
        server.setUser("test", "secret");
        server.start();
    }

    public MimeMessage[] getMessages() {
        return server.getReceivedMessages();
    }

    @Override
    protected void after() {
        super.after();
        server.stop();
    }
}