/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.util.List;
import java.util.Map;
import javax.security.auth.login.Configuration;
import midi.Helper;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.impl.CrossoverOperator;

/**
 *
 * @author hussain
 */
public class MusicCrssoverOperator extends CrossoverOperator {

    private final Map<Integer, double[]> before, after;

    public MusicCrssoverOperator(org.jgap.Configuration conf, int i, Helper helper) throws InvalidConfigurationException {
        super(conf, i);
        this.before = helper.before();
        this.after = helper.after();
    }

    @Override
    public void doCrossover(IChromosome firstMate, IChromosome secondMate,
            List a_candidateChromosomes, RandomGenerator generator) {
        super.doCrossover(firstMate, secondMate, a_candidateChromosomes, generator);
        Gene[] g1 = firstMate.getGenes();
        Gene[] g2 = secondMate.getGenes();
        int cut = 0;
        int n = g1.length;
        double max = 0;
        int idx;
        for (int i = 0; i < n; i++) {
            idx = R.ecodeBefore(g1,i);
            double t[] = before.get(idx);
            double t1 = 0;
            if (t != null) {
                t1 = t[(int) g2[i].getAllele()];
            }

            idx = R.ecodeBefore(g2,i);
            t = before.get(idx);
            double t2 = 0;
            if(t != null) {
                t2 = t[(int) g1[i].getAllele()];
            }
            if (t1 + t2 > max) {
                max = t1 + t2;
                cut = i;
            }
        }
        for (int i = 0; i < cut; i++) {
            Object tmp = g1[i].getAllele();
            g1[i].setAllele(g2[i].getAllele());
            g2[i].setAllele(tmp);
        }
    }

}
