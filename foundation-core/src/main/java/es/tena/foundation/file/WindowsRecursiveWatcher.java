package es.tena.foundation.file;

import com.sun.nio.file.ExtendedWatchEventModifier;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class WindowsRecursiveWatcher extends RecursiveWatcher {

    private WatchService watchService;
    private WatchKey rootWatchKey;
    private List<Kind> registeredKinds = new ArrayList<>();

    public WindowsRecursiveWatcher(Path root, List<Path> ignorePaths, int settleDelay, Trigger listener, List<WatchEvent.Kind> kinds) {
        super(root, ignorePaths, settleDelay, listener);
        this.watchService = null;
        this.rootWatchKey = null;
        
        if (kinds == null) {
            registeredKinds.add(StandardWatchEventKinds.ENTRY_CREATE);
            registeredKinds.add(StandardWatchEventKinds.ENTRY_DELETE);
            registeredKinds.add(StandardWatchEventKinds.ENTRY_MODIFY);
            registeredKinds.add(StandardWatchEventKinds.OVERFLOW);
        } else {
            registeredKinds = kinds;
        }
    }

    @Override
    public void beforeStart() throws Exception {
        Path extLibRootDir = Paths.get(root.toString());
        
        watchService = FileSystems.getDefault().newWatchService();
        WatchEvent.Modifier modifier = ExtendedWatchEventModifier.FILE_TREE;
        rootWatchKey = extLibRootDir.register(watchService, registeredKinds.toArray(new Kind[registeredKinds.size()]), modifier);
    }

    @Override
    protected void beforePollEventLoop() {
        // Nothing must happen before the event loop.
    }

    @Override
    public boolean pollEvents() throws InterruptedException {
        WatchKey watchKey = watchService.take();

        List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
        boolean hasRelevantEvents = false;

        // Filter ignored events
        for (WatchEvent<?> watchEvent : watchEvents) {
            if (registeredKinds.contains(watchEvent.kind())) {
                boolean ignoreEvent = false;

                Path extLibFilePath = (Path) watchEvent.context();
                Path filePath = toAbsoluteNormalizedPath(extLibFilePath.toString());

                for (Path ignorePath : ignorePaths) {
                    if (filePath.startsWith(ignorePath.toAbsolutePath().normalize())) {
                        ignoreEvent = true;
                        break;
                    }
                }
                if (!ignoreEvent) {
                    listener.eventTriggered(watchEvent.kind(), filePath);
                    hasRelevantEvents = true;
                    break;
                }
            }
        }

        watchKey.reset();
        return hasRelevantEvents;
    }

    private Path toAbsoluteNormalizedPath(String potentiallyRelativePathStr) {
        Path filePath = Paths.get(potentiallyRelativePathStr);

        if (!filePath.isAbsolute()) {
            return Paths.get(root.toString(), filePath.toString()).normalize();
        } else {
            return filePath.normalize();
        }
    }

    @Override
    public synchronized void afterStop() throws IOException {
        rootWatchKey.cancel();
        watchService.close();
    }
}
