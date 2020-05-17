package dashview;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import dashview.Utils.Utils;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        arrayOfParam(10, "aa", "bb", "cc");
        assertTrue(true);
    }

    private void arrayOfParam(int va, String... info) {
        System.out.println(info.length);
        for (int i = 0; i < info.length; i++)
            System.out.println(info[i]);
    }

    @Test
    public void readFileTest() throws TransformerException {
        
        // String theHTML = Utils.readFile("target/site/apidocs/dashview/Interfaces/IExample.html");
        // System.out.println(theHTML);

        String java = Utils.readFile("src/main/java/dashview/Interfaces/IExample.java");
        System.out.println(java);
        
    }

}
