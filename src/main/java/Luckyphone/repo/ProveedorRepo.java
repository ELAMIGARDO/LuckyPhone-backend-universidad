package Luckyphone.repo;

import Luckyphone.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepo extends JpaRepository<Proveedor, Integer> {

    Page<Proveedor> findByFechaEliminacionIsNull(Pageable pageable);

    Optional<Proveedor> findByIdProveedorAndFechaEliminacionIsNull(Integer idProveedor);
}
