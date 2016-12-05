/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import org.jgap.Gene;

/**
 *
 * @author hussain
 */
public final class R {
    private R(){}
    public static final int
            PIECE_LENGTH = 64,
            BASE_NUMBER = 25;
    public static int ecodeBefore(Gene[]g,int i){
         int a = i > 3 ? (int) g[i - 3].getAllele() : 0,
            b = i > 2 ? (int) g[i - 2].getAllele() : 0,
            c = i > 1 ? (int) g[i - 1].getAllele() : 0;
            int idx = BASE_NUMBER * BASE_NUMBER * a
                    + BASE_NUMBER * b
                    + c;
            return idx;
    }
}
