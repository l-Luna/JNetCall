package jnetbase.java.threads;

import java.util.LinkedList;
import java.util.List;

public final class ThreadExecutor implements Executor{

    private final List<Thread> _threads;

    public ThreadExecutor() {
        _threads = new LinkedList<>();
    }

    @Override
    public void close() {
        for (var thread : _threads)
            thread.interrupt();
        _threads.clear();
    }

    @Override
    public Thread createThread(Runnable action, String name) {      	
        var task = new Thread(action);
        task.setDaemon(true);
        if (name != null)
          	task.setName(name);
        _threads.add(task);
        task.start();
        return task;
    }

    @Override
    public Thread newThread(Runnable action) {
        return createThread(action, null);
    }
}
