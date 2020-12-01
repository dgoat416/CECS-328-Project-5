// Deron Washington II
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

/**
 * @author DGOAT
 * Date Started: 11/26/20
 * Date Finished: 11/28/20
 * Result: 
 */
public class Main
{
    /** 
     * contains all pads in ascending order
     */ 
    static List<BigInteger> pads;

    /**
     * contains all pads that are neither 
     * minimal, maximal or both
     */
    static List<BigInteger> neither;

    /**
     * contains all the minimal only pads
     */
    static List<BigInteger> minimal;

    /**
     * contains all the maximal only pads
     */
    static List<BigInteger> maximal;

    /**
     * contains all pads that are both minimal
     * and maximal
     */
    static List<BigInteger> both;

    /**
     * store previously calculated gcd elements 
     * and gcds
     */
    static Map<List<BigInteger>, BigInteger> lookup;
    
    /** 
     * contains the path of each hobbit that 
     * can make it across the gorge 
     */
    static List<String> paths;

    /**
     * A class to represent a pad
     */
    public static class Pad implements Comparable<Pad>
    {
        /**
         * value of the pad
         */
        public BigInteger value;

        /**
         * a flag to inform us if 
         * the pad is a minimal pad
         */
        public boolean min;

        /**
         * a flag to inform us if 
         * the pad is a maximal pad
         */
        public boolean max;

        /**
         * a flag to tell us if this pad
         * has been used and fell into the
         * gorge or can still be used to get
         * a hobbit across the gorge
         */
        public boolean isGood;

        /**
         * Default Constructor
         */
        public Pad() {
            this.value = new BigInteger("0");
            this.min = false;
            this.max = false;
            this.isGood = false;
        }

        /**
         * Parameterized Constructor
         * @param value = the value of the pad
         */
        public Pad(BigInteger value) {
            this.value = value;
            this.min = false;
            this.max = false;
            this.isGood = false;
        }

        /**
         * Parameterized Constructor
         * @param value = the value of the pad
         * @param isGood = can we use this pad
         */
        public Pad(BigInteger value, boolean isGood) {
            this.value = value;
            this.min = false;
            this.max = false;
            this.isGood = isGood;
        }


        /**
         * Parameterized Constructor
         * @param value = the value of the pad
         * @param isGood = can we use this pad?
         * @param min = is this a minimal pad?
         * @param max = is this a maximal pad?
         */
        public Pad(BigInteger value, boolean min, boolean max, boolean isGood) {
            this.value = value;
            this.min = min;
            this.max = max;
            this.isGood = isGood;
        }

		@Override
        public int compareTo(Pad o) {
			return this.value.compareTo(o.value);
		}

    }
    

    /**
     * Method to determine the Euclidean gcd
     * @param a = value to find gcd with
     * @param b = value to find gcd with
     * @return the gcd of a and b using the Euclidean gcd
     */
    public static BigInteger gcd(BigInteger a, BigInteger b)
    {
        // make sure the numbers are positive
        a = a.abs();
        b = b.abs();
      
        while (true)
        {
            // if a is ever 0 return b
            if (b.equals(BigInteger.ZERO))
                return a;
            else
                a = a.mod(b);

            // if b is ever 0 return a
            if (a.equals(BigInteger.ZERO))
                return b;
            else
                b = b.mod(a);
        }
    }


