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
public interface Player {
    
    public String[] getCards();
    public void setCards(String[] cardsPlayer);
    
    public int getBet();
    public void setBet(int betPlayer);

    public int getCash();
    public void setCash(int cashPlayer);

    public String nextMove(Game game);
    
}
