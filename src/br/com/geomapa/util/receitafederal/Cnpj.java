/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.util.receitafederal;

/**
 *
 * @author paulocanedo
 */
public class Cnpj {

    /**
     * Atributo para guardar o valor do CNPJ.
     */
    private String cnpj;

    /**
     * Cria uma nova instância de Cnpj.
     * <BR><BR>
     * <i>Exemplos: new Cnpj("12.345.678/0001-23"); new Cnpj("12345678000123");</i>
     * @param cnpj String com o valor do CNPJ a ser validado.
     * @throws MalFormatadoException 
     */
    public Cnpj(String cnpj) throws PessoaRFException {
        if (cnpj.length() == 18) {
            cnpj = retiraSimbolos(cnpj);
        }

        if (isValid(cnpj)) {
            this.cnpj = cnpj;
        } else {
            throw new PessoaRFException(String.format("O CNPJ %s é inválido!", cnpj));
        }
    }

    /**
     * Pega o valor do CNPJ contido no objeto.
     * @return uma String contendo o valor do CNPJ sem nenhum separador. Exemplo: 12345678000190
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * Método auxiliar para remoção de símbolos separadores do CNPJ.
     * @param cnpj String CNPJ que vai ser transformada em uma String contendo apenas os dígitos do CNPJ.
     * @return Uma String do CNPJ contendo o mesmo sem nenhum símbolo separador. Exemplo: 12345678000190
     */
    public static String retiraSimbolos(String cnpj) {
        cnpj = cnpj.replace(".", "");
        cnpj = cnpj.replace("/", "");
        cnpj = cnpj.replace("-", "");
        return cnpj;
    }

    public static String format(String cnpj) {
        StringBuilder sb = new StringBuilder(retiraSimbolos(cnpj));
        sb.insert(2, ".");
        sb.insert(6, ".");
        sb.insert(10, "/");
        sb.insert(15, "-");
        return sb.toString();
    }

    /**
     * Verifica se o CNPJ é válido.
     * @param strCnpj String com o valor do CPF a ser validado.
     * @return Verdadeiro se o CNPJ é válido, caso contrário retorna falso.
     */
    public static boolean isValid(String strCnpj) throws PessoaRFException {
        if (strCnpj.length() == 18) {
            strCnpj = retiraSimbolos(strCnpj);
        }

        if (strCnpj.length() != 14) {
            throw new PessoaRFException(String.format("O cnpj %s não possui os 14 dígitos necessários", strCnpj));
        }

        int soma = 0, digito;
        String cnpjCalc = strCnpj.substring(0, 12);

        char[] cnpjChar = strCnpj.toCharArray();

        for (int i = 0; i < 4; i++) {
            if (cnpjChar[i] - 48 >= 0 && cnpjChar[i] - 48 <= 9) {
                soma += (cnpjChar[i] - 48) * (6 - (i + 1));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (cnpjChar[i + 4] - 48 >= 0 && cnpjChar[i + 4] - 48 <= 9) {
                soma += (cnpjChar[i + 4] - 48) * (10 - (i + 1));
            }
        }
        digito = 11 - (soma % 11);

        cnpjCalc += (digito == 10 || digito == 11) ? "0" : Integer.toString(digito);

        soma = 0;
        for (int i = 0; i < 5; i++) {
            if (cnpjChar[i] - 48 >= 0 && cnpjChar[i] - 48 <= 9) {
                soma += (cnpjChar[i] - 48) * (7 - (i + 1));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (cnpjChar[i + 5] - 48 >= 0 && cnpjChar[i + 5] - 48 <= 9) {
                soma += (cnpjChar[i + 5] - 48) * (10 - (i + 1));
            }
        }
        digito = 11 - (soma % 11);
        cnpjCalc += (digito == 10 || digito == 11) ? "0" : Integer.toString(digito);

        boolean equals = strCnpj.equals(cnpjCalc);
        if (equals) {
            return true;
        } else {
            throw new PessoaRFException(String.format("O cnpj %s não é válido de acordo com as regras da receita federal", strCnpj));
        }
    }

    /**
     * Método toString sobreescrito para fornecer a impressão do CNPJ de uma forma mais amigável.
     * @return Uma String do CNPJ com símbolos de separação. Exemplo: 12.345.678/0001-90
     */
    @Override
    public String toString() {
        return format(cnpj);
    }
}
