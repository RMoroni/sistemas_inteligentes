
package comuns;
/**Fruta representa a fruta colocada em uma das posições do labirinto.
 *
 * @author Fer
 */
public class Fruta {
    
    //Array que armazena as características da fruta
    public String caracteristica[] = new String[5];
    //Armazena a energia real da fruta
    public int energiaReal;
    //Armazena a linha da posição da fruta no labirinto
    public int linha;
    //Armazena a coluna da posição da fruta no labirinto
    public int coluna;

    public Fruta(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }
    
    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public int getEnergiaReal() {
        return energiaReal;
    }
    
    public void setLinha(int linha) {
        this.linha = linha;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    public void setCaracteristica(String[] caracteristica) {
        this.caracteristica = caracteristica;
    }

    public void setEnergiaReal(int energiaReal) {
        this.energiaReal = energiaReal;
    }
    
    
}
