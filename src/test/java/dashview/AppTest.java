package dashview;

import static org.junit.Assert.assertTrue;

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
    public void readFileTest()  {
        
        // String theHTML = Utils.readFile("target/site/apidocs/dashview/Interfaces/IExample.html");
        // System.out.println(theHTML);

        String java = Utils.readFile("src/main/java/dashview/Interfaces/IExample.java");
        System.out.println(java);
        
    }

}
