package Luckyphone.security;

import Luckyphone.entity.Usuario;
import Luckyphone.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepo.findByUsuario(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (usuario.getEstado() == null || !usuario.getEstado()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        String roleName = "ROLE_" + usuario.getRol().getNombreRol().toUpperCase();

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsuario(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}