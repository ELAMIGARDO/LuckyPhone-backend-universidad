package Luckyphone.service;

import Luckyphone.entity.Proveedor;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.repo.ProveedorRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepo repo;

    public ProveedorService(ProveedorRepo repo) {
        this.repo = repo;
    }

    public Page<Proveedor> listar(Pageable pageable) {
        return repo.findByFechaEliminacionIsNull(pageable);
    }

    public Optional<Proveedor> obtenerPorId(Integer id) {
        return repo.findByIdProveedorAndFechaEliminacionIsNull(id);
    }

    public Proveedor guardar(Proveedor proveedor) {
        return repo.save(proveedor);
    }

    public void eliminar(Integer id) {
        Proveedor proveedor = repo.findByIdProveedorAndFechaEliminacionIsNull(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado: " + id));
        proveedor.setFechaEliminacion(LocalDateTime.now());
        proveedor.setEstado(false);
        repo.save(proveedor);
    }
}
