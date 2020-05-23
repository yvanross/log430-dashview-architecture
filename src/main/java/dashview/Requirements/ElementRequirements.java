package dashview.Requirements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.structurizr.model.Element;

import dashview.Requirements.Requirement.Type;

/**
 * Classe permettant de gérer toutes les exigences associé au projet Structurizr
 */
public final class ElementRequirements {

        private static SortedMap<String, List<Requirement>> mapElementRequirements = new TreeMap<String, List<Requirement>>();
      
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
        
        public static List<Requirement> unUsedRequirements(){
                List<Requirement> usedRequirements = new ArrayList<Requirement>();
                for (final Map.Entry<String, List<Requirement>> entry : mapElementRequirements.entrySet()) {
                 usedRequirements.addAll(entry.getValue());
               }
              List<Requirement> requirements = Requirements._toArray();
               requirements.removeAll(usedRequirements);
               return requirements;
        }
}