/*
 * MalFormatadoException.java
 *
 */
package br.com.geomapa.util.receitafederal;

/**
 * Esta classe é utilizada para o tratamento de exceção quando algum objeto recebe um parâmetro com formato incorreto.
 * @author Paulo Canedo Costa Rodrigues
 */
public class PessoaRFException extends RuntimeException {

    /**
     * Cria uma nova instância de MalFormatadoException, define null como mensagem de exceção.
     */
    public PessoaRFException() {
    }

    /**
     * Cria uma nova instância de MalFormatadoException, define msg como mensagem de exceção.
     * @param msg Mensagem em detalhe.
     */
    public PessoaRFException(String msg) {
        super(msg);
    }

    public PessoaRFException(Throwable cause) {
        super(cause);
    }

    public PessoaRFException(String message, Throwable cause) {
        super(message, cause);
    }

}