    /**
     * Method to determine if each pad is minimal, maximal, or both
     */
    public static void definePads()
    {           
        // instantiate all the needed static members
        lookup = new HashMap<List<BigInteger>, BigInteger>();
        minimal = new ArrayList<>();
        maximal = new ArrayList<>();
        both = new ArrayList<>();
        neither = new ArrayList<>();

        // for every pad in pad list determine if it is minimal, maximal, both or neither
        for (int i = 0; i < pads.size(); i++)
        {
            // make a pad out of the sorted list of numbers representing pads
            Pad temp = new Pad(pads.get(i), true, true, true);
            List<BigInteger> key = null;
            BigInteger gcd = null;
            BigInteger min = null;
            BigInteger max = null;

            // minimal? 
            for (int j = 0; j < i; j++)
            {
                // create the key to place or lookup in the hashmap
                min = temp.value.min(pads.get(j));
                max = temp.value.max(pads.get(j));
                key = Arrays.asList(min, max);

                // calculated this before
                if (lookup.containsKey(key))
                {
                    gcd = lookup.get(key);
                    
                    // not minimal
                    if (!gcd.equals(BigInteger.ONE))
                    {
                        temp.min = false;
                        break;
                    }

                }

                // haven't calculated this before
                gcd = gcd(pads.get(i), pads.get(j));
                lookup.put(key, gcd);

                // not minimal
                if (!gcd.equals(BigInteger.ONE))
                {
                    temp.min = false;
                    break;
                }
            }
            
            // maximal?
            for (int k = i + 1; k < pads.size(); k++)
            {
                // create the key to place or lookup in the hashmap
                min = temp.value.min(pads.get(k));
                max = temp.value.max(pads.get(k));
                key = Arrays.asList(min, max);
                 
                // calculated this before
                if (lookup.containsKey(key))
                {
                    gcd = lookup.get(key);
                    
                    // not maximal
                    if (!gcd.equals(BigInteger.ONE))
                    {
                        temp.max = false;
                        break;
                    }

                }

                // haven't calculated this before
                gcd = gcd(pads.get(i), pads.get(k));
                lookup.put(key, gcd);

                // not maximal
                if (!gcd.equals(BigInteger.ONE))
                {
                    temp.max = false;
                    break;
                 }
            }
            
            // both min and maximal pad
            if (temp.min == true && temp.max == true)
                both.add(temp.value);

            // only minimal pad
            else if (temp.min == true)
                minimal.add(temp.value);

            // only maximal pad
            else if (temp.max == true)
                maximal.add(temp.value);

            // none of the above    
            else 
                neither.add(temp.value);

        }
    }


    /**
     * Method to populate the static member 
     * paths with the paths of the maximum 
     * number of hobbits that can cross the 
     * gorge
     */
    public static void getHobbitPaths()
    {
        // instantitate static member
        paths = new ArrayList<String>();

        // this is the string we will use for the path
        // always starts at 1
        String temp = "1";

        // for both pads
        for (int i = 0; i < both.size(); i++) {
            temp += " " + both.get(i);
            paths.add(temp);
            temp = "1";
        }



        // hop from 1 -> minimal pad -> find another pad where gcd(minimal pad, another pad) > 1 
        // when looking for another pad search through maximal pads first then check neither pads
        // until we reach a maximal pad

        boolean maximalPadFound = false;
        int numPaths = Math.min(minimal.size(), maximal.size()) + both.size();
        int min = 0;
        int max = 0;
        StringBuilder path = new StringBuilder();
        
        // start from minimal pad and work towards a maximal pad
        for (int i = 0; i < minimal.size(); i++)
        {
            while (!maximalPadFound)
            {
                
            }

            temp = "1";
        }

        // for maximal pads
        do {
            temp += " " + minimal.get(min) + " " + maximal.get(max);
            paths.add(temp);

            if (minimal.get(min).compareTo(maximal.get(max)) > 0)
            {
                System.out.print("SOMETHING IS WRONG! Maximal is less than minimal! ");
                System.exit(-1);
            }


            min++;
            max++;
            temp = "1";
        } while (min < minimal.size() && max < maximal.size());

        // check if we have the maximal number of paths
        if (paths.size() < Math.min(minimal.size(), maximal.size()) + both.size()) {
            System.out.print("SOMETHING IS WRONG! THER ARE LESS PATHS THAN OPTIMAL! ");
            System.exit(-1);

        }

    }
    


    /**
     * Method to read the input from the file and populate 
     * the static reference of the pads accordingly
     * @param fileName = name of the input file
     */
	public static void readInput(String fileName) 
    {
        // Read input from file
        File inFile = new File(fileName);
        Scanner scan = null;

        // instanitate the static member representing our pads
        pads = new ArrayList<>();

        try 
        {
            scan = new Scanner(inFile);

            // get the pad
            while (scan.hasNextLine())
                pads.add(new BigInteger(scan.nextLine()));

            // sort the array
            Collections.sort(pads);

        } 
        catch (FileNotFoundException e) 
        {

            e.printStackTrace();
        } 
        finally 
        {
            scan.close();
        }

    }

    
	/**
     * Method to output the two paths of the maximum
     * number of hobbits that can make it across the
     * gorge 
     * @param outputName = name of the output file
     */
	public static void writeOutput(String outputName) 
    {
        File outFile = new File(outputName);
        PrintWriter writer = null;

        try 
        {
            writer = new PrintWriter(outFile);

            // print out the paths
            for (String path : paths)
                writer.println(path + " ");

        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            writer.close();
        }

    }

    
    public static void main(String[] args)
    {
        readInput("input (8).txt");
        definePads();
        getHobbitPaths();
        writeOutput("o.txt");
    }
}