package model;

/**
 * Implementação básica de uma árvore binária de busca (ABB).
 *
 * Esta classe fornece operações de inserção, busca e remoção sem
 * qualquer mecanismo de balanceamento. Ela serve de base para
 * estruturas como as árvores AVL. Os valores duplicados não são
 * inseridos novamente.
 */
public class ArvoreBinariaBusca {
    /** Nó raiz da árvore. Protegido para permitir acesso pelas
     * subclasses. */
    protected No raiz;

    /**
     * Insere um valor na árvore. Se a árvore estiver vazia, o novo
     * nó torna‑se a raiz. Caso contrário, a inserção é feita
     * recursivamente respeitando a ordenação da ABB.
     *
     * @param valor valor inteiro a ser inserido
     */
    public void inserir(int valor) {
        raiz = inserir(raiz, valor);
    }

    /**
     * Remove um valor da árvore. Se o valor não existir, não faz
     * nada. A altura dos nós é atualizada após a remoção para ser
     * compatível com classes derivadas que dependem desse campo.
     *
     * @param valor valor a ser removido
     */
    public void remover(int valor) {
        raiz = remover(raiz, valor);
    }

    /**
     * Verifica se um valor está presente na árvore.
     *
     * @param valor valor procurado
     * @return {@code true} se existir, {@code false} caso contrário
     */
    public boolean contem(int valor) {
        return buscar(raiz, valor) != null;
    }

    // ---------------- Métodos auxiliares ------------------

    /**
     * Insere recursivamente um valor na sub‑árvore enraizada em
     * {@code noAtual} e retorna a nova sub‑árvore. Atualiza a altura
     * de cada nó no caminho.
     */
    private No inserir(No noAtual, int valor) {
        if (noAtual == null) {
            return new No(valor);
        }
        if (valor < noAtual.valor) {
            noAtual.esquerda = inserir(noAtual.esquerda, valor);
        } else if (valor > noAtual.valor) {
            noAtual.direita = inserir(noAtual.direita, valor);
        } else {
            // valor duplicado; não insere novamente
            return noAtual;
        }
        // Atualiza altura considerando alturas dos filhos
        noAtual.altura = 1 + Math.max(altura(noAtual.esquerda), altura(noAtual.direita));
        return noAtual;
    }

    /**
     * Remove recursivamente um valor na sub‑árvore enraizada em
     * {@code noAtual} e retorna a nova sub‑árvore. Atualiza a altura
     * conforme necessário.
     */
    private No remover(No noAtual, int valor) {
        if (noAtual == null) {
            return null;
        }
        if (valor < noAtual.valor) {
            noAtual.esquerda = remover(noAtual.esquerda, valor);
        } else if (valor > noAtual.valor) {
            noAtual.direita = remover(noAtual.direita, valor);
        } else {
            // Encontrou o nó a remover
            if (noAtual.esquerda == null) {
                return noAtual.direita;
            } else if (noAtual.direita == null) {
                return noAtual.esquerda;
            } else {
                // Dois filhos: substituir pelo menor valor da sub‑árvore direita
                No sucessor = minimo(noAtual.direita);
                noAtual.valor = sucessor.valor;
                noAtual.direita = remover(noAtual.direita, sucessor.valor);
            }
        }
        // Atualiza altura
        noAtual.altura = 1 + Math.max(altura(noAtual.esquerda), altura(noAtual.direita));
        return noAtual;
    }

    /**
     * Busca recursivamente um valor na sub‑árvore enraizada em
     * {@code noAtual}. Retorna o nó correspondente ou {@code null}
     * se não encontrado.
     */
    private No buscar(No noAtual, int valor) {
        if (noAtual == null) return null;
        if (valor == noAtual.valor) return noAtual;
        if (valor < noAtual.valor) return buscar(noAtual.esquerda, valor);
        return buscar(noAtual.direita, valor);
    }

    /**
     * Retorna o nó de menor valor em uma sub‑árvore (extremo esquerdo).
     */
    private No minimo(No noAtual) {
        No corrente = noAtual;
        while (corrente.esquerda != null) {
            corrente = corrente.esquerda;
        }
        return corrente;
    }

    /**
     * Obtém a altura de um nó, considerando nulo como 0.
     */
    protected int altura(No no) {
        return (no == null) ? 0 : no.altura;
    }
}
