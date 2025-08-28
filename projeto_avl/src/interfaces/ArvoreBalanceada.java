package interfaces;

/**
 * Interface que define operações básicas de uma árvore balanceada.
 *
 * O objetivo desta interface é permitir que diferentes implementações
 * (como AVL, Red‑Black, etc.) sejam utilizadas pela interface gráfica
 * sem conhecer detalhes específicos da estrutura. Os métodos não
 * retornam valores pois a árvore é modificada internamente.
 */
public interface ArvoreBalanceada {

    /**
     * Insere um valor na árvore, preservando as propriedades da
     * estrutura balanceada. Se o valor já existir, ele não é
     * inserido novamente.
     *
     * @param valor valor inteiro a ser inserido
     */
    void inserir(int valor);

    /**
     * Remove um valor da árvore, se estiver presente, preservando as
     * propriedades da estrutura balanceada. Caso o valor não exista,
     * nenhum efeito colateral ocorre.
     *
     * @param valor valor inteiro a ser removido
     */
    void remover(int valor);
}
