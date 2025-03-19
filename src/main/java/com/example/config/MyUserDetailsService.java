package com.example.config;



import com.example.Entity.Users;
import com.example.Repository.UsersRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepo usersRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return MyUserDetails.build(user); // Используем  кастомный метод build
    }



//    @Transactional
//    public UserDetails loadUserByUsernameAndId(String username, Long userId) throws UsernameNotFoundException {
//        Users user = usersRepo.findByUserName(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Если userId не передан (например, вызов из стандартного метода), берем из user
//        if (userId == null) {
//            userId = user.getUser_id();
//        }
//
//        return MyUserDetails.build(user, userId); // Передаем userId
//    }
}


