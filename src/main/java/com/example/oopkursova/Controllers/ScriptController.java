package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Script;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.ScriptRepo;
import com.example.oopkursova.Service.ScriptService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class ScriptController {

    private final ScriptRepo scriptRepo;
    private final ScriptService service;
    private static final Logger logger = LoggerFactory.getLogger(ScriptController.class);


    public ScriptController(ScriptRepo scriptRepo, ScriptService service) {
        this.scriptRepo = scriptRepo;
        this.service = service;
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

    @GetMapping("/edit_script")
    public String showUpdateForm(Model model) {
        model.addAttribute("script", new Script());
        return "UpdateScriptFilms";
    }

    @PostMapping("/edit_script")
    public String updateScript(@RequestParam("id") Long id,@ModelAttribute Script updatedScript) {
        service.update(id,updatedScript);
        return "MenuDirectors";
    }


}
