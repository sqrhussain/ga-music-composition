/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package midi;

import ga.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 *
 * @author hussain
 */
public class Helper {

    private static final int VELOCITY = 127;
    private static final int NOTES_NUMBER = 24;
    private final String dataFileName;
    private static Map<String, Byte> map;
    private static Map<String, Integer> base;
    private static String[] reverseMap;
    private static final int BASE_NUMBER = R.BASE_NUMBER;

    static {
        map = new HashMap<>();
        base = new HashMap<>();
        reverseMap = new String[25];
        Scanner sc = new Scanner("60 C C# D D# E F F# G G# A A# B c c# d d# e f f# g g# a a# b");
        Byte st = (byte) sc.nextInt();
        int j = 1;
        for (int i = 0; i < NOTES_NUMBER; i++) {
            String str = sc.next();
            map.put(str, st);
            base.put(str, j);
            reverseMap[j] = str;
            st++;
            j++;
        }
        reverseMap[0] = "S";
        map.put("S", (byte) 0);
        map.put("s", (byte) 0);
        base.put("S", 0);
        base.put("s", 0);
    }
    private String[][] notes;

    public Map<Integer, double[]> before() {
        Map<Integer, double[]> ret = new HashMap<>();
        int pieces = notes.length;
        // count
        for (int piece = 0; piece < pieces; piece++) {
            int n = notes[piece].length;
            String[] x = notes[piece];
            for (int i = 0; i < n; i++) {
                int a = i > 3 ? base.get(x[i - 3]) : 0;
                int b = i > 2 ? base.get(x[i - 2]) : 0;
                int c = i > 1 ? base.get(x[i - 1]) : 0;
                int j = base.get(x[i]);
                int idx = a;
                idx *= BASE_NUMBER;
                idx += b;
                idx *= BASE_NUMBER;
                idx += c;
                if (!ret.containsKey(idx)) {
                    ret.put(idx, new double[25]);
                }
                double[] t = ret.get(idx);
                t[j]++;
                ret.put(idx, t);
            }
        }
        // normalize
        Set<Integer> keys = ret.keySet();
        for (Integer key : keys) {
            //System.out.print(key+": ");
            double den = 0;
            double[] arr = ret.get(key);
            for (int k = 0; k < arr.length; k++) {
                den += arr[k];
            }
            if (den == .0) {
                continue;
            }
            for (int k = 0; k < arr.length; k++) {
                arr[k] /= den;
            }
            //out(Arrays.toString(arr));
            ret.put(key, arr);
        }

        return ret;
    }

    public Map<Integer, double[]> after() {
        Map<Integer, double[]> ret = new HashMap<>();
        int pieces = notes.length;
        // count
        for (int piece = 0; piece < pieces; piece++) {
            int n = notes[piece].length;
            for (int i = 0; i < n; i++) {
                String[] x = notes[piece];
                int a = i < n - 4 ? base.get(x[i + 3]) : 0;
                int b = i < n - 3 ? base.get(x[i + 2]) : 0;
                int c = i < n - 2 ? base.get(x[i + 1]) : 0;
                int j = base.get(x[i]);
                int idx = a;
                idx *= BASE_NUMBER;
                idx += b;
                idx *= BASE_NUMBER;
                idx += c;
                if (!ret.containsKey(idx)) {
                    ret.put(idx, new double[25]);
                }
                double[] p = ret.get(idx);
                p[j]++;
                ret.put(idx, p);
            }
        }
        // normalize
        Set<Integer> keys = ret.keySet();
        for (Integer key : keys) {
            //System.out.print(key+": ");
            double den = 0;
            double[] arr = ret.get(key);
            for (int k = 0; k < arr.length; k++) {
                den += arr[k];
            }
            if (den == .0) {
                continue;
            }
            for (int k = 0; k < arr.length; k++) {
                arr[k] /= den;
            }
            //out(Arrays.toString(arr));
            ret.put(key, arr);
        }

        return ret;
    }

    private void initialize() throws FileNotFoundException {

        Scanner sc = new Scanner(new File(dataFileName));
        int pieces = sc.nextInt();
        notes = new String[pieces][];
        for (int piece = 0; piece < pieces; piece++) {
            int n = sc.nextInt();
            notes[piece] = new String[n];
            for (int i = 0; i < n; i++) {
                notes[piece][i] = sc.next();
            }
        }
    }

    public Helper(String dataFileName) throws FileNotFoundException {
        this.dataFileName = dataFileName;
        initialize();
    }

    public static byte[] strToBytes(String[] strs) {
        int n = strs.length;
        byte[] ret = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = map.get(strs[i]);
        }
        return ret;
    }

    public static void bytesToTrack(byte[] b, Sequence s) {
        Track track = s.createTrack();
        int n = b.length;
        for (int i = 0; i < n; i++) {
            byte note = b[i];
            if (note == 0) {
                track.add(createNoteOffEvent(note, i));
            } else {
                track.add(createNoteOnEvent(note, i));
            }
        }
    }

    public static String[] intToStr(int[] a) {
        String[] ret = new String[a.length];
        int n = a.length;
        for (int i = 0; i < n; i++) {
            ret[i] = reverseMap[a[i]];
        }
        return ret;
    }

    public static void play(Sequence s) throws MidiUnavailableException, InvalidMidiDataException {
        final Sequencer sr = MidiSystem.getSequencer();
        sr.open();
        sr.setTempoInBPM(192);
        sr.setSequence(s);
        sr.addMetaEventListener(new MetaEventListener() {

            @Override
            public void meta(MetaMessage metaMsg) {
                if (metaMsg.getType() == 0x2F) {
                    sr.close();
                }
            }
        });
        sr.start();
    }

    private void solve() throws FileNotFoundException, InvalidMidiDataException, MidiUnavailableException, IOException {
        byte[] b = strToBytes(notes[0]);
        Sequence s = new Sequence(Sequence.PPQ, 1);
        bytesToTrack(b, s);

        play(s);
        write(s, new File("test.mid"));
        //before();
//        Scanner sc = new Scanner(new File(dataFileName));

//        int t = sc.nextInt();
//        while(t-->0){
//            int n = sc.nextInt();
//            String [] notes = new String[n];
//            for(int i = 0 ; i < n ; i++){
//                notes[i] = sc.next();
//            }
//            byte[] b = strToBytes(notes);
//            Sequence s = new Sequence(Sequence.PPQ, 1);
//            bytesToTrack(b, s);
//            
//            play(s);
//        }
    }

    public static void main(String[] args) throws FileNotFoundException, InvalidMidiDataException, MidiUnavailableException, IOException {

        new Helper("data2").solve();
    }

    private static MidiEvent createNoteOnEvent(int nKey, long lTick) {
        return createNoteEvent(ShortMessage.NOTE_ON,
                nKey,
                VELOCITY,
                lTick);
    }

    private static MidiEvent createNoteOffEvent(int nKey, long lTick) {
        return createNoteEvent(ShortMessage.NOTE_OFF,
                nKey,
                0,
                lTick);
    }

    private static MidiEvent createNoteEvent(int nCommand,
            int nKey,
            int nVelocity,
            long lTick) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(nCommand,
                    0, // always on channel 1
                    nKey,
                    nVelocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.exit(1);
        }
        MidiEvent event = new MidiEvent(message,
                lTick);
        return event;
    }

    public static void write(Sequence sequence, File file) throws IOException {
        MidiSystem.write(sequence, 1, file);
    }

    private static void out(String strMessage) {
        System.out.println(strMessage);
    }
}
