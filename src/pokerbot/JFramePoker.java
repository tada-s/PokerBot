/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pokerbot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Tadashi
 */
public class JFramePoker extends javax.swing.JFrame {

    private BufferedImage biBackground;
    private Graphics2D gBackground;
    private Graphics2D gPanel1;
            
    private Game game = new Game();
    
    HashMap<String, BufferedImage> cards = new HashMap<>();

    /**
     * Creates new form JFramePoker
     */
    public JFramePoker() {
        initComponents();
        
        //jScrollPane2
        DefaultCaret caret = (DefaultCaret) jTextArea1.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        Print.jTextArea = jTextArea1;
        
        // Load card image files
        try {
            BufferedImage img;
            for(int i = 1; i <= 52; i++){
                String fileName = String.format("cards/CARD%d.bmp", i); 
                String cardName = String.format("%c%02d", Cards.getCardSuit(i), Cards.getCardNumber(i)); 
                img = ImageIO.read(new File(fileName));
                cards.put(cardName, img);
            }
            img = ImageIO.read(new File("cards/CARDBACK.bmp"));
            cards.put("BACK", img);
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(this, "Error al cargar las imagenes", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
        }

        // Initialize graphics drawing class
        biBackground = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_RGB);
        gBackground = (Graphics2D) biBackground.getGraphics();
        gPanel1 = (Graphics2D) jPanel1.getGraphics();
        
        game.newHumanVsBotGame();
        game.runGame();
        
        JFramePoker frame = this;
        Timer timer = new Timer();
        TimerTask task = new TimerTask(){
            public void run(){
                frame.repaint();
            }
        };
        timer.schedule(task, 0, 50);
        
        // Debug
        // "SFPPFFHHFFSSTTDDPPHH"
        //System.out.println(" ------------ " + "SFPPFFHHFFSSTTDDPPHH");
        /*String cardsPlayer1[] = game.player1.getCards();
        String cardsPlayer2[] = game.player2.getCards();
        System.out.println("Score Player1 " + Cards.score(cardsPlayer1, game.cardsBoard));
        System.out.println("Score Player2 " + Cards.score(cardsPlayer2, game.cardsBoard));
        switch(Cards.score(cardsPlayer1, game.cardsBoard).compareTo(Cards.score(cardsPlayer2, game.cardsBoard))){
            case -1:
                System.out.println("You lose");
                break;
            case 0:
                System.out.println("Draw");
                break;
            case 1:
                System.out.println("You win");
                break;
        }
        
        PokerBotPlayer pb = (PokerBotPlayer)game.player2;
        //double p1 = pb.getWinningOrDrawProbability(game.cardsBoard, 3);
        //System.out.printf("probability %.3f\n", p1);
        System.out.printf("probability %.3f\n", pb.getWinningProbability(game));*/
    }

    /* JFrame paint method */
    @Override
    public void paint(Graphics g){
        super.paint(g);
        draw();
    }
    
