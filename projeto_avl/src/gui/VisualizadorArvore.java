package gui;

import interfaces.ArvoreBalanceada;
import model.No;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Classe responsável por exibir graficamente a árvore e registrar
 * eventos de visualização. Utiliza Swing para desenhar os nós e
 * permite que a execução do algoritmo seja controlada passo a passo
 * por meio de um botão “Próximo Passo”.
 */
public class VisualizadorArvore {
    private JFrame frame;
    private JPanel painelDesenho;
    private JTextField campoValor;
    private JButton botaoInserir;
    private JButton botaoRemover;
    private JButton botaoProximo;
    private JTextArea areaLogs;
    private ArvoreBalanceada arvore;
    private CountDownLatch latch;
    private No raizVisualizada;
    private No noDestaque;
    private Color corDestaque;
    private final List<String> logEventos;

    /**
     * Constrói a interface gráfica. Os componentes são inicializados
     * mas a janela não é exibida até que {@link #mostrar()} seja
     * chamado.
     */
    public VisualizadorArvore() {
        logEventos = new ArrayList<>();
        SwingUtilities.invokeLater(() -> {
            criarComponentes();
        });
    }

    /**
     * Define a árvore que receberá eventos dos botões de inserir e
     * remover. Deve ser invocado antes de mostrar a janela.
     *
     * @param arvore árvore balanceada a controlar
     */
    public void setArvore(ArvoreBalanceada arvore) {
        this.arvore = arvore;
    }

