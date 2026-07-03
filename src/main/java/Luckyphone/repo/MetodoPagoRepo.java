package Luckyphone.repo;

import Luckyphone.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetodoPagoRepo extends JpaRepository<MetodoPago, Integer> {

    List<MetodoPago> findByFechaEliminacionIsNull();

    Optional<MetodoPago> findByIdMetodoPagoAndFechaEliminacionIsNull(Integer idMetodoPago);
}
