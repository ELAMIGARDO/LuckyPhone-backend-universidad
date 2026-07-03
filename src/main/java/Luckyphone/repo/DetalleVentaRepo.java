package Luckyphone.repo;

import Luckyphone.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

    @Repository
public interface DetalleVentaRepo extends JpaRepository<DetalleVenta, Integer> {
    List<DetalleVenta> findByVentaIdVenta(Integer idVenta);
}
