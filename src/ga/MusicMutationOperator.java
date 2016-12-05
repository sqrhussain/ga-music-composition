/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.ICompositeGene;
import org.jgap.IGeneticOperatorConstraint;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.RandomGenerator;
import org.jgap.impl.MutationOperator;

/**
 *
 * @author hussain
 */
public class MusicMutationOperator extends MutationOperator {

    public MusicMutationOperator(Configuration a_config, int a_desiredMutationRate) throws InvalidConfigurationException {
        super(a_config, a_desiredMutationRate);
    }

    @Override
    public void operate(Population a_population, java.util.List a_candidateChromosomes) {
        if (a_population == null || a_candidateChromosomes == null) {
            return;
        }
        if (getMutationRate() == 0 && getMutationRateCalc() == null) {
            return;
        }

        Configuration conf = getConfiguration();
        boolean mutate = false;
        RandomGenerator generator = conf.getRandomGenerator();
        int size = Math.min(conf.getPopulationSize(),
                a_population.size());
        IGeneticOperatorConstraint constraint = conf.getJGAPFactory().
                getGeneticOperatorConstraint();
        for (int i = 0; i < size; i++) {
            IChromosome chrom = a_population.getChromosome(i);
            Gene[] genes = chrom.getGenes();
            IChromosome copyOfChromosome = null;
            int target = generator.nextInt(R.PIECE_LENGTH);
            if (getMutationRateCalc() != null) {
                mutate = getMutationRateCalc().toBePermutated(chrom, target);
            } else {
                mutate = (generator.nextInt(getMutationRate()) == 0);
            }
            if (mutate) {
                if (constraint != null) {
                    List v = new ArrayList();
                    v.add(chrom);
                    if (!constraint.isValid(a_population, v, this)) {
                        continue;
                    }
                }
                if (copyOfChromosome == null) {
                    copyOfChromosome = (IChromosome) chrom.clone();
                    a_candidateChromosomes.add(copyOfChromosome);
                    genes = copyOfChromosome.getGenes();
                    if (m_monitorActive) {
                        copyOfChromosome.setUniqueIDTemplate(chrom.getUniqueID(), 1);
                    }
                }
                if (genes[target] instanceof ICompositeGene) {
                    ICompositeGene compositeGene = (ICompositeGene) genes[target];
                    if (m_monitorActive) {
                        compositeGene.setUniqueIDTemplate(chrom.getGene(target).getUniqueID(), 1);
                    }
                    for (int k = 0; k < compositeGene.size(); k++) {
                        mutateGene(compositeGene.geneAt(k), generator);
                        if (m_monitorActive) {
                            compositeGene.geneAt(k).setUniqueIDTemplate(
                                    ((ICompositeGene) chrom.getGene(target)).geneAt(k).getUniqueID(),
                                    1);
                        }
                    }
                } else {
                    mutateGene(genes[target], generator);
                    if (m_monitorActive) {
                        genes[target].setUniqueIDTemplate(chrom.getGene(target).getUniqueID(), 1);
                    }
                }
            }
        }
    }

    private void mutateGene(final Gene a_gene, final RandomGenerator a_generator) {
        for (int k = 0; k < a_gene.size(); k++) {
            // Retrieve value between 0 and 1 (not included) from generator.
            // Then map this value to range -1 and 1 (-1 included, 1 not).
            // -------------------------------------------------------------
            double percentage = -1 + a_generator.nextDouble() * 2;
            // Mutate atomic element by calculated percentage.
            // -----------------------------------------------
            a_gene.applyMutation(k, percentage);
        }
    }
}
