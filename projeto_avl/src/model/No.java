package model;

/**
 * Classe que representa um nó de uma árvore binária.
 *
 * Cada nó armazena um valor inteiro e referências para seus filhos
 * esquerdo e direito, além de um campo de altura utilizado pelas
 * estruturas balanceadas. A altura de um nó isolado é 1; a altura
 * de um nó nulo é considerada 0.
 */
public class No {
    public int valor;
    public No esquerda;
    public No direita;
    public int altura;

    /**
     * Cria um novo nó com o valor especificado. Inicialmente os
     * campos esquerda e direita são nulos e a altura é 1 (folha).
     *
     * @param valor valor inteiro a ser armazenado no nó
     */
    public No(int valor) {
        this.valor = valor;
        this.altura = 1;
    }
}
