
# Projeto AVL: Animador AVL

**Unidade Curricular: Algoritmos e Estrutura de Dados: Árvores**

**Professor:** João Vitor da Costa Andrade

## 1\. Introdução

As **árvores AVL** são árvores de busca binária auto-balanceadas. O nome é uma homenagem aos inventores Georgy Adelson-Velsky e Evgenii Landis, que propuseram essa estrutura em 1962. Em uma árvore AVL a altura das duas sub-árvores de qualquer nó difere em **no máximo um**. Sempre que a diferença (denominada *fator de balanceamento*) ultrapassa esse limite, realiza-se uma **rotação** para restabelecer o equilíbrio. Essa propriedade garante complexidade de tempo $O(\\log n)$ para operações de busca, inserção e remoção. Comparativamente, AVL e árvores rubro-negras oferecem tempos assintoticamente iguais, mas as AVL costumam ser mais estritas, o que as torna interessantes para aplicações em que a frequência de consultas supera a de atualizações.

O propósito deste trabalho é conduzir você na **implementação das operações de inserção e balanceamento** de uma árvore AVL e, sobretudo, integrar sua implementação a uma **interface gráfica** capaz de *animar passo a passo* cada etapa do algoritmo. A experiência de enxergar a árvore sendo construída e reequilibrada em tempo real reforça a internalização dos conceitos e evidencia a importância das rotações.

## 2\. Conhecendo o Framework

O projeto base fornece uma aplicação gráfica em Java/Swing que desenha a árvore em um painel. Essa interface de visualização comunica-se com a estrutura de dados por meio de uma **API de visualização**. A classe `VisualizadorArvore` cuida de todos os detalhes de desenho e interação; seu papel será chamá-la adequadamente a partir de sua implementação da árvore AVL. Apresentamos a seguir os métodos mais importantes dessa API:

- `visualizador.desenharPasso(No raiz, String mensagem)`: atualiza o desenho da árvore com a raiz informada e registra uma mensagem na área de logs. Use-o após cada alteração estrutural relevante (por exemplo, após uma inserção ou rotação) para que o usuário visualize o estado atual da árvore.
- `visualizador.destacarNo(No no, Color cor)`: destaca o nó fornecido com a cor especificada. Isso é útil para chamar a atenção para o *pivô* de uma rotação ou para o nó recém-inserido.
- `visualizador.registrarPontoDeLogica(String etapa)`: este método, descrito como essencial para depuração, **deve ser chamado imediatamente antes de calcular o fator de balanceamento de qualquer nó**. Passe uma mensagem contextual como `'Verificando FB do nó X'`. Essa chamada é obrigatória e será verificada durante a avaliação.
- `visualizador.pausar(String explicacao)`: bloqueia a execução do algoritmo até que o usuário pressione o botão “Próximo Passo”. Use-o antes e depois de cada rotação ou de operações complexas para permitir que o observador acompanhe a transformação. O parâmetro `explicacao` aparecerá nos logs e deve descrever brevemente o que está prestes a acontecer (por exemplo, “Realizando rotação simples à direita”).

A aplicação gráfica também exibe campos para inserir valores e botões para interação (“Inserir”, “Remover” e “Próximo Passo”). O seu código da AVL deve rodar em uma *thread* separada da interface para não congelar a GUI; as chamadas de pausa já tratam dessa sincronização internamente. Familiarize-se com a classe `VisualizadorArvore.java` para compreender onde esses métodos são definidos, mas concentre-se em usá-los nos pontos corretos do seu algoritmo.

## 3\. Roteiro de Trabalho

O trabalho está estruturado em etapas progressivas. Conclua cada uma cuidadosamente antes de prosseguir à próxima.

### 3.1 Etapa 1 – Métodos Auxiliares

Comece completando os métodos de apoio em `ArvoreAVL.java`.

1.  **Altura de um nó (`altura(No pontoAtual)`)** – Retorne a altura da sub-árvore enraizada em `pontoAtual`. Por convenção, a altura de um nó `null` é `0`. Este método é fundamental para computar o fator de balanceamento e atualizar o atributo `altura` dos nós.
2.  **Fator de balanceamento (`fatorBalanceamento(No pontoAtual)`)** – O fator de balanceamento é a diferença entre a altura da sub-árvore **direita** e a altura da sub-árvore **esquerda**. Assim, para um nó `X`, temos $FB(X) = altura(X.direita) - altura(X.esquerda)$. Chame `visualizador.registrarPontoDeLogica()` imediatamente antes de calcular e retornar esse valor, passando uma mensagem informativa como indicado acima.

