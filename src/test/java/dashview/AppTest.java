package dashview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import dashview.Utils.JavadocToMarkdown;
import dashview.Utils.Section;
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
        /*@ Jailbreak*/ JavadocToMarkdown jdtm = new JavadocToMarkdown();
        List<Map.Entry<String, String>> result = jdtm.getDocTags("* @param this is a test");
        System.out.println(result);
        assertEquals(1, result.size());
        assertEquals("@param", result.get(0).getKey());
        assertEquals("this is a test", result.get(0).getValue());

        ArrayList<Map.Entry<String,ArrayList<String>>> assocBuffer = new ArrayList<Map.Entry<String,ArrayList<String>>>();
        jdtm.fnAddTagsMarkdown(result.get(0), assocBuffer);
        assertEquals(1,assocBuffer.size());

    }

    @Test
    public void tokenize()
    {
        String value = "one the first";
        String[] result = value.split("\s+", 2);
        assertEquals("one",result[0]);
        assertEquals("the first",result[1]);
    }

        


    @Test
    public void matcherTest() {
        final Pattern p = Pattern.compile("^(?:[ |\t|*])*(@[a-zA-Z]+)(?:[\\s\\S])(.*$)");
        Matcher m = p.matcher("* @param this is a test");
        assertTrue(m.find());
        assertEquals(2, m.groupCount());
        assertEquals("@param", m.group(1));
        assertEquals("this is a test", m.group(2));
    }

    @Test
    public void getSection() {
        JavadocToMarkdown jdtm = new JavadocToMarkdown();
        String code = Utils.readFile("src/test/java/dashview/data/javadoc.txt");
        final List<Section> sections = jdtm.getSections(code);
         System.out.println(sections.get(0).doc);
        // System.out.println("-----------------");
        // System.out.println(sections.get(5).line);

        assertTrue(sections.get(0).doc.contains("this is an example do the classe my program"));
        assertTrue(sections.get(0).line.contains("public class MyProgram"));
     
        assertTrue(sections.get(1).doc.contains("* The single p-tag should force this to be preceded by a line"));
        assertTrue(sections.get(1).line.contains("function myFunc(int one, String two, Object... three) "));
     
        assertTrue(sections.get(2).doc.contains("* Comments that do not precede any function, class or variable are ignored"));
        assertTrue(sections.get(2).line.contains(""));
     
        assertTrue(sections.get(3).doc.contains("* You can document classes as well (and docs may have wrong indentation)"));
        assertTrue(sections.get(3).line.contains("public class MyClass"));
  
        assertTrue(sections.get(4).doc.contains("* The description may even be single-line and followed by a blank line"));
        assertTrue(sections.get(4).line.contains("private static Object boringFunc()"));

        assertTrue(sections.get(5).doc.contains("* @private"));
        assertTrue(sections.get(5).line.contains("String privateVariable ="));
        assertEquals(6,sections.size());

    }

    @Test
    public void getField(){
        JavadocToMarkdown jdtm = new JavadocToMarkdown();
      
        String field = jdtm.getFieldDeclaration("public class MyClass {String test} {return value};");
        assertEquals("public class MyClass",field);
        field = jdtm.getFieldDeclaration("function myFunc(int one, String two, Object... three)");
        assertEquals("function myFunc(int one, String two, Object... three)",field);
        field = jdtm.getFieldDeclaration("private static Object boringFunc() {");
        assertEquals("private static Object boringFunc()",field);

    }

    @Test
    public void replaceAll(){
        String description = "Bonjour mon coco <p> comment ca va </p> moi ca vas bien";
        description = description.	replaceAll("<(/)?p>", "\n\n");
        System.out.println(description);
        assertEquals("Bonjour mon coco \n\n comment ca va \n\n moi ca vas bien",description);
    }

    // @Test
    // public void removeSpace(){
    //     String data ="and there.  \r\r  But break because one. \n\n The line breakwell. \t\t ";
    //     data = data.replaceAll(" *([\n\r\t])","$1").replaceAll("([\n\r\t]) *","$1");
    //     assertEquals("and there.\r\rBut break because one.\n\nThe line breakwell.\t\t",data);
    // }

    @Test
    public void getDocDescription(){
        JavadocToMarkdown jdtm = new JavadocToMarkdown();
        // String description = jdtm.getDocDescription( " *    Except for this text which will have a line break\n");
        // assertEquals("Except for this text which will have a line break",description);
        
        // description = jdtm.getDocDescription("  * @param    one    the first parameter (type is always inferred)");
        // assertEquals("@param one the first parameter (type is always inferred)",description);
        
        String description = jdtm.getDocDescription("   * and there.\n" +
        "* <p>\n"+
        "* But break\n" +
        "* because one.\n" +
        "* <p>\n" +
        "* The line\n" +
        "* breakwell.\n" +
        "* <p> x");
        assertEquals("and there.\n\nBut break because one.\n\nThe line breakwell.\n\nx",description);
    
    }

    @Test
    public void remove_p(){
        assertEquals("allo \n\n mon \n\n coco", "allo <p> mon </p> coco".replaceAll("<(/)?p>", "\n\n"));
    }

    @Test
    public void javadoc2MarkdownTest() {

        String expected = Utils.readFile("src/test/java/dashview/data/javadoc.expected.txt");
        String code = Utils.readFile("src/test/java/dashview/data/javadoc.txt");

        JavadocToMarkdown jdtm = new JavadocToMarkdown();
        String result = jdtm.fromJavadoc(code, 1);

        // System.out.println(result);
        assertEquals(result, expected);
    }

}