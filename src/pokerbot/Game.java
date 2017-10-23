/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pokerbot;

/**
 *
 * @author Tadashi
 */
public class Game {
    public final static String STATE_NONE = "None";
    public final static String STATE_PREFLOP = "Pre-flop";
    public final static String STATE_FLOP = "Flop";
    public final static String STATE_TURN = "Turn";
    public final static String STATE_RIVER = "River";
    public final static String STATE_SHOWDOWN = "Showdown";
    
    public final static String ACTION_FOLD = "Fold";
    public final static String ACTION_CHECK = "Check";
    public final static String ACTION_RAISE = "Raise ";
    
    boolean swappedPlayer = false;

    /*public String cardsPlayer1[] = new String[2];
    public String cardsPlayer2[] = new String[2];
    public int betPlayer1;
    public int betPlayer2;
    public int cashPlayer1;
    public int cashPlayer2;*/
    public String cardsBoard[] = new String[5];
    public String state = STATE_NONE;
    public int state_turn = 0;
    public Player player1;
    public Player player2;
    
    
    public Game(){
        player1 = new HumanPlayer();
        player2 = new PokerBotPlayer();
        String cardsPlayer1[] = new String[2];
        String cardsPlayer2[] = new String[2];
        cardsPlayer1[0] = "H01";
        cardsPlayer1[1] = "H13";
        cardsPlayer2[0] = "C02";
        cardsPlayer2[1] = "D03";
        player1.setCards(cardsPlayer1);
        player2.setCards(cardsPlayer2);
        cardsBoard[0] = "C01";
        cardsBoard[1] = "C06";
        cardsBoard[2] = "H09";
        cardsBoard[3] = "C13";
        cardsBoard[4] = "C12";
        state = STATE_FLOP;
        state = STATE_SHOWDOWN;
    }
    
    public void newHumanVsHumanGame(){
        player1 = new HumanPlayer();
        player2 = new HumanPlayer();
        player1.setCash(1000);
        player2.setCash(1000);
        dealRandomCards();
        state = STATE_PREFLOP;
        state_turn = 0;
    }

    public void newHumanVsBotGame(){
        player1 = new HumanPlayer();
        player2 = new PokerBotPlayer();
        player1.setCash(1000);
        player2.setCash(1000);
        ((PokerBotPlayer) player2).opponentPlayer = player1;
        dealRandomCards();
        state = STATE_PREFLOP;
        state_turn = 0;
    }
    
    public void newBotVsBotGame(){
        player1 = new PokerBotPlayer();
        player2 = new PokerBotPlayer();
        player1.setCash(1000);
        player2.setCash(1000);
        ((PokerBotPlayer) player2).opponentPlayer = player1;
        ((PokerBotPlayer) player1).opponentPlayer = player2;
        dealRandomCards();
        state = STATE_PREFLOP;
        state_turn = 0;
    }
    
    public void runGame(){
        dealRandomCards();
        
        GameThread thread;
        if(swappedPlayer){
            thread = new GameThread(this, player2, player1);
        }else{
            thread = new GameThread(this, player1, player2);
        }
        thread.start();
        swappedPlayer = !swappedPlayer;
    }
    
    public class GameThread extends Thread{
        public Game game;
        public Player player1;
        public Player player2;
        public GameThread(Game game, Player player1, Player player2){
            this.game = game;
            this.player1 = player1;
            this.player2 = player2;
        }
        public boolean turn(){
            String player1Action;
            String player2Action;
            Print.print(game.state);
            // Player 1 talks
            state_turn = 1;
            player1Action = player1.nextMove(game);
            Print.print((swappedPlayer ? "Player1" : "Player2") + ": " + player1Action);
            game.takeAction(player1, player1Action);
            if(player1Action.equals(ACTION_FOLD)) return false;

            // Player 2 talks
            state_turn = 2;
            player2Action = player2.nextMove(game);
            Print.print((swappedPlayer ? "Player2" : "Player1") + ": " + player2Action);
            game.takeAction(player2, player2Action);
            if(player2Action.equals(ACTION_FOLD)) return false;
            
            if(player2Action.matches(ACTION_RAISE + "\\d*")){
                // Player 1 talks
                state_turn = 3;
                player1Action = player1.nextMove(game);
                Print.print((swappedPlayer ? "Player1" : "Player2") + ": " + player1Action);
                game.takeAction(player1, player1Action);
                if(player1Action.equals(ACTION_FOLD)) return false;
            }
            return true;
        }
        
