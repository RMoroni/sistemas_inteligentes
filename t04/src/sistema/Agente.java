package sistema;

import ambiente.*;
import arvore.TreeNode;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

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
    int plan[] = new int[20];
    double custo = 0;
    static int ct = -1;
           
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        
        //crencas do agente: Estado inicial, objetivo e atual
        estAtu = new Estado(8,0);
        prob.defEstIni(8, 0);
        prob.defEstObj(2, 8);
        
        //crencas do agente a respeito do labirinto
        prob.criarLabirinto(9, 9);
        colocarCrencasParedes();
    }
    
    /**
     * Define as crencas do agente a respeito das paredes do labirinto
     */
    public void colocarCrencasParedes(){
        //não sei pq, mas funcionou
        /*prob.crencaLabir.porParedeVertical(0, 1, 0);
        prob.crencaLabir.porParedeVertical(0, 0, 1);
        prob.crencaLabir.porParedeVertical(5, 8, 1);
        prob.crencaLabir.porParedeVertical(5, 5, 2);
        prob.crencaLabir.porParedeVertical(8, 8, 2);
        prob.crencaLabir.porParedeHorizontal(4, 7, 0);
        prob.crencaLabir.porParedeHorizontal(7, 7, 1);
        prob.crencaLabir.porParedeHorizontal(3, 5, 2);
        prob.crencaLabir.porParedeHorizontal(3, 5, 3);
        prob.crencaLabir.porParedeHorizontal(7, 7, 3);
        prob.crencaLabir.porParedeVertical(6, 7, 4);
        prob.crencaLabir.porParedeVertical(5, 6, 5);
        prob.crencaLabir.porParedeVertical(5, 7, 7);*/
        prob.crencaLabir.porParedeHorizontal(0, 1, 0);
        prob.crencaLabir.porParedeHorizontal(4, 7, 0);
        prob.crencaLabir.parede[1][0] = 1;
        prob.crencaLabir.parede[1][7] = 1;
        prob.crencaLabir.porParedeHorizontal(3, 5, 2);
        prob.crencaLabir.porParedeHorizontal(3, 5, 3);
        prob.crencaLabir.parede[3][7] = 1;
        prob.crencaLabir.porParedeVertical(5, 8, 1);
        prob.crencaLabir.parede[5][2] = 1;
        prob.crencaLabir.parede[8][2] = 1;
        prob.crencaLabir.parede[5][5] = 1;
        prob.crencaLabir.porParedeHorizontal(4, 5, 6);
        prob.crencaLabir.parede[7][4] = 1;
        prob.crencaLabir.porParedeVertical(5, 7, 7);
    }
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar() {
        ct++;
        
        //imprime o que foi pedido
        System.out.println("estado atual: " + estAtu.getString());
        System.out.print("acoes possiveis: { ");
        int acoesPossiveis[] = prob.acoesPossiveis(estAtu);
        int i;
        for(i=0; i<8; i++){
            if(acoesPossiveis[i] == 0)
                System.out.print(acao[i] + " ");           
        }
        System.out.println("}");
        System.out.println("ct = " + ct + " de " + (plan.length-1) + " acao escolhida = " + acao[plan[ct]]);
        
        //executa o plano de acoes: SOMENTE UMA ACAO POR CHAMADA DESTE METODO
        // Ao final do plano, verifique se o agente atingiu o estado objetivo verificando
        // com o teste de objetivo
        executarIr(plan[ct]);
        
        //imprime o que foi pedido                          
        System.out.println("custo ate o momento: " + custo);
        //calcula custo acumulado após executar a ação
        custo += prob.obterCustoAcao(estAtu, plan[ct], prob.suc(estAtu, plan[ct]));
        System.out.println();
        
        if(ct == plan.length-1)
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
    
    
    /**
     * Define qual busca será executada de acordo com a escolha do usuário
     * @param escolha busca custo-uniforme, A* com h1 ou A* com h2
     */
    public void executaBusca(String escolha){
        if(escolha.equals("Custo-uniforme"))
            buscaCustoUniforme();
        else if (escolha.equals("A* com h1"))
        {
            busca_ah1();
        }
        else
        {
            busca_ah2();
        }
    }
    
    /**
     * Cria o plano de ação do agente através da busca Custo-uniforme
     */
    public void buscaCustoUniforme(){
        
        //inicia com as características do nó raiz(estado inicial do agente)
        TreeNode raiz = new TreeNode(null); //raiz não tem pai
        raiz.setState(new Estado(8,0)); //estado dela é a posição do agente
        raiz.setAction(-1);
        raiz.setDepth(0); //profundidade 0
        raiz.setGn(0); //custo 0
        raiz.setHn(0);
        raiz.setGnHn(raiz.getGn(), raiz.getHn());
        
        //cria a árvore de busca de acordo com as expansões dos nós
        List<TreeNode> arvore = new ArrayList<>(); //onde serão colocados os nós
        arvore.add(raiz); //a raiz é o primeiro nó
        TreeNode pai = new TreeNode(null); //pai vai ser um 'auxiliar'
        pai = raiz; //inicializa ele com a raiz
        //armazena a fronteira de cada iteração
        List<TreeNode> fronteira = new ArrayList<>();
        
        while(true){
            
            //expansão do nó retirado da fronteira
            //adiciona na fronteira todos os seus nós filhos
            JOptionPane.showMessageDialog(null, pai.getState().getString());
            //descubro quais ações possíveis para o estado do pai
            int acoesPossiveis[] = prob.acoesPossiveis(pai.getState());
            int i;
            //para cada ação possível
            for(i=0; i<8; i++){
                if(acoesPossiveis[i] == 0){
                    TreeNode no = new TreeNode(pai); //cria um nó filho
                    no.setState(new Estado(pai.getState().getLin(), pai.getState().getCol()));
                    no.setState(prob.suc(no.getState(), i)); //o estado dele é o sucessor
                    //verifica se não é repetido
                    if(no.getState().getLin() != pai.getState().getLin() || no.getState().getCol() != pai.getState().getCol()){
                        no.setAction(i); //seta a ação escolhida
                        no.setDepth(pai.getDepth() + 1); //profundidade é a mesma do pai + 1
                        //custo do pai + custo para realizar a ação
                        no.setGn(pai.getFn() + prob.obterCustoAcao(pai.getState(), i, no.getState()));
                        no.setHn(0); //não existe hn no custo-uniforme
                        no.setGnHn(no.getGn(), no.getHn());
                        arvore.add(no); //adiciona na árvore de busca
                        fronteira.add(no); //adiciona na fronteira
                    }
                }                         
            }
            
            //faz a escolha do próximo nó
            //que é aquele que apresenta menor custo acumulado
            pai = fronteira.get(0);
            //para cada nó na fronteira
            for(TreeNode no : fronteira){
                if(no.getFn() < pai.getFn())
                    pai = no;
            }
            fronteira.remove(pai); //remove o nó escolhido para expandir
            if(prob.testeObjetivo(pai.getState())) //teste se o nó escolhido é o estado objetivo
                break;
        
        }
        
        //preenche custo total da busca e o plano de ação encontrado
        custo = pai.getFn();
        int i;
        for(i=pai.getDepth(); i>0; i--){
            plan[i] = pai.getAction();
            pai = pai.getParent();
        }
        
    }
    public void busca_ah1()
    {
        double distancia_euclidiana, gn; //variaveis para calcular fn
        
        //criando o nó raiz da árvore
        TreeNode raiz = new TreeNode(null);
        raiz.setState(new Estado(8, 0)); //inicia com estado do agente
        raiz.setAction(-1);
        raiz.setGn(0); //custo é 0
        raiz.setHn(0); //hn tbm
        
        //arvore de busca
        List<Estado> estados = new ArrayList<>();
        List<TreeNode> fronteira = new ArrayList<>();
        estados.add(raiz.getState()); //adiciona estado da raiz como estado já visitado
        TreeNode pai; //pai será um 'nó auxiliar'
        pai = raiz; //ele começão sendo a raiz da árvore
        
        //enquanto o pai não for o objetivo...
        while (!prob.testeObjetivo(pai.getState()))
        {
            //busco as ações possíveis para o estado do pai
            int acoesPossiveis[] = prob.acoesPossiveis(pai.getState());
            
            //para cada ação possível
            for(int i=0; i<8; i++){
                //se for válida
                if(acoesPossiveis[i] == 0)
                {
                    //crio um nó como filho...
                    TreeNode node = pai.addChild();
                    //o estado dele é o estado sucessor da ação atual a partir do estado do pai
                    node.setState(prob.suc(pai.getState(), i));
                    //se o estado já existe na lista de estados, pula para prox interação
                    
                    //tem essas duas maneiras de verificar, as duas deram ruim kkkk
                    if (!estados.contains(node.getState())){
                    /*boolean continua = false;
                    for (Estado estado : estados) {
                        if (node.getState().igualAo(estado))
                            continua = true;
                    }
                    if (!continua){*/
                    //a ação dele é a ação atual...
                    node.setAction(i);
                    System.out.println("est: " + node.getState().getString());
                    //calculo a distancia euclidiana até o objetivo, será o meu hn
                    distancia_euclidiana = Math.sqrt(
                            Math.pow(node.getState().getLin() - prob.estObj.getLin(), 2)
                            + Math.pow(node.getState().getCol() - prob.estObj.getCol(), 2)
                    );
                    //meu gn é o custo do pai + o custo para chegar no próximo estado
                    gn = pai.getFn() + prob.obterCustoAcao(pai.getState(), i, node.getState());
                    
                    //seta gn e hn
                    node.setGn((float) gn);
                    node.setHn((float) distancia_euclidiana);

                    //adiciona o nó na árvore
                    estados.add(node.getState());
                    //adiciona o nó na fronteira
                    fronteira.add(node);
                    }
                }          
            }
            //vou buscar o próximo 'pai', inicializo com o primeiro nó na fronteira
            pai = fronteira.get(0);
            //para cada nó na fronteira
            for(TreeNode node : fronteira){
                //quem tiver o menor fn será o escolhido
                if(node.getFn() <= pai.getFn())
                    pai = node;
            }
            fronteira.remove(pai); //remove o nó escolhido para expandir
        }
        //preenche custo total da busca e o plano de ação encontrado
        custo = pai.getFn();
        //vamos percorrer o vetor ao contrário, então a partir do
        //último nó pai, ele vai 'subindo' na árvore, olhando o pai
        //e salvando no plan[] a ação
        for(int i=pai.getDepth(); i>0; i--){
            plan[i] = pai.getAction();
            pai = pai.getParent();
        }
    }
    public void busca_ah2()
    {
        double distancia_manhattan, gn; //variaveis para calcular fn
        
        //criando o nó raiz da árvore
        TreeNode raiz = new TreeNode(null);
        raiz.setState(new Estado(8, 0)); //inicia com estado do agente
        raiz.setAction(-1);
        raiz.setGn(0); //custo é 0
        raiz.setHn(0); //hn tbm
        
        //arvore de busca
        List<Estado> estados = new ArrayList<>();
        List<TreeNode> fronteira = new ArrayList<>();
        estados.add(raiz.getState()); //adiciona estado da raiz como estado já visitado
        TreeNode pai; //pai será um 'nó auxiliar'
        pai = raiz; //ele começão sendo a raiz da árvore
        
        //enquanto o pai não for o objetivo...
        while (!prob.testeObjetivo(pai.getState()))
        {
            //busco as ações possíveis para o estado do pai
            int acoesPossiveis[] = prob.acoesPossiveis(pai.getState());
            
            //para cada ação possível
            for(int i=0; i<8; i++){
                //se for válida
                if(acoesPossiveis[i] == 0)
                {
                    //crio um nó como filho...
                    TreeNode node = pai.addChild();
                    //o estado dele é o estado sucessor da ação atual a partir do estado do pai
                    node.setState(prob.suc(pai.getState(), i));
                    //se o estado já existe na lista de estados, pula para prox interação
                    
                    //tem essas duas maneiras de verificar, as duas deram ruim kkkk
                    //if (!estados.contains(node.getState())){
                    boolean continua = false;
                    for (Estado estado : estados) {
                        if (node.getState().igualAo(estado))
                            continua = true;
                    }
                    if (!continua){
                    //a ação dele é a ação atual...
                    node.setAction(i);
                    System.out.println("est: " + node.getState().getString());
                    //calculo a distancia euclidiana até o objetivo, será o meu hn
                    distancia_manhattan = Math.abs(prob.suc(pai.getState(), i).getLin() - prob.estObj.getLin())
                            + Math.abs(prob.suc(pai.getState(), i).getCol() - prob.estObj.getCol());
                    //meu gn é o custo do pai + o custo para chegar no próximo estado
                    gn = pai.getFn() + prob.obterCustoAcao(pai.getState(), i, node.getState());
                    
                    //seta gn e hn
                    node.setGn((float) gn);
                    node.setHn((float) distancia_manhattan);

                    //adiciona o nó na árvore
                    estados.add(node.getState());
                    //adiciona o nó na fronteira
                    fronteira.add(node);
                    }
                }          
            }
            //vou buscar o próximo 'pai', inicializo com o primeiro nó na fronteira
            pai = fronteira.get(0);
            //para cada nó na fronteira
            for(TreeNode node : fronteira){
                //quem tiver o menor fn será o escolhido
                if(node.getFn() <= pai.getFn())
                    pai = node;
            }
            fronteira.remove(pai); //remove o nó escolhido para expandir
        }
        //preenche custo total da busca e o plano de ação encontrado
        custo = pai.getFn();
        //vamos percorrer o vetor ao contrário, então a partir do
        //último nó pai, ele vai 'subindo' na árvore, olhando o pai
        //e salvando no plan[] a ação
        for(int i=pai.getDepth(); i>0; i--){
            plan[i] = pai.getAction();
            pai = pai.getParent();
        }
    }
}    

