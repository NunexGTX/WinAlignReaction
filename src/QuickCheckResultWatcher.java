import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

import static java.nio.file.StandardWatchEventKinds.*;

public class QuickCheckResultWatcher {
    private Path result_xml_dir;

    private WatchService XMLWatcher;
    public static final int READ_ERROR = -1;
    public static final int[] validTestValues = new int[]{0,1,2,3};  //Undefined, pass, warning, fail
    public static final String MEASObject = "WHEEL_ALIGNMENT";

    public QuickCheckResultWatcher(Path result_xml_dir){
        this.result_xml_dir = result_xml_dir;

        try {
            XMLWatcher = FileSystems.getDefault().newWatchService();
            this.result_xml_dir.register(XMLWatcher, ENTRY_CREATE, ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean watchXML() throws InterruptedException {
        WatchKey key = XMLWatcher.take(); // blocks until something happens

        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            Path changed = (Path) event.context();   // relative to the watched dir
            if (changed.equals(result_xml_dir.getFileName())) {
                return true;
            }
        }

        if (!key.reset()) { // dir deleted or no longer accessible
            throw new FileSystemNotFoundException();
        }
        return false;
    }

    public TestResultValue quickCheckResultVal(){
        try{
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(result_xml_dir.toFile());
            String result = XPathFactory.newInstance().newXPath().evaluate("//MEAS[@OBJECT='"+MEASObject+"']/VALUE/@RESULT", doc);
            int testResult = Integer.parseInt(result.trim());

            if (Arrays.stream(validTestValues).anyMatch(v -> v == testResult)) {
                return TestResultValue.fromCode(testResult);
            }else {
                return TestResultValue.fromCode(-1);
            }
        }catch (IOException | SAXException e) {
            // File missing/unreadable, or XML malformed (e.g. caught mid-write)
            System.err.println("Could not read " + result_xml_dir + ": " + e.getMessage());
        } catch (ParserConfigurationException | XPathExpressionException e) {
            // Setup or XPath problem, which points to a bug rather than bad data
            System.err.println("XML processing error: " + e.getMessage());
        } catch (NumberFormatException e) {
            // WHEEL_ALIGNMENT missing, or RESULT empty / not a number
            System.err.println(MEASObject + " RESULT missing or invalid: " + e.getMessage());
        }

        return TestResultValue.fromCode(-1);
    }



}