Uma implementação correta desses métodos lhe permitirá identificar quando uma sub-árvore está desbalanceada (quando `FB` é `+2` ou `−2`) e decidir qual rotação aplicar.

### 3.2 Etapa 2 – Rotações

As rotações são operações locais que reestruturam a árvore para restaurar o balanceamento. As quatro situações de desbalanceamento e suas correspondentes rotações são:

| Caso de desbalanceamento | Situação | Rotação necessária |
| :----------------------- | :---------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------- |
| **Esquerda-Esquerda (LL)** | O nó desbalanceado é *esquerda-pesado* (`FB = −2`) e o filho esquerdo também é *esquerda-pesado* (`FB filho ≤ 0`).     | Rotação simples à **direita** |
| **Direita-Direita (RR)** | O nó desbalanceado é *direita-pesado* (`FB = +2`) e o filho direito também é *direita-pesado* (`FB filho ≥ 0`).       | Rotação simples à **esquerda** |
| **Esquerda-Direita (LR)** | O nó desbalanceado é *esquerda-pesado* (`FB = −2`) mas o filho esquerdo é *direita-pesado* (`FB filho > 0`).       | Rotação **dupla à direita** (rotação simples à esquerda no filho esquerdo seguida de rotação simples à direita no pivo) |
| **Direita-Esquerda (RL)** | O nó desbalanceado é *direita-pesado* (`FB = +2`) mas o filho direito é *esquerda-pesado* (`FB filho < 0`).       | Rotação **dupla à esquerda** (rotação simples à direita no filho direito seguida de rotação simples à esquerda no pivo)  |

Conceitualmente, uma **rotação simples à direita** move o filho esquerdo para a posição de raiz dessa sub-árvore e desloca a raiz original para a direita. Abaixo está um diagrama em ASCII ilustrando essa rotação:

```
      y                         x
     / \                       / \
    x   C       =>            A   y
   / \                           / \
  A   B                         B   C
```

Em `ArvoreAVL.java`, preencha os esqueletos de `rotacaoDireita(No pivoDesbalanceado)` e `rotacaoEsquerda(No pivoDesbalanceado)`, utilizando nomes de variáveis descritivos e atualizando as alturas dos nós envolvidos.

### 3.3 Etapa 3 – Inserção com Balanceamento

Sobrescreva o método `inserir(int valor)` para que, após a inserção padrão de ABB, ele verifique o fator de balanceamento de cada nó no caminho de volta da recursão e aplique as rotações necessárias quando um desbalanceamento (`FB = +2` ou `−2`) for detectado.

### 3.4 Exemplo Prático: Juntando o Código e a Visualização

Para ilustrar como seu código e as chamadas da API devem interagir, vamos simular a inserção de uma sequência que causa um desbalanceamento **Esquerda-Direita (LR)**: `30`, `10`, e finalmente `20`.

**Estado Inicial:** Após inserir `30` e `10`, a árvore está assim:

```
  30
 /
10
```

**Ação:** O usuário clica para **inserir o valor 20**.

1.  **Inserção Padrão:** Seu algoritmo desce a árvore e insere `20` como filho direito de `10`.
2.  **Primeira Visualização:** Imediatamente após a inserção, você deve mostrar o novo estado.
    ```java
    // ... lógica de inserção recursiva ...
    // Após adicionar o nó 20 na árvore:
    visualizador.desenharPasso(raiz, "Nó 20 inserido na árvore.");
    ```
3.  **Subindo e Verificando (Nó 10):** A recursão começa a "voltar". Ao chegar no nó `10`, você deve verificar seu balanceamento.
    ```java
    // (Ainda dentro da função de inserção recursiva, agora retornando para o nó 10)
    visualizador.registrarPontoDeLogica("Verificando FB do nó 10");
    // int fb = fatorBalanceamento(no10);
    // Seu cálculo mostra que o FB de 10 é +1 (direita-pesado, mas balanceado). Nada a fazer.
    ```
