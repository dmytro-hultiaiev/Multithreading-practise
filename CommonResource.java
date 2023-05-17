import java.io.FileWriter;
import java.io.IOException;

public class CommonResource {
    private int[] buffer;
    private int nextIn;
    private int nextOut;
    private int elementsQuantity;
    private final FileWriter file;

    public CommonResource(int buf_size, FileWriter file){
        buffer = new int[buf_size];
        nextIn = 0;
        nextOut = 0;
        elementsQuantity = 0;
        this.file = file;
    }

    synchronized void produce(String thread_name){

        while(elementsQuantity == buffer.length){
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Producer error: " + e);
            }
        }

        int num = (int)(Math.random()*100);

        buffer[nextIn] = num;

        try {
            file.write("Producer " + thread_name + " in write to buffer[" + nextIn + "] value: " + num + "\n");
        } catch (IOException e) {
            System.out.println("Producer error: " + e);
        }

        nextIn = (nextIn + 1) % buffer.length;
        elementsQuantity++;
        if(nextIn == 0) GlobalVar.fullBuffer++;

        notify();
    }

    synchronized void consume(String thread_name){

        while(elementsQuantity == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Consumer error: " + e);
            }
        }

        try {
            file.write("Consumer " + thread_name + " get in buffer[" + nextOut + "] value: " + buffer[nextOut] + "\n");
        } catch (IOException e) {
            System.out.println("Consumer error: " + e);
        }

        nextOut = (nextOut + 1) % buffer.length;
        elementsQuantity--;
        if(nextOut == 0) GlobalVar.emptyBuffer++;

        notify();
    }
}