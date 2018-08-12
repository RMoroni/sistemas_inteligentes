package sistema;

import ambiente.*;
import arvore.TreeNode;
import arvore.fnComparator;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author tacla
 */
public class Agente implements PontosCardeais {
    /* referência ao ambiente para poder atuar no mesmo*/
    Model model;
    Problema prob;
    Estado estAtu; // guarda o estado atual (posição atual do agente)
    int plan[];
    double custo;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m; 
        
        // Posiciona o agente fisicamente no ambiente na posicao inicial
        model.setPos(8,0);
    }
    
     /**
     * Agente escolhe qual ação será executada em um ciclo de raciocínio. 
     */
    public int deliberar() {
                
        //  contador de acoes
        ct++;
        
        // @todo a cada acao escolher uma acao {N, NE, L, SE, S, SO, O, NO}
        //executarIr(N);
        
        //vai para uma direção aleatório de 0 a 7
        executarIr(new Random().nextInt(8));
        
        return 1; // Se retornar -1, encerra o agente
    }

    /**
    * Atuador: solicita ao agente 'fisico' executar a acao. 
    */
    public int executarIr(int direcao) {
        model.ir(direcao);
        return 1; // deu certo
    }

    /**
     * Simula um sensor que realiza a leitura da posição atual no ambiente e
     * traduz para um par de coordenadas armazenadas em uma instância da classe
     * Estado.
     * @return Estado um objeto que representa a posição atual do agente no labirinto 
     */
    public Estado sensorPosicao() {
        int pos[];
        pos = model.lerPos();
        return new Estado(pos[0], pos[1]);
    }
    
}
