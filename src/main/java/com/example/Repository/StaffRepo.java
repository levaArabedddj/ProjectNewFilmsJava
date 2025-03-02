package com.example.Repository;




import com.example.DTO.DtoStaff;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface StaffRepo {

    List<DtoStaff> findAllStaff();
}

