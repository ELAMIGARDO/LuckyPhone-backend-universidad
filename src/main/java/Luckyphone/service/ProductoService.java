package Luckyphone.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import Luckyphone.entity.Producto;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.repo.ProductoRepo;
import Luckyphone.specification.ProductoSpecification;

@Service
public class ProductoService {

    private final ProductoRepo productorepo;

    public ProductoService(ProductoRepo productorepo) {
        this.productorepo = productorepo;
    }

    public Page<Producto> listar(Pageable pageable) {
        return productorepo.findByFechaEliminacionIsNull(pageable);
    }

    public Optional<Producto> obtenerPorId(Integer id) {
        return productorepo.findByIdProductoAndFechaEliminacionIsNull(id);
    }

    public Producto insertar(Producto pro) {
        return productorepo.save(pro);
    }

    public Producto actualizar(Integer id, Producto pro) {
        pro.setIdProducto(id);
        return productorepo.save(pro);
    }

    public void eliminar(Integer id) {
        Producto producto = productorepo.findByIdProductoAndFechaEliminacionIsNull(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
        producto.setFechaEliminacion(LocalDateTime.now());
        producto.setEstado(false);
        productorepo.save(producto);
    }

    public Page<Producto> buscar(String nombre, Integer idCategoria, Integer idProveedor, BigDecimal precioMin,
            BigDecimal precioMax, String codigoBarras, Integer stockMenorQue, Pageable pageable) {
        return productorepo.findAll(
                ProductoSpecification.buscar(nombre, idCategoria, idProveedor, precioMin, precioMax, codigoBarras, stockMenorQue),
                pageable);
    }
}
