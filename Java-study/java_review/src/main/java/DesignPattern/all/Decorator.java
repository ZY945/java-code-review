package DesignPattern.all;

interface DataStream {
    void write(String data);
}

class BasicStream implements DataStream {
    public void write(String data) {
        System.out.println("Writing: " + data);
    }
}

class CompressedStream implements DataStream {
    private DataStream stream;

    public CompressedStream(DataStream stream) {
        this.stream = stream;
    }

    public void write(String data) {
        System.out.println("Compressing data");
        stream.write(data);
    }
}

public class Decorator {
    public static void main(String[] args) {
        DataStream stream = new CompressedStream(new BasicStream());
        stream.write("Hello, World!");
    }
}