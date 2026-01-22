package com.tomas.backend.service.usuarios;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerUsuario(Long idUsuario) {
     return usuarioRepository.findById(idUsuario);
    }

    public List<Usuario> listaUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(idUsuario);
    }

    public Usuario actualizarUsuario(Long idUsuario, Usuario usuario) {
        Usuario usuarioExistente = obtenerUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setPassword(usuario.getPassword());

        return usuarioRepository.save(usuarioExistente);
    }


    public Usuario loginUsuario(String email, String password) {

            Optional<Usuario> usuarioOpt = obtenerUsuarioPorEmail(email);

            if (usuarioOpt.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();

            if (!usuario.getPassword().equals(password)) {
                throw new RuntimeException("Contraseña incorrecta");
            }

            return usuario;
    }

    public Usuario registrarUsuario(Usuario usuario) {

        Optional<Usuario> usuarioOpt = obtenerUsuarioPorEmail(usuario.getEmail());

        if (usuarioOpt.isPresent()) {
            throw new RuntimeException("Este Usuario ya existe");
        }

        return crearUsuario(usuario);
    }

}
