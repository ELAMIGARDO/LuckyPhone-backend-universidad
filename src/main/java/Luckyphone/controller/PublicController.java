package Luckyphone.controller;

import Luckyphone.entity.CategoriaProducto;
import Luckyphone.entity.Producto;
import Luckyphone.service.CategoriaProductoService;
import Luckyphone.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final ProductoService productoService;
    private final CategoriaProductoService categoriaService;

    public PublicController(ProductoService productoService,
                            CategoriaProductoService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/productos")
    public ResponseEntity<Page<Producto>> listarProductos(@PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(productoService.listar(pageable));
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Integer id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categorias")
    public ResponseEntity<Page<CategoriaProducto>> listarCategorias(@PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(categoriaService.listar(pageable));
    }
}