    /* Draw cards on jPanel1 */
    public void draw(){
        Graphics2D g = gBackground;
        int panelWidth = jPanel1.getWidth();
        int panelHeight = jPanel1.getHeight();
        
        int paddingWidth = 10;
        int cardWidth = 71;
        int cardHeight = 96;

        // Erase all drawings
        g.setColor(Color.white);
        g.fillRect(0, 0, panelWidth, panelHeight);

        // Draw Player1
        String cardsPlayer1[] = game.player1.getCards();
        int player1CardsX = panelWidth - 2 * cardWidth - paddingWidth - 30;
        int player1CardsY = panelHeight - cardHeight - 30;
        for(int i = 0; i < 2; i++){
            g.drawImage(cards.get(cardsPlayer1[i]), player1CardsX + i * (cardWidth + paddingWidth), player1CardsY, null);
        }
        g.setColor(Color.black);
        g.drawString("Bet: " + game.player1.getBet(), player1CardsX - 2 * (cardWidth + paddingWidth), player1CardsY + 10);
        g.drawString("Cash: " + game.player1.getCash(), player1CardsX - 2 * (cardWidth + paddingWidth), player1CardsY + 30);

        // Draw Player2
        int player2CardsX = 30;
        int player2CardsY = 30;
        String cardsPlayer2[] = game.player2.getCards();
        if(game.state.equals(Game.STATE_SHOWDOWN)){
            for(int i = 0; i < 2; i++){
                g.drawImage(cards.get(cardsPlayer2[i]), player2CardsX + i * (cardWidth + paddingWidth), player2CardsY, null);
            }
        }else{
            for(int i = 0; i < 2; i++){
                g.drawImage(cards.get("BACK"), player2CardsX + i * (cardWidth + paddingWidth), player2CardsY, null);
            }
        }
        g.setColor(Color.black);
        g.drawString("Bet: " + game.player2.getBet(), player2CardsX + 2 * (cardWidth + paddingWidth), player2CardsY + 10);
        g.drawString("Cash: " + game.player2.getCash(), player2CardsX + 2 * (cardWidth + paddingWidth), player2CardsY + 30);

        // Draw board
        int boardCardsX = panelWidth / 2 - (6 * cardWidth + 5 * paddingWidth) / 2;
        int boardCardsY = panelHeight / 2 - cardHeight / 2;
        int showingCards = game.getShowingCardsNumber();
        for(int i = 0; i < showingCards; i++){
            g.drawImage(cards.get(game.cardsBoard[i]), boardCardsX + (i + 1) * (cardWidth + paddingWidth), boardCardsY, null);
        }
        for(int i = showingCards; i < 5; i++){
            g.drawImage(cards.get("BACK"), boardCardsX + (i + 1) * (cardWidth + paddingWidth), boardCardsY, null);
        }
        
        // Draw deck
        int deckX = panelWidth / 2 - (6 * cardWidth + 5 * paddingWidth) / 2;
        int deckY = panelHeight / 2 - cardHeight / 2;
        for(int i = showingCards; i < 8; i++){
            g.drawImage(cards.get("BACK"), deckX + 8 - i, deckY - i, null);
        }

        // Copy buffered image to jPanel
        gPanel1.drawImage(biBackground, 0, 0, null);
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSlider3 = new javax.swing.JSlider();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Poker Bot");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Acciones"));

        jButton1.setText("Aumentar apuesta");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Pasar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Retirar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Siguiente Juego");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Opciones del Bot 1"));

        jLabel1.setText("Probabilidad Q");

        jSlider1.setValue(70);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Opciones del Bot 2"));

        jLabel3.setText("Probabilidad Q");

        jSlider3.setValue(40);
        jSlider3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider3StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jMenu1.setText("Nuevo juego");

        jMenuItem2.setText("Humano vs Bot");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Bot vs Bot");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 53, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        HumanPlayer p;
        p = (HumanPlayer) game.player1;
        p.order = Game.ACTION_CHECK;
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        HumanPlayer p;
        p = (HumanPlayer) game.player1;
        p.order = Game.ACTION_FOLD;
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int value = Integer.parseInt(JOptionPane.showInputDialog("Introduzca el valor para aumentar"));
        int raiseValue = value;
        HumanPlayer p;
        p = (HumanPlayer) game.player1;
        p.order = Game.ACTION_RAISE + Math.min(p.cashPlayer, raiseValue);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        game.newHumanVsBotGame();
        game.runGame();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        
        game.runGame();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        game.newBotVsBotGame();
        game.runGame();
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        if(game.player1 instanceof PokerBotPlayer){
            ((PokerBotPlayer)game.player1).q = (jSlider1.getValue() / 100.0);
        }
        
    }//GEN-LAST:event_jSlider1StateChanged

    private void jSlider3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider3StateChanged
        if(game.player1 instanceof PokerBotPlayer){
            ((PokerBotPlayer)game.player1).q = (jSlider1.getValue() / 100.0);
        }
        
    }//GEN-LAST:event_jSlider3StateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFramePoker().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
