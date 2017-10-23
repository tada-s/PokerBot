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
public class Cards {
    public static int getCardNumber(int index){
        return ((index - 1) % 13) + 1;
    }

    public static int getCardSuit(int index){
        switch(((index - 1) / 13) + 1){
            case 1:
                return 'C';
            case 2:
                return 'D';
            case 3:
                return 'H';
            case 4:
                return 'S';
            default:
                return '-';
        }
    }
    
    public static int getCardIndex(int suit, int number){
        int offset = 0;
        switch(suit){
            case 'C':
                offset = 0;
                break;
            case 'D':
                offset = 13;
                break;
            case 'H':
                offset = 26;
                break;
            case 'S':
                offset = 39;
                break;
        }
        return offset + number;
    }
    
    public static int getCardIndex(String card){
        int n = (card.charAt(1) - '0') * 10 + (card.charAt(2) - '0');
        return getCardIndex(card.charAt(0), n);
    }
    
    public static String getCardString(int index){
        return String.format("%c%02d", Cards.getCardSuit(index), Cards.getCardNumber(index));
    }
    
    public static String score(String cardsPlayer[], String cardsBoard[]){
        int countNumbers[] = new int[14 + 1];
        int suitesNumbers[] = new int[14 + 1];
        int countSuites[] = new int[4];
        int maxSuites[] = new int[4];
        
        for(String cards[] : new String[][]{cardsPlayer, cardsBoard}){
            for(String card : cards){
                int n = (card.charAt(1) - '0') * 10 + (card.charAt(2) - '0');
                if(n == 1){
                    n = 14;
                }
                countNumbers[n]++;
                switch(card.charAt(0)){
                    case 'S':
                        suitesNumbers[n] |= 0b0001;
                        countSuites[0]++;
                        maxSuites[0] = Math.max(maxSuites[0], n);
                        break;
                    case 'H':
                        suitesNumbers[n] |= 0b0010;
                        countSuites[1]++;
                        maxSuites[1] = Math.max(maxSuites[1], n);
                        break;
                    case 'C':
                        suitesNumbers[n] |= 0b0100;
                        countSuites[2]++;
                        maxSuites[2] = Math.max(maxSuites[2], n);
                        break;
                    case 'D':
                        suitesNumbers[n] |= 0b1000;
                        countSuites[3]++;
                        maxSuites[3] = Math.max(maxSuites[3], n);
                        break;
                }
            }
        }
        
        final int UNDEF = -1;
        int handHighCard = UNDEF;
        int handPoker = UNDEF;
        int handTriple1 = UNDEF;
        int handTriple2 = UNDEF;
        int handPair1 = UNDEF;
        int handPair2 = UNDEF;
        int handFlush = UNDEF;
        int handStraight = UNDEF;
        int handStraightFlush = UNDEF;
        for(int i = 2; i <= 14; i++){
            // Detect Straight and Straight Flush
            if(i + 4 <= 14){
                boolean isStraight = true;
                int suites = 0b1111;
                for(int j = 0; j < 5; j++){
                    if(countNumbers[i + j] == 0){
                        isStraight = false;
                    }
                    suites &= suitesNumbers[i + j];
                }
                if(isStraight && suites != 0b0000){
                    handStraightFlush = i;
                    handStraight = i;
                }else if(isStraight){
                    handStraight = i;
                }
            }
            // Detect other hands
            switch(countNumbers[i]){
                case 4:
                    handPoker = i;
                    break;
                case 3:
                    handTriple2 = handTriple1;
                    handTriple1 = i;
                    break;
                case 2:
                    handPair2 = handPair1;
                    handPair1 = i;
                    break;
                case 1:
                    handHighCard = i;
                    break;
            }
        }
        // Detect Flush
        for(int s = 0; s < 4; s++){
            if(countSuites[s] >= 5){
                handFlush = Math.max(handFlush, maxSuites[s]);
            }
        }
        
        /*
        int handHighCard = UNDEF;
        int handPoker = UNDEF;
        int handTriple = UNDEF;
        int handDoublePair = UNDEF;
        int handPair = UNDEF;
        int handFlush = UNDEF;
        int handStraight = UNDEF;
        int handStraightFlush = UNDEF;
        */
        // "SFPPFFHHFFSSTTDDPPHH"
        // "12345678901234567890"
        // "00000000000000000000"
        if(handStraightFlush != UNDEF){
            return String.format("%02d000000000000000000", handStraightFlush);
        }else if(handPoker != UNDEF){
            return String.format("00%02d0000000000000000", handPoker);
        }else if(handTriple1 != UNDEF && (handPair1 != UNDEF || handTriple2 != UNDEF)){
            return String.format("0000%02d%02d000000000000", handTriple1, Math.max(handPair1, handTriple2));
        }else if(handFlush != UNDEF){
            return String.format("00000000%02d0000000000", handFlush);
        }else if(handStraight != UNDEF){
            return String.format("0000000000%02d00000000", handStraight);
        }else if(handTriple1 != UNDEF){
            return String.format("000000000000%02d000000", handTriple1);
        }else if(handPair1 != UNDEF && handPair2 != UNDEF){
            return String.format("00000000000000%02d%02d00", handPair1, handPair2);
        }else if(handPair1 != UNDEF){
            return String.format("0000000000000000%02d00", handPair1, handPair2);
        }else if(handHighCard != UNDEF){
            return String.format("000000000000000000%02d", handHighCard);
        }else{
            return"00000000000000000000";
        }
    }
}
