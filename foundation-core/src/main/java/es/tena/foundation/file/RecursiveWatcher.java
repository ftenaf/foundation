package es.tena.foundation.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public abstract class RecursiveWatcher {

    protected static final Logger logger = Logger.getLogger(RecursiveWatcher.class.getSimpleName());

    protected Path root;
    protected List<Path> ignorePaths;
    private final int settleDelay;
    protected final Trigger listener;

    private final AtomicBoolean running;

    private Thread watchThread;
    private Timer timer;

    public RecursiveWatcher(Path root, List<Path> ignorePaths, int settleDelay, Trigger listener) {
        this.root = root;
        this.ignorePaths = ignorePaths;
        this.settleDelay = settleDelay;
        this.listener = listener;

        this.running = new AtomicBoolean(false);
    }

    /**
     * Creates a recursive watcher for the given root path. The returned watcher
     * will ignore the ignore paths and fire an event through the
     * {@link WatchListener} as soon as the settle delay (in ms) has passed.
     *
     * <p>
     * The method returns a platform-specific recursive watcher:
     * {@link WindowsRecursiveWatcher} for Windows and
     * {@link DefaultRecursiveWatcher} for other operating systems.
     *
     * @param root
     * @param ignorePaths
     * @param settleDelay
     * @param listener
     * @return
     */
    public static RecursiveWatcher createRecursiveWatcher(Path root, List<Path> ignorePaths, int settleDelay, Trigger listener) {
        return new WindowsRecursiveWatcher(root, ignorePaths, settleDelay, listener, null);
    }

    /**
     * Starts the watcher service and registers watches in all of the
     * sub-folders of the given root folder.
     *
     * <p>
     * This method calls the {@link #beforeStart()} method before everything
     * else. Subclasses may execute their own commands there. Before the watch
     * thread is started, {@link #beforePollEventLoop()} is called. And in the
     * watch thread loop, {@link #pollEvents()} is called.
     *
     * <p>
     * <b>Important:</b> This method returns immediately, even though the
     * watches might not be in place yet. For large file trees, it might take
     * several seconds until all directories are being monitored. For normal
     * cases (1-100 folders), this should not take longer than a few
     * milliseconds.
     *
     * @throws java.lang.Exception
     */
    public void start() throws Exception {
        // Call before-start hook
        beforeStart();

        // Start watcher thread
        watchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                running.set(true);
                beforePollEventLoop(); // Call before-loop hook

                while (running.get()) {
                    try {
                        boolean relevantEvents = pollEvents();
                        if (relevantEvents) {
                            restartWaitSettlementTimer();
                        }
                    } catch (InterruptedException e) {
                        logger.log(Level.FINE, "Could not poll the events", e);
                        running.set(false);
                    }
                }
            }
        }, "Watcher/" + root.toFile().getName());

        watchThread.start();
    }

    /**
     * Stops the watch thread by interrupting it and subsequently calls the
     * {@link #afterStop()} template method (to be implemented by subclasses.
     */
    public synchronized void stop() {
        if (watchThread != null) {
            try {
                running.set(false);
                watchThread.interrupt();

                // Call after-stop hook
                afterStop();
            } catch (IOException e) {
                logger.log(Level.FINE, "Could not close watcher", e);
            }
        }
    }

    private synchronized void restartWaitSettlementTimer() {
        logger.log(Level.FINE, "File system events registered. Waiting {0}ms for settlement ....", settleDelay);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer("FsSettleTim/" + root.toFile().getName());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.log(Level.FINE, "File system actions (on watched folders) settled. Updating watches ...");
//                watchEventsOccurred();
//                fireListenerEvents();
            }
        }, settleDelay);
    }

//    private synchronized void fireListenerEvents() {
//        if (listener != null) {
//            logger.log(Level.INFO, "- Firing watch event (watchEventsOccurred) ...");
////            listener.eventTriggered();
//        }
//    }

    /**
     * Called before the {@link #start()} method. This method is only called
     * once.
     *
     * @throws java.lang.Exception
     */
    protected abstract void beforeStart() throws Exception;

    /**
     * Called in the watch service polling thread, right before the
     * {@link #pollEvents()} loop. This method is only called once.
     */
    protected abstract void beforePollEventLoop();

    /**
     * Called in the watch service polling thread, inside of the
     * {@link #pollEvents()} loop. This method is called multiple times.
     *
     * @return
     * @throws java.lang.InterruptedException
     */
    protected abstract boolean pollEvents() throws InterruptedException;

    /**
     * Called in the watch service polling thread, whenever a file system event
     * occurs. This may be used by subclasses to (re-)set watches on folders.
     * This method is called multiple times.
     * @return 
     */
//    protected abstract String watchEventsOccurred();

    /**
     * Called after the {@link #stop()} method. This method is only called once.
     *
     * @throws java.io.IOException
     */
    protected abstract void afterStop() throws IOException;

    public interface Trigger {
        public void eventTriggered(WatchEvent.Kind k, Path p);
    }
}
