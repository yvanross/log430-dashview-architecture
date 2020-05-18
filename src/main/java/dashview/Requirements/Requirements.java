package dashview.Requirements;

import java.util.Map;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.structurizr.model.Element;

import dashview.Requirements.Requirement.Type;

/**
 * Classe permettant de gérer toutes les exigences associé au projet Structurizr
 */
public final class Requirements {

        private static Map<String, Requirement> mapKeyRequirements = new HashMap<String, Requirement>();
        private static Map<String, List<Requirement>> mapElementRequirements = new HashMap<String, List<Requirement>>();

        /**
         * Delete all requirement
         */
        private static void clear() {
                mapKeyRequirements = new HashMap<String, Requirement>();
        }

        /**
         * Add a requirement to the map of requirement
         * 
         * @param requirement: An instance of a requirement
         */
        public static void add(final Requirement requirement) {
                mapKeyRequirements.put(requirement.key(), requirement);
        };

        /**
         * Generate a markdown string of a Structurizr element and its associated
         * requirements
         * 
         * @param element Structurizer element
         * @param type    Requirement.Type options: FUNCTIONAL, QUALITY, CONSTRAINT
         * @return string of texte formatted in markdown
         */
        public static String toMarkdown(final Element element, Type type) {
                String result = "### " + element.getName() + "\n" + element.getDescription() + "\n\n";
                result += Requirement.markdownHeader();
                List<Requirement> requirements = mapElementRequirements.get(element.getId());
                if (requirements != null) {
                        for (Requirement requirement : requirements) {
                                if (requirement != null && requirement.type() == type)
                                        result += requirement._toMarkdown();
                        }
                }
                return "\n" + result;
        }

        /**
         * Generate markdowns string of a single requirement
         * 
         * @param key: Key of a requirement to convert in markdown
         * @return a specific requirement in markdown format
         */
        public static String toMarkdown(final String key) {
                return mapKeyRequirements.get(key)._toMarkdown();
        }

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
         * Add a requirement to a Structurizer element
         * 
         * @param element: Structurizer to associate requirement to
         * @param keys     List of key of requirements to associate to structurizer
         *                 element
         * @throws Exception if element is null
         */
        public static void addToElement(final Element element, final String... keys) throws Exception {
                if (element == null)
                        throw new Exception("!!!!!!!¡¡¡ elementAddRequirement element parameter is null !!!!!!!¡¡¡");

                for (int i = 0; i < keys.length; i++) {
                        Requirement requirement = Requirements.get(keys[i]);
                        if (requirement == null)
                                throw new Exception("!!!!!!!¡¡¡ Exigence  " + keys[i] + " do not exist !!!!!!!¡¡¡");

                        List<Requirement> requirements = mapElementRequirements.get(element.getId());
                        if (requirements == null)
                                requirements = new ArrayList<Requirement>();
                        requirements.add(requirement);
                        mapElementRequirements.put(element.getId(), requirements);
                }
        }

        /**
         * create specification directly in code instead of using YAML files
         * 
         * @deprecated should use YAML file
         */
        public static void createAll() {
                Requirements.add(new Requirement("EF01", null, Requirement.Type.CONSTRAINT, "Général",
                                "Configuration de l’application avec un fichier XML",
                                "L’application doit utiliser un fichier de configuration, sous le format XML, pour déterminer les alarmes et capteurs disponibles. La liste des alarmes et des capteurs sont définis selon la table CAN fournie par la Formule ÉTS."));
        }

        /**
         * Convert all requirements to YAML format
         * 
         * @param filename: name of the file to write requirement to
         */

        public static void toYaml(String filename) {
                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
                objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
                ArrayList<Requirement> requirements = Requirements._toArray();
                File file = new File(filename);
                try {
                        objectMapper.writeValue(file, requirements);
                } catch (JsonGenerationException e) {
                        e.printStackTrace();
                } catch (JsonMappingException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        /** Display a list of title requirement */
        public static String keyTitles() {
                String result = "";
                ArrayList<Requirement> requirements = Requirements._toArray();
                for (Requirement requirement : requirements)
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
        public static void fromYaml(String filename) {
                // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                // File file = new File(classLoader.getResource(filename).getFile());
                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
                objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

                try {
                        byte[] jsonData = Files.readAllBytes(Paths.get(filename));

                        List<Requirement> requirements = Arrays
                                        .asList(objectMapper.readValue(jsonData, Requirement[].class));
                        Requirements.clear();
                        for (final Requirement entry : requirements) {
                                Requirements.add(entry);
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

        /**
         * Convert Map of requirement to an ArrayList
         * 
         * @return Array list of requirement
         */
        private static ArrayList<Requirement> _toArray() {
                ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                for (final Map.Entry<String, Requirement> entry : mapKeyRequirements.entrySet()) {
                        requirements.add(entry.getValue());
                }
                return requirements;
        }

}