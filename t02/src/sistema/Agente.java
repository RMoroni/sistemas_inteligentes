package sistema;

import ambiente.*;
import problema.*;
import comuns.*;
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
    int plan[] = {N,N,N,N,L,L,L,L,L,L,L,L,N,N}; //sequencia de planos que o agente deve seguir
    double custo;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        
        //@todo T2: Aqui vc deve preencher a formulacao do problema 
        
        //@todo T2: crencas do agente a respeito do labirinto
        prob.criarLabirinto(9, 9);
                      
        //@todo T2: crencas do agente: Estado inicial, objetivo e atual
        // utilizar atributos da classe Problema
        estAtu = new Estado(8, 0);
        prob.defEstIni(8, 0);
        prob.defEstObj(2, 8);
        custo = 0.0;
    }
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar() {
        ct++;      
        
        System.out.println("Estado atual: " + estAtu.getString());
        System.out.println("Ações possiveis: ");
        //dá erro aqui
        //for (int i=0; i < 8; i++)
        //    System.out.print(acao[prob.acoesPossiveis(estAtu)[i]]);
  
        //executa ação, o ct já existia então eu uso ele como contador
        executarIr(plan[ct]);
        System.out.println("ct = " + ct + " Ação escolhida: " + acao[plan[ct]]);
        
        //atualiza o custo, ele tem dois metodos para obter custo
        //nesse que tem 3 param parece ter a conta do jeito que a gente
        //espera, por isso repeti o estAtu...
        custo += prob.obterCustoAcao(estAtu, plan[ct], estAtu);
        System.out.println("Custo acumulado: " + custo);
        
        //atualiza estado atual do agente
        estAtu = prob.suc(estAtu, plan[ct]);

        //se chegou no objetivo, finaliza
        if (prob.testeObjetivo(estAtu))
        {
            System.out.println("fim!");
            return -1;
        }
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