        public void run(){
            game.state = STATE_PREFLOP;
            if(turn() == false) return;
            
            game.state = STATE_FLOP;
            if(turn() == false) return;

            game.state = STATE_TURN;
            if(turn() == false) return;
            
            game.state = STATE_RIVER;
            if(turn() == false) return;

            game.state = STATE_SHOWDOWN;
            System.out.println(game.state);
            // "SFPPFFHHFFSSTTDDPPHH"
            //System.out.println(" ------------ " + "SFPPFFHHFFSSTTDDPPHH");
            String cardsPlayer1[] = game.player1.getCards();
            String cardsPlayer2[] = game.player2.getCards();
            System.out.println("Score Player1 " + Cards.score(cardsPlayer1, game.cardsBoard));
            System.out.println("Score Player2 " + Cards.score(cardsPlayer2, game.cardsBoard));
            int r = Cards.score(cardsPlayer1, game.cardsBoard).compareTo(Cards.score(cardsPlayer2, game.cardsBoard));
            if(r < 0){
                Print.print(swappedPlayer ? "You lose" : "You win");
                game.takeAction(player1, Game.ACTION_FOLD);
            }else if(r == 0){
                Print.print("Draw");
                int total = player1.getBet() + player2.getBet();
                int mid = total / 2;
                player2.setCash(player2.getCash() + mid);
                player1.setBet(0);
                player2.setBet(0);
                player1.setCash(player1.getCash() + total - mid);
                player1.setBet(0);
                player2.setBet(0);
            }else{
                Print.print(swappedPlayer ? "You win" : "You lose");
                game.takeAction(player2, Game.ACTION_FOLD);
            }
        }
    };
    
    public void takeAction(Player player, String action){
        switch(action){
            case ACTION_CHECK:
                if(player == player1){
                    if(player1.getBet() < player2.getBet()){
                        int diff = player2.getBet() - player1.getBet();
                        player1.setCash(player1.getCash() - diff);
                        player1.setBet(player2.getBet());
                    }
                }else{
                    if(player2.getBet() < player1.getBet()){
                        int diff = player1.getBet() - player2.getBet();
                        player2.setCash(player2.getCash() - diff);
                        player2.setBet(player1.getBet());
                    }
                }
                break;
            case ACTION_FOLD:
                if(player == player1){
                    player2.setCash(player2.getCash() + player1.getBet() + player2.getBet());
                    player1.setBet(0);
                    player2.setBet(0);
                }else{
                    player1.setCash(player1.getCash() + player1.getBet() + player2.getBet());
                    player1.setBet(0);
                    player2.setBet(0);
                }
                break;
            default:
                int raiseValue = Integer.parseInt(action.substring(ACTION_RAISE.length()));
                if(player == player1){
                    int diff = player2.getBet() - player1.getBet();
                    player.setBet(player.getBet() + raiseValue + diff);
                    player.setCash(player.getCash() - raiseValue - diff);
                }else{
                    int diff = player1.getBet() - player2.getBet();
                    player.setBet(player.getBet() + raiseValue + diff);
                    player.setCash(player.getCash() - raiseValue - diff);
                }
                break;
        }
    }

    private void dealRandomCards(){
        boolean isSelected[] = new boolean[52 + 1];
        for(String possibleCards[] : new String[][]{player1.getCards(), player2.getCards(), this.cardsBoard}){
            for(int i = 0; i < possibleCards.length; i++){
                int c = (int)(Math.random() * 52 + 1);
                while(isSelected[c]){
                    c = (int)(Math.random() * 52 + 1);
                }
                possibleCards[i] = Cards.getCardString(c);
                isSelected[c] = true;
            }
        }
    }
    
    public int getShowingCardsNumber(){
        switch(state){
            case Game.STATE_NONE:
            case Game.STATE_PREFLOP:
                return 0;
            case Game.STATE_FLOP:
                return 3;
            case Game.STATE_TURN:
                return 4;
            case Game.STATE_RIVER:
            case Game.STATE_SHOWDOWN:
                return 5;
            default:
                return 5;
        }
    }
        
    
}
