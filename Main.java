import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

/**
 * @author DGOAT
 * Date Started: 11/26/20
 * Date Finished: 12/4/20
 * Result: 4/4
 */
public class Main
{
    /** 
     * contains all pads in ascending order
     */ 
    static List<BigInteger> pads;

    /**
     * contains all pads in the order:
     * maximal -> neither 
     * where each number in the maximal/neither group
     * are sorted in ascending order
     */
    static List<BigInteger> priorityPads;

    /**
     * each key is a maximal pad and each set
     * is the set of numbers that have gcd > 1 
     * with the key including the key itself
     */
    static Map<BigInteger, Set<BigInteger>> maxPadGCDMap;

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
     * Default Constructor to instantiate all needed
     * static members
     */
    private static void init()
    {
        // instantiate all the needed static members
        pads = new ArrayList<>();
        lookup = new HashMap<List<BigInteger>, BigInteger>();
        minimal = new ArrayList<>();
        maximal = new ArrayList<>();
        both = new ArrayList<>();
        neither = new ArrayList<>();
        priorityPads = new ArrayList<BigInteger>();
        paths = new ArrayList<String>();
        maxPadGCDMap = new HashMap<BigInteger, Set<BigInteger>>();
    }


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
            // if b is ever 0 return a
            if (b.equals(BigInteger.ZERO))
                return a;
            else
                a = a.mod(b);

            // if a is ever 0 return b
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
     * Method to create a hashmap of all numbers that 
     * have a gcd greater than one with the key (a  maximal pad)
     * including the key itself
     */
    public static void createGcdSet4MaximalPads()
    {
        // for each pad
        for (BigInteger maxPad : maximal) 
        {
            Set<BigInteger> ts = new TreeSet<>();
            BigInteger min = null;
            BigInteger max = null;

            // create a set of numbers that share a gcd larger than 1
            for (BigInteger b : pads) 
            {
                min = b.min(maxPad);
                max = b.max(maxPad);

                // if we have it in the lookup table
                if (lookup.getOrDefault(Arrays.asList(min, max), gcd(min, max)).compareTo(BigInteger.ONE) > 0)
                    ts.add(b);
            }

            // place our new max pad -> [set of numbers with gcd greater than 1 including itself]
            maxPadGCDMap.put(maxPad, ts);
        }

    }


    /**
     * Method to find compatible path (if it is available)
     * @param b = the big integer value of the pad to find all compatible pads for
     * @return = a list indicating the path from b to a maximal pad if it exists
     * if there is a null anywhere in compatible list then disregard the whole list
     */
    public static List<BigInteger> findOptCompatiblePath(BigInteger b)
    {
        // all variable declarations to avoid repeat code
        List<BigInteger> compatibleList = new ArrayList<BigInteger>();
        BigInteger min = null;
        BigInteger max = null;
        BigInteger pad = null;
        BigInteger gcd = null;
        List<BigInteger> key = null;
        
        // get all compatible pads from this one to the next
        for (int i = 0; i < priorityPads.size(); i++)
        {
            // calculate this once per iteration
            pad = priorityPads.get(i);
            
            // if b >= pad disregard
            if (b.compareTo(pad) >= 0)
                continue;
            
            // get the correct order for the list
            min = b.min(pad);
            max = b.max(pad);
            key = Arrays.asList(min, max);
            
            // calculated this before
            if (lookup.containsKey(key))
                gcd = lookup.get(key);
                
            // haven't calculated this before
            else 
            {
                gcd = gcd(min, max);
                lookup.put(key, gcd);
            }
                
            // add to compatibleList if the gcd > 1
            if (gcd.compareTo(BigInteger.ONE) > 0)
            {
                compatibleList.add(pad);

                // not maximal pad? then we need to go deeper into recursion
                if (!maximal.contains(pad))
                    compatibleList.addAll(findOptCompatiblePath(pad));
                
                // return the compatible list
                return compatibleList;
            }
        }

        return null;
    }


