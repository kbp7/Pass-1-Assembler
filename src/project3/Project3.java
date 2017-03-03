package project3;
/*
KEVIN POON
N00900355
Intro to Systems Software
Instructor Sandeep Reddivari

This program implements a pass 1 assembler for SIC/XE programs. 
Reads a text file containing SIC/XE commands and creates a symbol table.
SICOPS.txt contains all valid SIC/XE commands and their designated
opcode and bytes consumed. 

Output: Results.txt
        Contains the original input file contents followed by the table which 
        displays each label and address in hexadecimal.
 */
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.net.URL;

class DataItem {                                
    private int val;               //value
    private String word;           //key
    private String opcode;
    private int bytes;
    private int address;
    private String label;
    
//--------------------------------------------------------------
    public DataItem(int ii, String xx, String yy)   { //for symbol table
        address = ii;
        label = xx;
        word = yy;
    }
//--------------------------------------------------------------
    public DataItem(String xx, String yy, int num)   { //for sicops
        word = xx;
        opcode = yy; 
        bytes = num;
    }    
//--------------------------------------------------------------
    public int getVal() { 
        return val; 
    }
//--------------------------------------------------------------
    public String getKey()  {
        return word;
    }
//--------------------------------------------------------------
    public String getOp()  {
        return opcode;
    }
//--------------------------------------------------------------
    public int getBytes()  {
        return bytes;
    }
//--------------------------------------------------------------
    public int getAddress()  {
        return address;
    }
//--------------------------------------------------------------
    public String getLabel()  {
        return label;
    }
//--------------------------------------------------------------
}  
////////////////////////////////////////////////////////////////
class HashTable
    {
    private final DataItem[] hashArray;    // array holds hash table
    private final int arraySize;
    //private DataItem nonItem;        // for deleted items
// -------------------------------------------------------------
    public HashTable(int size)       // constructor
        {
        arraySize = size;
        hashArray = new DataItem[arraySize];
        //nonItem = new DataItem(null, -1);   // deleted item key is -1
        }
// -------------------------------------------------------------
    public void displayTable() //SICOPS TABLE
        {
        System.out.println("================== SICOPS ====================");
        for(int j=0; j<arraySize; j++)
            {
            if(hashArray[j] != null)
                System.out.println("[" + j + "]" + hashArray[j].getKey()
				   + " [Opcode: " + hashArray[j].getOp() + "]"
                                   + " [Bytes: " + hashArray[j].getBytes() + "]");
            
        }
        System.out.println();
    }
// -------------------------------------------------------------
    public void displaySym() //Symbol table
        {
        System.out.println("Table Location   Label     Address");
        for(int j=0; j<arraySize; j++)
            {
            if(hashArray[j] != null)
                System.out.printf("%-16d %-9s %-9S\n", j,
                                   hashArray[j].getLabel(), 
                                   String.format("%03X", hashArray[j].getAddress()));
        }
        System.out.println();
    }
// -------------------------------------------------------------
    public void displayResults(String filename, HashTable sicops) throws IOException  {
        URL path = Project3.class.getResource(filename);
        File myFile = new File(path.getFile());
        String[] input;
        
        try (BufferedReader buff = new BufferedReader(new FileReader(myFile))) {
            
            input = new String[100];
            String line;
            
            //copy lines into string array
            for (int i=0; ((line = buff.readLine()) != null); i++) {
                input[i] = line;
                //System.out.println(input[i]);
            }
        }
        int currentAddress = 0; //default start address
        HashTable sym = new HashTable(500);
        int search;
        System.out.println();
        //tokenize the line
        for (int i=0; input[i]!= null; i++) {
            //whitespace is delimiter
            String[] token = input[i].split("\\s+");
            //blank line
            if(token.length<=1) {
                if(token[0].equals("")) {
                    System.out.println();
                }
                if(token[0].contains("."))    {
                    System.out.printf("        %-40s\n", input[i]);
                }
            }
            if(token.length>=2)    {
                if(token[0].contains("."))  {
                    System.out.printf("        %-40s\n", input[i]);
                    continue;
                }
                //keywords: START, BASE, END, WORD, RESW, BYTE, RESB
                if(token[1].equals("START"))    {
                    if(token[0].equals("")) {
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        continue;
                    }
                    else    {
                        currentAddress = Integer.parseInt(token[2], 16);
                        DataItem item = new DataItem(currentAddress, token[0], token[1]);
                        sym.insert(item);
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        continue;
                    }
                }
                if(token[1].equals("BASE"))    {
                    DataItem item = new DataItem(currentAddress, token[0], token[1]);
                    sym.insert(item);
                    System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                    continue;
                }
                if(token[1].equals("END"))  {
                    System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                    break;
                }
                String s = token[1];
                if(!token[0].equals("")) { //has label
                    if(!s.equals("WORD") && !s.equals("RESW") && !s.equals("BYTE") && !s.equals("RESB")) {
                        search = sicops.find(hashFunc(token[1]), token[1]);
                        if(search < 0)  { //not found in SICOPS table
                            System.out.println("[Invalid Instruction: " + token[1] + "]");
                            continue;
                        }
                        else    {
                           
                            DataItem item = new DataItem(currentAddress, token[0], token[1]);
                            sym.insert(item);
                            System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                            currentAddress += (sicops.hashArray[search].getBytes());
                            continue;
                        }
                    }
                    if(s.equals("RESW")) {
                        DataItem item = new DataItem(currentAddress, token[0], token[1]);
                        sym.insert(item);
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        currentAddress += (3 * Integer.parseInt(token[2]));
                        continue;
                    }
                    if(s.equals("RESB")) {
                        DataItem item = new DataItem(currentAddress, token[0], token[1]);
                        sym.insert(item);
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        currentAddress += (Integer.parseInt(token[2]));
                        continue;
                    }
                    if(s.equals("WORD")) {
                        DataItem item = new DataItem(currentAddress, token[0], token[1]);
                        sym.insert(item);
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        currentAddress += 3;
                    }   
                    if(s.equals("BYTE")) {
                        DataItem item = new DataItem(currentAddress, token[0], token[1]);
                        sym.insert(item);
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        currentAddress += 1;
                    }
                }
                    //no label
                else if(token[0].equals(""))  {
                    if(token[1].equals("WORD")) {
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        currentAddress += 3;
                        continue;
                    }
                    if(token[1].equals("BYTE")) {
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        currentAddress += 1;
                        continue;
                    }
                    search = sicops.find(hashFunc(token[1]), token[1]);
                    if(search < 0)  { //not found in sicops table
                        System.out.println("[Invalid Instruction: " + token[1] + "]");
                    }
                    else    {
                        System.out.printf("%-7s %-40s\n", String.format("%04X", currentAddress), input[i]);
                        currentAddress += (sicops.hashArray[search].getBytes());
                    }
                }
                
            }       
        }
        System.out.println();
        sym.displaySym();   
    }   
// -------------------------------------------------------------
    public int hashFunc(String myString)
        {
        //unique hashVal even for different words with same letters
        int intLength = myString.length() / 4;
        long sum = 0;
        for (int j = 0; j < intLength; j++) {
            char c[] = myString.substring(j * 4, (j * 4) + 4).toCharArray();
            long mult = 1;
            for (int k = 0; k < c.length; k++) {
                sum += c[k] * mult;
                mult *= 256;
                }
            }

        char c[] = myString.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
            }

        return(int)(Math.abs(sum) % arraySize);
        }
        
      
