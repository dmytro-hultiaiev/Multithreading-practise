import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class GlobalVar {
    public static byte CR2_byte = 12;
    public static short CR2_short = 200;
    public static int CR2_int = 1800;
    public static long CR2_long = 890000L;
    public static float CR2_float = 1.7f;
    public static double CR2_double = 18.9;
    public static boolean CR2_boolean = true;
    public static char CR2_char = 'c';
    public static int fullBuffer = 0;
    public static int emptyBuffer = 0;
    public static CyclicBarrier barrier = new CyclicBarrier(2);
    public static Semaphore SR1 = new Semaphore(0 ,true);
    public static Semaphore SR2 = new Semaphore(0 ,true);

    public static boolean isCondition(){
        return (fullBuffer >= 2 && emptyBuffer >= 2);
    }
}
