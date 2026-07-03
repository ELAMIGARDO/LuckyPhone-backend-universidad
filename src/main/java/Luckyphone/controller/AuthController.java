package Luckyphone.controller;

import Luckyphone.dto.LoginRequest;
import Luckyphone.dto.LoginResponse;
import Luckyphone.entity.Usuario;
import Luckyphone.repo.UsuarioRepo;
import Luckyphone.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepo usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepo usuarioRepo,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepo.findByUsuario(request.getUsuario())
                .orElse(null);

        if (usuario == null || usuario.getEstado() == null || !usuario.getEstado()) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String role = usuario.getRol().getNombreRol().toUpperCase();
        String token = jwtUtil.generateToken(usuario.getUsuario(), role);

        return ResponseEntity.ok(new LoginResponse(token, role, usuario.getNombre()));
    }
}
