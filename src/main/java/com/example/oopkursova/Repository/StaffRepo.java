package com.example.oopkursova.Repository;



import com.example.oopkursova.DTO.DtoStaff;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface StaffRepo {

    List<DtoStaff> findAllStaff();
}

