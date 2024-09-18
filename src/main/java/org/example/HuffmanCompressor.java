package org.example;

import java.io.*;
import java.util.*;

public class HuffmanCompressor {
    public static void compress(File source, File destination){

        try(FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(destination);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)){

            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            Object[] huffmanData = createZip(bytes);

            objectOutputStream.writeObject(huffmanData[0]);
            objectOutputStream.writeObject(huffmanData[1]);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Object[] createZip(byte[] bytes){
        PriorityQueue<ByteNode> nodes = generateNodes(bytes);
        ByteNode root = createHuffmanTree(nodes);
        Map<Byte, String> huffmanCodes= getHuffmanCodes(root);
        byte[] huffmanBytes = createZipWithBytesAndCodes(bytes, huffmanCodes);
        return new Object[]{huffmanBytes,huffmanCodes};
    }

    private static PriorityQueue<ByteNode> generateNodes(byte[] bytes){
        Map<Byte, Integer> nodesValue = new HashMap<>();
        PriorityQueue<ByteNode> nodes = new PriorityQueue<>();

        for(byte b : bytes){
            nodesValue.put(b, nodesValue.getOrDefault(b, 0) + 1);
        }

        for(Map.Entry<Byte, Integer> entry : nodesValue.entrySet()){
            nodes.add(new ByteNode(entry.getKey(), entry.getValue()));
        }

        return nodes;
    }

    private static ByteNode createHuffmanTree(PriorityQueue<ByteNode> nodes) {
        while(nodes.size() > 1){
            ByteNode leftNode = nodes.poll();
            ByteNode rightNode = nodes.poll();
            ByteNode newNode = new ByteNode(null, leftNode.getFrequency() + rightNode.getFrequency());
            newNode.setLeftNode(leftNode);
            newNode.setRightNode(rightNode);
            nodes.add(newNode);
        }
        return nodes.poll();
    }

    private static Map<Byte, String> getHuffmanCodes(ByteNode root){
        Map<Byte, String> huffmanCodes = new HashMap<>();

        class Codes{
            void getCodes(ByteNode node, String code) {
                if(node != null){
                    if(node.getData() == null){
                        getCodes(node.getLeftNode(), code + "0"  );
                        getCodes(node.getRightNode(),code + "1");
                    }
                    else {
                        huffmanCodes.put(node.getData(), code);
                    }
                }
            }
        }

        Codes c = new Codes();
        c.getCodes(root.getLeftNode(), "0");
        c.getCodes(root.getRightNode(), "1");

        return huffmanCodes;
    }
    private static byte[] createZipWithBytesAndCodes(byte[] bytes, Map<Byte, String> huffmanCodes){
        StringBuilder strBuilder = new StringBuilder();
        for (byte b : bytes)
            strBuilder.append(huffmanCodes.get(b));

        int length=(strBuilder.length()+7)/8;
        byte[] huffmanBytes = new byte[length];
        int idx = 0;
        for (int i = 0; i < strBuilder.length(); i += 8) {
            String strByte;
            if (i + 8 > strBuilder.length())
                strByte = strBuilder.substring(i);
            else
                strByte = strBuilder.substring(i, i + 8);
            huffmanBytes[idx] = (byte) Integer.parseInt(strByte, 2);
            idx++;
        }
        return huffmanBytes;
    }
    public static void decompress(File source, File destination) {
        try (FileInputStream inStream = new FileInputStream(source);
             ObjectInputStream objectInStream = new ObjectInputStream(inStream);
             FileOutputStream outStream = new FileOutputStream(destination);) {

            byte[] huffmanBytes = (byte[]) objectInStream.readObject();
            Map<Byte, String> huffmanCodes = (Map<Byte, String>) objectInStream.readObject();

            byte[] bytes = performDecompression(huffmanCodes, huffmanBytes);

            outStream.write(bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] performDecompression(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder huffmanString = new StringBuilder();
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);
            huffmanString.append(byteToBinaryString(!flag, b));
        }
        Map<String, Byte> decoderMap = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            decoderMap.put(entry.getValue(), entry.getKey());
        }
        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < huffmanString.length();) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                if (i + count > huffmanString.length()) break;
                String key = huffmanString.substring(i, i + count);
                b = decoderMap.get(key);
                if (b == null) count++;
                else flag = false;
            }
            byteList.add(b);
            i += count;
        }
        byte[] originalBytes = new byte[byteList.size()];
        for (int i = 0; i < originalBytes.length; i++)
            if(byteList.get(i) != null)
                originalBytes[i] = byteList.get(i);
        return originalBytes;
    }
    private static String byteToBinaryString(boolean flag, byte b) {
        int byte0 = b;
        if (flag) byte0 |= 256;
        String str0 = Integer.toBinaryString(byte0);
        if (flag || byte0 < 0)
            return str0.substring(str0.length() - 8);
        else return str0;
    }
}
