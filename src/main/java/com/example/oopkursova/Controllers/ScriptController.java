package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Entity.Script;
import com.example.oopkursova.Repository.ScriptRepo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ScriptController {

    private final ScriptRepo scriptRepo;

    public ScriptController(ScriptRepo scriptRepo) {
        this.scriptRepo = scriptRepo;
    }

    @GetMapping("/CreatingScriptMovie")
    public String createScript(Model model){
        model.addAttribute("script", new Script());
        return "CreatingScriptMovie";
    }

    @PostMapping("addScriptToMovie")
    public String createScriptMovie(@Valid Script script){
        scriptRepo.save(script);
        return "MenuDirectors";
    }


}
