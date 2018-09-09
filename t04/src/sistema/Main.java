/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import ambiente.*;
import javax.swing.JOptionPane;

/**
 *
 * @author tacla
 */
public class Main {
    public static void main(String args[]) {
        // Cria o ambiente (modelo) = labirinto com suas paredes
        Model model = new Model(9, 9);
        model.labir.porParedeVertical(0, 1, 0);
        model.labir.porParedeVertical(0, 0, 1);
        model.labir.porParedeVertical(5, 8, 1);
        model.labir.porParedeVertical(5, 5, 2);
        model.labir.porParedeVertical(8, 8, 2);
        model.labir.porParedeHorizontal(4, 7, 0);
        model.labir.porParedeHorizontal(7, 7, 1);
        model.labir.porParedeHorizontal(3, 5, 2);
        model.labir.porParedeHorizontal(3, 5, 3);
        model.labir.porParedeHorizontal(7, 7, 3);
        model.labir.porParedeVertical(6, 7, 4);
        model.labir.porParedeVertical(5, 6, 5);
        model.labir.porParedeVertical(5, 7, 7);
        
        // seta a posição inicial do agente no ambiente - nao interfere no 
        // raciocinio do agente, somente no amibente simulado
        model.setPos(8, 0);
        model.setObj(2, 8);
        
        // Cria um agente
        Agente ag = new Agente(model);
        
        // Ciclo de execucao do sistema
        // desenha labirinto
        model.desenhar(); 
        
        //escolha da busca pelo usuário para montar o plano de ação do agente
        Object[] opcoes = { "Custo-uniforme", "A* com h1", "A* com h2"};
        String resposta;
        resposta = (String) JOptionPane. showInputDialog(null, "Qual busca deve ser executada ?", 
                "Plano de ação do agente", JOptionPane.QUESTION_MESSAGE, null, opcoes, null);        
                    
        // agente escolhe proxima açao e a executa no ambiente (modificando
        // o estado do labirinto porque ocupa passa a ocupar nova posicao)
        System.out.println("\n*** Inicio do ciclo de raciocinio do agente ***\n");
        while (ag.deliberar(resposta) != -1) {  
            model.desenhar(); 
        }
        
        //ao terminar de executar o plano é testado se o estado objetivo
        //foi alcançado
        if(ag.prob.testeObjetivo(ag.estAtu)){
            System.out.println("Objetivo alcançado!");
            System.out.println("Custo final: " + ag.custo);
        }else
            System.out.println("Objetivo não alcançado!");
    }
}