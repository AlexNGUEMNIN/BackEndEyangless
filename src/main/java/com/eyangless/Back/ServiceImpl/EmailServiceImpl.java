package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.Service.EmailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;



@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    @Override
    public void envoieMailhtml(String to, String subject, String htmlbody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlbody, true); // true indicates HTML content mailSender.send(message);
        mailSender.send(message);
    }

//    La fonction ci permet d'envoyer le code opt par mail suivant la fonction envoieMailHtml
    @Override
    public void sendOtpEmail(String email, String otp) {
        try {
            String htmlContent = buildOtpEmailTemplate(otp);
            envoieMailhtml(email, "Code de réinitialisation de mot de passe", htmlContent);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email OTP", e);
        }
    }

//    C'est le template du mail que l'utilisateur va recevoir
    private String buildOtpEmailTemplate(String otp) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <title>Code de réinitialisation</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "    <div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                "        <h2 style='color: #007bff; text-align: center;'>Réinitialisation de mot de passe</h2>" +
                "        <p>Bonjour,</p>" +
                "        <p>Vous avez demandé la réinitialisation de votre mot de passe. Voici votre code de vérification :</p>" +
                "        <div style='text-align: center; margin: 30px 0;'>" +
                "            <span style='font-size: 32px; font-weight: bold; color: #007bff; padding: 20px; border: 2px solid #007bff; border-radius: 8px; display: inline-block;'>" +
                otp +
                "            </span>" +
                "        </div>" +
                "        <p><strong>Important :</strong></p>" +
                "        <ul>" +
                "            <li>Ce code expire dans <strong>10 minutes</strong></li>" +
                "            <li>Ne partagez jamais ce code avec personne</li>" +
                "            <li>Si vous n'avez pas demandé cette réinitialisation, ignorez ce message</li>" +
                "        </ul>" +
                "        <hr style='margin: 30px 0; border: none; border-top: 1px solid #eee;'>" +
                "        <p style='color: #666; font-size: 12px; text-align: center;'>" +
                "            Cet email a été envoyé automatiquement, merci de ne pas y répondre." +
                "        </p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
