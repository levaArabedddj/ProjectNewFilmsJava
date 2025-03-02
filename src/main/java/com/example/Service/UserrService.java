package com.example.Service;


import com.example.Entity.Users;
import com.example.Repository.UsersRepo;
import com.example.loger.Loggable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserrService {
    @Autowired
    private final UsersRepo usersRepo;

//    @Loggable
//    public Users usersCreate(Users user){
//        if(usersRepo.findByGmail(user.getGmail()) !=null){
//            throw new IllegalArgumentException("Users not this gmail");
//        }
//        return usersRepo.save(user);
//    }


}
