package Luckyphone.repo;

import Luckyphone.entity.MovimientoInventario;
import Luckyphone.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoInventarioRepo extends JpaRepository<MovimientoInventario, Integer> {

    Page<MovimientoInventario> findByProducto(Producto producto, Pageable pageable);

    Page<MovimientoInventario> findByTipo(String tipo, Pageable pageable);
}
