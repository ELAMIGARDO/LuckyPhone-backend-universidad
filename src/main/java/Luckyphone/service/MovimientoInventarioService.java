package Luckyphone.service;

import Luckyphone.entity.MovimientoInventario;
import Luckyphone.entity.Producto;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.exception.StockInsuficienteException;
import Luckyphone.repo.MovimientoInventarioRepo;
import Luckyphone.repo.ProductoRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepo repo;
    private final ProductoRepo productoRepo;

    public MovimientoInventarioService(MovimientoInventarioRepo repo, ProductoRepo productoRepo) {
        this.repo = repo;
        this.productoRepo = productoRepo;
    }

    public Page<MovimientoInventario> listar(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<MovimientoInventario> listarPorProducto(Integer idProducto, Pageable pageable) {
        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + idProducto));
        return repo.findByProducto(producto, pageable);
    }

    @Transactional
    public MovimientoInventario registrar(MovimientoInventario movimiento) {
        Producto producto = productoRepo.findById(movimiento.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado: " + movimiento.getProducto().getIdProducto()));

        movimiento.setStockAnterior(producto.getStock());

        int nuevoStock = producto.getStock();
        if ("ENTRADA".equalsIgnoreCase(movimiento.getTipo())) {
            nuevoStock += movimiento.getCantidad();
        } else if ("SALIDA".equalsIgnoreCase(movimiento.getTipo())) {
            nuevoStock -= movimiento.getCantidad();
            if (nuevoStock < 0) {
                throw new StockInsuficienteException("Stock insuficiente para: " + producto.getNombre());
            }
        } else if ("AJUSTE".equalsIgnoreCase(movimiento.getTipo())) {
            nuevoStock = movimiento.getCantidad();
        }

        producto.setStock(nuevoStock);
        productoRepo.save(producto);

        movimiento.setStockNuevo(nuevoStock);
        return repo.save(movimiento);
    }

    public Page<MovimientoInventario> listarPorTipo(String tipo, Pageable pageable) {
        return repo.findByTipo(tipo, pageable);
    }
}
