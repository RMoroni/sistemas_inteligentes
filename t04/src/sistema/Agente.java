package sistema;

import ambiente.*;
import arvore.TreeNode;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.ArrayList;
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
    int plan[];
    int plan_length = 20; //tamanho do vetor de plano
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
    
     /**
     * Define as crencas do agente a respeito das paredes do labirinto
     */
    public void colocarCrencasParedes(){
        prob.crencaLabir.porParedeVertical(0, 1, 0);
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
        prob.crencaLabir.porParedeVertical(5, 7, 7);
    }
    
    /**
     * Define qual busca será executada de acordo com a escolha do usuário
     * @param escolha busca custo-uniforme, A* com h1 ou A* com h2
     */
    public void executaBusca(String escolha){
        if(escolha.equals("Custo-uniforme"))
            buscaCustoUniforme();
        else
            busca_ah1();
    }
    
    /**
     * Cria o plano de ação do agente através da busca Custo-uniforme
     */
    public void buscaCustoUniforme(){
        
        //inicia com as características do nó raiz(estado inicial do agente)
        TreeNode raiz = new TreeNode(null);
        raiz.setState(new Estado(8,0));
        raiz.setAction(-1);
        raiz.setDepth(0);
        raiz.setGn(0);
        raiz.setHn(0);
        raiz.setGnHn(raiz.getGn(), raiz.getHn());
        
        //cria a árvore de busca de acordo com as expansões dos nós
        List<TreeNode> arvore = new ArrayList<>();
        arvore.add(raiz);
        TreeNode pai = new TreeNode(null);
        pai = raiz;
        //armazena a fronteira de cada iteração
        List<TreeNode> fronteira = new ArrayList<>();
        
        while(true){
            
            //expansão do nó retirado da fronteira
            //adiciona na fronteira todos os seus nós filhos
            JOptionPane.showMessageDialog(null, pai.getState().getString());
            int acoesPossiveis[] = prob.acoesPossiveis(pai.getState());
            int i;
            for(i=0; i<8; i++){
                if(acoesPossiveis[i] == 0){
                    TreeNode no = new TreeNode(pai);
                    no.setState(new Estado(pai.getState().getLin(), pai.getState().getCol()));
                    no.setState(prob.suc(no.getState(), i));
                    if(no.getState().getLin() != pai.getState().getLin() || no.getState().getCol() != pai.getState().getCol()){
                        no.setAction(i);
                        no.setDepth(pai.getDepth() + 1);
                        no.setGn(pai.getFn() + prob.obterCustoAcao(pai.getState(), i, no.getState()));
                        no.setHn(0);
                        no.setGnHn(no.getGn(), no.getHn());
                        arvore.add(no);
                        fronteira.add(no);
                    }
                }                         
            }
            
            //faz a escolha do próximo nó
            //que é aquele que apresenta menor custo acumulado
            pai = fronteira.get(0);
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
        plan = new int[20];
        int fn[] = new int[8];
        int acao[] = new int[8]; 
        
        int count_plano = 0, count_fronteira = 0, menor, menor_indice=0;
        double distancia_euclidiana, gn; //variaveis para calcular fn
        
        Estado estado = estAtu; //inicializo o estado que irei utilizar para buscar
        //Estado fronteira[] = new Estado[8]; //minha fronteira é um vetor de estados
        
        while (prob.testeObjetivo(estado))
        {
            int acoesPossiveis[] = prob.acoesPossiveis(estado);
            
            for(int i=0; i<8; i++)
            {
                if(acoesPossiveis[i] == 0)
                {
                    //fronteira[count_fronteira] = prob.suc(estado, i); //coloco o estado na fronteira
                    acao[count_fronteira] = i; //salvo a ação relacionada com o possível destino
                    
                    //calculo a distancia euclidiana, será o meu hn
                    distancia_euclidiana = Math.sqrt(
                            Math.pow(prob.suc(estado, i).getLin() - prob.estObj.getLin(), 2)
                            + Math.pow(prob.suc(estado, i).getCol() - prob.estObj.getCol(), 2)
                    );
                    gn = prob.obterCustoAcao(estado, i, prob.suc(estado, i)); //custo para chegar em n
                    fn[count_fronteira] = (int)(gn + distancia_euclidiana); //salvo o fn relacionado com o possivel destino
                    count_fronteira++; //incrementa o count   
                }          
            }
            menor = Integer.MAX_VALUE; //inicializo o menor valor
            for(int i=0; i<count_fronteira; i++) //para cada elemento na fronteira
            {
                if (fn[i] <= menor) //busco qual tem o menor fn
                {
                    menor = fn[i]; //salvo o valor fn que é menor
                    menor_indice = i; //preciso salvar o indice para achar a ação depois
                }
            }
            estado = prob.suc(estado, acao[menor_indice]); //atualizo meu estado
            plan[count_plano] = acao[count_fronteira]; //coloco a ação escolhida no plano
            count_plano++; //incrementa count do plano
            count_fronteira = 0; //zera count da fronteira
        }
        this.plan_length = count_plano;
    }
}    

