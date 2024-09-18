package org.example;

public class ByteNode implements Comparable<ByteNode> {

    private Byte data;
    private int frequency;
    private ByteNode leftNode;
    private ByteNode rightNode;

    public ByteNode(Byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    public Byte getData() {
        return data;
    }

    public int getFrequency() {
        return frequency;
    }

    public ByteNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(ByteNode leftNode) {
        this.leftNode = leftNode;
    }

    public ByteNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(ByteNode rightNode) {
        this.rightNode = rightNode;
    }

    @Override
    public int compareTo(ByteNode o) {
        return Integer.compare(this.frequency, o.frequency);
    }
}
