package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.crypto.NullCipher;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
    sortedCharFreqList = new ArrayList<CharFreq>();
    int[] occurences = new int[128];
    double totalChars = 0;
    while(StdIn.hasNextChar()){
        char Character=StdIn.readChar();
        occurences[Character]++;
        totalChars++;
    }
    for(int i= 0; i<128;i++){
        if(occurences[i] != 0){
            char c =(char)i;
            double freq=occurences[i]/totalChars;
            sortedCharFreqList.add(new CharFreq(c,freq));
        }
    }
    if(sortedCharFreqList.size()==1){
        int num = (int) sortedCharFreqList.get(0).getCharacter();
        if(num==127){num=0;}
        else{num++;}
        sortedCharFreqList.add(new CharFreq((char)num,0.0));
    }
	Collections.sort(sortedCharFreqList);
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
    //make a loop that iterates through each element of the sortedCharFreqList
    Queue<TreeNode> source =new Queue<TreeNode>();
    Queue<TreeNode> target =new Queue<TreeNode>();
    Queue<TreeNode> ptrs= new Queue<TreeNode>();
   
    for(int i = 0; i<sortedCharFreqList.size(); i++){
       TreeNode temp = new TreeNode(sortedCharFreqList.get(i),null,null);
        source.enqueue(temp);
    }
    while( !source.isEmpty() || target.size() != 1){
        while(ptrs.size()<2){
            if(target.isEmpty()){
                ptrs.enqueue(source.dequeue());
            }
                else{
                    if(source.isEmpty() == false){
                        if(source.peek().getData().getProbOcc() <= target.peek().getData().getProbOcc()){
                            ptrs.enqueue(source.dequeue());
                        }
                        else{
                            ptrs.enqueue(target.dequeue());
                    }
                }
                else if(source.isEmpty()){
                    ptrs.enqueue(target.dequeue());
                }
            }
        }
        TreeNode ptr1=ptrs.isEmpty() ? null :ptrs.dequeue();
        TreeNode ptr2=ptrs.isEmpty() ? null :ptrs.dequeue();
        double p1 = (ptr1==null) ? 0 :ptr1.getData().getProbOcc();
        double p2 = (ptr2==null) ? 0 :ptr2.getData().getProbOcc();
        
        target.enqueue(new TreeNode(new CharFreq(null,p1+p2),ptr1,ptr2));
    }
    huffmanRoot=target.dequeue();
}
   // while(source.peek() != null && target.size() > 1){
     //   TreeNode first = new TreeNode();
      //  TreeNode second = new TreeNode();
       // if(target.peek() == null){
        //    first = source.dequeue();
         //   second = source.dequeue();
       // }
        //double total = first.getData().getProbOcc() + second.getData().getProbOcc();
        //target.enqueue(new TreeNode(new CharFreq(null,total), first, second));
    //}


    
	/* Your code goes here */

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {

	/* Your code goes here */
    encodings=new String[128];
    makeEncodings(huffmanRoot, "");
}

private void makeEncodings(TreeNode root, String s){
    if (root==null) 
        return;
    else {
        makeEncodings(root.getLeft(), s + "0");
        if (root.getData().getCharacter() != null) {
            encodings[root.getData().getCharacter()] = s;
            return;
        }
        makeEncodings(root.getRight(), s + "1");
    }
    }


    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String bits="";
        StdIn.setFile(fileName);
        makeEncodings();
        while(StdIn.hasNextChar()){
            bits+=encodings[StdIn.readChar()];
        }
        writeBitString(encodedFile, bits);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);

	/* Your code goes here */
    StdOut.setFile(decodedFile);
    makeEncodings();
    String bits=readBitString(encodedFile);
    //System.out.println(bitString);
    TreeNode root=huffmanRoot;
    for (Character strings : bits.toCharArray()) {
        if(strings=='0') 
            root=root.getLeft();
        if(strings=='1') 
            root=root.getRight();
        if(root.getData().getCharacter()!=null){
            StdOut.print(root.getData().getCharacter());
            root=huffmanRoot;
        }
    }
}


    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
