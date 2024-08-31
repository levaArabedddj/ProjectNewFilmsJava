package com.example.oopkursova.config;


import com.example.oopkursova.Repository.UsersRepo;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
//    @Transactional
//    public Users loadUserByUsername(String username) {
//        Users user = usersRepo.findByGmail(username);
//        Hibernate.initialize(user.getMoviesList());
//        return user;
//    }
    public void createUser(String name, String rawPassword) {
        Users user = new Users();
        user.setName(name);
        user.setPassword(passwordEncoder.encode(rawPassword));
        usersRepo.save(user);
    }
}


