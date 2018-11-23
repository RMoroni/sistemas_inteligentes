
package comuns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    public Fruta frutas[];
    
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
    */
    public void porFruta() {
        
        String line = "";
        try {
            //faz a leitura do arquivo
            BufferedReader br_file = new BufferedReader(new FileReader ("frutasLabirinto.txt"));
            
            int indFruta = 0;
            //percorre todas as posições do labirinto
            for (int lin = 0; lin < 9; lin++){
                for(int col = 0; col < 9; col++){
                    if(parede[lin][col] != 1){ //se não for parede
                        
                        //lê a próxima linha do arquivo
                        line = br_file.readLine();
                        int energia = line.charAt(line.length()-1);
                        String caracteristicas[] = new String[5];
                        caracteristicas = line.split(",");
                        
                        //cria a fruta
                        frutas[indFruta] = new Fruta(lin, col);
                        frutas[indFruta].setCaracteristica(caracteristicas);
                        frutas[indFruta].setEnergiaReal(energia);
                        indFruta++;

                    }
                }
            }
                       
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
              
    }
}
