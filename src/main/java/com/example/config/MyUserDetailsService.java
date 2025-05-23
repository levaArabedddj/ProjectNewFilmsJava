package com.example.config;



import com.example.Entity.Director;
import com.example.Entity.DirectorProfiles;
import com.example.Entity.Users;
import com.example.Enum.UserRole;
import com.example.Repository.DirectorProfilesRepo;
import com.example.Repository.DirectorRepo;
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
    @Autowired
    private  DirectorRepo directorRepo;
    @Autowired
    private  DirectorProfilesRepo directorProfilesRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return MyUserDetails.build(user); // Используем  кастомный метод build
    }

    /** Если есть пользователь с таким email (gmail), возвращаем его, иначе создаём новый. */
    @Transactional
    public Users findOrCreateByEmail(String email) {
        String username = email.split("@")[0];

        String givenName= email.split("@")[0];
        String familyName = email.split("@")[0];
        return usersRepo.findByGmail(email)
                .orElseGet(() -> {
                    // 1) Создаём Users
                    Users newUser = new Users();
                    newUser.setGmail(email);
                    newUser.setUserName(username);
                    newUser.setPassword("");                  // OAuth-пользователь
                    newUser.setRole(UserRole.DIRECTOR);
                    Users savedUser = usersRepo.save(newUser);

                    // 2) Создаём DirectorProfiles с реальными именем/фамилией
                    DirectorProfiles profiles = new DirectorProfiles();
                    profiles.setFirstName(
                            givenName != null && !givenName.isBlank()
                                    ? givenName
                                    : username
                    );
                    profiles.setLastName(
                            familyName != null && !familyName.isBlank()
                                    ? familyName
                                    : username
                    );
                    // если есть @NotNull или @NotBlank на других полях —
                    // заполните их либо дефолтными значениями
                    profiles.setGender(null);
                    profiles.setPhoneNumber(null);
                    DirectorProfiles savedProfiles = directorProfilesRepo.save(profiles);

                    // 3) Создаём Director и связываем всё вместе
                    Director director = new Director();
                    director.setUsers(savedUser);
                    director.setName(savedProfiles.getFirstName());
                    director.setSurName(savedProfiles.getLastName());
                    director.setDirectorProfiles(savedProfiles);
                    directorRepo.save(director);

                    return savedUser;
                });
    }
}


