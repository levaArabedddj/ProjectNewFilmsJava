package com.example.oopkursova.config;


import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepo usersRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepo.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return MyUserDetails.build(user); // Используем  кастомный метод build
    }


}


