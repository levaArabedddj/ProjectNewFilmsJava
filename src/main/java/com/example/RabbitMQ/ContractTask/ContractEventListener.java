package com.example.RabbitMQ.ContractTask;

import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationAnswerEvent;
import com.example.RabbitMQ.DtoRabbitMQ.ContractNegotiationEvent;
import com.example.Service.PdfService;
import com.example.Service.SenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContractEventListener {

    @Autowired
    private SenderService emailService;

    @Autowired
    private PdfService pdfService;
    private static Logger log = LoggerFactory.getLogger(ContractEventListener.class);

    @RabbitListener(queues = "contractConfigQueue")
    public void handleContractEvent(ContractNegotiationEvent event) {
        log.info("контракт отправлен на почту актеру");
        try {
            byte[] pdf = pdfService.generateContractPdf(event);
            log.info("Контракт успешно отправлен актёру на {}", event.getActorEmail());

            emailService.sendWithAttachment(
                    event.getActorEmail(),
                    "Ваш контракт по фильму: " + event.getMovieTitle(),
                    "Добрый день, " + event.getFullNameActor() + ". В приложении — финальный контракт.",
                    pdf
            );

            log.info("контракт отправлен на почту директору");
            emailService.sendWithAttachment(
                    event.getDirectorEmail(),
                    "Контракт актёра: " + event.getFullNameActor(),
                    "Контракт финализирован. Подписи актёра и режиссёра в приложении.",
                    pdf
            );
            log.info("Контракт успешно отправлен актёру на {}", event.getDirectorEmail());

        } catch (Exception e) {
            log.error("Ошибка при обработке события контракта: {}", e.getMessage(), e);
        }
    }
}
