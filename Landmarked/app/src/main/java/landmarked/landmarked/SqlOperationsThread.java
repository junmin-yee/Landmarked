package landmarked.landmarked;

import java.util.concurrent.locks.ReentrantLock;

public class SqlOperationsThread extends Thread {
    private Thread myThread;
    private final ReentrantLock threadLock = new ReentrantLock();

    public void run()
    {
        //overwritten method that executes when start() is called on thread
    }

    public void start()
    {

    }
}
