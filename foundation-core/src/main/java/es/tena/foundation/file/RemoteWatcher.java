package es.tena.foundation.file;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public abstract class RemoteWatcher {
    
    protected static final Logger logger = Logger.getLogger(RemoteWatcher.class.getSimpleName());
    /**
     * Monitors a remote path with a delay
     * @param delay integer in milliseconds
     * @param path a remote path to be monitored: \\\\remoteserver\\folder
     * @throws org.apache.commons.vfs2.FileSystemException 
     */
    public void watchRemoteFolder(final int delay, final String path) throws org.apache.commons.vfs2.FileSystemException {
        /**
         * need a non-daemon thread, because <code>DefaultFileMonitor</code> is
         * internally marked as a daemon thread.
         */
        Thread t = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    while (true) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Error al monitorizar {0}", path);
                }
            }
        });
        t.start();
        FileSystemManager manager = VFS.getManager();
        FileObject file = manager.resolveFile(path);

        DefaultFileMonitor fm = new DefaultFileMonitor(new FileListener() {
            @Override
            public void fileChanged(final FileChangeEvent fileChangeEvt) throws Exception {
                whenFileChanged(fileChangeEvt);
            }

            @Override
            public void fileCreated(FileChangeEvent fileChangeEvt) throws Exception {
                whenFileCreated(fileChangeEvt);
            }

            @Override
            public void fileDeleted(FileChangeEvent fileChangeEvt) throws Exception {
                whenFileDeleted(fileChangeEvt);
            }
        });

        fm.setDelay(delay);
        fm.addFile(file);
//        FileObject[] children = file.getChildren();
//        for (FileObject child : children) {
//            System.out.println(child.getURL());
//        }
        fm.start();
    }

    public abstract void whenFileDeleted(FileChangeEvent fileChangeEvt);
    public abstract void whenFileCreated(FileChangeEvent fileChangeEvt);
    public abstract void whenFileChanged(FileChangeEvent fileChangeEvt);
//        System.out.println("@" + System.currentTimeMillis() + ": " + fileChangeEvt.getFile().getName() + " created ..");
}
