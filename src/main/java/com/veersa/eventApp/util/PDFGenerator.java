package com.veersa.eventApp.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;


import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.Ticket;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PDFGenerator {
    private final QRGenerator qrCodeGenerator;

    public void generateTicketsPdf(Booking booking, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=tickets_booking_" + booking.getId() + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            for (Ticket ticket : booking.getTickets()) {
                document.add(new Paragraph("Ticket Number: " + ticket.getTicketNumber()));
                document.add(new Paragraph("Event: " + ticket.getEvent().getName()));
                document.add(new Paragraph("Seat: " + ticket.getSeatNumber()));
                document.add(new Paragraph("Issued At: " + ticket.getIssuedAt()));
                document.add(new Paragraph("Checked In: " + ticket.getCheckedIn()));
                document.add(new Paragraph(" "));

                Image qrImage = Image.getInstance(qrCodeGenerator.generateQRCodeImage(ticket.getTicketNumber()));
                qrImage.scaleToFit(150, 150);
                document.add(qrImage);

                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException("PDF generation error", e);
        }
    }

}
