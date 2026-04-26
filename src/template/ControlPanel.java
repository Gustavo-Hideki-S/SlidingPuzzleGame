package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiSlider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author gusta
 */
public class ControlPanel extends EngineFrame {

    private Main puzzle;
    private GuiButton btnResolver;
    private GuiButton btnEmbaralhar;
    private GuiButton btnImagem;
    private List<GuiComponent> components;
    private GuiSlider barraVel;
    private JFileChooser arquivo;
    private Image referencia;

    public ControlPanel(Main puzzle) {
        super(
                300,
                600,
                "Painel de Controle",
                60,
                true
        );
        this.puzzle = puzzle;
        setLocation(70, 60);

    }

    @Override
    public void create() {
        btnEmbaralhar = new GuiButton(40, 20, 220, 40, "Emabralhar", this);
        btnResolver = new GuiButton(40, 80, 220, 40, "Resolver", this);
        btnImagem = new GuiButton(40, 240, 220, 40, "Trocar Imagem", this);
        barraVel = new GuiSlider(40, 160, 220, 40, 0.2, 0.2, 0.01, this);

        arquivo = new JFileChooser();
        arquivo.setFileFilter(new FileNameExtensionFilter("Imagens", "jpg", "png", "jpeg"));

        components = new ArrayList<>();

        components.add(btnResolver);
        components.add(btnEmbaralhar);
        components.add(barraVel);
        components.add(btnImagem);

    }

    @Override
    public void update(double delta) {

        for (GuiComponent c : components) {
            c.update(delta);
        }

        if (btnResolver.isMousePressed()) {
            puzzle.resolver();
        }
        if (btnEmbaralhar.isMousePressed()) {
            puzzle.embaralhar();
        }
        if (barraVel.isMouseDown()) {
            puzzle.setINTERVALO_ANIMACAO(barraVel.getValue());
        }
        if (btnImagem.isMousePressed()) {
            int resultado = arquivo.showOpenDialog(null);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File selectedFile = arquivo.getSelectedFile();
                puzzle.trocarImagem(selectedFile.getAbsolutePath());
            }
        }
        if (referencia == null || referencia != puzzle.getImagemPeca()) {
            // Redimensionamos a imagem da Main para o tamanho do quadrado do painel (220x220)
            referencia = puzzle.getImagemPeca().copy().resize(220, 220);
        }
    }

    @Override
    public void draw() {
        for (GuiComponent c : components) {
            c.draw();
        }
        setFontSize(16);
        drawText("Velocidade: " + String.format("%.2f", barraVel.getValue()), 40, 140, BLACK);
        drawRectangle(40, 320, 220, 220, BLACK);
        if (referencia != null) {
            drawImage(referencia, 40, 320);
        }
    }
}
