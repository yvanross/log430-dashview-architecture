package dashview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void readFileTest() {

        // String theHTML =
        // Utils.readFile("target/site/apidocs/dashview/Interfaces/IExample.html");
        // System.out.println(theHTML);

        String java = Utils.readFile("src/main/java/dashview/Interfaces/IExample.java");
        System.out.println(java);

    }

    @Test
    public void getDocTagsTest() {
        JavadocToMarkdown jdtm = new JavadocToMarkdown();
        List<Map.Entry<String,String>> result =  jdtm.getDocTags("* @param this is a test");
        System.out.println(result);
        assertEquals(1,result.size());
        assertEquals("@param",result.get(0).getKey());
        assertEquals("this is a test",result.get(0).getValue());
        
    }

    @Test
    public void matcherTest(){
        final Pattern p = Pattern.compile("^(?:[ |\t|*])*(@[a-zA-Z]+)(?:[\\s\\S])(.*$)");
        Matcher m = p.matcher("* @param this is a test");
        assertTrue(m.find());
        assertEquals(2,m.groupCount());
        assertEquals("@param",m.group(1));
        assertEquals("this is a test",m.group(2));
    }


    @Test
    public void javadoc2MarkdownTest()  {
        
        String code = Utils.readFile("src/test/java/dashview/data/javadoc.txt");
        String expected = Utils.readFile("src/test/java/dashview/data/javadoc.expected.txt");
        JavadocToMarkdown jdtm = new JavadocToMarkdown();
        String result = jdtm.fromJavadoc(code, 1);
        System.out.println(result);
        assertEquals(jdtm.fromJavadoc(code, 1),expected);
    }



}