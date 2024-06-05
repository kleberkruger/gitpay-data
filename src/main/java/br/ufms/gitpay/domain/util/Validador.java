package br.ufms.gitpay.domain.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Validador {

    public static void validarNomePessoa(String nome) {
        validarNuloTamanho("Nome", nome, 3, 50);
    }

    public static void validarNomeEmpresa(String nome) {
        validarNuloTamanho("Nome", nome, 1, 50);
    }

    public static void validarRazaoSocial(String razaoSocial) {
        validarNuloTamanho("Razão Social", razaoSocial, 0, 50);
    }

    public static void validarUsuario(String nome) {
        validarNuloTamanho("Nome de usuário", nome, 3, 50);
    }

    public static void validarData(String data) {
        if (data.matches("^\\d{8}$")) {
            data = data.substring(0, 2) + "/" + data.substring(2, 4) + "/" + data.substring(4, 8);
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        try {
            LocalDate.parse(data, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida (dd/MM/yyyy)");
        }
    }

    public static void validarDataNascimento(LocalDate data) {
        if (data.isAfter(LocalDate.now()) || data.isBefore(LocalDate.now().minusYears(200))) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }
    }

    public static void validarTelefone(String telefone) {
        validarNuloTamanho("Telefone", telefone, 10, 11);
    }

    public static void validarEmail(String email) {
        validarNuloTamanho("Email", email, 5, 50);
    }

    public static void validarSenha(String senha) {
        validarNuloTamanho("Senha", senha, 3, 50);
    }

    public static void validarSenhaNumerica(String senha) {
        validarNuloTamanho("Senha", senha, 3, 50);
    }

    public static void validarCPF(String cpf) {
        validarNuloTamanho("CPF", cpf, 11, 11);

        if (cpf == null || !cpf.matches("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("Formato inválido");
        }
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("Formato inválido");
        }
        int[] digitos = cpf.chars().map(Character::getNumericValue).toArray();

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += digitos[i] * (10 - i);
        }

        int resto = soma % 11;
        int digitoVerificador1 = (resto < 2) ? 0 : 11 - resto;

        if (digitos[9] != digitoVerificador1) {
            throw new IllegalArgumentException("CPF inválido");
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += digitos[i] * (11 - i);
        }

        resto = soma % 11;
        int digitoVerificador2 = (resto < 2) ? 0 : 11 - resto;

        if (digitos[10] != digitoVerificador2) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    public static void validarCNPJ(String cnpj) {
        validarNuloTamanho("CNPJ", cnpj, 14, 14);

        if (cnpj == null || !cnpj.matches("\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("Formato inválido");
        }
        cnpj = cnpj.replaceAll("\\D", "");
        if (cnpj.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("Formato inválido");
        }
        int[] digitos = cnpj.chars().map(Character::getNumericValue).toArray();

        int soma = 0;
        int peso = 2;
        for (int i = 11; i >= 0; i--) {
            soma += digitos[i] * peso;
            peso++;
            if (peso == 10)
                peso = 2;
        }

        int digitoVerificador1 = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

        if (digitos[12] != digitoVerificador1) {
            throw new IllegalArgumentException("CNPJ inválido");
        }

        soma = 0;
        peso = 2;
        for (int i = 12; i >= 0; i--) {
            soma += digitos[i] * peso;
            peso++;
            if (peso == 10)
                peso = 2;
        }

        int digitoVerificador2 = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

        if (digitos[13] != digitoVerificador2) {
            throw new IllegalArgumentException("CNPJ inválido");
        }
    }

    public static void validarCodigoBanco(int codigo) {
        if (codigo < 1 || codigo > 999) {
            throw new IllegalArgumentException("Código invalido");
        }
    }

    public static void validarCodigoBanco(String codigo) {
        validarNuloTamanho("Código do banco", codigo, 1, 3);
        validarPorExpressaoRegular("Código do banco", codigo, "\\d{1,3}");
    }

    private static void validarNuloTamanho(String atributo, String valor, int min, int max) {
        valor = valor != null ? valor.trim() : "";
        if (min > 0 && valor.isEmpty()) {
            throw new IllegalArgumentException(atributo + " nulo ou em branco");
        } else if (valor.length() < min || valor.length() > max) {
            throw new IllegalArgumentException(String.format("%s deve conter %s caracteres", atributo,
                    min < max ? "entre " + min + " e " + max : max));
        }
    }

    private static void validarPorExpressaoRegular(String atributo, String valor, String expressao) {
        if (!valor.matches(expressao)) {
            throw new IllegalArgumentException(atributo + " em formato inválido");
        }
    }
}
