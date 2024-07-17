import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static String sendEmail(String sender, String[] receivers, String subject, String result) {
        String smtpHost = "10.134.34.241";  // SMTP 服务器
        int smtpPort = 587;                 // SMTP 端口
        String smtpEhlo = "ismetoad";       // 这是非常重要的，这是身份验证！！！

        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "false");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 如果需要身份验证，请在此处提供用户名和密码
                return null;
            }
        });

        try {
            // 创建邮件消息
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            InternetAddress[] recipientAddresses = new InternetAddress[receivers.length];
            for (int i = 0; i < receivers.length; i++) {
                recipientAddresses[i] = new InternetAddress(receivers[i]);
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);
            message.setSubject(subject);
            message.setContent(result, "text/html; charset=utf-8");

            // 发送邮件
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, smtpPort, smtpEhlo, null);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            return "success";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "fail to send";
        }
    }

    public static void main(String[] args) {
        // 测试发送邮件
        String sender = "sender@example.com";
        String[] receivers = {"receiver@example.com"};
        String subject = "Test Subject";
        String result = "<h1>This is a test email</h1>";
        
        String response = sendEmail(sender, receivers, subject, result);
        System.out.println(response);
    }
}
