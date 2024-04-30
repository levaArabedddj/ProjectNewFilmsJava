package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Script;
import com.example.oopkursova.Loggable;
import com.example.oopkursova.Repository.ScriptRepo;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ScriptController {

    private final ScriptRepo scriptRepo;
    private static final Logger logger = LoggerFactory.getLogger(ScriptController.class);


    public ScriptController(ScriptRepo scriptRepo) {
        this.scriptRepo = scriptRepo;
    }
    @Loggable
    @GetMapping("/CreatingScriptMovie")
    public String createScript(Model model){
        model.addAttribute("script", new Script());
        return "CreatingScriptMovie";
    }
    @Loggable
    @PostMapping("addScriptToMovie")
    public String createScriptMovie(@Valid Script script){
        scriptRepo.save(script);
        logger.info("Script added to movie: {}", script);
        return "MenuDirectors";
    }


}
