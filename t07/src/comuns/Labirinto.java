
package comuns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import static org.antlr.works.IDE.sc;

/**Labirinto representa um labirinto com paredes. A indexação das posições do
 * labirinto é dada por par ordenado (linha, coluna). A linha inicial é zero e
 * a linha máxima é (maxLin - 1). A coluna inicial é zero e a máxima é 
 * (maxCol - 1).
 *
 * @author Tacla 
 */
public class Labirinto {
    /*Array que representa o labirinto sendo as posições = 1 aquelas que 
      contêm paredes */
    public int parede[][];
    /*Número máximo de colunas do labirinto */
    private final int maxCol;      
    /*Número máximo de linhas do labirinto */
    private final int maxLin;
    /*Array que armazena as frutas presentes no labirinto*/
    public List<Fruta> frutas = new ArrayList<>();
    
    public Labirinto(int maxLinhas, int maxColunas) {
        this.maxCol = maxColunas;
        this.maxLin = maxLinhas;
        parede = new int[maxLin][maxCol];
    }
    
    public int getMaxLin() {
        return this.maxLin;
    }
    public int getMaxCol() {
        return this.maxCol;
    }
    
   /** Constroi parede horizontal da coluna inicial até a final na linha indicada.
    * @param ini: coluna inicial entre 0 e número máximo de colunas - 1
    * @param fim: coluna final (deve ser maior que coluna inicial)
    * @param linha: em qual linha colocar a parede (entre 0 e máx. de linhas - 1)
    */
    public void porParedeHorizontal(int ini, int fim, int linha) {
        if (fim >= ini && ini >= 0 && fim < maxCol && linha >= 0 && linha < maxLin) {
            for (int c = ini; c <= fim; c++) {
                parede[linha][c] = 1;
            }
        }
    }
    /** Constroi parede vertical da linha inicial até a final na coluna indicada.
     * até a final 
    * @param ini: linha inicial entre 0 e  máximo de linhas - 1
    * @param fim: linha final (deve ser maior do que linha inicial)
    * @param coluna: em qual coluna colocar a parede (entre 0 e máx. de colunas - 1)
    */
    public void porParedeVertical(int ini, int fim, int coluna) {
        if (fim >= ini && ini >= 0 && fim < maxLin && coluna >= 0 && coluna < maxCol) {
            for (int l = ini; l <= fim; l++) {
                parede[l][coluna] = 1;
            }
        }
    }
    
    /** Coloca uma fruta por posição, desde que não seja uma parede.
     * @param iteracao: corresponde à iteração que está sendo executada
     * @param frutasHashMap: corresponde às frutas que serão colocadas no labirinto
    */
    public void porFruta(int iteracao, HashMap frutasHashMap) {
        
        String fruta;
        
        //define a partir de qual fruta do HashMap iniciará o
        //preenchimento das posições do labirinto
        int indFrutaHashMap = 1 + (55 * (iteracao-1));
        
        //percorre todas as posições do labirinto
        for (int lin = 0; lin < 9; lin++){
            for(int col = 0; col < 9; col++){
                if(parede[lin][col] != 1){ //se não for parede

                    //pega a próxima fruta do HashMap
                    fruta = (String) frutasHashMap.get(indFrutaHashMap);
                    int energia = Character.getNumericValue(fruta.charAt(fruta.length()-1));
                    String caracteristicas[] = new String[5];
                    caracteristicas = fruta.split(",");

                    //cria a fruta
                    Fruta novaFruta = new Fruta(lin, col);
                    novaFruta.setCaracteristica(caracteristicas);
                    novaFruta.setEnergiaReal(energia);
                    frutas.add(novaFruta);
                    indFrutaHashMap++;

                }
            }
        }                                   
    }
    
    /** Retorna a fruta encontrada na posição.
     * @param linha: corresponde à linha em que está a fruta
     * @param coluna: corresponde à coluna em que está a fruta
     * @return fruta que está sendo procurada
    */
    public Fruta getFruta(int linha, int coluna) {
        for(Fruta fruta : frutas){
            if(fruta.getLinha() == linha && fruta.getColuna() == coluna)
                return fruta;           
        }
        return null;
    }
}
