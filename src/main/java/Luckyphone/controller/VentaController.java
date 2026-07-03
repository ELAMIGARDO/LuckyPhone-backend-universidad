package Luckyphone.controller;

import Luckyphone.dto.CrearVentaRequest;
import Luckyphone.entity.DetalleVenta;
import Luckyphone.entity.Venta;
import Luckyphone.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService service;

    public VentaController(VentaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Venta>> listar(@PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleVenta>> obtenerDetalles(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerDetalles(id));
    }

    @PostMapping
    public ResponseEntity<Venta> crear(@Valid @RequestBody CrearVentaRequest request, Authentication authentication) {
        return ResponseEntity.ok(service.crear(request, authentication.getName()));
    }
}