4.  **Subindo e Verificando (Nó 30):** A recursão continua subindo e chega na raiz, o nó `30`.
    ```java
    // (Retornando para o nó 30)
    visualizador.registrarPontoDeLogica("Verificando FB do nó 30");
    // int fb = fatorBalanceamento(no30);
    // Seu cálculo mostra que o FB de 30 é -2. DESBALANCEADO!
    ```
5.  **Identificando o Caso e Preparando a Rotação:** Você detecta que o pivô `30` está desbalanceado (`FB = -2`) e seu filho esquerdo, `10`, está direita-pesado (`FB = +1`). Este é o caso **Esquerda-Direita (LR)**.
    ```java
    // Agora, dentro do seu método de balanceamento...
    visualizador.destacarNo(no30, Color.ORANGE); // Destaca o pivô do problema.
    visualizador.pausar("Pivô 30 desbalanceado (FB=-2). Caso LR. Iniciando rotação dupla...");
    ```
6.  **Executando a Rotação Dupla:** Seu código agora realiza as duas rotações. É aqui que o **comentário obrigatório** deve aparecer\!
    ```java
    // Dentro do `if` que trata o caso LR:
    // Verificação: O filho direito era o nó de valor 20.  <-- COMENTÁRIO OBRIGATÓRIO AQUI!
    pivoDesbalanceado.esquerda = rotacaoEsquerda(pivoDesbalanceado.esquerda); // 1ª rotação
    return rotacaoDireita(pivoDesbalanceado); // 2ª rotação
    ```
7.  **Finalizando a Visualização:** Após a conclusão da rotação dupla, a árvore estará balanceada. Você deve mostrar o resultado final.
    ```java
    // Após o método de balanceamento retornar a nova raiz da sub-árvore...
    visualizador.desenharPasso(novaRaiz, "Após rotação dupla (LR) no pivô 30.");
    visualizador.pausar("Balanceamento concluído.");
    ```

Este fluxo garante que o usuário veja cada passo lógico: a inserção, a detecção do desbalanceamento e a correção através da rotação, cumprindo todos os objetivos do projeto.

### 3.5 Checklist de Visualização (OBRIGATÓRIO)

Para que a visualização cumpra seu papel, **é obrigatório** que seu código integre as operações com a API do visualizador da seguinte forma:

1.  **Antes de calcular qualquer fator de balanceamento** de um nó `N`, chame
    `visualizador.registrarPontoDeLogica("Verificando FB do nó " + N.valor);`
2.  **Antes de iniciar uma rotação**:
    a) **Destaque o pivô** com `visualizador.destacarNo(pivo, Color.ORANGE)`.
    b) **Pause a execução** com `visualizador.pausar("Iniciando Rotação [TIPO] no nó " + pivo.valor);`.
3.  **Após concluir uma rotação**:
    a) **Atualize o desenho** com `visualizador.desenharPasso(raiz, "Rotação [TIPO] concluída.");`
    b) **Pause novamente** para que o resultado possa ser analisado com `visualizador.pausar("Estrutura rebalanceada.");`.

> Observação: Um ponto crítico para avaliação consiste em **demonstrar que você compreendeu a rotação dupla esquerda-direita**. No exato local do seu código onde você trata o caso **Dupla (Esquerda-Direita)**, adicione um comentário de **uma linha** respondendo à pergunta: *“Qual o valor do nó que era o filho direito do nó desbalanceado antes da primeira rotação?”*. Use o formato exato abaixo, substituindo **X** pelo valor correto:
>
> ```java
> // Verificação: O filho direito era o nó de valor X.
> ```

## 4\. Considerações Finais

- **Encapsulamento:** Respeite a organização proposta: classes de modelo no pacote `model`, interface gráfica no pacote `gui`, interfaces em `interfaces`.
- **Comentários:** Mantenha seu código claro e comentado, justificando suas escolhas e explicando cada etapa de maneira concisa e objetiva.
- **Uso da API de visualização:** As chamadas a `visualizador.registrarPontoDeLogica()` e ao comentário dirigido têm finalidades puramente pedagógicas. Elas foram projetadas para ajudá-lo a refletir sobre a lógica do algoritmo em pontos-chave e a facilitar a depuração. A correta utilização de ambas é parte essencial da avaliação.