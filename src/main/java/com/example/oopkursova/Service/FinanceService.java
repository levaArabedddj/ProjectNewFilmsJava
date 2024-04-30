package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.Finance;
import com.example.oopkursova.Repository.FinanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FinanceService {

    @Autowired
    private final FinanceRepo financeRepo;

    public FinanceService(FinanceRepo financeRepo) {
        this.financeRepo = financeRepo;
    }
    public void updateFinance(Long id, Finance updatedFinance) {
        Finance finance = financeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Finance with id " + id + " not found"));

        finance.setBudget(updatedFinance.getBudget());
        finance.setActorsSalary(updatedFinance.getActorsSalary());
        finance.setCrewSalary(updatedFinance.getCrewSalary());
        finance.setAdvertisingCost(updatedFinance.getAdvertisingCost());
        finance.setEditingCost(updatedFinance.getEditingCost());
        finance.setEquipmentCost(updatedFinance.getEquipmentCost());

        financeRepo.save(finance);
    }

    public Optional<Finance> findById(long id){
        return financeRepo.findById(id);
    }
}
