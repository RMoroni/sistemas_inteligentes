package sistema;

import ambiente.*;
import arvore.TreeNode;
import problema.*;
import comuns.*;
import static comuns.PontosCardeais.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

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
    int plan[] = new int[20];
    int tamPlan = 0; //armazena a qntdade de ações do plano do agente
    double custo = 0;
    static int ct;
    
    //Estratégia 1: baseline
    //Estratégia 2: fuzzy
    int estrategia = 1;
    double energia = 3.0;
    
    public Agente(Model m) {
        this.model = m;
        prob = new Problema();
        ct = -1;
        
        //crencas do agente: Estado inicial, objetivo e atual
        int pos[] = model.lerPos();
        estAtu = new Estado(pos[0], pos[1]);
        prob.defEstIni(pos[0], pos[1]);
        pos = model.lerPosObj();
        prob.defEstObj(pos[0], pos[1]);
        
        //crencas do agente a respeito do labirinto
        prob.criarLabirinto(9, 9);
        colocarCrencasParedes();
        
        //inicializa o plano do agente com -1 em todas as posições
        inicializaPlan();
    }
    
    /**Escolhe qual ação (UMA E SOMENTE UMA) será executada em um ciclo de raciocínio
     * @param busca Custo-uniforme, A* com h1, A* com h2
     * @return 1 enquanto o plano não acabar; -1 quando acabar
     */
    public int deliberar(String busca) {
        
        int vivo = 1;
        //se é primeiro ciclo, executa algoritmo de busca com base na formulação do problema
        if(ct == -1){
            executaBusca(busca);
            //conta as ações do plano do agente para poder executar o plano em seguida
            contaAcoesPlano();
        }else{       

            if(estrategia == 1){
                if(estrategiaBaseline() == 1 && energia > 1.5){
                    System.out.println("estado atual: " + estAtu.getString());
                    System.out.println("energia atual: " + energia);
                    System.out.println("ct = " + ct + " de " + (tamPlan-1) + " acao escolhida = " + acao[plan[ct]]);
                    //executa o plano de acoes: SOMENTE UMA ACAO POR CHAMADA DESTE METODO
                    executarIr(plan[ct]);
                    //atualiza o estado atual do agente
                    prob.suc(estAtu, plan[ct]);
                    energia -= 1.5;
                }else{
                    energia = 50.0;
                    vivo = 0;
                }
                
            }else{

                System.out.println("estado atual: " + estAtu.getString());
                System.out.println("energia atual: " + energia);
                System.out.println("ct = " + ct + " de " + (tamPlan-1) + " acao escolhida = " + acao[plan[ct]]);                
                
                Fruta fruta = model.labir.getFruta(estAtu.getLin(), estAtu.getCol());
                int energy = criaModeloID3(fruta); //encontra o valor da fruta de acordo com o modelo ID3
                              
                String fileName = "rules.fcl";                           
                FIS fis = FIS.load(fileName, true); //abre o arquivo de regras

                // Error while loading?
                if (fis == null) {
                    System.err.println("Can't load file: '" + fileName + "'");
                    System.exit(0);
                }

                // Show 
                //JFuzzyChart.get().chart(fis);

                // Set inputs
                fis.setVariable("energyFruit", energy);
                fis.setVariable("energyAgent", energia);

                // Evaluate
                fis.evaluate();

                // Show output variable's chart
                Variable eat = fis.getVariable("eat");

                //JFuzzyChart.get().chart(eat, eat.getDefuzzifier(), true);

                // Print ruleSet
                //System.out.println(fis);

                System.out.println(fis.getVariable("eat"));
                //se a decisão do Fuzzy for sim, coloca a energia da fruta no agente

                if (eat.getMembership("yes") > 0.5 && eat.getMembership("no") < 0.3){
                    if(fruta.getEnergiaReal() == 0){
                        energia = 50.0;
                        vivo = 0;
                    }else
                        energia += fruta.getEnergiaReal();
                }
            }

        }
        ct++;
        
        if(ct == tamPlan || vivo == 0)
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
        prob.crencaLabir.porParedeVertical(6, 8, 1);
        prob.crencaLabir.porParedeVertical(5, 5, 2);
        prob.crencaLabir.porParedeVertical(8, 8, 2);
        prob.crencaLabir.porParedeHorizontal(4, 7, 0);
        prob.crencaLabir.porParedeHorizontal(3, 5, 2);
        prob.crencaLabir.porParedeHorizontal(3, 6, 3);
        prob.crencaLabir.porParedeVertical(6, 7, 4);
        prob.crencaLabir.porParedeVertical(5, 6, 5);
        prob.crencaLabir.porParedeVertical(5, 7, 7);
    }

    public double getEnergia() {
        return energia;
    }
    
    /**
     * Define qual busca será executada de acordo com a escolha do usuário
     * @param escolha busca custo-uniforme, A* com h1 ou A* com h2
     */
    public void executaBusca(String busca){
        if(busca.equals("Custo-uniforme"))
            buscaCustoUniforme();
        else if(busca.equals("A* com h1"))
            busca_ah(1);
        else
            busca_ah(2);       
    }
    
    /**
     * Conta as ações presentes no plano do agente
     */
    public void contaAcoesPlano(){
        int i = 0;
        while(plan[i] != -1)
            i++;
        tamPlan = i;
    }
    
    /**
     * Inicializa o plano do agente 
     */
    public void inicializaPlan(){
        int i;
        for(i=0; i<plan.length; i++)
            plan[i] = -1;
    }
    
    /**
     * Cria o plano de ação do agente através da busca Custo-uniforme
     */
    public void buscaCustoUniforme(){
        
        //inicia com as características do nó raiz(estado inicial do agente)
        TreeNode raiz = new TreeNode(null); //raiz não tem pai       
        raiz.setState(new Estado(estAtu.getLin(),estAtu.getCol())); //estado dela é a posição do agente
        raiz.setAction(-1);
        raiz.setGn(0); //custo 0
        raiz.setHn(0);
        
        //armazena a fronteira de cada iteração
        List<TreeNode> fronteira = new ArrayList<>();
        fronteira.add(raiz);
        //armazena os estados já explorados
        List<Estado> estadosExplorados = new ArrayList();
        
        while(!prob.testeObjetivo(fronteira.get(0).getState())){
            
            //verifica quais ações possíveis para o nó que será expandido
            //ou seja, quais são seus filhos
            int acoesPossiveis[] = prob.acoesPossiveis(fronteira.get(0).getState());
            int i;
            //para cada ação possível
            for(i=0; i<8; i++){
                if(acoesPossiveis[i] == 0){
                    //cria um nó filho
                    TreeNode node = fronteira.get(0).addChild();
                    //como o método suc altera o estado do nó que é passado, foi instanciado
                    //um novo estado para conseguir obter o estado sucessor
                    Estado estadoTemp = new Estado(fronteira.get(0).getState().getLin(), fronteira.get(0).getState().getCol());
                    node.setState(prob.suc(estadoTemp, i));
                    node.setAction(i);
                    node.setGn(fronteira.get(0).getGn() + prob.obterCustoAcao(fronteira.get(0).getState(), i, node.getState()));
                    node.setHn(0);
                    
                    //verifica se o estado deste nó filho já foi explorado
                    boolean continua = false;
                    if (estadosExplorados.contains(node.getState()))
                        continua = true;
                    
                    //verifica se o estado deste nó filho já está na fronteira
                    //se estiver, verifica qual dos dois tem g(n) menor
                    //mantendo na fronteira o nó com menor custo acumulado
                    if (!continua){
                        for(TreeNode no : fronteira){
                            if(node.getState().igualAo(no.getState())){
                                if(node.getGn() < no.getGn()){
                                    fronteira.remove(no);
                                    fronteira.add(node);                                    
                                }
                                continua = true;
                                break;
                            }
                        }
                    }
                    
                    //caso o nó filho ainda ñ tenha sido explorado e
                    //não existe outro nó na fronteira de mesmo estado
                    //ele é adicionado na fronteira
                    if(!continua)
                        fronteira.add(node);                                            
                }                         
            }
            
            //adiciona aos estados explorados o estado do nó que acabou de ser expandido
            estadosExplorados.add(new Estado(fronteira.get(0).getState().getLin(), fronteira.get(0).getState().getCol()));
            //remove da fronteira o nó que foi expandido
            fronteira.remove(0);
            
            //ordena a fronteira
            Collections.sort (fronteira, new Comparator() {
                public int compare(Object o1, Object o2) {
                    TreeNode no1 = (TreeNode) o1;
                    TreeNode no2 = (TreeNode) o2;
                    return no1.getGn() < no2.getGn() ? -1 : (no1.getGn() > no2.getGn() ? +1 : 0);
                }
            });
        
        }
        
        //preenche custo total da busca e o plano de ação encontrado
        TreeNode node = fronteira.get(0); 
        custo = node.getGn();
        int i;
        for(i=node.getDepth() - 1; i>=0; i--){
            plan[i] = node.getAction();
            node = node.getParent();
        }
        
    }
    
    /**
     * Cria o plano de ação do agente através da busca A*
     * utilizando uma heurística
     * @param h heurística 1 ou 2
     * 1: distância euclidiana
     * 2: manhattan
     */
    public void busca_ah(int h){
        
        //variável utilizada para calcular h(n)
        float hn;
        
        //inicia com as características do nó raiz(estado inicial do agente)
        TreeNode raiz = new TreeNode(null); //raiz não tem pai
        raiz.setState(new Estado(estAtu.getLin(),estAtu.getCol())); //estado dela é a posição do agente
        raiz.setAction(-1);
        raiz.setGnHn(0, calculaHeuristica(h, raiz));
        
        //armazena a fronteira de cada iteração
        List<TreeNode> fronteira = new ArrayList<>();
        fronteira.add(raiz); //primeiro nó na fronteira é a raiz
        //armazena os estados já explorados
        List<Estado> estadosExplorados = new ArrayList();
        
        while(!prob.testeObjetivo(fronteira.get(0).getState())){
            
            //verifica quais ações possíveis para o nó que será expandido
            //ou seja, quais são seus filhos
            int acoesPossiveis[] = prob.acoesPossiveis(fronteira.get(0).getState());
            int i;
            //para cada ação possível
            for(i=0; i<8; i++){
                if(acoesPossiveis[i] == 0){
                    //cria um nó filho
                    TreeNode node = fronteira.get(0).addChild();
                    //como o método suc altera o estado do nó que é passado, foi instanciado
                    //um novo estado para conseguir obter o estado sucessor
                    Estado estadoTemp = new Estado(fronteira.get(0).getState().getLin(), fronteira.get(0).getState().getCol());
                    node.setState(prob.suc(estadoTemp, i)); //estado sucessor é setado para o nó
                    node.setAction(i); //ação escolhida é setada para o nó
                    //gn é o custo da ação, hn é a heuristica escolhida
                    node.setGnHn(fronteira.get(0).getGn() + prob.obterCustoAcao(fronteira.get(0).getState(), i, node.getState()), calculaHeuristica(h, node));
                    
                    //verifica se o estado deste nó filho já foi explorado
                    boolean continua = false;
                    if (estadosExplorados.contains(node.getState()))
                        continua = true;
                    
                    //verifica se o estado deste nó filho já está na fronteira
                    //se estiver, verifica qual dos dois tem f(n) menor
                    //mantendo na fronteira o nó com menor custo acumulado
                    if (!continua){
                        for(TreeNode no : fronteira){
                            if(node.getState().igualAo(no.getState())){
                                if(node.getFn() < no.getFn()){
                                    fronteira.remove(no);
                                    fronteira.add(node);                                    
                                }
                                continua = true;
                                break;
                            }
                        }   
                    }
                    //caso o nó filho ainda ñ tenha sido explorado e
                    //não existe outro nó na fronteira de mesmo estado
                    //ele é adicionado na fronteira
                    if (!continua)
                        fronteira.add(node); 
                }                         
            }
            
            //adiciona aos estados explorados o estado do nó que acabou de ser expandido
            estadosExplorados.add(new Estado(fronteira.get(0).getState().getLin(), fronteira.get(0).getState().getCol()));
            //remove da fronteira o nó que foi expandido
            fronteira.remove(0);
            
            //ordena a fronteira
            Collections.sort (fronteira, new Comparator() {
                public int compare(Object o1, Object o2) {
                    TreeNode no1 = (TreeNode) o1;
                    TreeNode no2 = (TreeNode) o2;
                    return no1.getFn() < no2.getFn() ? -1 : (no1.getFn() > no2.getFn() ? +1 : 0);
                }
            });
        
        }
        
        //preenche custo total da busca e o plano de ação encontrado
        TreeNode node = fronteira.get(0); 
        custo = node.getFn();
        int i;
        for(i=node.getDepth() - 1; i>=0; i--){
            plan[i] = node.getAction();
            node = node.getParent();
        }
    }
    
    /**
     * Calcula a heurística para cada o nó da árvore de busca
     * @param h heurística 1 ou 2
     * @param no nó que receberá o valor de h(n)
     * @return valor de h(n)
     */
    public float calculaHeuristica(int h, TreeNode no){
        float hn;
        //1: distância euclidiana (hipotenusa), raiz do quadrado das diferenças
        //2: distância manhattan/2, distância ignorando as diagonais (em L)
        if(h == 1){
            hn = (float) Math.sqrt(
                  Math.pow(no.getState().getLin() - prob.estObj.getLin(), 2)
                  + Math.pow(no.getState().getCol() - prob.estObj.getCol(), 2));
        }else{
            hn = Math.abs(no.getState().getLin() - prob.estObj.getLin())
                 + Math.abs(no.getState().getCol() - prob.estObj.getCol());
            hn /= 2; //divide por 2 para ficar menor do que a distância real
        }
        return hn;
    }

    /**
     * Executa estratégia baseline
     * @return 1 se o agente continua vivo e 0 se o agente morreu
     */
    public int estrategiaBaseline(){
    
        Random gerador = new Random();
        int comerFruta = gerador.nextInt(2);
        if(comerFruta == 1){
            Fruta fruta = model.labir.getFruta(estAtu.getLin(), estAtu.getCol());
            if(fruta.getEnergiaReal() == 0)
                return 0;
            else{
                energia += fruta.getEnergiaReal();
                return 1;
            }
        }
        return 1;
    }
    
    /**
     * Cria modelo ID3
     */
    public int criaModeloID3(Fruta fruta){
        
        String rgb[] = {"R", "G", "B"};      
        String rgb_fruta[] = fruta.getCaracteristica();
        
        //inicia com as características do nó raiz
        TreeNode raiz = new TreeNode(null); //raiz não tem pai
        raiz.setCaracteristica("c1");
        raiz.setAcao(null); //raiz não tem ação antecedente
        
        TreeNode pai = raiz;       
        TreeNode node = pai.addChild();
        node.setCaracteristica("c3");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("c0");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("c2");
        node.setAcao("B");
        
        pai = pai.getChildren().get(0);
        for(int i = 0; i < 3; i++){
            node = pai.addChild();
            node.setCaracteristica("c2");
            node.setAcao(rgb[i]); 
        }
        
        pai = pai.getChildren().get(0);
        node = pai.addChild();
        node.setCaracteristica("4");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("B");
        
        pai = pai.getParent().getChildren().get(1);
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("0");
        node.setAcao("B");
        
        pai = pai.getParent().getChildren().get(2);
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("4");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("B");
        
        pai = pai.getParent().getParent().getChildren().get(1);
        node = pai.addChild();
        node.setCaracteristica("c3");
        node.setAcao("K");
        node = pai.addChild();
        node.setCaracteristica("0");
        node.setAcao("W");
        
        pai = pai.getChildren().get(0);
        for(int i = 0; i < 3; i++){
            node = pai.addChild();
            node.setCaracteristica("c2");
            node.setAcao(rgb[i]); 
        }
        
        pai = pai.getChildren().get(0);
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("0");
        node.setAcao("B");
        
        pai = pai.getParent().getChildren().get(1);
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("4");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("B");
        
        pai = pai.getParent().getChildren().get(2);
        node = pai.addChild();
        node.setCaracteristica("0");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("B");
        
        pai = pai.getParent().getParent().getParent().getChildren().get(2);
        for(int i = 0; i < 3; i++){
            node = pai.addChild();
            node.setCaracteristica("c3");
            node.setAcao(rgb[i]); 
        }
        
        pai = pai.getChildren().get(0);
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("0");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("B");
        
        pai = pai.getParent().getChildren().get(1);
        node = pai.addChild();
        node.setCaracteristica("0");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("B");
        
        pai = pai.getParent().getChildren().get(2);
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("R");
        node = pai.addChild();
        node.setCaracteristica("2");
        node.setAcao("G");
        node = pai.addChild();
        node.setCaracteristica("4");
        node.setAcao("B");
        
        //percorre a árvore
        TreeNode aux = raiz;
        List<TreeNode> filhos = new ArrayList<>();
        while (!aux.getChildren().isEmpty()){
            filhos = aux.getChildren();
            String carac;
            if (aux.getCaracteristica().equals("c0")) {
                carac = rgb_fruta[0];
            } else if (aux.getCaracteristica().equals("c1")) {
                carac = rgb_fruta[1];
            } else if (aux.getCaracteristica().equals("c2")) {
                carac = rgb_fruta[2];
            } else {
                carac = rgb_fruta[3];
            }
            for (TreeNode no : filhos){
                if (no.getAcao().equals(carac)){
                    aux = no;
                }
            }  
        }
        return Integer.parseInt(aux.getCaracteristica());
    }
}