// -------------------------------------------------------------
    public void insert(DataItem item) // insert a DataItem
    // (assumes table not full)
        {
        String key = item.getKey();      // extract key
        int value = item.getBytes();
        int hashVal = hashFunc(key);  // hash the key
                                    // until empty cell or -1,
                                    
        if (value == -1) {
            System.out.println("Searching for " + key+"...");
            int findResult = find(hashVal, key);
            //System.out.println("find result: " + findResult);
            if(findResult == -1) {
                System.out.println("ERROR: " + key + " not found.");
            }
            
            return;
        }
        int j = 0; //for quad probe
        while(hashArray[hashVal] != null) {
            
            //System.out.println("COLLISION: Index " + hashVal + " contains "
                                //+ hashArray[hashVal].getKey()); //collisions
            ++j;
            hashVal = (hashVal + j * j);  //quadratic probing
            hashVal %= arraySize;         // wraparound
            }
        if (hashArray[hashVal] == null) {
            hashArray[hashVal] = item;    // insert item
            //System.out.println("Stored " + key + " at index " + hashVal + " value: " + value);
        }
        
    }
      // end insert()

// -------------------------------------------------------------
    public int find(int hashVal, String key)    // find item with key
        {
        int j = 0;
        int cellsCheck = 0;
        while((hashArray[hashVal] != null) && (cellsCheck<arraySize))  // until empty cell,
            {                               // found the key?
            //System.out.println(hashArray[hashVal].getKey());
            if(hashArray[hashVal].getKey().equals(key)) {
                /*
                System.out.println(key + " found at index " + hashVal
                + " value: " + hashArray[hashVal].getBytes());
                */
                return hashVal;   // yes, return item
                }
            ++j;
            hashVal = (hashVal + j * j);                 // go to next cell
            hashVal %= arraySize;      // wraparound if necessary
            cellsCheck++;
        }
        //System.out.println("index "+hashVal+" contains "+ hashArray[hashVal]);
        return -1;                  // can't find item
    }
//-----------------------------------------------------------------------------
    public int findLabel(String label)    // find item with key
        {
        for(int i=0; i<arraySize; i++)  {
            if(hashArray[i].getLabel().equals(label))   {
                System.out.println("Duplicate label: " + label);
                return -1;
            }
        }
        return 1;           
    }
// -------------------------------------------------------------
}  // end class HashTable
////////////////////////////////////////////////////////////////

public class Project3 {
    
    public static void main(String[] args) throws IOException {
        
        System.out.println("Project 3 Start...");
        System.out.println("Input file: " + args[0]);
        URL path = Project3.class.getResource("SICOPS.txt");
        File file = new File(path.getFile());   
        
        //begin creating table

        int size;
                                    // get sizes
        size = 136;
        
                                      // make table
        HashTable theHashTable = new HashTable(size);
        System.out.println("Size of table: " + size);
        String cl; //current line
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringTokenizer st;
            String oper; //operand or register
            String ops;  //opcode
            int format;  //format/bytes consumed
            
            while (((cl = br.readLine())) != null) {
                st = new StringTokenizer(cl);
                oper = st.nextToken();
                ops = st.nextToken();
                if(st.hasMoreTokens())
                    format = Integer.parseInt(st.nextToken());
                else
                    format = 0;
                DataItem myItem = new DataItem(oper, ops, format);
                theHashTable.insert(myItem);
            }
        }
        //display symbol table
        theHashTable.displayTable();
        //read input file to string array
        theHashTable.displayResults(args[0], theHashTable);
                
                
    }//end main    

//--------------------------------------------------------------
   public static String getString() throws IOException
      {
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(isr);
      String s = br.readLine();
      return s;
      }
//--------------------------------------------------------------
   public static char getChar() throws IOException
      {
      String s = getString();
      return s.charAt(0);
      }
//-------------------------------------------------------------
   public static int getInt() throws IOException
      {
      String s = getString();
      return Integer.parseInt(s);
      }
//--------------------------------------------------------------
   }  
////////////////////////////////////////////////////////////////

