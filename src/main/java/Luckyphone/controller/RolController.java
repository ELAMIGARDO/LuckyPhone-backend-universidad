package Luckyphone.controller;

import Luckyphone.entity.Rol;
import Luckyphone.repo.RolRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolRepo repo;

    public RolController(RolRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(repo.findAll());
    }
}
