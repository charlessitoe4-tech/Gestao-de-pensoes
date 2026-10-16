package service;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/** Envia email por SMTP STARTTLS usando apenas configuração externa. */
public final class SmtpEmailSender implements EmailSender {

    private final String host;
    private final String portConfigurada;
    private final String username;
    private final String password;
    private final String remetente;

    public SmtpEmailSender() {
        this(System.getenv("GESTAO_PENSOES_SMTP_HOST"),
                System.getenv("GESTAO_PENSOES_SMTP_PORT"),
                System.getenv("GESTAO_PENSOES_SMTP_USER"),
                System.getenv("GESTAO_PENSOES_SMTP_PASSWORD"),
                System.getenv("GESTAO_PENSOES_SMTP_FROM"));
    }

    SmtpEmailSender(String host, String port, String username,
            String password, String remetente) {
        this.host = host;
        this.portConfigurada = port;
        this.username = username;
        this.password = password;
        this.remetente = remetente;
    }

    @Override
    public void enviar(String destinatario, String assunto, String mensagem) {
        if (vazio(host) || vazio(username) || vazio(password) || vazio(remetente)) {
            throw new IllegalStateException(
                    "Configure GESTAO_PENSOES_SMTP_HOST, GESTAO_PENSOES_SMTP_USER, "
                    + "GESTAO_PENSOES_SMTP_PASSWORD e GESTAO_PENSOES_SMTP_FROM "
                    + "para recuperar senhas por email.");
        }
        int port;
        try {
            port = vazio(portConfigurada) ? 587 : Integer.parseInt(portConfigurada);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("GESTAO_PENSOES_SMTP_PORT deve ser uma porta válida.", ex);
        }
        Properties propriedades = new Properties();
        propriedades.put("mail.smtp.auth", "true");
        propriedades.put("mail.smtp.starttls.enable", "true");
        propriedades.put("mail.smtp.starttls.required", "true");
        propriedades.put("mail.smtp.host", host);
        propriedades.put("mail.smtp.port", Integer.toString(port));
        propriedades.put("mail.smtp.connectiontimeout", "10000");
        propriedades.put("mail.smtp.timeout", "10000");
        propriedades.put("mail.smtp.writetimeout", "10000");

        Session sessao = Session.getInstance(propriedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message email = new MimeMessage(sessao);
            email.setFrom(new InternetAddress(remetente));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario, false));
            email.setSubject(assunto);
            email.setContent(mensagem, "text/plain; charset=UTF-8");
            Transport.send(email);
        } catch (MessagingException | IllegalArgumentException ex) {
            throw new IllegalStateException("Não foi possível enviar o email de recuperação.", ex);
        }
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
