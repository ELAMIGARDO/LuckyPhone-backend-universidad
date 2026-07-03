package Luckyphone.service;

import Luckyphone.dto.CrearVentaRequest;
import Luckyphone.entity.*;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.exception.StockInsuficienteException;
import Luckyphone.repo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepo ventaRepo;
    private final DetalleVentaRepo detalleRepo;
    private final ProductoRepo productoRepo;
    private final MovimientoInventarioRepo movimientoRepo;
    private final UsuarioRepo usuarioRepo;

    public VentaService(VentaRepo ventaRepo,
                        DetalleVentaRepo detalleRepo,
                        ProductoRepo productoRepo,
                        MovimientoInventarioRepo movimientoRepo,
                        UsuarioRepo usuarioRepo) {
        this.ventaRepo = ventaRepo;
        this.detalleRepo = detalleRepo;
        this.productoRepo = productoRepo;
        this.movimientoRepo = movimientoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public Page<Venta> listar(Pageable pageable) {
        return ventaRepo.findAll(pageable);
    }

    public List<DetalleVenta> obtenerDetalles(Integer idVenta) {
        return detalleRepo.findByVentaIdVenta(idVenta);
    }

    @Transactional
    public Venta crear(CrearVentaRequest request, String username) {
        Usuario usuario = usuarioRepo.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setTipoComprobante(request.getTipoComprobante());
        venta.setEstadoVenta("COMPLETADA");
        venta.setUsuario(usuario);
        venta.setMetodoPago(request.getMetodoPago());

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVenta detalle : request.getDetalles()) {
            Producto producto = productoRepo.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado: " + detalle.getProducto().getIdProducto()));
            detalle.setPrecioUnitario(producto.getPrecio());
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
            total = total.add(subtotal);
            detalle.setVenta(venta);

            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para: " + producto.getNombre());
            }

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(producto);
            mov.setTipo("SALIDA");
            mov.setCantidad(detalle.getCantidad());
            mov.setStockAnterior(producto.getStock());
            mov.setStockNuevo(producto.getStock() - detalle.getCantidad());
            mov.setObservacion("Venta #" + (venta.getIdVenta() != null ? venta.getIdVenta() : "nueva"));

            producto.setStock(mov.getStockNuevo());
            productoRepo.save(producto);
            movimientoRepo.save(mov);
        }

        venta.setTotal(total);
        venta = ventaRepo.save(venta);

        for (DetalleVenta detalle : request.getDetalles()) {
            detalle.setVenta(venta);
            detalleRepo.save(detalle);
        }

        return venta;
    }
}
