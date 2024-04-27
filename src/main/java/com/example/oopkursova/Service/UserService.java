package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.UsersRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    @Autowired
    private final UsersRepo usersRepo;

    public Users usersCreate(Users user){
        if(usersRepo.findByGmail(user.getGmail()) !=null){
            throw new IllegalArgumentException("Users not this gmail");
        }
        return usersRepo.save(user);
    }


}
