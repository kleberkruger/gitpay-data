package br.ufms.gitpay.data.repository;

import br.ufms.gitpay.domain.model.usuario.Pessoa;
import br.ufms.gitpay.domain.model.usuario.Usuario;

public interface UsuarioRepository extends Repository<Usuario<? extends Pessoa>, String> {
}
