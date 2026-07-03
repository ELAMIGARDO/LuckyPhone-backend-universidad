package Luckyphone.specification;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import Luckyphone.entity.Producto;

public class ProductoSpecification {

    private ProductoSpecification() {
    }

    public static Specification<Producto> buscar(String nombre, Integer idCategoria, Integer idProveedor,
            BigDecimal precioMin, BigDecimal precioMax, String codigoBarras, Integer stockMenorQue) {

        Specification<Producto> spec = (root, query, cb) -> cb.isNull(root.get("fechaEliminacion"));

        if (nombre != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }
        if (idCategoria != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoria").get("idCategoria"), idCategoria));
        }
        if (idProveedor != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("proveedor").get("idProveedor"), idProveedor));
        }
        if (precioMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("precio"), precioMin));
        }
        if (precioMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("precio"), precioMax));
        }
        if (codigoBarras != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("codigoBarras"), codigoBarras));
        }
        if (stockMenorQue != null) {
            spec = spec.and((root, query, cb) -> cb.lessThan(root.get("stock"), stockMenorQue));
        }

        return spec;
    }
}
