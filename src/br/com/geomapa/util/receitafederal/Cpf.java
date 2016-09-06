/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.receitafederal;

/**
 *
 * @author paulocanedo
 */
public class Cpf {

    /**
     * Atributo para guardar o valor do CPF.
     */
    private String cpf;

    /**
     * Cria uma nova instância de Cpf.
     * <BR><BR>
     * <i>Exemplos: new Cpf("123.456.789-00"); new Cpf("12345678900");</i>
     * @param strCpf String com o valor do CPF a ser validado.
     * @throws PessoaRFException 
     */
    public Cpf(String strCpf) throws PessoaRFException {
        if (strCpf.length() == 14) {
            strCpf = retiraSimbolos(strCpf);
        }

        if (isValid(strCpf)) {
            cpf = strCpf;
        } else {
            throw new PessoaRFException(String.format("O CPF %s é inválido!", strCpf));
        }
    }

    /**
     * Pega o valor do CPF contido no objeto.
     * @return uma String contendo o valor do CPF sem nenhum separador. Exemplo: 12345678900
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Verifica se o CPF é válido.
     * @param strCpf String com o valor do CPF a ser validado.
     * @return Verdadeiro se o CPF é válido, caso contrário retorna falso.
     */
    public static boolean isValid(String strCpf) {
        try {
            strCpf = retiraSimbolos(strCpf);

            if (strCpf.length() != 11) {
                throw new PessoaRFException(String.format("O cpf %s não possui os 11 dígitos necessários", strCpf));
            }

            if (strCpf.equals("00000000000") || strCpf.equals("11111111111") || strCpf.equals("22222222222")
                    || strCpf.equals("33333333333") || strCpf.equals("44444444444") || strCpf.equals("55555555555")
                    || strCpf.equals("66666666666") || strCpf.equals("77777777777") || strCpf.equals("88888888888")
                    || strCpf.equals("99999999999")) {
                throw new PessoaRFException(String.format("O cpf %s é inválido", strCpf));
            }

            int d1 = 0;
            int d2 = 0;
            int digito1 = 0;
            int digito2 = 0;
            int resto = 0;
            int digitoCPF = 0;
            String nDigResult;

            for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
                digitoCPF = Integer.valueOf(strCpf.substring(nCount - 1, nCount)).intValue();

                d1 = d1 + (11 - nCount) * digitoCPF;
                d2 = d2 + (12 - nCount) * digitoCPF;
            }

            resto = (d1 % 11);

            if (resto < 2) {
                digito1 = 0;
            } else {
                digito1 = 11 - resto;
            }

            d2 += 2 * digito1;

            resto = (d2 % 11);

            if (resto < 2) {
                digito2 = 0;
            } else {
                digito2 = 11 - resto;
            }

            String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf.length());

            nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
            boolean equals = nDigVerific.equals(nDigResult);
            if (equals) {
                return true;
            } else {
                throw new PessoaRFException(String.format("O cpf %s não é válido de acordo com as regras da receita federal", strCpf));
            }
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Método auxiliar para remoção de símbolos separadores do CPF.
     * @param cpf String CPF que vai ser transformada em uma String contendo apenas os dígitos do CPF.
     * @return Uma String do CPF contendo o mesmo sem nenhum símbolo separador. Exemplo: 12345678900
     */
    public static String retiraSimbolos(String cpf) {
        cpf = cpf.replace(".", "");
        cpf = cpf.replace("-", "");
        return cpf;
    }

    public static String format(String cpf) {
        StringBuilder sb = new StringBuilder(retiraSimbolos(cpf));
        sb.insert(3, ".");
        sb.insert(7, ".");
        sb.insert(11, "-");
        return sb.toString();
    }

    /**
     * Método toString sobreescrito para fornecer a impressão do CPF de uma forma mais amigável.
     * @return Uma String do CPF com símbolos de separação. Exemplo: 123.456.789-00
     */
    @Override
    public String toString() {
        return format(cpf);
    }
}
