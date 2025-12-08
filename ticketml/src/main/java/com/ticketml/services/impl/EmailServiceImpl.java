package com.ticketml.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ticketml.common.entity.Order;
import com.ticketml.common.entity.Ticket;
import com.ticketml.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendTicketEmail(String to, Order order, List<Ticket> tickets) {
        try {
            logger.info("Sending ticket email to: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject("🎫 Vé điện tử TicketML - Đơn hàng #" + order.getId());

            String eventName = tickets.get(0).getTicketType().getEvent().getTitle();
            String location = tickets.get(0).getTicketType().getEvent().getLocation();
            String time = tickets.get(0).getTicketType().getEvent().getStartDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN")));

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<!DOCTYPE html>");
            htmlContent.append("<html lang='vi'>");
            htmlContent.append("<head>");
            htmlContent.append("<meta charset='UTF-8'>");
            htmlContent.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            htmlContent.append("<style>");
            htmlContent.append("body { margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; }");
            htmlContent.append(".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }");
            htmlContent.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px 20px; text-align: center; }");
            htmlContent.append(".header h1 { margin: 0; font-size: 28px; font-weight: 600; }");
            htmlContent.append(".header p { margin: 10px 0 0 0; font-size: 14px; opacity: 0.9; }");
            htmlContent.append(".content { padding: 30px 20px; }");
            htmlContent.append(".success-badge { background-color: #10b981; color: white; padding: 8px 16px; border-radius: 20px; display: inline-block; font-size: 14px; font-weight: 500; margin-bottom: 20px; }");
            htmlContent.append(".event-info { background-color: #f8f9ff; border-left: 4px solid #667eea; padding: 20px; border-radius: 8px; margin: 20px 0; }");
            htmlContent.append(".event-info p { margin: 8px 0; color: #333; font-size: 15px; line-height: 1.6; }");
            htmlContent.append(".event-info strong { color: #667eea; font-weight: 600; display: inline-block; min-width: 100px; }");
            htmlContent.append(".tickets-title { font-size: 20px; font-weight: 600; color: #333; margin: 30px 0 20px 0; padding-bottom: 10px; border-bottom: 2px solid #667eea; }");
            htmlContent.append(".ticket-card { background: linear-gradient(to right, #ffffff 0%, #f8f9ff 100%); border: 2px solid #e5e7eb; border-radius: 12px; padding: 20px; margin-bottom: 20px; position: relative; overflow: hidden; }");
            htmlContent.append(".ticket-card::before { content: ''; position: absolute; top: 0; left: 0; width: 5px; height: 100%; background: linear-gradient(180deg, #667eea 0%, #764ba2 100%); }");
            htmlContent.append(".ticket-header { margin-left: 15px; margin-bottom: 15px; }");
            htmlContent.append(".ticket-type { font-size: 18px; font-weight: 600; color: #333; margin: 0 0 8px 0; }");
            htmlContent.append(".ticket-code { font-size: 13px; color: #6b7280; font-family: 'Courier New', monospace; background-color: #f3f4f6; padding: 4px 8px; border-radius: 4px; display: inline-block; }");
            htmlContent.append(".qr-container { text-align: center; margin-top: 15px; padding: 15px; background-color: white; border-radius: 8px; border: 1px solid #e5e7eb; }");
            htmlContent.append(".qr-container img { border-radius: 8px; }");
            htmlContent.append(".footer { background-color: #f9fafb; padding: 20px; text-align: center; color: #6b7280; font-size: 13px; line-height: 1.6; }");
            htmlContent.append(".footer strong { color: #667eea; }");
            htmlContent.append(".divider { height: 1px; background: linear-gradient(to right, transparent, #e5e7eb, transparent); margin: 20px 0; }");
            htmlContent.append("</style>");
            htmlContent.append("</head>");
            htmlContent.append("<body>");

            htmlContent.append("<div class='container'>");

            // Header
            htmlContent.append("<div class='header'>");
            htmlContent.append("<h1>🎫 TicketML</h1>");
            htmlContent.append("<p>Vé điện tử của bạn đã sẵn sàng!</p>");
            htmlContent.append("</div>");

            // Content
            htmlContent.append("<div class='content'>");
            htmlContent.append("<span class='success-badge'>✓ Thanh toán thành công</span>");
            htmlContent.append("<p style='color: #6b7280; font-size: 14px; margin-top: 10px;'>Mã đơn hàng: <strong>#").append(order.getId()).append("</strong></p>");

            // Event Info
            htmlContent.append("<div class='event-info'>");
            htmlContent.append("<p><strong>🎭 Sự kiện:</strong> ").append(eventName).append("</p>");
            htmlContent.append("<p><strong>📅 Thời gian:</strong> ").append(time).append("</p>");
            htmlContent.append("<p><strong>📍 Địa điểm:</strong> ").append(location).append("</p>");
            htmlContent.append("</div>");

            htmlContent.append("<div class='divider'></div>");

            // Tickets
            htmlContent.append("<h3 class='tickets-title'>Danh sách vé của bạn</h3>");

            for (Ticket ticket : tickets) {
                htmlContent.append("<div class='ticket-card'>");
                htmlContent.append("<div class='ticket-header'>");
                htmlContent.append("<p class='ticket-type'>").append(ticket.getTicketType().getType()).append("</p>");
                htmlContent.append("<span class='ticket-code'>Mã vé: ").append(ticket.getQrCode()).append("</span>");
                htmlContent.append("</div>");
                htmlContent.append("<div class='qr-container'>");
                htmlContent.append("<img src='cid:qr-").append(ticket.getQrCode()).append("' width='180' height='180' alt='QR Code' />");
                htmlContent.append("<p style='margin-top: 10px; font-size: 12px; color: #6b7280;'>Quét mã QR để soát vé</p>");
                htmlContent.append("</div>");
                htmlContent.append("</div>");
            }

            htmlContent.append("</div>");

            // Footer
            htmlContent.append("<div class='footer'>");
            htmlContent.append("<strong>📱 Lưu ý quan trọng</strong><br/>");
            htmlContent.append("Vui lòng xuất trình mã QR này tại quầy soát vé khi tham dự sự kiện.<br/>");
            htmlContent.append("Mỗi mã QR chỉ được sử dụng một lần.<br/><br/>");
            htmlContent.append("<em>Cảm ơn bạn đã tin tưởng TicketML!</em>");
            htmlContent.append("</div>");

            htmlContent.append("</div>");
            htmlContent.append("</body>");
            htmlContent.append("</html>");

            helper.setText(htmlContent.toString(), true);

            for (Ticket ticket : tickets) {
                byte[] qrImage = generateQRCode(ticket.getQrCode(), 250, 250);
                ByteArrayResource resource = new ByteArrayResource(qrImage);
                helper.addInline("qr-" + ticket.getQrCode(), resource, "image/png");
            }

            mailSender.send(message);
            logger.info("Email sent successfully!");

        } catch (Exception e) {
            logger.error("Failed to send email", e);
        }
    }

    private byte[] generateQRCode(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}