package Luckyphone.repo;

import Luckyphone.entity.CategoriaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaProductoRepo extends JpaRepository<CategoriaProducto, Integer> {

    Page<CategoriaProducto> findByFechaEliminacionIsNull(Pageable pageable);

    Optional<CategoriaProducto> findByIdCategoriaAndFechaEliminacionIsNull(Integer idCategoria);
}
