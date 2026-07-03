package Luckyphone.repo;

import Luckyphone.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsuario(String usuario);

    Page<Usuario> findByFechaEliminacionIsNull(Pageable pageable);

    Optional<Usuario> findByIdUsuarioAndFechaEliminacionIsNull(Integer idUsuario);
}
