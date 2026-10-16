package service;

/** Envia mensagens de email transacionais. */
@FunctionalInterface
public interface EmailSender {

    void enviar(String destinatario, String assunto, String mensagem);
}
