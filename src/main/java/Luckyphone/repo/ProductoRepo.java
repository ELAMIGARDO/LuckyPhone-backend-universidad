package Luckyphone.repo;

import Luckyphone.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepo extends JpaRepository<Producto, Integer>, JpaSpecificationExecutor<Producto> {

    Page<Producto> findByFechaEliminacionIsNull(Pageable pageable);

    Optional<Producto> findByIdProductoAndFechaEliminacionIsNull(Integer idProducto);
}
