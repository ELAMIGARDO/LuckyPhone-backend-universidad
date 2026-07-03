package Luckyphone.service;

import Luckyphone.entity.CategoriaProducto;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.repo.CategoriaProductoRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CategoriaProductoService {

    private final CategoriaProductoRepo repo;

    public CategoriaProductoService(CategoriaProductoRepo repo) {
        this.repo = repo;
    }

    public Page<CategoriaProducto> listar(Pageable pageable) {
        return repo.findByFechaEliminacionIsNull(pageable);
    }

    public Optional<CategoriaProducto> obtenerPorId(Integer id) {
        return repo.findByIdCategoriaAndFechaEliminacionIsNull(id);
    }

    public CategoriaProducto guardar(CategoriaProducto categoria) {
        return repo.save(categoria);
    }

    public void eliminar(Integer id) {
        CategoriaProducto categoria = repo.findByIdCategoriaAndFechaEliminacionIsNull(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
        categoria.setFechaEliminacion(LocalDateTime.now());
        categoria.setEstado(false);
        repo.save(categoria);
    }
}
