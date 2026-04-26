package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import br.com.davidbuzatto.jsge.math.Vector2;

/**
 *
 * @author gusta
 */
public class Peca {
    
   private Vector2 pos;
   private double tamanho;
   private int valor;
   private Image imagem;
   
   public Peca( double x, double y, double tamanho, int valor, Image imagem) {
       this.pos = new Vector2( x, y);
       this.tamanho = tamanho;
       this.valor = valor;
       this.imagem = imagem;
   }
   
    
   public void desenhar (EngineFrame e, int tamanhoGrade) {
      // e.fillRectangle(pos.x, pos.y, tamanho, tamanho, EngineFrame.RED);
      // e.drawText(String.valueOf(valor), pos.x + 10, pos.y + 10, 20, EngineFrame.WHITE);
        e.drawImage(imagem,new Rectangle(valor % tamanhoGrade * tamanho, valor/ tamanhoGrade * tamanho,tamanho,tamanho),new Rectangle(pos.x,pos.y,tamanho,tamanho),EngineFrame.WHITE);
        //e.drawRectangle(pos.x, pos.y, tamanho, tamanho, EngineFrame.BLACK);
   }
   
   public boolean intercepta (int x, int y) {
       return x >= pos.x && x <= pos.x + tamanho && 
              y >= pos.y && y <= pos.y + tamanho;
   }

    public int getValor() {
        return valor;
    }
   
    public void setPos(double x, double y){
        this.pos.x = x;
        this.pos.y = y;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Image getImagem() {
        return imagem;
    }

    public void setImagem(Image imagem) {
        this.imagem = imagem;
    }
    
    
}
