import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;

import static java.nio.file.StandardWatchEventKinds.*;

public class QuickCheckResultWatcher {
    private ConfigManager configManager;
    private Path intercepted_xml;
    private WatchService XMLWatcher;

    public static final int READ_ERROR = -1;
    public static final int[] validTestValues = new int[]{0,1,2,3};  //Undefined, pass, warning, fail

    private WatchKey key;

    public QuickCheckResultWatcher(ConfigManager configManager){
        this.configManager = configManager;

        try {
            XMLWatcher = FileSystems.getDefault().newWatchService();
            configManager.getXml_dir().register(XMLWatcher, ENTRY_CREATE, ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean watchXML() throws InterruptedException {
        System.out.println("Waiting for a new XML file at: "+configManager.getXml_dir());
        key = XMLWatcher.take(); // blocks until something happens

        for (WatchEvent<?> event : key.pollEvents()) {
            //WatchEvent.Kind<?> kind = event.kind(); //Gives what type of operation it was from those we are watching, either a created or a modified file
            String new_file_created = event.context().toString();

            key.reset(); // Re-arm key

            if (new_file_created.endsWith(".xml")) {
                //Only accept valid .xml files
                intercepted_xml = Path.of(configManager.getXml_dir() + "/" + new_file_created);
                System.out.println("New xml detected at: "+intercepted_xml);
                return true;
            }
        }

        if (!key.reset()) { // dir deleted or no longer accessible
            throw new FileSystemNotFoundException();
        }
        return false;
    }

    public void watchRelease(){
        key.reset();
    }

    public TestResultValue quickCheckResultVal(){
        try (InputStream in = Files.newInputStream(intercepted_xml)){
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            InputSource src = new InputSource(new InputStreamReader(in, StandardCharsets.UTF_8)); //To make sure it converts the XML file into an expected UTF_8 character format
            Document doc = dbf.newDocumentBuilder().parse(src);

            String result = XPathFactory.newInstance().newXPath().evaluate("//MEAS[@OBJECT='"+ configManager.getMeasObject().name() +"']/VALUE/@RESULT", doc);
            int testResult = Integer.parseInt(result.trim());

            if (Arrays.stream(validTestValues).anyMatch(v -> v == testResult)) {
                return TestResultValue.fromCode(testResult);
            }else {
                return TestResultValue.fromCode(-1);
            }
        }catch (IOException | SAXException e) {
            // File missing/unreadable, or XML malformed (e.g. caught mid-write)
            System.err.println("Could not read " + configManager.getXml_dir() + ": " + e.getMessage());
        } catch (ParserConfigurationException | XPathExpressionException e) {
            // Setup or XPath problem, which points to a bug rather than bad data
            System.err.println("XML processing error: " + e.getMessage());
        } catch (NumberFormatException e) {
            // WHEEL_ALIGNMENT missing, or RESULT empty / not a number
            System.err.println(configManager.getMeasObject().name() + " RESULT missing or invalid: " + e.getMessage());
        }

        return TestResultValue.fromCode(-1);
    }



}
