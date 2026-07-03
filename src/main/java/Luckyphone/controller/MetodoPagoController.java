package Luckyphone.controller;

import Luckyphone.entity.MetodoPago;
import Luckyphone.service.MetodoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoService service;

    public MetodoPagoController(MetodoPagoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> obtenerPorId(@PathVariable Integer id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetodoPago> crear(@RequestBody MetodoPago metodoPago) {
        return ResponseEntity.ok(service.guardar(metodoPago));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Integer id, @RequestBody MetodoPago metodoPago) {
        metodoPago.setIdMetodoPago(id);
        return ResponseEntity.ok(service.guardar(metodoPago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
