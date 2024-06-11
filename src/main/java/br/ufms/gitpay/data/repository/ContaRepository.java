package br.ufms.gitpay.data.repository;

import br.ufms.gitpay.domain.model.conta.ContaBancaria;
import br.ufms.gitpay.domain.model.conta.DadosConta;

public interface ContaRepository<C extends ContaBancaria> extends Repository<C, DadosConta> {
}
