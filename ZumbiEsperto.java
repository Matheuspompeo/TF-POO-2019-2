public class ZumbiEsperto extends Personagem {
    private Personagem alvo;

    public ZumbiEsperto(int linInicial,int colInicial){
        super(10,"ZumbiEsp",linInicial,colInicial);
        alvo = null;
    }

    private Personagem escolheAlvo(){
        System.out.println("Procurando vitima");
        for(int l=0;l<Jogo.NLIN;l++){
            for(int c=0;c<Jogo.NCOL;c++){
                Personagem p = Jogo.getInstance().getCelula(l,c).getPersonagem();
                if (p != null && (p instanceof Bobao) && !p.infectado()){
                    System.out.println("Achou alvo");
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public void atualizaPosicao() {
        if (alvo == null){
            alvo = escolheAlvo();
            return;
        }
        // Posicao do personagem corrente
        int oldLin = this.getCelula().getLinha();
        int oldCol = this.getCelula().getColuna();
        
        // Posicao do alvo
        int linAlvo = alvo.getCelula().getLinha();
        int colAlvo = alvo.getCelula().getColuna();

        System.out.println("Pos Zumbi:"+oldLin+","+oldCol+" Pos alvo:"+linAlvo+","+colAlvo);


        // Calcular para onde vai
        int lin = oldLin;
        int col = oldCol;
        
        // Movimenta-se em direção ao alvo
        if (lin < linAlvo) lin++;
        if (lin > linAlvo) lin--;

        if (col < colAlvo) col++;
        if (col > colAlvo) col--;
        System.out.println("Depois de incrementar: Pos Zumbi:"+lin+","+col+" Pos alvo:"+linAlvo+","+colAlvo);

        // Ajusta para não cair fora do tabuleiro
        if (lin < 0) lin = 0;
        if (lin >= Jogo.NLIN) lin = Jogo.NLIN-1;
        if (col < 0) col = 0;
        if (col >= Jogo.NCOL) col = Jogo.NCOL-1;

        System.out.println("Depois de ajustar: Pos Zumbi:"+lin+","+col+" Pos alvo:"+linAlvo+","+colAlvo);

        // Se chegou no alvo, infecta
        if (lin == linAlvo && col == colAlvo && !alvo.infectado()){
            System.out.println("Infectou");
            alvo.infecta();
            alvo = null;
            return;
        }

        // Move o ZumbiEsperto 
        if (Jogo.getInstance().getCelula(lin, col).getPersonagem() != null){
            return;
        }else{
            // Limpa celula atual
            Jogo.getInstance().getCelula(oldLin, oldCol).setPersonagem(null);
            // Coloca personagem na nova posição
            Jogo.getInstance().getCelula(lin, col).setPersonagem(this);
        }
    }

    @Override
    public void influenciaVizinhos() {
        /*
        int lin = this.getCelula().getLinha();
        int col = this.getCelula().getColuna();
        for(int l=lin-1;l<=lin+1;l++){
            for(int c=col-1;c<=col+1;c++){
                // Se a posição é dentro do tabuleiro
                if (l>=0 && l<Jogo.NLIN && c>=0 && c<Jogo.NCOL){
                    // Se não é a propria celula
                    if (!( lin == l && col == c)){
                        // Recupera o personagem da célula vizinha
                        Personagem p = Jogo.getInstance().getCelula(l,c).getPersonagem();
                        // Se não for nulo, infecta
                        if (p != null){
                            p.infecta();
                        }
                    }
                }
            }
        }
        */
    }

    @Override
    public void verificaEstado() {
        // Como não sofre influencia de ninguém, o estado nunca muda
    }
}