package dashview.Requirements;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.structurizr.model.CodeElement;
import com.structurizr.model.Component;
import com.structurizr.model.Element;
import com.structurizr.model.Perspective;
import com.structurizr.model.Relationship;

import dashview.Utils.JavadocToMarkdown;
import dashview.Utils.Property;
import dashview.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import dashview.Requirements.Requirement.Type;

/**
 * Classe permettant de gérer toutes les exigences associé au projet Structurizr
 */
public final class Requirements {

  private static SortedMap<String, Requirement> mapKeyRequirements = new TreeMap<String, Requirement>();

  /**
   * Delete all requirement
   */
  private static void clear() {
    mapKeyRequirements = new TreeMap<String, Requirement>();
  }

  /**
   * Add a requirement to the map of requirement
   * 
   * @param requirement: An instance of a requirement
   */
  public static void add(final Requirement requirement) {
    mapKeyRequirements.put(requirement.key(), requirement);
  };

  public static void add(final Element element, final String keys) throws Exception {
    if (element == null)
            throw new Exception("!!!!!!!¡¡¡ elementAddRequirement element parameter is null !!!!!!!¡¡¡");
            String new_requirements = "";
    String element_requirements = element.getProperties().get(Property.REQUIREMENTS.toString());
    
    if (element_requirements == null)
     new_requirements = keys;
     else
     new_requirements = keys + "," + element_requirements;

    element.addProperty(Property.REQUIREMENTS.toString(), new_requirements);
}

public static void add(final Relationship element, final String keys) throws Exception {
  if (element == null)
          throw new Exception("!!!!!!!¡¡¡ elementAddRequirement element parameter is null !!!!!!!¡¡¡");


  String element_requirements = element.getProperties().get(Property.REQUIREMENTS.toString());
  String new_requirements = "";
  if (element_requirements == null)
  new_requirements = keys;
  else
  new_requirements = keys + "," + element_requirements;
element.addProperty(Property.REQUIREMENTS.toString(), new_requirements);
}

  /**
   * get markdown requirement document from list of string
   * 
   * @param requirements String of commas separete requirement keys
   * @return String containing a markdown of all requirements
   */
  public static String toMarkdown(final String requirementKeys) {
    if (requirementKeys == null)
      return "";

    final String[] keys = requirementKeys.replace(" ","").split(",");
    String result = "";

    for (int i = 0; i < keys.length; i++) {
      result += mapKeyRequirements.get(keys[i])._toMarkdown();
    }
    return result;
  }

  public static String toMarkdown(final Element element, Type type) {
    String technology = "";
    String interfaces = "";
    
    if(element instanceof Component){
      Component comp = (Component) element;
      technology = "Technologie: " + comp.getTechnology() + "\n\n";
      Set<CodeElement> codeElements = comp.getCode();
      Iterator<CodeElement> iterator = codeElements.iterator();
      final JavadocToMarkdown javadocToMarkdown = new JavadocToMarkdown();
      while(iterator.hasNext()){
        CodeElement codeElement = iterator.next();
        String path = codeElement.getType().replaceAll("\\.",File.separator);

        interfaces += "\n\n\n\n" + javadocToMarkdown
            .fromJavadoc(Utils.readFile("src/main/java/" + path +".java"), 3);
        
      }
    }
    String perspectives = "#### Perspectives \n\n";
    Iterator<Perspective> itPerspective = element.getPerspectives().iterator();
    while(itPerspective.hasNext()){
      Perspective perspective = itPerspective.next();
      perspectives += perspective.getName() + " - " + perspective.getDescription() + "\n\n";
    }

    String url = "";
    if(element.getUrl() != null)
      url = "["+ element.getUrl() + "](" + element.getUrl()+")\n";

    String result = "### " + element.getName() + "\n" + 
    url + 
    element.getDescription() + "\n\n"+
    technology +
    "Tags: " + element.getTags() + "\n\n";
    result += Requirement.markdownHeader();
    String keys = element.getProperties().get(Property.REQUIREMENTS.toString());
    result += Requirements.toMarkdown(keys);
    result += perspectives;
    result += interfaces;
    return "\n" + result;
  }

