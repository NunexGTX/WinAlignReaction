import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class QuickCheckResultWatcher {
    private Path result_xml_path;

    private WatchService XMLWatcher;

    public QuickCheckResultWatcher(Path result_xml_path){
        this.result_xml_path = result_xml_path;

        try {
            XMLWatcher = FileSystems.getDefault().newWatchService();
            this.result_xml_path.getParent().register(XMLWatcher, ENTRY_CREATE, ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean watchXML() throws InterruptedException {
        WatchKey key = XMLWatcher.take(); // blocks until something happens

        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            Path changed = (Path) event.context();   // relative to the watched dir
            if (changed.equals(result_xml_path.getFileName())) {
                return true;
            }
        }

        if (!key.reset()) { // dir deleted or no longer accessible
            throw new FileSystemNotFoundException();
        }
        return false;
    }

    public int quickCheckResultVal(){
        return -1;
    }



}
