/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pokerbot;

import javax.swing.JTextArea;

/**
 *
 * @author Tadashi
 */
public class Print {
    static JTextArea jTextArea = null;
    public static void print(String str){
        System.out.println(str);
        jTextArea.append(str + "\n");
    }
    
    
}
