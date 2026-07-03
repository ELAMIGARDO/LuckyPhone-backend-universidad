package Luckyphone.controller;

import Luckyphone.entity.CategoriaProducto;
import Luckyphone.service.CategoriaProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaProductoController {

    private final CategoriaProductoService service;

    public CategoriaProductoController(CategoriaProductoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaProducto>> listar(@PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProducto> obtenerPorId(@PathVariable Integer id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoriaProducto> crear(@Valid @RequestBody CategoriaProducto categoria) {
        return ResponseEntity.ok(service.guardar(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProducto> actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaProducto categoria) {
        categoria.setIdCategoria(id);
        return ResponseEntity.ok(service.guardar(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
