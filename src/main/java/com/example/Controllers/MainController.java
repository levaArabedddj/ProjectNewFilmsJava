package com.example.Controllers;


import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class MainController {

    @GetMapping("/")
    public String home() {
        return "Main";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @Cacheable(value = "menuDirectorsCache")
    @GetMapping("/Main")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String MainController() {
        return "/MenuDirectors";
    }

    @CacheEvict(value = "menuDirectorsCache", allEntries = true)
    @GetMapping("/invalidateCache")
    public String invalidateCache() {
        // Кэш будет очищен
        return "redirect:/Main";
    }
}



