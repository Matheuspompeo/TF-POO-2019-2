import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Platform;
/**
 *
 * @author joao, matheus
 */
public class Jogo extends Application {
    public static final int CELL_WIDTH = 20;
    public static final int CELL_HEIGHT = 20;
    public static final int NLIN = 10;
    public static final int NCOL = 10;
    public static final int QTDADEJORNALISTAS = 5;
    public static final int QTDADEPSICOPATAS = 1;

    public static Jogo jogo = null;

    private Random random;
    private Map<String, Image> imagens;
    private List<Celula> celulas;
    private List<Personagem> personagens;

    public static Jogo getInstance(){
        return jogo;
    }

    public Jogo(){
        jogo = this;
        random = new Random();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Retorna um número aleatorio a partir do gerador unico
    public int aleatorio(int limite){
        return random.nextInt(limite);
    }

    // Retorna a celula de uma certa linha,coluna
    public Celula getCelula(int nLin,int nCol){
        int pos = (nLin*NCOL)+nCol;
        return celulas.get(pos);
    }

    private void loadImagens() {
        imagens = new HashMap<>();

        // Armazena as imagens dos personagens
        Image aux = new Image("file:Imagens\\img1.jpg");
        imagens.put("Normal", aux);
        aux = new Image("file:Imagens\\img2.jpg");
        imagens.put("Infectado", aux);
        aux = new Image("file:Imagens\\img8.jpg");
        imagens.put("Psicopata", aux);
        aux = new Image("file:Imagens\\img6.jpg");
        imagens.put("Morto", aux);
        aux = new Image("file:Imagens\\img7.jpg");
        imagens.put("Esperto", aux);
        aux = new Image("file:Imagens\\back.jpg");
        imagens.put("Vazio", aux);

        // Armazena a imagem da celula ula
        imagens.put("Null", null);
    }

    public Image getImage(String id){
        return imagens.get(id);
    }

    @Override
    public void start(Stage primaryStage) {
        // Carrega imagens
        loadImagens();

        // Configura a interface com o usuario
        primaryStage.setTitle("Simulador");
        GridPane tab = new GridPane();
        tab.setAlignment(Pos.CENTER);
        tab.setHgap(10);
        tab.setVgap(10);
        tab.setPadding(new Insets(25, 25, 25, 25));
        // Monta o "tabuleiro"
        celulas = new ArrayList<>(NLIN*NCOL);
        for (int lin = 0; lin < NLIN; lin++) {
            for (int col = 0; col < NCOL; col++) {
                Celula cel = new Celula(lin,col);
                cel.setOnAction(e->cliqueNaCelula(e));
                celulas.add(cel);
                tab.add(cel, col, lin);
            }
        }

        // Cria a lista de personagens
        personagens = new ArrayList<>(NLIN*NCOL);
  
        personagens.add(new PsicopataEsperto(6,6));
        //personagens.add(new Jornalista(0,0));
        //personagens.add(new Jornalista(4,0));
        
        
        // Cria jornalistas aleatorios
        for(int i=0;i<QTDADEJORNALISTAS;i++){
            // Lembrte: quando um personagem é criado ele se vincula
            // automaticamente na célula indicada nos parametros
            // linha e coluna (ver o construtor de Personagem)
            boolean posOk = false;
            while(!posOk){
                int lin = random.nextInt(NLIN);
                int col = random.nextInt(NCOL);
                if (this.getCelula(lin, col).getPersonagem() == null){
                    personagens.add(new Jornalista(lin,col));
                    posOk = true;
                }
            }
        }
        
        // Cria Psicopatas aleatórios
        for(int i=0;i<QTDADEPSICOPATAS;i++){
            boolean posOk = false;
            while(!posOk){
                int lin = random.nextInt(NLIN);
                int col = random.nextInt(NCOL);
                if (this.getCelula(lin, col).getPersonagem() == null){
                    personagens.add(new Psicopata(lin,col));
                    posOk = true;
                }
            }
        }

        // Define o botao que avança a simulação
        Button avanca = new Button("NextStep");
        avanca.setOnAction(e->avancaSimulacao());
        // Define outros botoes
        
        // Monta a cena e exibe
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(25, 25, 25, 25));
        hb.getChildren().add(tab);
        hb.getChildren().add(avanca);      
        
        Scene scene = new Scene(hb);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void avancaSimulacao(){
        // Avança um passo em todos os personagens
        personagens.forEach(p->{
            p.atualizaPosicao();
            p.verificaEstado();
            p.influenciaVizinhos();
        });
        // Verifica se o jogo acabou
        long vivos = personagens
                    .stream()
                    .filter(p->!(p instanceof Psicopata))
                    .filter(p->!(p instanceof PsicopataEsperto))
                    .filter(p->p.estaVivo())
                    .count();
        if (vivos == 0){
            Alert msgBox = new Alert(AlertType.INFORMATION);
            msgBox.setHeaderText("Fim de Jogo");
            msgBox.setContentText("Todos os jornalistas morreram!");
            msgBox.showAndWait();
            System.exit(0);
        }
    }

    

    public void cliqueNaCelula(ActionEvent e){
        Celula c = (Celula)e.getSource();
        System.out.println("Celula: l="+c.getLinha()+" c="+c.getColuna());
        Personagem p = c.getPersonagem();
        if (p.infectado()){
            p.cura();
        }
    }
}

