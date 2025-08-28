import gui.VisualizadorArvore;
import model.ArvoreAVL;

/**
 * Classe principal responsável por iniciar a aplicação de
 * visualização da árvore AVL. Cria uma instância do
 * VisualizadorArvore e da ArvoreAVL, associa ambos e exibe a GUI.
 * Hello stranger, perdido? por favor, leia o roteiro_avl.md antes de qualquer coisa.
 */
public class Main {
    public static void main(String[] args) {
        // A interface gráfica deve ser criada e manipulada na
        // thread de despacho de eventos do Swing. Entretanto, a
        // inserção e remoção na árvore ocorre em threads
        // separadas para evitar congelar a interface.
        javax.swing.SwingUtilities.invokeLater(() -> {
            VisualizadorArvore visualizador = new VisualizadorArvore();
            ArvoreAVL arvoreAVL = new ArvoreAVL(visualizador);
            visualizador.setArvore(arvoreAVL);
            visualizador.mostrar();
        });
    }
}
