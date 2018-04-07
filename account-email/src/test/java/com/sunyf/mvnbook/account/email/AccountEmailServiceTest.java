package com.sunyf.mvnbook.account.email;

import static junit.framework.Assert.assertEquals;

import javax.mail.Message;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

public class AccountEmailServiceTest {

    private GreenMail greenMail;

    @Before
    public void startMailServer() throws Exception {
        //greenMail = new GreenMail(ServerSetup.SMTP);
        //此处的8085端口要和service.properties的端口配置一致
        greenMail = new GreenMail(new ServerSetup(8099, null, "smtp"));
        greenMail.setUser("test@sunyf.com", "123456");
        greenMail.start();
    }

    @Test
    public void testSendMail() throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("account-email.xml");
        AccountEmailService accountEmailService = (AccountEmailService) ctx.getBean("accountEmailService");

        String subject = "Test Subject";
        String htmlText = "<h3>Test</h3>";
        accountEmailService.sendMail("test2@sunyf.com", subject, htmlText);

        greenMail.waitForIncomingEmail(2000, 1);

        Message[] msgs = greenMail.getReceivedMessages();
        assertEquals(1, msgs.length);
        assertEquals(subject, msgs[0].getSubject());
        assertEquals(htmlText, GreenMailUtil.getBody(msgs[0]).trim());
        //System.out.println("test send mail !");
    }

    @After
    public void stopMailServer() throws Exception {
        greenMail.stop();
    }
}
