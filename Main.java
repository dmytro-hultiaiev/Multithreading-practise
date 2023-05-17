import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.*;

class Thread1 implements Runnable {
    private final CommonResource CR1;
    private final Thread thread;
    private final FileWriter file;

    public Thread1(CommonResource CR1, FileWriter file){
        this.CR1 = CR1;
        this.file = file;
        thread = new Thread(this, "Thread1");
        thread.start();
    }

    public Thread getThread(){
        return thread;
    }

    @Override
    public void run() {
        while (!GlobalVar.isCondition()){
            CR1.consume(thread.getName());
        }

        if (!GlobalVar.barrier.isBroken()){
            GlobalVar.barrier.reset();
        }
        GlobalVar.SR1.release();
        GlobalVar.SR2.release();
        try {
            file.write(thread.getName() + " finished!\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Thread2 implements Runnable {
    private final CommonResource CR1;
    private final Thread thread;
    private final FileWriter file;

    public Thread2(CommonResource CR1, FileWriter file){
        this.CR1 = CR1;

        this.file = file;
        thread = new Thread(this, "Thread2");
        thread.start();
    }

    public Thread getThread(){
        return thread;
    }

    @Override
    public void run() {
        while (!GlobalVar.isCondition()){
            try {
                GlobalVar.SR2.release();
                file.write(thread.getName() + " opens semaphore SR2 for the Thread3\n");

                GlobalVar.SR1.acquire();
                file.write(thread.getName() + " works AFTER synchronization point\n");

                CR1.produce(thread.getName());
            } catch (IOException | InterruptedException e) {
                System.out.println(thread.getName() + " error: " + e);
            }
        }
        if (!GlobalVar.barrier.isBroken()){
            GlobalVar.barrier.reset();
        }
        GlobalVar.SR1.release();
        GlobalVar.SR2.release();
        try {
            file.write(thread.getName() + " finished!\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Thread3 implements Runnable {
    private final Thread thread;
    private final FileWriter file;
    private final ReentrantLock mutex;

    public Thread3(ReentrantLock mutex, FileWriter file){
        this.mutex = mutex;
        this.file = file;

        thread = new Thread(this, "Thread3");
        thread.start();
    }

    public Thread getThread(){
        return thread;
    }

    @Override
    public void run() {
        while (!GlobalVar.isCondition()){
            try {
                mutex.lock();
                GlobalVar.CR2_byte = (byte)(GlobalVar.CR2_byte + 3);
                GlobalVar.CR2_short = (short)(GlobalVar.CR2_byte + 10);
                GlobalVar.CR2_int += 108;
                file.write(thread.getName() + " modified CR2_byte, CR2_short, CR2_int\n");
                mutex.unlock();

                GlobalVar.barrier.await();
                file.write(thread.getName() + " synchronized in barrier\n");

                mutex.lock();
                double sum = GlobalVar.CR2_long + GlobalVar.CR2_float + GlobalVar.CR2_double;
                file.write(thread.getName() +" use (sum) CR2_long, CR2_float, CR2_double and get result: " + sum + "\n");
                mutex.unlock();

                GlobalVar.SR1.release();
                file.write(thread.getName() + " opens semaphore SR1 for the Thread2\n");

                GlobalVar.SR2.acquire();
                file.write(thread.getName() + " works AFTER synchronization point\n");

            } catch (IOException | InterruptedException e) {
                System.out.println(thread.getName() + " error: " + e);
            } catch (BrokenBarrierException e){
                break;
            }
        }
        if (!GlobalVar.barrier.isBroken()){
            GlobalVar.barrier.reset();
        }
        GlobalVar.SR1.release();
        GlobalVar.SR2.release();
        try {
            file.write(thread.getName()  + " finished!\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Thread4 implements Runnable {
    private final CommonResource CR1;
    private final Thread thread;
    private final FileWriter file;

    public Thread4(CommonResource CR1, FileWriter file){
        this.CR1 = CR1;
        this.file = file;
        thread = new Thread(this, "Thread4");
        thread.start();
    }

    public Thread getThread(){
        return thread;
    }

    @Override
    public void run() {
        while (!GlobalVar.isCondition()){
            CR1.consume(thread.getName());
        }
        if (!GlobalVar.barrier.isBroken()){
            GlobalVar.barrier.reset();
        }
        GlobalVar.SR1.release();
        GlobalVar.SR2.release();
        try {
            file.write(thread.getName() + " finished!\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Thread5 implements Runnable {
    private final CommonResource CR1;
    private final Thread thread;
    private final FileWriter file;

    public Thread5(CommonResource CR1, FileWriter file){
        this.CR1 = CR1;
        this.file = file;
        thread = new Thread(this, "Thread5");
        thread.start();
    }

    public Thread getThread(){
        return thread;
    }

    @Override
    public void run() {
        while (!GlobalVar.isCondition()){
            CR1.produce(thread.getName());
        }
        if (!GlobalVar.barrier.isBroken()){
            GlobalVar.barrier.reset();
        }
        GlobalVar.SR1.release();
        GlobalVar.SR2.release();
        try {
            file.write(thread.getName() + " finished!\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Thread6 implements Runnable {
    private final Thread thread;
    private final FileWriter file;
    private final ReentrantLock mutex;

    public Thread6(ReentrantLock mutex, FileWriter file){
        this.mutex = mutex;
        this.file = file;

        thread = new Thread(this, "Thread6");
        thread.start();
    }

    public Thread getThread(){
        return thread;
    }

    @Override
    public void run() {
        while (!GlobalVar.isCondition()){
            try {

                GlobalVar.barrier.await();
                file.write(thread.getName() + " synchronized in barrier\n");

                mutex.lock();
                if(GlobalVar.CR2_boolean){
                    GlobalVar.CR2_char = 'a';
                }
                else {
                    GlobalVar.CR2_char = 'b';
                }
                file.write(thread.getName() + " use CR2_boolean, CR2_char\n");
                mutex.unlock();

            } catch (IOException | InterruptedException e) {
                System.out.println(thread.getName() + " error: " + e);
            } catch (BrokenBarrierException e){
                break;
            }
        }
        if (!GlobalVar.barrier.isBroken()){
            GlobalVar.barrier.reset();
        }
        GlobalVar.SR1.release();
        GlobalVar.SR2.release();
        try {
            file.write(thread.getName() + " finished!\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try{
            FileWriter file = new FileWriter("src/result.log");

            CommonResource CR1 = new CommonResource(7, file);
            ReentrantLock mutex = new ReentrantLock();

            Thread1 thread1 = new Thread1(CR1, file);
            Thread2 thread2 = new Thread2(CR1, file);
            Thread3 thread3 = new Thread3(mutex, file);
            Thread4 thread4 = new Thread4(CR1, file);
            Thread5 thread5 = new Thread5(CR1, file);
            Thread6 thread6 = new Thread6(mutex, file);

            thread1.getThread().join();
            thread2.getThread().join();
            thread3.getThread().join();
            thread4.getThread().join();
            thread5.getThread().join();
            thread6.getThread().join();

            file.write("Main finished");
            file.close();

        } catch (IOException | InterruptedException e) {
            System.out.println("Main error: " + e);
        }
    }
}