    /**
     * Method to return the path the hobbit must travel to get
     * across the gorge
     * @param b = the value of the path you are currently on
     */
    public static void findPath(BigInteger b)
    {
        List<BigInteger> compatiblePath = findOptCompatiblePath(b);
        StringBuilder path = new StringBuilder("1 " + b.toString() + " ");

        for (BigInteger i : compatiblePath)
        {
            // add each pad to path and remove from pad pool
            path.append(i.toString() + " ");
            priorityPads.remove(i);
        }

         // add to our list of paths   
        paths.add(path.toString());
    }


    /**
     * Method to populate the static member 
     * paths with the paths of the maximum 
     * number of hobbits that can cross the 
     * gorge
     */
    public static void getHobbitPaths()
    {
        // this is the string we will use for the path
        // always starts at 1
        String temp = "1 ";

        // for both pads
        for (int i = 0; i < both.size(); i++) 
        {
            temp += both.get(i) + " ";
            paths.add(temp);
            temp = "1 ";
        }

        // maximum number of hobbits that can cross the gorge
        int numPaths = Math.min(minimal.size(), maximal.size()) + both.size();

        // from my list if max pads create a set that has gcd > 1
        createGcdSet4MaximalPads();

        // create a priority list
        priorityPads.addAll(maximal);
        priorityPads.addAll(neither);
        
        // start from minimal pad and work towards a maximal pad
        for (int i = 0; i < minimal.size(); i++)
        {
            findPath(minimal.get(i));

            if (paths.size() == numPaths)
            {
                System.out.print("FOUND THE OPTIMAL NUMBER");
                break;
            }

            else
            {
                System.out.print("SOMETHING IS WRONG! THER ARE LESS PATHS THAN OPTIMAL! ");
                System.exit(-1);                    
            }
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
                writer.println(path);

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

    /**
     * Method to test if the paths that were generated
     * follow the rules that were set in place
     * specifically maximality/minimality of pads
     * 
     * RULES:
     * - can only jump to a minimal pad from start (1)
     * - must end at a maximal pad
     * - each pad you jump to must be greater than the previous
     *   pad you were on and must share a gcd larger than 1
     * @return true if the paths follow all of the rules above
     *         false otherwise
     */
    public static boolean validPaths()
    {
        // check if each pad is valid
        for (String path : paths) 
        {
            // separate path by space
            List<String> pathPads = Arrays.asList(path.split(" "));
            BigInteger min = null;
            BigInteger max = null;

            // (0th pad is always 1) first pad must be a minimal pad
            if (!minimal.contains(new BigInteger(pathPads.get(1).strip()))
                    && !both.contains(new BigInteger(pathPads.get(1).strip())))
                return false;

            // last pad must be maximal
            if (!maximal.contains(new BigInteger(pathPads.get(pathPads.size() - 1).strip()))
                    && !both.contains(new BigInteger(pathPads.get(1).strip())))
                return false;

            // all middle pads must have gcd > 1
            for (int i = 2; i < pathPads.size(); i++) 
            {
                BigInteger a = new BigInteger(pathPads.get(i - 1).strip());
                BigInteger b = new BigInteger(pathPads.get(i).strip());
                min = a.min(b);
                max = a.max(b);

                // calculated this before
                if (lookup.getOrDefault(Arrays.asList(min, max), gcd(min, max)).equals(BigInteger.ONE))
                    return false;
            }
        }

        return true;
    }
    
    /**
     * Method to get the maximum number of hobbits across
     * the gorge
     */
    public static void getHobbitsAcrossTheGorge()
    {
        readInput("input.txt");
        definePads();
        getHobbitPaths();

        if (validPaths() == false)
        {
            System.out.print("HOUSTON WE HAVE A PROBLEM");
            System.exit(-1);
        }
        
        writeOutput("output.txt");
    }

    public static void main(String[] args)
    {
        init();
        getHobbitsAcrossTheGorge();
    }
}
