/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

import ambiente.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author tacla
 */
public class Main {
    public static void main(String args[]) {
        
        String resposta;
        HashMap<Integer,String> frutas = new HashMap<Integer,String>();
        HashMap<Integer,Double> pontuacao = new HashMap<Integer,Double>();
        
        //escolha da busca pelo usuário para montar o plano de ação do agente
        Object[] opcoes = { "Custo-uniforme", "A* com h1", "A* com h2"};      
        resposta = (String) JOptionPane. showInputDialog(null, "Qual busca deve ser executada ?", 
                "Plano de ação do agente", JOptionPane.QUESTION_MESSAGE, null, opcoes, null);
        
        //carrega o arquivo de frutas
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("frutasLabirinto.txt")));
            String line = null;
            int lineNumber = 1;
            while ((line = bufferedReader.readLine()) != null) {
                    frutas.put(lineNumber, line);
                    lineNumber++;
            }
            bufferedReader.close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
       
        // Cria o ambiente (modelo) = labirinto com suas paredes
        Model model = new Model(9, 9);
        model.labir.porParedeVertical(0, 1, 0);
        model.labir.porParedeVertical(0, 0, 1);
        model.labir.porParedeVertical(6, 8, 1);
        model.labir.porParedeVertical(5, 5, 2);
        model.labir.porParedeVertical(8, 8, 2);
        model.labir.porParedeHorizontal(4, 7, 0);
        model.labir.porParedeHorizontal(3, 5, 2);
        model.labir.porParedeHorizontal(3, 6, 3);
        model.labir.porParedeVertical(6, 7, 4);
        model.labir.porParedeVertical(5, 6, 5);
        model.labir.porParedeVertical(5, 7, 7);
        
        for(int iteracao = 1; iteracao <= 100; iteracao++){
                       
            //coloca as frutas nas posições do labirinto
            model.labir.porFruta(iteracao, frutas);

            // sorteia a posição inicial do agente no ambiente
            Random gerador = new Random();
            int linhaSorteada = gerador.nextInt(9);
            int colunaSorteada = gerador.nextInt(9);
            while(model.setPos(linhaSorteada, colunaSorteada) == -1){
                linhaSorteada = gerador.nextInt(9);
                colunaSorteada = gerador.nextInt(9);
            }

            // sorteia posição do objeto no ambiente
            linhaSorteada = gerador.nextInt(9);
            colunaSorteada = gerador.nextInt(9);
            while(model.setObj(linhaSorteada, colunaSorteada) == -1){
                linhaSorteada = gerador.nextInt(9);
                colunaSorteada = gerador.nextInt(9);
            }

            // Cria um agente
            Agente ag = new Agente(model);

            // Ciclo de execucao do sistema
            // desenha labirinto
            model.desenhar();         

            // agente escolhe proxima açao e a executa no ambiente (modificando
            // o estado do labirinto porque ocupa passa a ocupar nova posicao)
            System.out.println("\n*** Inicio do ciclo de raciocinio do agente ***\n"
                    + "Cenario: " + iteracao + "\n");
            while (ag.deliberar(resposta) != -1) {  
                model.desenhar(); 
            }
            //armazena a pontuação do agente nesta iteração
            pontuacao.put(iteracao, ag.getEnergia());

            //ao terminar de executar o plano é testado se o estado objetivo
            //foi alcançado
            if(ag.prob.testeObjetivo(ag.estAtu)){
                System.out.println("Objetivo alcançado!");
                System.out.println("Custo final: " + ag.custo);
            }else
                System.out.println("Objetivo não alcançado!");
        }
        
        System.out.println();
        System.out.println("<id-cenario>, <pontuacao>\n");
        for(int i = 1; i <= 100; i++)
            System.out.println(i + "  " + pontuacao.get(i));
    }
}

/*import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

import net.sourceforge.jFuzzyLogic.rule.Variable;



/**
 *
 * @author tacla
 */
/*public class Main {
    public static void main(String[] args) throws Exception {
        // Load from 'FCL' file
        String fileName = "./tipper.fcl";
        FIS fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Show 
        JFuzzyChart.get().chart(fis);

        // Set inputs
        fis.setVariable("service", 5);
        fis.setVariable("food", 7.5);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable tip = fis.getVariable("tip");
        
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);

        // Print ruleSet
        System.out.println(fis);
        
        // print membership degree for output terms
        System.out.println("cheap="+tip.getMembership("cheap"));
        System.out.println("average="+tip.getMembership("average"));
        System.out.println("generous="+tip.getMembership("generous"));
    }
}*/