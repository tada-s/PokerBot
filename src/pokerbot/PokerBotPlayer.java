/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pokerbot;

import static pokerbot.Game.STATE_NONE;

/**
 *
 * @author Tadashi
 */
public class PokerBotPlayer implements Player{
    public String cardsPlayer[] = new String[2];
    public int betPlayer;
    public int cashPlayer;
    public Player opponentPlayer;
    
    //public double q = 0.4;
    public double q = 0.7;
    
    public long searchNumber = 50000;
    
    public static final int DECISION_RETIRE = -1;
    public static final int DECISION_CALL = 0;
    public static final int DECISION_RAISE = 1;
    public int decisionMatrix[][] = new int[11][11];
    
    public double raiseVector[] = new double[11];
    
    public PokerBotPlayer(){
        String matrixStr[] = 
                ("I R R R R R R R R R R\n" +
                "I I R R R R R R R R R\n" +
                "I I I R R R R R R R R\n" +
                "A I I I R R R R R R R\n" +
                "A I I I R R R R R R R\n" +
                "A I I I I R R R R R R\n" +
                "A I I I I R R R R R R\n" +
                "A I I I I I I R R R R\n" +
                "A I I A A A A A A A I\n" +
                "A I A A A A A A A A I\n" +
                "A I A A A A A A A A I").split("\\s+");
        for(int i = 0; i < 11; i++){
            for(int j = 0; j < 11; j++){
                switch(matrixStr[i * 11 + j]){
                    case "R":
                        decisionMatrix[i][j] = DECISION_RETIRE;
                        break;
                    case "I":
                        decisionMatrix[i][j] = DECISION_CALL;
                        break;
                    case "A":
                        decisionMatrix[i][j] = DECISION_RAISE;
                        break;
                }
            }
        }
        
        String raiseStr[] = ("0.001 0.001 0.003 0.005 0.005 0.009 0.01 0.011 0.015 0.3 1").split("\\s+");
        for(int i = 0; i < 11; i++){
            raiseVector[i] = Double.parseDouble(raiseStr[i]);
        }
        
    }
    
    public String nextMove(Game game){
        if(cashPlayer == 0){
            return Game.ACTION_CHECK;
        }
        if(game.state_turn == 3){
            double winningProbability = getWinningProbability(game);
            double bet = opponentPlayer.getBet();
            int d_Win = (int)Math.ceil(winningProbability * 10);
            int d_Bet = (int)Math.ceil(bet * 10 / (this.cashPlayer + this.betPlayer));
            int d = decisionMatrix[d_Win][d_Bet];
            System.out.printf("w: %f\n", winningProbability);
            System.out.printf("%d,%d = %d", d_Win, d_Bet, d);
            switch(d){
                case DECISION_RETIRE:
                    return Game.ACTION_FOLD;
                case DECISION_CALL:
                case DECISION_RAISE:
                    return Game.ACTION_CHECK;
                default:
                    return "-";
            }
        }

        if(Math.random() < q){
            // Probability Strategy
            double winningProbability = getWinningProbability(game);
            double bet = opponentPlayer.getBet();
            int d_Win = (int)Math.ceil(winningProbability * 10);
            int d_Bet = (int)Math.ceil(bet * 10 / (this.cashPlayer + this.betPlayer));
            int d = decisionMatrix[d_Win][d_Bet];
            System.out.printf("w: %f\n", winningProbability);
            System.out.printf("%d,%d = %d", d_Win, d_Bet, d);
            switch(d){
                case DECISION_RETIRE:
                    return Game.ACTION_FOLD;
                case DECISION_CALL:
                    return Game.ACTION_CHECK;
                case DECISION_RAISE:
                    return Game.ACTION_RAISE + Math.min(opponentPlayer.getCash(), getRaiseValue(winningProbability));
                default:
                    return "-";
            }
        }else{
            // Lie Strategy
            int r = (int) (Math.random()*10);
            return Game.ACTION_RAISE + Math.min(opponentPlayer.getCash(), (int)Math.ceil(this.cashPlayer * raiseVector[r]));
        }
    }
    
    public int getRaiseValue(double winningProbability){
        int d_Win = (int)Math.ceil(winningProbability * 10);
        return (int)(Math.ceil(this.cashPlayer * raiseVector[d_Win]));
    }
    
    public double getWinningProbability(Game game){
        String boardCards[] = game.cardsBoard;
        //int knownCardsNumber = game.getShowingCardsNumber();
        int knownCardsNumber = 0;
        
        boolean isSelected[] = new boolean[52 + 1];
        for(String card : cardsPlayer){
            isSelected[Cards.getCardIndex(card)] = true;
        }
        for(int i = 0; i < knownCardsNumber; i++){
            isSelected[Cards.getCardIndex(boardCards[i])] = true;
        }
        
        String possiblePlayerCards[] = new String[2];
        String possibleUnknownBoardCards[] = new String[5 - knownCardsNumber];

        long winninOrDrawCase = 0;
        for(long k = 0; k < searchNumber; k++){
            for(String possibleCards[] : new String[][]{possiblePlayerCards, possibleUnknownBoardCards}){
                for(int i = 0; i < possibleCards.length; i++){
                    int c = (int)(Math.random() * 52 + 1);
                    while(isSelected[c]){
                        c = (int)(Math.random() * 52 + 1);
                    }
                    possibleCards[i] = Cards.getCardString(c);
                    isSelected[c] = true;
                }
            }
            
            String possibleBoardCards[] = new String[5];
            for(int i = 0; i < knownCardsNumber; i++){
                possibleBoardCards[i] = boardCards[i];
            }
            for(int i = knownCardsNumber; i < 5; i++){
                possibleBoardCards[i] = possibleUnknownBoardCards[i - knownCardsNumber];
            }
            
            String score1 = Cards.score(cardsPlayer, possibleBoardCards);
            String score2 = Cards.score(possiblePlayerCards, possibleBoardCards);
            if(score1.compareTo(score2) > 0){
                winninOrDrawCase++;
            }
            
            for(String possibleCards[] : new String[][]{possiblePlayerCards, possibleUnknownBoardCards}){
                for(int i = 0; i < possibleCards.length; i++){
                    isSelected[Cards.getCardIndex(possibleCards[i])] = false;
                }
            }
        }
        
        return winninOrDrawCase / (1.0 * searchNumber);
    }    
 
    
    // Getter and setter
    public String[] getCards() {
        return cardsPlayer;
    }

    public void setCards(String[] cardsPlayer) {
        this.cardsPlayer = cardsPlayer;
    }

    public int getBet() {
        return betPlayer;
    }

    public void setBet(int betPlayer) {
        this.betPlayer = betPlayer;
    }

    public int getCash() {
        return cashPlayer;
    }

    public void setCash(int cashPlayer) {
        this.cashPlayer = cashPlayer;
    }
}
