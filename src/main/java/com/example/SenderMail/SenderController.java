package com.example.SenderMail;


import com.example.Entity.Users;
import com.example.Repository.UsersRepo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class SenderController {

    private final SenderClass mailSender;
    private final UsersRepo usersRepo;
    private static final Logger logger = LoggerFactory.getLogger(SenderController.class);

    @GetMapping("/index")
    public String index() {
        return "enter-email";
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestParam String email, HttpSession session, Model model) {
        Optional<Users> optionalUser = usersRepo.findByGmail(email);
        if (optionalUser.isPresent()) {
            String code = generateVerificationCode();
            session.setAttribute("verificationCode", code);
            session.setAttribute("email", email);
            String subject = "Your Verification Code";
            String text = "Your verification code is: " + code;
            mailSender.sendSimpleMessage(email, subject, text);
            model.addAttribute("email", email);
            return "verify-code";
        } else {
            model.addAttribute("error", "Email not found");
            return "enter-email";
        }
    }

    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam String code, HttpSession session, Model model) {
        String sessionCode = (String) session.getAttribute("verificationCode");

        logger.info("Session Code: " + sessionCode);
        logger.info("Provided Code: " + code);

        if (sessionCode != null && sessionCode.equals(code)) {
            session.removeAttribute("verificationCode");
            session.removeAttribute("email");
            return "redirect:/success";
        } else {
            model.addAttribute("error", "Invalid code");
            return "verify-code";
        }
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
