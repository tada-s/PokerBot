/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pokerbot;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tadashi
 */
public class HumanPlayer implements Player{
    public String cardsPlayer[] = new String[2];
    public int betPlayer;
    public int cashPlayer;
    public Player opponentPlayer;

    public final static String waitingOrder = "Waiting...";
    public String order = waitingOrder;
    
    public String nextMove(Game game){
        try {
            while(order.equals(waitingOrder)){
                Thread.sleep(100);
            }
            String returnString = order;
            order = "Waiting...";
            return returnString;
        } catch (InterruptedException ex) {
            Logger.getLogger(HumanPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
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
