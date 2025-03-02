package com.example.Repository;


import com.example.DTO.DtoStaff;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StaffRepoImpl implements StaffRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<DtoStaff> findAllStaff() {
        return entityManager.createQuery(
                "SELECT new com.example.oopkursova.DTO.DtoStaff(a.name, a.surName, a.salaryPerHour, a.rating) FROM Actors a " +
                        "UNION " +
                        "SELECT new com.example.oopkursova.DTO.DtoStaff(c.name, c.surName, c.salaryPerHours, NULL) FROM FilmCrewMembers c",
                DtoStaff.class
        ).getResultList();
    }
}

