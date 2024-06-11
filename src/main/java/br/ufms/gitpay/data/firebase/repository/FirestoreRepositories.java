package br.ufms.gitpay.data.firebase.repository;

import br.ufms.gitpay.data.repository.*;
import br.ufms.gitpay.domain.model.banco.Banco;
import br.ufms.gitpay.domain.model.conta.ContaBancaria;

import java.util.HashMap;
import java.util.Map;

public class FirestoreRepositories implements Repositories {

    public static final FirestoreRepositories INSTANCE = new FirestoreRepositories();

    private final BancoFirestoreRepository bancoRepository;
    private final UsuarioFirestoreRepository usuarioRepository;
    private final ChavePixFirestoreRepository chavePixRepository;
    private final Map<Integer, ContaRepository<? extends ContaBancaria>> contaRepositoryMap;
    private final TransacaoRepository transacaoRepository;

    public FirestoreRepositories() {
        this.bancoRepository = new BancoFirestoreRepository();
        this.usuarioRepository = new UsuarioFirestoreRepository();
        this.chavePixRepository = new ChavePixFirestoreRepository();
        this.transacaoRepository = new TransacaoFirestoreRepository();
        this.contaRepositoryMap = new HashMap<>();
    }

    @Override
    public BancoRepository getBancoRepository() {
        return bancoRepository;
    }

    @Override
    public UsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }

    @Override
    public ChavePixRepository getChavePixRepository() {
        return chavePixRepository;
    }

    @Override
    public ContaRepository<? extends ContaBancaria> getContaRepository(int numeroConta) {
        return contaRepositoryMap.computeIfAbsent(numeroConta, k -> k == Banco.GitPay.getCodigo() ?
                new ContaGitPayFirestoreRepository() :
                new ContaExternaFirestoreRepository(k));
    }

    @Override
    public TransacaoRepository getTransacaoRepository() {
        return transacaoRepository;
    }
}
