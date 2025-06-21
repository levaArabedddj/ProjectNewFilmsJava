package com.example.Service;

import com.example.RabbitMQ.DtoRabbitMQ.ContractNegotiationEvent;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Optional;


@Service
public class PdfService {

    public byte[] generateContractPdf(ContractNegotiationEvent event) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            System.out.println("зашли в метод , создаем документ");
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            System.out.println(event);
            document.add(new Paragraph("🎬 Contract for Film Participation"));
            document.add(new Paragraph("Film Title: " + event.getMovieTitle()));
            document.add(new Paragraph("Actor: " + event.getFullNameActor()));
            document.add(new Paragraph("Director: " + event.getFullNameDirector()));
            document.add(new Paragraph("Salary: " + event.getProposedSalary() + " USD"));
            document.add(new Paragraph("Bonuses: " + event.getBonuses()));
            document.add(new Paragraph("Penalties: " + event.getPenalty()));
            document.add(new Paragraph("Payment Schedule: " + event.getPaymentSchedule()));
            document.add(new Paragraph("Actor Signature: " +
                    Optional.ofNullable(event.getActorSignature()).orElse("")));
            document.add(new Paragraph("Director Signature: " +
                    Optional.ofNullable(event.getDirectorSignature()).orElse("")));
            document.add(new Paragraph("Start Date: " + event.getStartDate()));
            document.add(new Paragraph("End Date: " + event.getEndDate()));

            System.out.println("зашли в метод , создан документ");

            document.close();
            System.out.println("размер файла -"+ out.size());
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации PDF", e);
        }
    }
}

