package dashview.Requirements;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
import java.util.Iterator;
import java.util.List;

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

      
        public static String toMarkdown(List<Requirement> requirements){
                String result= Requirement.markdownHeader();
                Iterator<Requirement> iterator = requirements.iterator();
                while(iterator.hasNext()){
                        Requirement requirement = iterator.next();
                        result += requirement._toMarkdown();
                }
                return result;
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
        public static ArrayList<Requirement> _toArray() {
                ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                for (final Map.Entry<String, Requirement> entry : mapKeyRequirements.entrySet()) {
                        requirements.add(entry.getValue());
                }
                return requirements;
        }

}