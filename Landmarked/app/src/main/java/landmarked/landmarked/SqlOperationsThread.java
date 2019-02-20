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
        //this function will overload Thread.start() to initialize a thread if it hasn't been initialized. For example
        //when the app is being started up.
        if(myThread == null)
        {
            myThread = new Thread(this, "SQL operations");
        }
        myThread.start();
    }

    public void lock()
    {
        myThread.
    }
}
