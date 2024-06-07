package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.domain.model.conta.*;

import java.util.*;

public abstract class ContaFirestoreRepository<C extends ContaBancaria> extends FirestoreRepository<C, DadosConta> {

    protected ContaFirestoreRepository(int banco) {
        super("bancos/" + String.format("%03d", banco) + "/contas");
    }

    @Override
    protected Optional<String> getId(C conta) {
        return Optional.of(conta.getNumero() + "-" + conta.getTipo().getAbreviacao());
    }

    @Override
    protected String idToStr(DadosConta dadosConta) {
        return dadosConta.numero() + "-" + dadosConta.tipo().getAbreviacao();
    }

    @Override
    protected Map<String, Object> entityToMap(C conta) {
        Map<String, Object> data = new HashMap<>();
        data.put("tipo", conta.getTipo());
        data.put("banco", conta.getBanco());
        data.put("agencia", conta.getAgencia());
        data.put("numero", conta.getNumero());
        data.put("digito", conta.getDigito());
        return data;
    }
}
