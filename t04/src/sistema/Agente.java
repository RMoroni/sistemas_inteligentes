package sistema;

import ambiente.*;
import problema.*;
import comuns.*;
import arvore.*;
import static comuns.PontosCardeais.*;

/**
 *
 * @author tacla
 */
public class Agente implements PontosCardeais {
    /* referência ao ambiente para poder atuar no mesmo*/
    Model model;
    Problema prob; // formulacao do problema
    Estado estAtu; // guarda o estado atual (posição atual do agente)
    
    //sequencia de acoes a ser executada pelo agente
    //int plan[] = {N,N,N,N,L,L,L,L,L,NE,NE,L};
    int plan[];
    int plan_length = 20; //tamanho do vetor de plano
    double custo = 0;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        
        //crencas do agente a respeito do labirinto
        prob.criarLabirinto(9, 9);
               
        //crencas do agente: Estado inicial, objetivo e atual
        estAtu = new Estado(8,0);
        prob.defEstIni(8, 0);
        prob.defEstObj(2, 8);
        
        plan = busca_ah1();
    }
    
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar() {
        ct++;
        
        //imprime o que foi pedido
        System.out.println("estado atual: " + estAtu.getString());
        /*System.out.print("acoes possiveis: { ");
        int acoesPossiveis[] = prob.acoesPossiveis(estAtu);
        int i;
        for(i=0; i<8; i++){
            if(acoesPossiveis[i] == 0)
                System.out.print(acao[i] + " ");           
        }
        System.out.println("}");*/
        System.out.println("ct = " + ct + " de " + (plan.length-1) + " acao escolhida = " + acao[plan[ct]]);
        
        //executa o plano de acoes: SOMENTE UMA ACAO POR CHAMADA DESTE METODO
        // Ao final do plano, verifique se o agente atingiu o estado objetivo verificando
        // com o teste de objetivo
        executarIr(plan[ct]);
        
        //imprime o que foi pedido                          
        //System.out.println("custo ate o momento: " + custo);
        //calcula custo acumulado após executar a ação
        //custo += prob.obterCustoAcao(estAtu, plan[ct], prob.suc(estAtu, plan[ct]));
        System.out.println();
        
        if(ct == plan_length-1)
            return -1;
        else
            return 1;
    }
    
    /**Funciona como um driver ou um atuador: envia o comando para
     * agente físico ou simulado (no nosso caso, simulado)
     * @param direcao N NE S SE ...
     * @return 1 se ok ou -1 se falha
     */
    public int executarIr(int direcao) {
        model.ir(direcao);
        return 1; 
    }   
    
    // Sensor
    public Estado sensorPosicao() {
        int pos[];
        pos = model.lerPos();
        return new Estado(pos[0], pos[1]);
    }
}    