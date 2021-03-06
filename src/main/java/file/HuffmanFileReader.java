package file;

import huffman.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.Tree;

import java.io.*;

public class HuffmanFileReader {
    private final static Logger logger = LoggerFactory.getLogger(HuffmanFileReader.class);
    private File readingFile;

    public HuffmanFileReader(String path) {
        readingFile = new File(path);
    }

    private byte[] getByteInBits(int nextByte){
         byte[] bits = new byte[Constants.BITS_IN_BYTE];
        for(int i = 0; i<Constants.BITS_IN_BYTE; i++){
            int b = ((nextByte >> (Constants.BITS_IN_BYTE-i-1)));
            if(b%2==0) bits[i] = 0;
            else bits[i] = 1;
        }
        return bits;
    }
    private String bitsToString(byte[] nextByte){
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < Constants.BITS_IN_BYTE; i++) {
            if(nextByte[i]==0) message.append("0");
            else message.append("1");
        }
        return message.toString();
    }

    public String readFile() throws IOException {
        FileInputStream fis = new FileInputStream(readingFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        int b;
        StringBuilder message = new StringBuilder();
        while((b= bis.read())!=-1){
            message.append(bitsToString(getByteInBits(b)));
        }
        logger.debug("decoded message: {}", message);
        return message.toString();
    }
}
