/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.util.Map;
import midi.Helper;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

/**
 *
 * @author ARNOULD
 */
public class MusicFitnessFunction extends FitnessFunction {

    private final Map<Integer, double[]> before, after;
    private static final int BASE_NUMBER = 25;

    public MusicFitnessFunction(Helper helper) {
        this.before = helper.before();
        this.after = helper.after();
    }
    
    

    @Override
    protected double evaluate(IChromosome ic) {
        Gene[] f= ic.getGenes();
        return sequence(f);
    }


    private double sequence(Gene[] gene){
        double fit_value = 0;
        int n = gene.length;
        double t[];
        int a, b, c, idx;
        for (int i = 0; i < n; i++) {

            a = i > 3 ? (int) gene[i - 3].getAllele() : 0;
            b = i > 2 ? (int) gene[i - 2].getAllele() : 0;
            c = i > 1 ? (int) gene[i - 1].getAllele() : 0;

            idx = BASE_NUMBER * BASE_NUMBER * a
                    + BASE_NUMBER * b
                    + c;
            t = before.get(idx);
            if (t != null) {
                fit_value += t[(int) gene[i].getAllele()];
            }

            a = i < n - 4 ? (int) gene[i + 3].getAllele() : 0;
            b = i < n - 3 ? (int) gene[i + 2].getAllele() : 0;
            c = i < n - 2 ? (int) gene[i + 1].getAllele() : 0;
            idx = BASE_NUMBER * BASE_NUMBER * a
                    + BASE_NUMBER * b
                    + c;

            t = after.get(idx);
            if (t != null) {
                fit_value += t[(int) gene[i].getAllele()];
            }
        }

        return fit_value/2/n;
    }
}
