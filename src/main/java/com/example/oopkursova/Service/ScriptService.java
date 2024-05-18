package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.Script;
import com.example.oopkursova.Repository.ScriptRepo;
import com.example.oopkursova.loger.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScriptService {

    @Autowired
    private final ScriptRepo scriptRepo;

    public ScriptService(ScriptRepo scriptRepo) {
        this.scriptRepo = scriptRepo;
    }

    @Loggable
    public Script findById(Long id) {
        Optional<Script> scriptOptional = scriptRepo.findById(id);
        return scriptOptional.orElseThrow(() -> new RuntimeException("Script with id " + id + " not found"));
    }

    @Loggable

    public void update(Long id ,Script updatedScript) {
        Script script = scriptRepo.findById(id).
                orElseThrow(() -> new RuntimeException("Script with id "+ id + "not found"));
        Script existingScript = findById(updatedScript.getId());
        existingScript.setContent(updatedScript.getContent());
        scriptRepo.save(existingScript);
    }


}
