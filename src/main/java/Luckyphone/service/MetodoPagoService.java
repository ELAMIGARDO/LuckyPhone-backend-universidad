package Luckyphone.service;

import Luckyphone.entity.MetodoPago;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.repo.MetodoPagoRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoService {

    private final MetodoPagoRepo repo;

    public MetodoPagoService(MetodoPagoRepo repo) {
        this.repo = repo;
    }

    public List<MetodoPago> listar() {
        return repo.findByFechaEliminacionIsNull();
    }

    public Optional<MetodoPago> obtenerPorId(Integer id) {
        return repo.findByIdMetodoPagoAndFechaEliminacionIsNull(id);
    }

    public MetodoPago guardar(MetodoPago metodoPago) {
        return repo.save(metodoPago);
    }

    public void eliminar(Integer id) {
        MetodoPago metodoPago = repo.findByIdMetodoPagoAndFechaEliminacionIsNull(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado: " + id));
        metodoPago.setFechaEliminacion(LocalDateTime.now());
        metodoPago.setEstado(false);
        repo.save(metodoPago);
    }
}
