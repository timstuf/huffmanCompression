package huffman;

import file.HuffmanFileWriter;
import file.OriginalFileReader;
import file.OriginalFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.Node;
import tree.TableBuilder;
import tree.Tree;
import tree.TreeBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Coder {
    final static Logger logger = LoggerFactory.getLogger(Coder.class);
    private Tree tree;
    private final String path;

    Coder(String path) throws FileNotFoundException {
        this.path = path;
    }

    void codeMessage() throws IOException {
        String message = new OriginalFileReader(path).readFile();
        logger.debug("message to be coded = {}", message);
        Map<Character, Integer> table = getFrequencyTable(message);
        tree = new TreeBuilder().build(table);
        Map<Character, String> huffmanMap = new TableBuilder().buildTable(tree);

        String encodedMessage = shifrMessage(huffmanMap, message);
        writeTable(table);
        Byte[] encodedFileBytes = convertListToByteArray(encodeBytes(encodedMessage));
        new HuffmanFileWriter(encodedFileBytes, new File("res.hf")).writeIntoFile("");
    }

    private void writeTree() throws IOException {
        String encodedTree = tree.encodeHuffmanTree();
        List<Byte> b = encodeBytes(encodedTree);
        Byte[] encodedFileBytes = convertListToByteArray(b);
        new HuffmanFileWriter(encodedFileBytes, new File("D:\\resTa.hf")).writeIntoFile("");
    }

    private void writeTable(Map<Character, Integer> table) throws IOException {
        StringBuilder st = new StringBuilder();
        table.forEach((key, value) -> {
            st.append(key);
            st.append(' ');
            st.append(value);
            st.append('\n');
        });
        st.deleteCharAt(st.length() - 1);
        new OriginalFileWriter("table.txt").writeIntoFile(st.toString());
    }

    private String shifrMessage(Map<Character, String> huffmanMap, String message) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            result.append(huffmanMap.get(message.charAt(i)));
        }
        result.append(huffmanMap.get(Constants.EOF));
        logger.debug("encoded message = {}", result.toString());
        return result.toString();
    }

    private Map<Character, Integer> getFrequencyTable(String message) {
        Map<Character, Integer> table = new HashMap<>();
        List<Character> messageInChars = new ArrayList<>();
        for (char ch : message.toCharArray()) {
            messageInChars.add(ch);
        }

        messageInChars.forEach((ch) -> {
            if (table.containsKey(ch)) {
                table.put(ch, 1 + table.get(ch));
            } else {
                table.put(ch, 1);
            }
        });
        table.put(Constants.EOF, 1);
        table.forEach((key, value) -> logger.debug("the frequency table: character {}, it's frequency {}", key, value));
        return table;
    }

    private Byte[] convertListToByteArray(List<Byte> bytes) {
        Byte[] actualBytes = new Byte[bytes.size()];
        return bytes.toArray(actualBytes);
    }

    private List<Byte> encodeBytes(String encodedMessage) {
        List<Byte> bytes = new ArrayList<>();
        StringBuilder message = new StringBuilder(encodedMessage);
        int size;
        if (message.length() % Constants.BITS_IN_BYTE == 0) size = message.length() / Constants.BITS_IN_BYTE;
        else size = message.length() / Constants.BITS_IN_BYTE + 1;
        while (message.length() > 0) {
            if (message.length() < Constants.BITS_IN_BYTE) {
                int len = message.length();
                for (int i = Constants.BITS_IN_BYTE - 1; i >= len; i--) {
                    message.append("0");
                }
            }
            bytes.add(convertBitArrayToByte(convertByteToBitsArray(message.substring(0, Constants.BITS_IN_BYTE))));
            message.delete(0, Constants.BITS_IN_BYTE);
        }
        return bytes;
    }

    private byte convertBitArrayToByte(byte[] bits) {
        byte buffer = 0x00;
        for (int i = 0; i < Constants.BITS_IN_BYTE; i++) {
            buffer = (byte) (buffer << 1);
            buffer |= bits[i];
        }
        logger.debug("the byte that holds bits : {}", buffer);
        return buffer;
    }

    private byte[] convertByteToBitsArray(String mes) {
        StringBuilder loggerMessage = new StringBuilder();
        byte[] bits = new byte[Constants.BITS_IN_BYTE];
        for (int i = 0; i < Constants.BITS_IN_BYTE; i++) {
            if (mes.charAt(i) == '0') bits[i] = 0;
            else bits[i] = 1;
            loggerMessage.append(bits[i]);
        }
        logger.debug("message converted to bits (actually bytes) array: {}", loggerMessage.toString());
        return bits;
    }

}
