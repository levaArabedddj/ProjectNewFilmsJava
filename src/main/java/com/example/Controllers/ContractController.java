package com.example.Controllers;

import com.example.DTO.NegotiationRequest;
import com.example.DTO.SignatureRequest;
import com.example.Entity.ContractNegotiation;
import com.example.Service.ContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contract")
public class ContractController {

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * 1) Инициировать переговоры (режиссёр шлёт первую версию)
     *    body: NegotiationRequest
     */
    @PostMapping("/initiate/{actorId}/{directorId}/{movieId}")
    public ResponseEntity<ContractNegotiation> initiate(
            @PathVariable Long actorId,
            @PathVariable Long directorId,
            @PathVariable Long movieId,
            @Valid @RequestBody NegotiationRequest req
    ) {
        ContractNegotiation neg = contractService.initialeNegotiation(
                actorId, directorId, movieId,
                req.getProposedSalary(),
                req.getPenalty(),
                req.getBonuses(),
                req.getPaymentSchedule()
        );
        return ResponseEntity.ok(neg);
    }

    /**
     * 2) Режиссёр предлагает новую версию
     *    body: NegotiationRequest
     */
    @PutMapping("/{id}/director-propose")
    public ResponseEntity<ContractNegotiation> directorPropose(
            @PathVariable Long id,
            @Valid @RequestBody NegotiationRequest req
    ) {
        return ResponseEntity.ok(
                contractService.directorPropose(
                        id,
                        req.getProposedSalary(),
                        req.getPenalty(),
                        req.getBonuses(),
                        req.getPaymentSchedule()
                )
        );
    }

    /**
     * 3) Актёр предлагает правки
     *    body: NegotiationRequest
     */
    @PutMapping("/{id}/actor-propose")
    public ResponseEntity<ContractNegotiation> actorPropose(
            @PathVariable Long id,
            @Valid @RequestBody NegotiationRequest req
    ) {
        return ResponseEntity.ok(
                contractService.actorPropose(
                        id,
                        req.getProposedSalary(),
                        req.getPenalty(),
                        req.getBonuses(),
                        req.getPaymentSchedule()
                )
        );
    }

    /**
     * 4a) Актёр подписывает
     *    body: SignatureRequest
     */
    @PutMapping("/{id}/actor-sign")
    public ResponseEntity<ContractNegotiation> actorSign(
            @PathVariable Long id,
            @Valid @RequestBody SignatureRequest req
    ) {
        return ResponseEntity.ok(
                contractService.actorSign(id, req.getSignature())
        );
    }

    /**
     * 4b) Режиссёр подписывает
     *    body: SignatureRequest
     */
    @PutMapping("/{id}/director-sign")
    public ResponseEntity<ContractNegotiation> directorSign(
            @PathVariable Long id,
            @Valid @RequestBody SignatureRequest req
    ) {
        return ResponseEntity.ok(
                contractService.directorSign(id, req.getSignature())
        );
    }


}

