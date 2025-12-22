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

            if (tickets == null || tickets.isEmpty()) {
                logger.warn("No tickets provided for order {} - skipping email", order != null ? order.getId() : null);
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject("Vé tham dự sự kiện - Đơn hàng #" + order.getId());

            var event = tickets.get(0).getTicketType().getEvent();
            String eventName = event.getTitle();
            String location = event.getLocation();
            String eventImage = event.getImageUrl();
            String time = event.getStartDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN")));

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<!DOCTYPE html>");
            htmlContent.append("<html lang='vi'>");
            htmlContent.append("<head>");
            htmlContent.append("<meta charset='UTF-8'>");
            htmlContent.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            htmlContent.append("<style>");
            htmlContent.append("body { margin: 0; padding: 0; font-family: 'Inter', 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #f3f4f6; color: #374151; }");
            htmlContent.append(".container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }");
            htmlContent.append(".banner { width: 100%; height: 200px; object-fit: cover; background-color: #e5e7eb; display: block; }");
            htmlContent.append(".content { padding: 40px 30px; }");
            htmlContent.append(".header-title { margin: 0 0 10px 0; font-size: 24px; font-weight: 700; color: #111827; letter-spacing: -0.5px; }");
            htmlContent.append(".order-ref { font-size: 14px; color: #6b7280; margin-bottom: 24px; display: block; }");
            htmlContent.append(".event-details { background-color: #f9fafb; border-radius: 12px; padding: 20px; margin-bottom: 30px; border: 1px solid #e5e7eb; }");
            htmlContent.append(".detail-row { display: flex; margin-bottom: 12px; align-items: flex-start; }");
            htmlContent.append(".detail-row:last-child { margin-bottom: 0; }");
            htmlContent.append(".detail-text { font-size: 15px; color: #4b5563; line-height: 1.5; }");
            htmlContent.append(".detail-text strong { color: #1f2937; font-weight: 600; display: block; margin-bottom: 2px; }");
            htmlContent.append(".divider { height: 1px; background-color: #e5e7eb; margin: 30px 0; }");
            htmlContent.append(".ticket-section-title { font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 20px; }");
            htmlContent.append(".ticket-card { border: 1px solid #e5e7eb; border-radius: 12px; padding: 20px; margin-bottom: 20px; background-color: #fff; transition: all 0.2s; text-align: center; }");
            htmlContent.append(".ticket-type-badge { background-color: #eef2ff; color: #6366f1; padding: 6px 12px; border-radius: 20px; font-size: 13px; font-weight: 600; margin: 0 0 15px 0; display: inline-block; }");
            htmlContent.append(".qr-box { padding: 10px; background: #fff; border-radius: 8px; display: inline-block; }");
            htmlContent.append(".qr-box img { display: block; border-radius: 8px; width: 160px; height: 160px; margin: 0 auto; }");
            htmlContent.append(".ticket-code { margin-top: 15px; font-family: 'Courier New', monospace; font-size: 14px; color: #6b7280; background: #f3f4f6; padding: 4px 12px; border-radius: 6px; letter-spacing: 1px; display: inline-block; word-break: break-all; }");
            htmlContent.append(".footer { background-color: #fafafa; padding: 20px; text-align: center; border-top: 1px solid #f3f4f6; }");
            htmlContent.append(".footer p { margin: 5px 0; font-size: 13px; color: #9ca3af; }");
            htmlContent.append("</style>");
            htmlContent.append("</head>");
            htmlContent.append("<body>");

            htmlContent.append("<div class='container'>");

            String bannerSrc = (eventImage != null && !eventImage.isEmpty())
                    ? eventImage
                    : "https://via.placeholder.com/600x200/6366f1/ffffff?text=" + eventName.replaceAll(" ", "+");

            htmlContent.append("<img class='banner' src='").append(bannerSrc).append("' alt='Event Banner' />");

            htmlContent.append("<div class='content'>");
            htmlContent.append("<h1 class='header-title'>Bạn đã đặt vé thành công!</h1>");
            htmlContent.append("<span class='order-ref'>Mã đơn hàng: #").append(order.getId()).append("</span>");

            htmlContent.append("<div class='event-details'>");
            htmlContent.append("<div class='detail-row'>");
            htmlContent.append("<div class='detail-text'><strong>Sự kiện</strong>").append(eventName).append("</div>");
            htmlContent.append("</div>");
            htmlContent.append("<div class='detail-row'>");
            htmlContent.append("<div class='detail-text'><strong>Thời gian</strong>").append(time).append("</div>");
            htmlContent.append("</div>");
            htmlContent.append("<div class='detail-row'>");
            htmlContent.append("<div class='detail-text'><strong>Địa điểm</strong>").append(location).append("</div>");
            htmlContent.append("</div>");
            htmlContent.append("</div>");

            htmlContent.append("<div class='divider'></div>");

            htmlContent.append("<div class='ticket-section-title'>Vé điện tử của bạn</div>");

            for (Ticket ticket : tickets) {
                htmlContent.append("<div class='ticket-card'>");
                htmlContent.append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' border='0' style='border-collapse:collapse; text-align:center;'>");
                htmlContent.append("<tr><td style='padding:0; text-align:center;'>");
                htmlContent.append("<span class='ticket-type-badge'>").append(ticket.getTicketType().getType()).append("</span>");
                htmlContent.append("</td></tr>");
                htmlContent.append("<tr><td style='padding:0; text-align:center;'>");
                htmlContent.append("<div class='qr-box'>");
                htmlContent.append("<img src='cid:qr-").append(ticket.getQrCode()).append("' width='160' height='160' alt='QR Code' style='display:block;border-radius:8px;width:160px;height:160px;margin:0 auto;' />");
                htmlContent.append("</div>");
                htmlContent.append("</td></tr>");
                htmlContent.append("<tr><td style='padding:0; text-align:center;'>");
                htmlContent.append("<div class='ticket-code'>").append(ticket.getQrCode()).append("</div>");
                htmlContent.append("</td></tr>");
                htmlContent.append("</table>");
                htmlContent.append("</div>");
            }

            htmlContent.append("</div>");

            htmlContent.append("<div class='footer'>");
            htmlContent.append("<strong>Lưu ý quan trọng</strong><br/>");
            htmlContent.append("Vui lòng xuất trình mã QR này tại quầy soát vé khi tham dự sự kiện.<br/>");
            htmlContent.append("Mỗi mã QR chỉ được sử dụng một lần.<br/><br/>");
            htmlContent.append("<em>Cảm ơn bạn đã tin tưởng TicketML!</em>");
            htmlContent.append("</div>");

            htmlContent.append("</div>");
            htmlContent.append("</body>");
            htmlContent.append("</html>");

            helper.setText(htmlContent.toString(), true);

            for (Ticket ticket : tickets) {
                byte[] qrImage = generateQRCode(ticket.getQrCode(), 300, 300); // Tăng size gốc lên 300 để ảnh nét hơn khi resize CSS xuống 160
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

