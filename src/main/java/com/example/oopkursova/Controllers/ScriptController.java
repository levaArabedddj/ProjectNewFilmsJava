package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Script;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.ScriptRepo;
import com.example.oopkursova.Service.ScriptService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/Script")
public class ScriptController {

    private final ScriptRepo scriptRepo;
    private final ScriptService service;



    public ScriptController(ScriptRepo scriptRepo, ScriptService service) {
        this.scriptRepo = scriptRepo;
        this.service = service;
    }
    @Loggable
    @GetMapping("/CreatingScriptMovie")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String createScript(Model model){
        model.addAttribute("script", new Script());
        return "CreatingScriptMovie";
    }
    @Loggable
    @PostMapping("addScriptToMovie")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String createScriptMovie(@Valid Script script){
        scriptRepo.save(script);
        return "MenuDirectors";
    }
    @Loggable
    @GetMapping("/edit_script")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String showUpdateForm(Model model) {
        model.addAttribute("script", new Script());
        return "UpdateScriptFilms";
    }

    @Loggable
    @PostMapping("/edit_script")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String updateScript(@RequestParam("id") Long id ,
                               @ModelAttribute Script updatedScript) {
        service.update(id,updatedScript);
        return "MenuDirectors";
    }


}
