package Luckyphone.controller;

import Luckyphone.dto.UsuarioResponseDTO;
import Luckyphone.entity.Usuario;
import Luckyphone.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listar(@PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable).map(UsuarioResponseDTO::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return service.obtenerPorId(id)
                .map(u -> ResponseEntity.ok(new UsuarioResponseDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(new UsuarioResponseDTO(service.guardar(usuario)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(new UsuarioResponseDTO(service.actualizar(id, usuario)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
