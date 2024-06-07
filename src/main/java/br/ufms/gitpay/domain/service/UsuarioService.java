package br.ufms.gitpay.domain.service;

import br.ufms.gitpay.data.repository.UsuarioRepository;
import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;

import java.util.concurrent.CompletableFuture;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    CompletableFuture<Usuario<? extends Pessoa>> atualizarDados(Usuario<? extends Pessoa> usuario) {
        return usuarioRepository.save(usuario);
    }
}
