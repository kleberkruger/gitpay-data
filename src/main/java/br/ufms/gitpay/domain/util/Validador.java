package br.ufms.gitpay.domain.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

public class Validador {

    public static String validarNomePessoa(String nome) {
        validarNuloTamanho("Nome", nome, 3, 50);
        validarPorExpressaoRegular("Nome", nome, "^[a-zA-ZÀ-ÖØ-öø-ÿ -]+$");

        return nome.trim();
    }

    // FIXME: melhorar esta implementação. [Exemplo: valor '!' pode?]
    public static String validarNomeEmpresa(String nome) {
        validarNuloTamanho("Nome", nome, 1, 50);
        return nome.trim();
    }

    public static String validarRazaoSocial(String razaoSocial) {
        validarRazaoSocial(razaoSocial, true);
        return razaoSocial.trim();
    }

    // FIXME: melhorar esta implementação. [Exemplo: valor '!?:' pode?]
    public static String validarRazaoSocial(String razaoSocial, boolean required) {
        if (!required && (razaoSocial == null || razaoSocial.trim().isEmpty())) return "";

        validarNuloTamanho("Razão Social", razaoSocial, !required ? 0 : 3, 50);
        return razaoSocial.trim();
    }

    public static String validarUsuario(String nome) {
        String usuarioRegex = "^(?!.*([._])\\1)(?!.*\\.$)(?!^\\.)[a-zA-Z0-9_]+(?:[._][a-zA-Z0-9_]+)*_?$";

        validarNuloTamanho("Nome de usuário", nome, 3, 30);
        validarPorExpressaoRegular("Nome de usuário", nome, usuarioRegex);

        return nome.trim();
    }

    // FIXME: melhorar este método
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
        if (data.isAfter(LocalDate.now()) || data.isBefore(LocalDate.now().minusYears(150))) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }
    }

    public static String validarTelefone(String telefone) {
        return validarTelefone(telefone, true);
    }

    public static String validarTelefone(String telefone, boolean required) {
        if (!required && (telefone == null || telefone.trim().isEmpty())) return "";

        boolean isCelular = isCelular(telefone);
        validarNuloTamanho("Telefone", telefone, !isCelular ? 10 : 11);
        validarPorExpressaoRegular("Telefone", telefone, !isCelular ? "^\\d{11}$" : "^\\d{10}$");

        return telefone.trim();
    }

    public static String validarEmail(String email) {
        return validarEmail(email, true);
    }

    public static String validarEmail(String email, boolean required) {
        if (!required && (email == null || email.trim().isEmpty())) return "";

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        validarPorExpressaoRegular("Email", email, emailRegex);

        return email;
    }

    public static void validarSenha(String senha) {
        validarNuloTamanho("Senha", senha, 3, 50);
    }

    public static void validarSenhaNumerica(String senha) {
        validarNuloTamanho("Senha", senha, 3, 50);
        validarSomenteNumeros("Senha", senha);
    }

    public static void validarCPF(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("CPF em formato inválido: " + cpf);
        }
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF em formato inválido: " + cpf);
        }
        int[] digitos = cpf.chars().map(Character::getNumericValue).toArray();

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += digitos[i] * (10 - i);
        }

        int resto = soma % 11;
        int digitoVerificador1 = (resto < 2) ? 0 : 11 - resto;

        if (digitos[9] != digitoVerificador1) {
            throw new IllegalArgumentException("CPF inválido: " + cpf);
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
        if (cnpj == null || !cnpj.matches("\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("CNPJ em formato inválido: " + cnpj);
        }
        cnpj = cnpj.replaceAll("\\D", "");
        if (cnpj.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("CNPJ em formato inválido: " + cnpj);
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
            throw new IllegalArgumentException("CNPJ inválido: " + cnpj);
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

    public static void validarIntervaloNumero(String atributo, double numero, double min, double max) {
        if (numero < min || numero > max) {
            throw new IllegalArgumentException(atributo + " inválido: " + numero);
        }
    }

    public static void validarCodigoBanco(int codigo) {
        validarIntervaloNumero("Código de banco", codigo, 1, 999);
    }

    public static void validarCodigoBanco(String codigo) {
        validarNuloEmBranco("Código de banco", codigo);
        validarSomenteNumeros("Código de banco", codigo, true);
        validarCodigoBanco(Integer.parseInt(codigo));
    }


    public static void validarNuloEmBranco(String atributo, String valor) {
        valor = valor != null ? valor.trim() : "";
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(atributo + " nulo ou em branco");
        }
    }

    public static void validarNuloTamanho(String atributo, String valor, int tamanho) {
        validarNuloTamanho(atributo, valor, tamanho, tamanho);
    }

    public static void validarNuloTamanho(String atributo, String valor, int min, int max) {
        valor = valor != null ? valor.trim() : "";
        if (min > 0 && valor.isEmpty()) {
            throw new IllegalArgumentException(atributo + " nulo ou em branco");
        } else if (valor.length() < min || valor.length() > max) {
            throw new IllegalArgumentException(String.format("%s deve conter %s caracteres", atributo,
                    min < max ? "entre " + min + " e " + max : max));
        }
    }

    public static void validarSomenteNumeros(String atributo, String valor) {
        validarSomenteNumeros(atributo, valor, false);
    }

    public static void validarSomenteNumeros(String atributo, String valor, boolean unsigned) {
        String regex = unsigned ? "^[+-]?\\d+$" : "^\\d+$";
        if (!valor.matches(regex)) {
            throw new IllegalArgumentException(atributo + " inválido: " + valor);
        }
    }

    public static void validarPorExpressaoRegular(String atributo, String valor, String expressao) {
        Objects.requireNonNull(valor, atributo + " nulo");

        if (!valor.matches(expressao)) {
            throw new IllegalArgumentException(atributo + " em formato inválido: " + valor);
        }
    }

    private static boolean isCelular(String numero) {
        return numero.length() > 2 && numero.charAt(2) == '9';
    }
}
