package Luckyphone.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Luckyphone.entity.Producto;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.service.CategoriaProductoService;
import Luckyphone.service.ProductoService;
import Luckyphone.service.ProveedorService;

@RestController
@RequestMapping("/products")
public class ProductoController {

    private final ProductoService productoservice;
    private final CategoriaProductoService categoriaService;
    private final ProveedorService proveedorService;

    public ProductoController(ProductoService productoservice,
                              CategoriaProductoService categoriaService,
                              ProveedorService proveedorService) {
        this.productoservice = productoservice;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<Page<Producto>> listar(@PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(productoservice.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Integer id) {
        Optional<Producto> producto = productoservice.obtenerPorId(id);
        return producto.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> insertar(@Valid @RequestBody Producto pro) {
        return ResponseEntity.ok(productoservice.insertar(pro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @Valid @RequestBody Producto pro) {
        return ResponseEntity.ok(productoservice.actualizar(id, pro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoservice.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<Producto>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) Integer idProveedor,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String codigoBarras,
            @RequestParam(required = false) Integer stockMenorQue,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {

        if (idCategoria != null) {
            categoriaService.obtenerPorId(idCategoria)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada: " + idCategoria));
        }
        if (idProveedor != null) {
            proveedorService.obtenerPorId(idProveedor)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado: " + idProveedor));
        }

        return ResponseEntity.ok(productoservice.buscar(
                nombre, idCategoria, idProveedor, precioMin, precioMax, codigoBarras, stockMenorQue, pageable));
    }
}
