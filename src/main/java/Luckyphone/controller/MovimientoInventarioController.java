package Luckyphone.controller;

import Luckyphone.entity.MovimientoInventario;
import Luckyphone.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioService service;

    public MovimientoInventarioController(MovimientoInventarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<MovimientoInventario>> listar(
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<Page<MovimientoInventario>> listarPorProducto(
            @PathVariable Integer idProducto,
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorProducto(idProducto, pageable));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<MovimientoInventario>> listarPorTipo(
            @PathVariable String tipo,
            @PageableDefault(size = 20, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorTipo(tipo, pageable));
    }

    @PostMapping
    public ResponseEntity<MovimientoInventario> registrar(@Valid @RequestBody MovimientoInventario movimiento) {
        return ResponseEntity.ok(service.registrar(movimiento));
    }
}
