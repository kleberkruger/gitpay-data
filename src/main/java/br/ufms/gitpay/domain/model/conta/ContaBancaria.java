package br.ufms.gitpay.domain.model.conta;

import br.ufms.gitpay.domain.model.usuario.TipoPessoa;

// FIXME: Transformar esta interface em uma classe abstrata com o método getDados já implementado??
public interface ContaBancaria {

    TipoConta getTipoConta();

    String getBanco();

    int getAgencia();

    int getNumero();

    int getDigito();

    String getNomeTitular();

    String getDocTitular();

    DadosConta getDadosConta();

    default TipoPessoa getTipoPessoa() {
        return switch (getDocTitular().length()) {
            case 11 -> TipoPessoa.PESSOA_FISICA;
            case 14 -> TipoPessoa.PESSOA_JURIDICA;
            default -> throw new IllegalArgumentException("Documento inválido. Use somente CPF ou CNPJ");
        };
    }

    default String getAgenciaString() {
        return getDadosConta().agenciaString();
    }

    default String getNumeroDigito() {
        return getDadosConta().numeroDigito();
    }
}
