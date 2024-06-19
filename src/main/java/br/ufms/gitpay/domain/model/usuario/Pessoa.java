package br.ufms.gitpay.domain.model.usuario;

import br.ufms.gitpay.domain.util.Validador;

public abstract class Pessoa {

    private String nome;
    private String telefone;
    private String email;

    protected Pessoa(String nome) {
        this.setNome(nome);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = switch (getTipo()) {
            case PESSOA_FISICA -> Validador.validarNomePessoa(nome, true);
            case PESSOA_JURIDICA -> Validador.validarNomeEmpresa(nome);
        };
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        Validador.validarTelefone(telefone);
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Validador.validarEmail(email);
        this.email = email;
    }

    public abstract TipoPessoa getTipo();
}
