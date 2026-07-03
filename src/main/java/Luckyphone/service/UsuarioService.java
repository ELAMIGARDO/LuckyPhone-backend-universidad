package Luckyphone.service;

import Luckyphone.entity.Usuario;
import Luckyphone.exception.RecursoNoEncontradoException;
import Luckyphone.repo.UsuarioRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepo repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepo repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Usuario> listar(Pageable pageable) {
        return repo.findByFechaEliminacionIsNull(pageable);
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        return repo.findByIdUsuarioAndFechaEliminacionIsNull(id);
    }

    public Usuario guardar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return repo.save(usuario);
    }

    public Usuario actualizar(Integer id, Usuario usuario) {
        Usuario existente = repo.findByIdUsuarioAndFechaEliminacionIsNull(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
        existente.setNombre(usuario.getNombre());
        existente.setUsuario(usuario.getUsuario());
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        existente.setEstado(usuario.getEstado());
        existente.setRol(usuario.getRol());
        return repo.save(existente);
    }

    public void eliminar(Integer id) {
        Usuario usuario = repo.findByIdUsuarioAndFechaEliminacionIsNull(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
        usuario.setFechaEliminacion(LocalDateTime.now());
        usuario.setEstado(false);
        repo.save(usuario);
    }
}