  public static String toMarkdown(final List<Requirement> requirements) {
    String result = Requirement.markdownHeader();
    final Iterator<Requirement> iterator = requirements.iterator();
    while (iterator.hasNext()) {
      final Requirement requirement = iterator.next();
      result += requirement._toMarkdown();
    }
    return result;
  }

  // /**
  // * Generate markdowns string of a single requirement
  // *
  // * @param key: Key of a requirement to convert in markdown
  // * @return a specific requirement in markdown format
  // */
  // public static String toMarkdown(final String key) {
  // return mapKeyRequirements.get(key)._toMarkdown();
  // }

  /**
   * get a single requirement
   * 
   * @param key: Key of the requirement to get
   * @return a single requirement corresponding to a key. Null if key is invalid
   */
  public static Requirement get(final String key) {
    return mapKeyRequirements.get(key);
  }

  /**
   * create specification directly in code instead of using YAML files
   * 
   * @deprecated should use YAML file
   */
  public static void createAll() {
    Requirements.add(new Requirement("EF01", null, Requirement.Type.CONSTRAINT, "Général",
        "Configuration de l’application avec un fichier XML",
        "L’application doit utiliser un fichier de configuration, sous le format XML, pour déterminer les alarmes et capteurs disponibles. La liste des alarmes et des capteurs sont définis selon la table CAN fournie par la Formule ÉTS.",
        1, 3));
  }

  /**
   * Convert all requirements to YAML format
   * 
   * @param filename: name of the file to write requirement to
   */

  public static void toYaml(final String filename) {
    final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    final ArrayList<Requirement> requirements = Requirements._toArray();
    final File file = new File(filename);
    try {
      objectMapper.writeValue(file, requirements);
    } catch (final JsonGenerationException e) {
      e.printStackTrace();
    } catch (final JsonMappingException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /** Display a list of title requirement */
  public static String keyTitles() {
    String result = "";
    final ArrayList<Requirement> requirements = Requirements._toArray();
    for (final Requirement requirement : requirements)
      result += requirement.keyTitle() + "\n";

    return result;
  }

  /**
   * Read a YAML requirement file and create requirement map
   * 
   * @param filename: File name of the requirement.YAML file - key: "EF21" parent:
   *                  null type: "FUNCTIONAL" category: "" title: "" description:
   *                  """
   */
  public static void fromYaml(final String filename) {
    // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    // File file = new File(classLoader.getResource(filename).getFile());
    final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

    try {
      final byte[] jsonData = Files.readAllBytes(Paths.get(filename));

      final List<Requirement> requirements = Arrays.asList(objectMapper.readValue(jsonData, Requirement[].class));
      Requirements.clear();
      for (final Requirement entry : requirements) {
        Requirements.add(entry);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Convert Map of requirement to an ArrayList
   * 
   * @return Array list of requirement
   */
  public static ArrayList<Requirement> _toArray() {
    final ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    for (final Map.Entry<String, Requirement> entry : mapKeyRequirements.entrySet()) {
      requirements.add(entry.getValue());
    }
    return requirements;
  }

  /**
   * priority table in markdown
   */
  public static String priorityTable() {
    return Requirements.priorityTableFromRequirements(Requirements._toArray());
  }

  public static String priorityTableFromRequirements(final List<Requirement> requirements) {
    if (requirements == null)
      return "There is no requirement for this element";

    final SortedMap<Integer, List<String>> data = new TreeMap<Integer, List<String>>();
    final Iterator<Requirement> iterator = requirements.iterator();
    while (iterator.hasNext()) {
      final Requirement requirement = iterator.next();
      List<String> keys = data.get(requirement.priority());
      if (keys == null)
        keys = new ArrayList<String>();
      keys.add(requirement.key());
      data.put(requirement.priority(), keys);
    }

    String markdown = "|Priority|Requirements|\n|--|--|\n";
    for (int i = 1; i <= 9; i++) {
      final List<String> keys = data.get(i);
      if (keys == null)
        markdown += "|" + String.valueOf(i) + "| |\n";
      else
        markdown += "|" + String.valueOf(i) + "|" + String.join(",", keys) + "|\n";
    }
    return markdown;
  }

}