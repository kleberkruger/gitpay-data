package br.ufms.gitpay.domain.util;

import java.time.LocalDate;

public class Validar {

    private static String validarNuloTamanhoCaracteres(String atributo, String valor, int min, int max,
                                                       String regex, String regexMsgErro, boolean obrigatorio) {
        assert (!obrigatorio || min > 0);

        regexMsgErro = regexMsgErro != null ? regexMsgErro.trim() : "";
        valor = valor != null ? valor.trim() : "";

        if (!obrigatorio && valor.isEmpty()) {
            return valor;
        } else if (min > 0 && valor.isEmpty()) {
            throw new IllegalArgumentException(atributo + " nulo ou em branco");
        } else if (valor.length() < min || valor.length() > max) {
            throw new IllegalArgumentException(String.format("%s deve conter %s caracteres", atributo,
                    min < max ? "entre " + min + " e " + max : max));
        } else if (regex != null && !valor.matches(regex)) {
            throw new IllegalArgumentException(String.format("%s inválido: [%s] %s", atributo, valor, regexMsgErro));
        }

        return valor;
    }

    private static String validarNuloFormato(String atributo, String valor, String regex, String regexMsgErro,
                                             boolean obrigatorio) {
        regexMsgErro = regexMsgErro != null ? regexMsgErro.trim() : "";
        valor = valor != null ? valor.trim() : "";

        if (!obrigatorio && valor.isEmpty()) {
            return valor;
        } else if (regex != null && !valor.matches(regex)) {
            throw new IllegalArgumentException(String.format("%s inválido: [%s] %s", atributo, valor, regexMsgErro));
        }

        return valor;
    }

    public static String nomePessoa(String nome) {
        return nomePessoa(nome, false);
    }

    public static String nomePessoa(String nome, boolean completo) {
        nome = validarNuloTamanhoCaracteres("Nome", nome, 3, 50, "^[a-zA-ZÀ-ÖØ-öø-ÿ -]+$",
                "Informe somente letras", true);
        if (completo && nome.split(" ").length < 2) {
            throw new IllegalArgumentException("Nome incompleto. Informe o sobrenome");
        }
        return nome;
    }

    public static String nomeEmpresa(String nome) {
        return validarNuloTamanhoCaracteres("Nome", nome, 1, 50, null, null, true);
    }

    public static String razaoSocial(String razaoSocial) {
        return razaoSocial(razaoSocial, true);
    }

    public static String razaoSocial(String razaoSocial, boolean obrigatorio) {
        return validarNuloTamanhoCaracteres("Razão social", razaoSocial, 3, 50,
                ".*[\\p{L}].*", "Informe ao menos uma letra", obrigatorio);
    }

    public static String usuario(String usuario) {
        return validarNuloTamanhoCaracteres("Usuário", usuario, 3, 30,
                "^(?!.*([._])\\1)(?!.*\\.$)(?!^\\.)[a-zA-Z0-9_]+(?:[._][a-zA-Z0-9_]+)*_?$", null, true);
    }

    public static String telefone(String telefone) {
        return telefone(telefone, true);
    }

    public static String telefone(String telefone, boolean obrigatorio) {
        return validarNuloFormato("Telefone", telefone, "", null, obrigatorio);
    }

    public static String email(String email) {
        return email(email, true);
    }

    public static String email(String email, boolean obrigatorio) {
        return validarNuloTamanhoCaracteres("Email", email, 3, 30,
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", null, obrigatorio);
    }

    public static String senha(String senha) {
        return senha(senha, false);
    }

    public static String senha(String senha, boolean numerica) {
        return validarNuloTamanhoCaracteres("Senha", senha, 3, 30, "", null, true);
    }

    public static LocalDate dataNascimento(String dataNascimento) {
        String data = validarNuloFormato("Data de nascimento", dataNascimento, "", null, true);
        return dataNascimento(LocalDate.parse(data));
    }

    public static LocalDate dataNascimento(LocalDate dataNascimento) {
        return dataNascimento;
    }

    public static String cpf(String cpf) {
        cpf = validarNuloFormato("CPF", cpf, "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", null, true);
        cpf = cpf.replaceAll("\\D", "");

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

        return cpf;
    }

    public static String cnpj(String cnpj) {
        cnpj = validarNuloFormato("CNPJ", cnpj, "\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", null, true);
        cnpj = cnpj.replaceAll("\\D", "");

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

        return cnpj;
    }

    public static void main(String[] args) {
        try {
            nomePessoa("kleber kruger", true);
            razaoSocial("  k467", true);
            cpf("021.357.301-65");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
