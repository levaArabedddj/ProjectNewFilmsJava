package com.example.oopkursova;

import com.example.Entity.Director;
import com.example.Entity.MoviesPackage.Finance;
import com.example.Entity.Users;
import com.example.Repository.DirectorRepo;
import com.example.Repository.FinanceRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.Service.FinanceService;
import com.example.config.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FinanceTest {

    private MyUserDetails userDetails;
    private Users user;
    private Director director;
    private MoviesRepo moviesRepo;

    @Mock private DirectorRepo directorRepo;
    @Mock private UsersRepo usersRepo;
    @InjectMocks private FinanceService financeService;
    @Mock private  FinanceRepo financeRepo;

    private Finance existing;

    @BeforeEach
    void setUp() {
//        finance = new Finance();
//        finance.setId(1L);

        existing = new Finance();
        existing.setId(1L);
        existing.setBudget(new BigDecimal("1000.00"));
        when(financeRepo.findById(1L)).thenReturn(Optional.of(existing));
    }


    @Test
    void updateFinance_AllValid_SavesUpdatedEntity() {
        // 1) Подготовка: существующая запись в репозитории
        Finance existing = new Finance();
        existing.setId(1L);
        existing.setBudget(new BigDecimal("1000.00"));
        // расходы по умолчанию = 0

        when(financeRepo.findById(1L)).thenReturn(Optional.of(existing));

        // 2) Новые данные, которые не нарушают ограничений
        Finance updated = new Finance();
        updated.setId(1L);
        updated.setBudget(new BigDecimal("1200.00"));
        updated.setActorsSalary(new BigDecimal("300.00"));
        updated.setCrewSalary(new BigDecimal("200.00"));
        updated.setAdvertisingCost(new BigDecimal("200.00"));
        updated.setEditingCost(new BigDecimal("100.00"));
        updated.setEquipmentCost(new BigDecimal("300.00"));

        // 3) Вызов
        financeService.updateFinance(1L, updated);

        // 4) Проверки, что existing изменился
        assertEquals(new BigDecimal("1200.00"), existing.getBudget());
        assertEquals(new BigDecimal("300.00"), existing.getActorsSalary());
        assertEquals(new BigDecimal("200.00"), existing.getCrewSalary());
        assertEquals(new BigDecimal("200.00"), existing.getAdvertisingCost());
        assertEquals(new BigDecimal("100.00"), existing.getEditingCost());
        assertEquals(new BigDecimal("300.00"), existing.getEquipmentCost());

        // 5) Убедимся, что метод save вызвался
        verify(financeRepo).save(existing);
    }


//    @Test
//    void updateFinance_NullBudget_ThrowsIllegalArgument() {
//        Finance upd = new Finance();
//        upd.setId(1L);
//        upd.setBudget(null);
//
//        IllegalArgumentException ex = assertThrows(
//                IllegalArgumentException.class,
//                () -> financeService.updateFinance(1L, upd)
//        );
//        assertTrue(ex.getMessage().contains("Budget cannot be null"));
//        verify(financeRepo, never()).save(any());
//    }
//
//    @Test
//    void updateFinance_NegativeBudget_ThrowsIllegalArgument() {
//        Finance upd = new Finance();
//        upd.setId(1L);
//        upd.setBudget(new BigDecimal("-10.00"));
//
//        IllegalArgumentException ex = assertThrows(
//                IllegalArgumentException.class,
//                () -> financeService.updateFinance(1L, upd)
//        );
//        assertTrue(ex.getMessage().contains("cannot be negative"));
//        verify(financeRepo, never()).save(any());
//    }

    @Test
    void updateFinance_ActorsSalaryExceedsHalfBudget_ThrowsIllegalArgument() {
        Finance upd = spy(new Finance());
        upd.setId(1L);
        upd.setBudget(new BigDecimal("100.00"));
        doReturn(new BigDecimal("60.00"))
                .when(upd).getActorsSalary(); // >50%
        upd.setCrewSalary(BigDecimal.ZERO);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> financeService.updateFinance(1L, upd)
        );

        assertTrue(ex.getMessage().contains("Total salary exceed budget"));
        verify(financeRepo, never()).save(any());
    }

    @Test
    void updateFinance_AdvertisingCostExceeds20Percent_ThrowsIllegalArgument() {
        Finance upd = spy(new Finance());
        upd.setId(1L);
        upd.setBudget(new BigDecimal("100.00"));
        doReturn(new BigDecimal("25.00"))
                .when(upd).getAdvertisingCost(); // 25%

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> financeService.updateFinance(1L, upd)
        );
        assertTrue(ex.getMessage().contains("cannot exceed 20%"));
        verify(financeRepo, never()).save(any());
    }

    @Test
    void updateFinance_TotalExpensesExceedBudget_ThrowsIllegalArgument() {
        Finance upd = spy(new Finance());
        upd.setId(1L);
        upd.setBudget(new BigDecimal("100.00"));
        // суммарные расходы = 30+30+10+20+40 = 130
        doReturn(new BigDecimal("25.00"))
                .when(upd).getActorsSalary();

        doReturn(new BigDecimal("24.00"))
                .when(upd).getCrewSalary();

        doReturn(new BigDecimal("15.00"))
                .when(upd).getAdvertisingCost();

        doReturn(new BigDecimal("20.00"))
                .when(upd).getEditingCost();

        doReturn(new BigDecimal("20.00"))
                .when(upd).getEquipmentCost();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> financeService.updateFinance(1L, upd)
        );
        assertTrue(ex.getMessage().contains("Total expenses exceed budget"));
        verify(financeRepo, never()).save(any());
    }

}
