package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.io.File;
import java.util.*;

/**
 *
 * @author gusta
 */
public class Main extends EngineFrame {

    private static final int TAMANHO = 3;
    private Peca[][] grade;
    private double tamanhoPeca;
    private Image imagemPeca;

    private Queue<Vector2> filaAnimacao = new LinkedList<>();
    private double tempoContador = 0;
    private double INTERVALO_ANIMACAO = 0.2;

    private Set<String> visitedStates = new HashSet<>();
    private int[][] estadoObjetivo;

    public Main() {
        super(600, 600, "Puzzle", 60, true);
    }

    @Override
    public void create() {
        tamanhoPeca = getScreenWidth() / TAMANHO;
        imagemPeca = loadImage("resources/images/paisagem.jpg").resize(600, 600);
        grade = new Peca[TAMANHO][TAMANHO];

        estadoObjetivo = new int[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                estadoObjetivo[i][j] = i * TAMANHO + j;
            }
        }

        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (!(i == TAMANHO - 1 && j == TAMANHO - 1)) {
                    grade[i][j] = new Peca(j * tamanhoPeca, i * tamanhoPeca, tamanhoPeca, i * TAMANHO + j, imagemPeca);
                }
            }
        }
    }

    @Override
    public void update(double delta) {
        if (!filaAnimacao.isEmpty()) {
            tempoContador += delta;
            if (tempoContador >= INTERVALO_ANIMACAO) {
                Vector2 mov = filaAnimacao.poll();
                moverPeca((int) mov.y, (int) mov.x);
                tempoContador = 0;
            }
        } else if (isMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            for (int i = 0; i < TAMANHO; i++) {
                for (int j = 0; j < TAMANHO; j++) {
                    if (grade[i][j] != null && grade[i][j].intercepta(getMouseX(), getMouseY())) {
                        moverPeca(i, j);
                    }
                }
            }
        }

        if (isKeyPressed(KEY_ENTER)) {
            filaAnimacao.clear();
            embaralhar();
        }
        if (isKeyPressed(KEY_R)) {
            resolver();
        }
    }

    public void resolver() {
        visitedStates.clear();
        filaAnimacao.clear();
        String estadoInicial = gerarEstadoAtual();

        resolverRecursivo(0);

        resetarGradeParaEstado(estadoInicial);
        System.out.println("Animação de Backtracking Iniciada!");
    }

    private boolean resolverRecursivo(int profundidade) {
        if (estaResolvido()) {
            return true;
        }
        if (profundidade > 15) {
            return false;
        }
        String estado = gerarEstadoAtual();
        if (visitedStates.contains(estado)) {
            return false;
        }
        visitedStates.add(estado);

        for (Vector2 mov : gerarMovimentosPossiveis()) {
            Vector2 vazioAntes = posVazio();

            filaAnimacao.add(new Vector2(mov.x, mov.y));
            moverPeca((int) mov.y, (int) mov.x);

            if (resolverRecursivo(profundidade + 1)) {
                return true;
            }

            filaAnimacao.add(new Vector2(vazioAntes.x, vazioAntes.y));
            moverPeca((int) vazioAntes.y, (int) vazioAntes.x);
        }
        return false;
    }

    private void moverPeca(int lin, int col) {
        Vector2 v = posVazio();
        if (Math.abs((int) v.y - lin) + Math.abs((int) v.x - col) == 1) {
            grade[(int) v.y][(int) v.x] = grade[lin][col];
            grade[lin][col] = null;
            recalcularPosicoes();
        }
    }

    private void recalcularPosicoes() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (grade[i][j] != null) {
                    grade[i][j].setPos(j * tamanhoPeca, i * tamanhoPeca);
                }
            }
        }
    }

    private boolean estaResolvido() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (grade[i][j] != null) {
                    if (grade[i][j].getValor() != estadoObjetivo[i][j]) {
                        return false;
                    }
                } else if (!(i == TAMANHO - 1 && j == TAMANHO - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Vector2 posVazio() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (grade[i][j] == null) {
                    return new Vector2(j, i);
                }
            }
        }
        return new Vector2(TAMANHO - 1, TAMANHO - 1);
    }

    private List<Vector2> gerarMovimentosPossiveis() {
        List<Vector2> movs = new ArrayList<>();
        Vector2 v = posVazio();
        int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] d : dirs) {
            int nl = (int) v.y + d[0], nc = (int) v.x + d[1];
            if (nl >= 0 && nl < TAMANHO && nc >= 0 && nc < TAMANHO) {
                movs.add(new Vector2(nc, nl));
            }
        }
        return movs;
    }

    private String gerarEstadoAtual() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                sb.append(grade[i][j] == null ? "null" : grade[i][j].getValor()).append(",");
            }
        }
        return sb.toString();
    }

    private void resetarGradeParaEstado(String estadoStr) {
        String[] valores = estadoStr.split(",");
        Peca[][] novaGrade = new Peca[TAMANHO][TAMANHO];
        List<Peca> pecas = new ArrayList<>();
        for (Peca[] linha : grade) {
            for (Peca p : linha) {
                if (p != null) {
                    pecas.add(p);
                }
            }
        }
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                String val = valores[i * TAMANHO + j];
                if (!val.equals("null")) {
                    int vInt = Integer.parseInt(val);
                    for (Peca p : pecas) {
                        if (p.getValor() == vInt) {
                            novaGrade[i][j] = p;
                            break;
                        }
                    }
                } else {
                    novaGrade[i][j] = null;
                }
            }
        }
        grade = novaGrade;
        recalcularPosicoes();
    }

    public void embaralhar() {
        int[][] movimentos = {{1, 2}, {1, 1}, {0, 1}, {0, 0}};
        for (int[] m : movimentos) {
            moverPeca(m[0], m[1]);
        }
        filaAnimacao.clear();
    }

    @Override
    public void draw() {
        clearBackground(WHITE);
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (grade[i][j] != null) {
                    grade[i][j].desenhar(this, TAMANHO);
                }
            }
        }
    }
    // getters e setters

    public double getINTERVALO_ANIMACAO() {
        return INTERVALO_ANIMACAO;
    }

    public void setINTERVALO_ANIMACAO(double INTERVALO_ANIMACAO) {
        this.INTERVALO_ANIMACAO = INTERVALO_ANIMACAO;
    }

    public Image getImagemPeca() {
        return imagemPeca;
    }

    public void setImagemPeca(Image imagemPeca) {
        this.imagemPeca = imagemPeca;
    }

    public void trocarImagem(String caminho) {
        try {
            Image novaImg = loadImage(caminho);
            if (novaImg != null) {
                this.imagemPeca = novaImg;
                for (int i = 0; i < TAMANHO; i++) {
                    for (int j = 0; j < TAMANHO; j++) {
                        if (grade[i][j] != null) {
                            grade[i][j].setImagem(novaImg);
                        }
                    }
                }
                System.out.println("Imagem atualizada com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Main puzzle = new Main();
        ControlPanel p = new ControlPanel(puzzle);
    }
}
