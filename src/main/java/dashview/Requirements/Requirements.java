package dashview.Requirements;

import java.util.Map;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
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

public final class Requirements {

        private static Map<String, Requirement> mapRequirements = new HashMap<String, Requirement>();
        private static Map<String, List<Requirement>> mapElementRequirements = new HashMap<String, List<Requirement>>();

        // delete all specifications
        private static void clear() {
                mapRequirements = new HashMap<String, Requirement>();
        }

        // add 
        public static void add(final Requirement requirement) {
                mapRequirements.put(requirement.key(), requirement);
        };

        public static String toMarkdown(final Element element) {
                String result = "### " + element.getName() + "\n" + element.getDescription() + "\n\n";
                result += Requirement.markdownHeader();
                List<Requirement> requirements = mapElementRequirements.get(element.getId());
                if (requirements != null) {
                        for (Requirement requirement : requirements) {
                                if (requirement != null)
                                        result += requirement._toMarkdown();
                        }
                }
                return "\n" + result;
        }

        public static String toMarkdown() {
                String exigences = Requirement.markdownHeader();

                for (final Map.Entry<String, Requirement> entry : mapRequirements.entrySet()) {
                        exigences += entry.getValue()._toMarkdown();
                }
                return exigences;
        }

        public static String toMarkdown(final String key) {
                return mapRequirements.get(key)._toMarkdown();
        }

        public static Requirement get(final String key) {
                return mapRequirements.get(key);
        }


        public static void elementAddRequirement(final Element element, final String... keys) throws Exception {
               for(int i = 0; i<keys.length; i++){
                Requirement exigence = Requirements.get(keys[i]);
                if (exigence == null)
                        throw new Exception("!!!!!!!¡¡¡ Exigence  " + keys[i] + " do not exist !!!!!!!¡¡¡");
                        
                List<Requirement> requirements = mapElementRequirements.get(element.getId());
                if (requirements == null)
                        requirements = new ArrayList<Requirement>();
                requirements.add(exigence);
                mapElementRequirements.put(element.getId(), requirements);
        }
}

        // create specification directly in code
        public static void createAll() {

                Requirements.add(new Requirement("EF01", null, Requirement.Type.CONSTRAINT, "Général",
                                "Configuration de l’application avec un fichier XML",
                                "L’application doit utiliser un fichier de configuration, sous le format XML, pour déterminer les alarmes et capteurs disponibles. La liste des alarmes et des capteurs sont définis selon la table CAN fournie par la Formule ÉTS."));
                     }

        private static ArrayList<Requirement> toArray() {
                ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                for (final Map.Entry<String, Requirement> entry : mapRequirements.entrySet()) {
                        requirements.add(entry.getValue());
                }
                return requirements;
        }

        public static void toYaml() {
                // ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
                // need to create a maven project to download new packages.
                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
                objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
                ArrayList<Requirement> requirements = Requirements.toArray();
                File file = new File("requirements.yml");
                try {
                        objectMapper.writeValue(file, requirements);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public static void fromYaml(String filename) {
                // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                // File file = new File(classLoader.getResource(filename).getFile());

                // Instantiating a new ObjectMapper as a YAMLFactory
                // ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
                objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

                try {
                        byte[] jsonData = Files.readAllBytes(Paths.get(filename));

                        List<Requirement> requirements = Arrays.asList(objectMapper.readValue(jsonData, Requirement[].class));
                        Requirements.clear();
                        for (final Requirement entry : requirements) {
                                Requirements.add(entry);
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

}