    /**
     * Exibe a janela do visualizador. Se já estiver visível, não faz
     * nada.
     */
    public void mostrar() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.setVisible(true);
            }
        });
    }

    // ---------------------- API de visualização ----------------------

    /**
     * Atualiza o desenho da árvore e registra uma mensagem de log.
     * Esta chamada deve ser feita sempre que a estrutura for
     * modificada de forma significativa.
     *
     * @param novaRaiz nova raiz da árvore a ser desenhada
     * @param mensagem mensagem explicativa para o log
     */
    public void desenharPasso(No novaRaiz, String mensagem) {
        // Guarda evento para testes
        logEventos.add("desenharPasso:" + mensagem);
        this.raizVisualizada = novaRaiz;
        // Adiciona mensagem aos logs na thread da GUI
        SwingUtilities.invokeLater(() -> {
            areaLogs.append(mensagem + "\n");
            painelDesenho.repaint();
        });
    }

    /**
     * Destaca um nó específico com uma cor informada. O destaque é
     * visualizado enquanto o nó permanece apontado por esta classe.
     *
     * @param no  nó a ser destacado
     * @param cor cor do destaque
     */
    public void destacarNo(No no, Color cor) {
        logEventos.add("destacarNo:" + (no == null ? "null" : no.valor));
        this.noDestaque = no;
        this.corDestaque = cor;
        SwingUtilities.invokeLater(() -> painelDesenho.repaint());
    }

    /**
     * Registrador de pontos de lógica usados para depuração e
     * verificação. Apenas registra um evento no log de eventos.
     *
     * @param etapa descrição do ponto de lógica
     */
    public void registrarPontoDeLogica(String etapa) {
        logEventos.add("registrarPontoDeLogica:" + etapa);
        // Também registra no log visível para ajudar o aluno
        SwingUtilities.invokeLater(() -> areaLogs.append("[LOG] " + etapa + "\n"));
    }

    /**
     * Pausa a execução até que o usuário clique no botão “Próximo
     * Passo”. Mostra a explicação no log antes de bloquear. Quando o
     * botão for acionado, a execução continua.
     *
     * @param explicacao texto a ser exibido no log durante a pausa
     */
    public void pausar(String explicacao) {
        logEventos.add("pausar:" + explicacao);
        // Exibe a explicação na área de logs
        SwingUtilities.invokeLater(() -> areaLogs.append(explicacao + "\n"));
        // Cria um novo CountDownLatch e habilita o botão
        latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> botaoProximo.setEnabled(true));
        try {
            // Aguarda até que o botão “Próximo Passo” seja pressionado
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Ao sair da pausa, desabilita o botão
        SwingUtilities.invokeLater(() -> botaoProximo.setEnabled(false));
    }

    /**
     * Retorna uma lista imutável de eventos registrados. Este método
     * é utilizado pelos testes para verificar chamadas internas. Os
     * eventos incluem "desenharPasso", "destacarNo", "registrarPontoDeLogica"
     * e "pausar" com informações de contexto.
     *
     * @return lista de eventos registrados
     */
    public List<String> getLogEventos() {
        return new ArrayList<>(logEventos);
    }

    // ------------------ Construção da interface ------------------

    private void criarComponentes() {
        frame = new JFrame("Visualizador de Árvores AVL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Painel de entrada de dados
        JPanel painelEntrada = new JPanel();
        painelEntrada.setLayout(new FlowLayout());
        painelEntrada.add(new JLabel("Valor:"));
        campoValor = new JTextField(10);
        painelEntrada.add(campoValor);
        botaoInserir = new JButton("Inserir");
        botaoRemover = new JButton("Remover");
        botaoProximo = new JButton("Próximo Passo");
        botaoProximo.setEnabled(false);
        painelEntrada.add(botaoInserir);
        painelEntrada.add(botaoRemover);
        painelEntrada.add(botaoProximo);
        frame.add(painelEntrada, BorderLayout.NORTH);

        // Área de desenho
        painelDesenho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharArvore((Graphics2D) g);
            }
        };
        painelDesenho.setBackground(Color.WHITE);
        frame.add(painelDesenho, BorderLayout.CENTER);

        // Área de logs
        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        JScrollPane scrollLogs = new JScrollPane(areaLogs);
        scrollLogs.setPreferredSize(new Dimension(800, 150));
        frame.add(scrollLogs, BorderLayout.SOUTH);

        // Listeners dos botões
        botaoInserir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (arvore == null) return;
                String texto = campoValor.getText().trim();
                try {
                    int valor = Integer.parseInt(texto);
                    // Executa a inserção em uma thread separada
                    new Thread(() -> arvore.inserir(valor)).start();
                } catch (NumberFormatException ex) {
                    areaLogs.append("Valor inválido: " + texto + "\n");
                }
                campoValor.setText("");
            }
        });
        botaoRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (arvore == null) return;
                String texto = campoValor.getText().trim();
                try {
                    int valor = Integer.parseInt(texto);
                    new Thread(() -> arvore.remover(valor)).start();
                } catch (NumberFormatException ex) {
                    areaLogs.append("Valor inválido: " + texto + "\n");
                }
                campoValor.setText("");
            }
        });
        botaoProximo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (latch != null) {
                    latch.countDown();
                }
            }
        });
    }

    /**
     * Desenha a estrutura atual da árvore no painel de desenho.
     * O método utiliza uma abordagem recursiva para posicionar os
     * nós em níveis e espaçamentos proporcionais. O nó
     * destacado, se presente, é desenhado em uma cor distinta.
     */
    private void desenharArvore(Graphics2D g) {
        if (raizVisualizada == null) return;
        int largura = painelDesenho.getWidth();
        int altura = painelDesenho.getHeight();
        desenharNo(g, raizVisualizada, largura / 2, 30, largura / 4);
    }

    /**
     * Desenha um nó e suas sub‑árvores recursivamente.
     *
     * @param g contexto gráfico
     * @param no nó atual
     * @param x coordenada x do nó
     * @param y coordenada y do nó
     * @param offset deslocamento horizontal para posicionar filhos
     */
    private void desenharNo(Graphics2D g, No no, int x, int y, int offset) {
        if (no == null) return;
        // Desenha conexões com filhos
        if (no.esquerda != null) {
            int childX = x - offset;
            int childY = y + 60;
            g.setColor(Color.BLACK);
            g.drawLine(x, y, childX, childY);
            desenharNo(g, no.esquerda, childX, childY, offset / 2);
        }
        if (no.direita != null) {
            int childX = x + offset;
            int childY = y + 60;
            g.setColor(Color.BLACK);
            g.drawLine(x, y, childX, childY);
            desenharNo(g, no.direita, childX, childY, offset / 2);
        }
        // Desenha o nó (círculo)
        int raio = 15;
        int diametro = raio * 2;
        // Verifica se este nó deve ser destacado
        if (no == noDestaque) {
            g.setColor(corDestaque != null ? corDestaque : Color.RED);
            g.fillOval(x - raio, y - raio, diametro, diametro);
            g.setColor(Color.WHITE);
        } else {
            g.setColor(new Color(230, 230, 250));
            g.fillOval(x - raio, y - raio, diametro, diametro);
            g.setColor(Color.BLACK);
        }
        g.drawOval(x - raio, y - raio, diametro, diametro);
        // Desenha o valor
        String texto = String.valueOf(no.valor);
        FontMetrics fm = g.getFontMetrics();
        int textoLargura = fm.stringWidth(texto);
        int textoAltura = fm.getAscent();
        g.drawString(texto, x - textoLargura / 2, y + textoAltura / 2);
    }
}
