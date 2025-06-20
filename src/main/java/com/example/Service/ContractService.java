package com.example.Service;

import com.example.DTO.ContractInitData;
import com.example.Entity.*;
import com.example.Entity.Movies;
import com.example.Enum.ContractStatus;
import com.example.RabbitMQ.CastingTask.TrialShootingDayEventPublisher;
import com.example.RabbitMQ.ContractTask.ContractEventPublisher;
import com.example.RabbitMQ.DtoRabbitMQ.ContractNegotiationEvent;
import com.example.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ContractService {

    private final ContractNegotationRepo contractNegotationRepo;
    private final ContractRepo contractRepo;
    private final UsersRepo usersRepo;
    private final MoviesRepo moviesRepo;
    private final DirectorRepo directorRepo;
    private final ContractEventPublisher contractEventPublisher;

    @Autowired
    public ContractService(ContractNegotationRepo contractNegotationRepo, ContractRepo contractRepo, UsersRepo usersRepo, MoviesRepo moviesRepo, DirectorRepo directorRepo, ContractEventPublisher contractEventPublisher) {
        this.contractNegotationRepo = contractNegotationRepo;
        this.contractRepo = contractRepo;
        this.usersRepo = usersRepo;
        this.moviesRepo = moviesRepo;
        this.directorRepo = directorRepo;
        this.contractEventPublisher = contractEventPublisher;
    }


    @Transactional
    public ContractNegotiation initialeNegotiation(Long actorId, Long directorId, Long movieId,
                                                   Double proposedSalary, Double penalty,
                                                   Double bonuses, String paymentSchedule) {


//        Users actor = usersRepo.findById(actorId).orElseThrow(() -> new RuntimeException("User not found"));
//        Director director = directorRepo.findByUserUserId(directorId).orElseThrow(()-> new RuntimeException("Director not found"));
//        Movies movies = moviesRepo.findById(movieId).orElseThrow(()-> new RuntimeException("Movies not found"));

        ContractInitData init = usersRepo.findInitData(actorId, directorId, movieId);
        ContractNegotiation contr = new ContractNegotiation();

        contr.setActor(init.getActor());
        contr.setDirector(init.getDirector());
        contr.setMovie(init.getMovie());
        contr.setProposedSalary(proposedSalary);
        contr.setPenalty(penalty);
        contr.setBonuses(bonuses);
        contr.setPaymentSchedule(paymentSchedule);
        contr.setStatus(ContractStatus.WAITING_ACTOR);
        contractNegotationRepo.save(contr);

        ContractNegotiationEvent event = new ContractNegotiationEvent();
        event.setContractId(contr.getId());
        event.setActorId(actorId);
        event.setFullNameActor(init.getActor().getActor().getName());
        event.setActorEmail(init.getActor().getGmail());
        event.setDirectorId(directorId);
        event.setFullNameDirector(init.getDirector().getName());
        event.setDirectorEmail(init.getDirector().getUsers().getGmail());
        event.setMovieId(movieId);
        event.setMovieTitle(init.getMovie().getTitle());
        event.setProposedSalary(proposedSalary);
        event.setPenalty(penalty);
        event.setBonuses(bonuses);
        event.setPaymentSchedule(paymentSchedule);
        event.setStartDate(LocalDate.now());
        event.setEndDate(LocalDate.now().plusYears(1));
        event.setConfidentialityAgreement(false);

        contractEventPublisher.publishContract(event);
        System.out.println("отправил в паблишер");

        return contractNegotationRepo.save(contr);
    }

    @Transactional
    public ContractNegotiation directorPropose(Long negotiationId,
                                               Double proposedSalary, Double penalty,
                                               Double bonuses, String paymentSchedule){

        ContractNegotiation contr = contractNegotationRepo.findById(negotiationId).orElseThrow(()-> new RuntimeException("Contract not found"));

        if(contr.getStatus() == ContractStatus.ACCEPTED || contr.getStatus() == ContractStatus.REJECTED){
            throw new IllegalStateException("Negotiation already closed");
        }

        contr.setProposedSalary(proposedSalary);
        contr.setPenalty(penalty);
        contr.setBonuses(bonuses);
        contr.setPaymentSchedule(paymentSchedule);
        contr.setDirectorSignature(null); // сброс предыдущих подписей
        contr.setActorSignature(null);
        contr.setStatus(ContractStatus.WAITING_ACTOR);

        return contractNegotationRepo.save(contr);
    }

    /** 3) Актёр вносит правки → режиссёру */
    @Transactional
    public ContractNegotiation actorPropose(Long negotiationId,
                                            Double proposedSalary, Double penalty,
                                            Double bonuses, String paymentSchedule) {
        ContractNegotiation neg = contractNegotationRepo.findById(negotiationId)
                .orElseThrow(() -> new EntityNotFoundException("Negotiation not found"));

        if (neg.getStatus() == ContractStatus.ACCEPTED || neg.getStatus() == ContractStatus.REJECTED) {
            throw new IllegalStateException("Negotiation already closed");
        }

        neg.setProposedSalary(proposedSalary);
        neg.setPenalty(penalty);
        neg.setBonuses(bonuses);
        neg.setPaymentSchedule(paymentSchedule);
        neg.setDirectorSignature(null);
        neg.setActorSignature(null);
        neg.setStatus(ContractStatus.WAITING_DIRECTOR);

        return contractNegotationRepo.save(neg);
    }

    /** 4a) Актёр подписывает ▼ */
    @Transactional
    public ContractNegotiation actorSign(Long negotiationId, String signature) {
        ContractNegotiation neg = contractNegotationRepo.findById(negotiationId)
                .orElseThrow(() -> new EntityNotFoundException("Negotiation not found"));

        if (neg.getStatus() == ContractStatus.WAITING_ACTOR) {
            neg.setActorSignature(signature);
            // теперь ждём подписи режиссёра
            neg.setStatus(ContractStatus.WAITING_DIRECTOR);
            return contractNegotationRepo.save(neg);
        }

        // если уже есть подпись режиссёра — закрываем переговоры
        if (neg.getDirectorSignature() != null) {
            neg.setActorSignature(signature);
            return finalizeContract(neg);
        }

        throw new IllegalStateException("Cannot actorSign in current state: " + neg.getStatus());
    }

    /** 4b) Режиссёр подписывает ▲ */
    @Transactional
    public ContractNegotiation directorSign(Long negotiationId, String signature) {
        ContractNegotiation neg = contractNegotationRepo.findById(negotiationId)
                .orElseThrow(() -> new EntityNotFoundException("Negotiation not found"));

        if (neg.getStatus() == ContractStatus.WAITING_DIRECTOR) {
            neg.setDirectorSignature(signature);
            // теперь ждём подписи актёра
            neg.setStatus(ContractStatus.WAITING_ACTOR);
            return contractNegotationRepo.save(neg);
        }

        // если уже есть подпись актёра — закрываем переговоры
        if (neg.getActorSignature() != null) {
            neg.setDirectorSignature(signature);
            return finalizeContract(neg);
        }

        throw new IllegalStateException("Cannot directorSign in current state: " + neg.getStatus());
    }

    /** 5) Как только обе подписи есть — создаём финальный контракт и удаляем все переговоры */
    private ContractNegotiation finalizeContract(ContractNegotiation neg) {
        // создаём Contract
        Contract contract = new Contract();
        contract.setUser(neg.getActor());
        contract.setMovie(neg.getMovie());
        contract.setSalary(BigDecimal.valueOf(neg.getProposedSalary()));
        contract.setPenalty(neg.getPenalty());
        contract.setBonuses(neg.getBonuses());
        contract.setPaymentSchedule(neg.getPaymentSchedule());
        contract.setStatus(ContractStatus.ACCEPTED);
        contract.setActorSignature(neg.getActorSignature());
        contract.setDirectorSignature(neg.getDirectorSignature());
        // остальные поля можно заполнить по умолчанию…

        contractRepo.save(contract);

        // удаляем все переговоры по этому актёру/фильму
        contractNegotationRepo.deleteByActorAndMovie(neg.getActor(), neg.getMovie());

        return neg;
    }

    /** (Опционально) отмена переговоров */
    @Transactional
    public ContractNegotiation rejectNegotiation(Long negotiationId, String reason) {
        ContractNegotiation neg = contractNegotationRepo.findById(negotiationId)
                .orElseThrow(() -> new EntityNotFoundException("Negotiation not found"));
        neg.setStatus(ContractStatus.REJECTED);
        // можно сохранить причину в дополнительном поле, если понадобится
        return contractNegotationRepo.save(neg);
    }

}
