package dashview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

  @Test
  public void readFileTest() {

    // String theHTML =
    // Utils.readFile("target/site/apidocs/dashview/Interfaces/IExample.html");
    // (theHTML);

    final String java = Utils.readFile("src/main/java/dashview/Interfaces/IExample.java");

  }

  @Test
  public void getDocTagsTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {

    final JavadocToMarkdown jdtm = new JavadocToMarkdown();
    final Method getDocTags = JavadocToMarkdown.class.getDeclaredMethod("getDocTags", String.class);
    getDocTags.setAccessible(true);
    @SuppressWarnings("unchecked")
    final List<Map.Entry<String, String>> result = (List<Map.Entry<String, String>>) getDocTags.invoke(jdtm,
        "* @param this is a test");

    assertEquals(1, result.size());
    assertEquals("@param", result.get(0).getKey());
    assertEquals("this is a test", result.get(0).getValue());

    final ArrayList<Map.Entry<String, ArrayList<String>>> assocBuffer = new ArrayList<Map.Entry<String, ArrayList<String>>>();

    final Method fnAddTagsMarkdown = JavadocToMarkdown.class.getDeclaredMethod("fnAddTagsMarkdown",
      Map.Entry.class, assocBuffer.getClass());
      fnAddTagsMarkdown.setAccessible(true);
      fnAddTagsMarkdown.invoke(jdtm,result.get(0), assocBuffer);
   
      assertEquals(1, assocBuffer.size());

    }

    // @Test
    // public void tokenize() {
    //   final String value = "one the first";
    //   final String[] result = value.split("\s+", 2);
    //   assertEquals("one", result[0]);
    //   assertEquals("the first", result[1]);
    // }

    @Test
    public void matcherTest() {
      final Pattern p = Pattern.compile("^(?:[ |\t|*])*(@[a-zA-Z]+)(?:[\\s\\S])(.*$)");
      final Matcher m = p.matcher("* @param this is a test");
      assertTrue(m.find());
      assertEquals(2, m.groupCount());
      assertEquals("@param", m.group(1));
      assertEquals("this is a test", m.group(2));
    }

    @Test
    public void getSection() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
      final JavadocToMarkdown jdtm = new JavadocToMarkdown();
      final String code = Utils.readFile("src/test/java/dashview/data/javadoc.txt");
      Method getSections = JavadocToMarkdown.class.getDeclaredMethod("getSections",String.class);
      getSections.setAccessible(true);

      @SuppressWarnings("unchecked")
      final List<Section> sections = (List<Section>) getSections.invoke(jdtm, code);

      assertTrue(sections.get(0).doc.contains("this is an example do the classe my program"));
      assertTrue(sections.get(0).line.contains("public class MyProgram"));

      assertTrue(sections.get(1).doc.contains("* The single p-tag should force this to be preceded by a line"));
      assertTrue(sections.get(1).line.contains("function myFunc(int one, String two, Object... three) "));

      assertTrue(
          sections.get(2).doc.contains("* Comments that do not precede any function, class or variable are ignored"));
      assertTrue(sections.get(2).line.contains(""));

      assertTrue(
          sections.get(3).doc.contains("* You can document classes as well (and docs may have wrong indentation)"));
      assertTrue(sections.get(3).line.contains("public class MyClass"));

      assertTrue(
          sections.get(4).doc.contains("* The description may even be single-line and followed by a blank line"));
      assertTrue(sections.get(4).line.contains("private static Object boringFunc()"));

      assertTrue(sections.get(5).doc.contains("* @private"));
      assertTrue(sections.get(5).line.contains("String privateVariable ="));
      assertEquals(6, sections.size());

    }

    @Test
    public void getField() throws NoSuchMethodException, SecurityException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {
      final JavadocToMarkdown jdtm = new JavadocToMarkdown();
      Method getFieldDeclaration = JavadocToMarkdown.class.getDeclaredMethod("getFieldDeclaration", String.class);
      getFieldDeclaration.setAccessible(true);

      String field = (String) getFieldDeclaration.invoke(jdtm,"public class MyClass {String test} {return value};");
      assertEquals("public class MyClass", field);
      field = (String) getFieldDeclaration.invoke(jdtm,"function myFunc(int one, String two, Object... three)");
      assertEquals("function myFunc(int one, String two, Object... three)", field);
      field = (String) getFieldDeclaration.invoke(jdtm, "private static Object boringFunc() {");
      assertEquals("private static Object boringFunc()", field);

    }

    @Test
    public void replaceAll() {
      String description = "Bonjour mon coco <p> comment ca va </p> moi ca vas bien";
      description = description.replaceAll("<(/)?p>", "\n\n");
      assertEquals("Bonjour mon coco \n\n comment ca va \n\n moi ca vas bien", description);
    }

    // @Test
    // public void removeSpace(){
    // String data ="and there. \r\r But break because one. \n\n The line breakwell.
    // \t\t ";
    // data = data.replaceAll(" *([\n\r\t])","$1").replaceAll("([\n\r\t]) *","$1");
    // assertEquals("and there.\r\rBut break because one.\n\nThe line
    // breakwell.\t\t",data);
    // }

    @Test
    public void getDocDescription() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
      final JavadocToMarkdown jdtm = new JavadocToMarkdown();
      Method getDocDescription = JavadocToMarkdown.class.getDeclaredMethod("getDocDescription", String.class);
      getDocDescription.setAccessible(true);
      final String description = (String) getDocDescription.invoke(jdtm, "   * and there.\n" + "* <p>\n"
          + "* But break\n" + "* because one.\n" + "* <p>\n" + "* The line\n" + "* breakwell.\n" + "* <p> x");
      assertEquals("and there.\n\nBut break because one.\n\nThe line breakwell.\n\nx", description);

    }

    @Test
    public void remove_p() {
      assertEquals("allo \n\n mon \n\n coco", "allo <p> mon </p> coco".replaceAll("<(/)?p>", "\n\n"));
    }

    @Test
    public void javadoc2MarkdownTest() {

      final String expected = Utils.readFile("src/test/java/dashview/data/javadoc.expected.txt");
      final String code = Utils.readFile("src/test/java/dashview/data/javadoc.txt");

      final JavadocToMarkdown jdtm = new JavadocToMarkdown();
      final String result = jdtm.fromJavadoc(code, 1);

      System.out.println(result);
        assertEquals(expected, result);
    }

}