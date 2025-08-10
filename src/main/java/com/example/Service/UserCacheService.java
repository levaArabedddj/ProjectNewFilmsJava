package com.example.Service;

import com.example.DTO.UserCacheDTO;
import com.example.Entity.Users;
import com.example.Repository.UsersRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserCacheService {
    private final UsersRepo usersRepo;

    public UserCacheService(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    @Cacheable(value = "users", key = "#username")
    public UserCacheDTO loadUserCacheDTO(String username) {
        Users user = usersRepo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserCacheDTO(
                user.getUser_id(),
                user.getUserName(),
                user.getRole().name(),
                user.getGmail(),
                user.getPassword()
        );
    }
}

