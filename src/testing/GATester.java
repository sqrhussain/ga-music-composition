/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import ga.MusicCrssoverOperator;
import ga.MusicFitnessFunction;
import ga.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import midi.Helper;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.NaturalSelector;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.WeightedRouletteSelector;

/**
 *
 * @author hussain
 */
public class GATester {

    /**
     * @param args the command line arguments
     */
    private static final int PIECE_LENGTH = R.PIECE_LENGTH,
            MAX_NOTE = 24,
            MAX_ALLOWED_EVOLUTIONS = 10000;
    private static int POPULATION_SIZE;

    public List<Double> solve(String inputPath, String fitnessOutputPath) throws FileNotFoundException, InvalidConfigurationException, MidiUnavailableException, InvalidMidiDataException {
        Configuration conf = new DefaultConfiguration();
        conf.setPreservFittestIndividual(true);
        Helper helper = new Helper(inputPath);
        // ---------------------------------------------------------
        FitnessFunction myFunc = new MusicFitnessFunction(helper);
        conf.setFitnessFunction(myFunc);

        //CrossoverOperator crossover = new MusicCrssoverOperator(conf, 100, helper);
        //conf.addGeneticOperator(crossover);
        
        //NaturalSelector selector = new BestChromosomesSelector(conf);
        
        NaturalSelector selector = new WeightedRouletteSelector(conf);
        conf.addNaturalSelector(selector, false);
        
        //MutationOperator mutation = new MIDIMutationOperator(conf, 1000000);
        //conf.addGeneticOperator(mutation);
        
        // --------------------------------------------------------------
        Gene[] sampleGenes = new Gene[PIECE_LENGTH];
        for (int i = 0; i < PIECE_LENGTH; i++) {
            sampleGenes[i] = new IntegerGene(conf, 0, MAX_NOTE);
        }
        IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);
        // ------------------------------------------------------------
        conf.setPopulationSize(POPULATION_SIZE);
        Genotype population;
        population = Genotype.randomInitialGenotype(conf);
        double evolutionTime = System.currentTimeMillis();
        List<Double> fitness = new ArrayList<>();
        for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) {
            population.evolve();
            fitness.add(population.getFittestChromosome().getFitnessValue());
        }
        evolutionTime = System.currentTimeMillis() - evolutionTime;
        // ------------------------------------------------------------
        IChromosome bestSolutionSoFar = population.getFittestChromosome();
        System.out.println("The best solution has a fitness value of "
                + bestSolutionSoFar.getFitnessValue());
        System.out.printf("This took %f milliseconds%n", evolutionTime);
        int noteInt[] = new int[PIECE_LENGTH];
        for (int i = 0; i < bestSolutionSoFar.size(); i++) {
            noteInt[i] = ((Integer) bestSolutionSoFar.getGene(i).getAllele());
            System.out.print(noteInt[i] + " ");
        }
        System.out.println("");

        String[] noteStr = Helper.intToStr(noteInt);
        byte[] noteByte = Helper.strToBytes(noteStr);
        Sequence s = new Sequence(Sequence.PPQ, 1);
        Helper.bytesToTrack(noteByte, s);
        Helper.play(s);
        System.out.println(Arrays.toString(noteStr));

        // Write Fitness to file
        PrintStream out = new PrintStream(new File(fitnessOutputPath));
        String toWrite = fitness.toString();
        toWrite = toWrite.substring(1, toWrite.length() - 1);
        out.println(toWrite);
        out.close();

        // Write stats
        out = new PrintStream(new File(fitnessOutputPath + ".stat"));
        out.println("Time Elapsed\tLast Fitness Value");
        out.println(evolutionTime + "\t" + fitness.get(fitness.size() - 1));
        out.println("Note sequence:");
        out.println(Arrays.toString(noteStr));
        return fitness;
    }

    public static void main(String[] args) throws FileNotFoundException, InvalidConfigurationException, MidiUnavailableException, InvalidMidiDataException {
        POPULATION_SIZE = 700;
        new GATester().solve("data2", "wrs/population" + POPULATION_SIZE);
    }

}
