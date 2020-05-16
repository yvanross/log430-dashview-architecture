package dashview.Exigences;

import java.util.Map;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.structurizr.model.Element;

public final class Exigences {

        private static Map<String, Exigence> mapExigences = new HashMap<String, Exigence>();
        private static Map<String, List<Exigence>> mapElementExigences = new HashMap<String, List<Exigence>>();

        private static void clear() {
                mapExigences = new HashMap<String, Exigence>();
        }

        public static void add(final Exigence exigence) {
                mapExigences.put(exigence.key(), exigence);
        };

        public static String toMarkdown(final Element element) {
                String result = "### " + element.getName() + "\n" + element.getDescription() + "\n\n";
                result += Exigence.markdownHeader();
                List<Exigence> exigences = mapElementExigences.get(element.getId());
                if (exigences != null) {
                        for (Exigence exigence : exigences) {
                                if (exigence != null)
                                        result += exigence._toMarkdown();
                        }
                }
                return "\n" + result;
        }

        public static String toMarkdown() {
                String exigences = Exigence.markdownHeader();

                for (final Map.Entry<String, Exigence> entry : mapExigences.entrySet()) {
                        exigences += entry.getValue()._toMarkdown();
                }
                return exigences;
        }

        public static String toMarkdown(final String key) {
                return mapExigences.get(key)._toMarkdown();
        }

        public static Exigence get(final String key) {
                return mapExigences.get(key);
        }

        // public void removeElement(final Element element) {
        // elements.remove(element);
        // }

        public static void elementAddExigence(final Element element, final String key) throws Exception {
                Exigence exigence = Exigences.get(key);
                if (exigence == null)
                        throw new Exception("!!!!!!!¡¡¡ Exigence  " + key + " do not exist !!!!!!!¡¡¡");
                        
                List<Exigence> exigences = mapElementExigences.get(element.getId());
                if (exigences == null)
                        exigences = new ArrayList<Exigence>();
                exigences.add(exigence);
                mapElementExigences.put(element.getId(), exigences);
        }

        public static void createAll() {

                Exigences.add(new Exigence("EF01", null, Exigence.Type.CONSTRAINT, "Général",
                                "Configuration de l’application avec un fichier XML",
                                "L’application doit utiliser un fichier de configuration, sous le format XML, pour déterminer les alarmes et capteurs disponibles. La liste des alarmes et des capteurs sont définis selon la table CAN fournie par la Formule ÉTS."));
                Exigences.add(new Exigence("EF02", null, Exigence.Type.FUNCTIONAL, "Générale",
                                "Configuration de l’application dans les paramètres d’iOS",
                                "Le système doit permettre de changer quelques configurations directement dans les paramètres de l’application sur iOS. Les configurations doivent inclure, entre autres, le changement de mode entre pilote et ingénieur de piste ainsi que le changement des couleurs de l’interface de pâle à foncé."));
                Exigences.add(new Exigence("EF03", null, Exigence.Type.FUNCTIONAL, "Générale",
                                "Gérer les données reçues en temps réel",
                                "L’application doit constamment recevoir des données du bus CAN via Wi-Fi à partir du module Can2Ethernet et, en comparant avec la table des messages CAN, associer ces données aux capteurs et alarmes pour les afficher."));
                Exigences.add(new Exigence("EF04", null, Exigence.Type.FUNCTIONAL, "Générale",
                                "Afficher des couleurs spécifiques pour les RPM",
                                "L’application doit afficher l’indicateur de RPM avec un code de couleur précis, soit de jaune à rouge en passant par une zone orange visible. De 3000 RPM à 15 000 RPM, l’indicateur doit être dans le spectre de jaune à rouge. À 15 000 RPM et plus, l’indicateur doit être rouge. De plus, le rouge doit changer selon un paramètre calculé par l’ACL lorsqu’un message correspondant au « id » de la table CAN de ce paramètre est reçu."));
                Exigences.add(new Exigence("EF05", null, Exigence.Type.FUNCTIONAL, "Générale",
                                "Afficher des couleurs spécifiques pour la température des pneus",
                                "L’application doit afficher la température des trois capteurs de chaque pneu selon des couleurs spécifiques et avec des transitions fluides. Les capteurs sont situés à l’extérieur, au milieu et à l’intérieur de chacun des pneus. Lorsque la température est de 25°C et moins, la couleur est bleue. Entre 25°C et 65°C, la couleur passe de bleue à jaune. Entre 65°C et 95°C, l’indicateur passe de jaune à rouge. Finalement, en haut de 95°C, la couleur est rouge."));
                Exigences.add(new Exigence("EF06", null, Exigence.Type.FUNCTIONAL, "Générale",
                                "Afficher des couleurs spécifiques pour la température du moteur",
                                "Le Dash Display doit afficher la température du moteur à l’aide de quatre couleurs. Lorsque la température est de 70°C et moins, l’indicateur doit être bleu. Entre 70°C et 90°C, la couleur utilisée est le vert. Entre 90°C et 100°C, l’indicateur doit être jaune et, finalement, il doit être rouge lorsque la température dépasse le 100°C."));
                Exigences.add(new Exigence("EF07", null, Exigence.Type.FUNCTIONAL, "Générale",
                                "Afficher des couleurs spécifiques pour la puissance de la batterie",
                                "Le système doit afficher la puissance de la batterie à l’aide de deux couleurs. Lorsque la batterie est à la puissance maximale (14V) jusqu’à 11.5 V, le fond du capteur est vert. Lorsque la puissance atteint 11.5 V et en dessous, le fond devient rouge."));
                Exigences.add(new Exigence("EF08", null, Exigence.Type.FUNCTIONAL, "Générale",
                                "Afficher des couleurs spécifiques pour les alertes",
                                "Le Dash Display doit afficher les alertes de deux façons selon leur statut. Dans tous les cas, elles sont affichées en rouge. Lorsqu’elles sont en cours, elles ont une opacité de 100 %. Sinon, l’opacité diminue à 30 %."));
                Exigences.add(new Exigence("EF09", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Visualiser les alarmes et les capteurs sur l’interface pilote",
                                "L’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement."));
                Exigences.add(new Exigence("EF10", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Changer l’interface lors de l’appui sur le bouton du volant",
                                "L’application doit changer l’interface affichée lorsque le pilote appuie sur un bouton du volant. Le bouton envoie un message CAN à l’application pour lui indiquer de changer. L’application en mode pilote affichera quatre interfaces différentes en boucle."));
                Exigences.add(new Exigence("EF11", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Contenu de la première interface",
                                "L’application doit afficher, sur la première interface, les capteurs suivants : la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute."));
                Exigences.add(new Exigence("EF12", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Contenu de la deuxième interface",
                                "Le Dash Display doit afficher, sur la deuxième interface, les capteurs suivants : la pression et la température des pneus, le biais de frein, l’antiroulis, l’odomètre, le voltage de la batterie, la température du moteur et un indicateur d’utilisation et l’angle du système de réduction de traînée."));
                Exigences.add(new Exigence("EF13", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Contenu de la troisième interface",
                                "L’application doit afficher, sur la troisième interface, le temps total de course, le temps du tour courant, la différence de temps par rapport au meilleur temps, le meilleur temps de tour de piste et la différence de temps par rapport au dernier de tour de piste."));
                Exigences.add(new Exigence("EF14", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Contenu de la quatrième interface",
                                "Dash Display doit afficher, sur la quatrième interface, un schéma de la piste de course et le déplacement de la voiture en temps réel. De plus, les temps suivants doivent être présents : le temps du tour de piste, le meilleur temps et la différence par rapport au meilleur temps."));
                Exigences.add(new Exigence("EF15", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Affichage en mode paysage pour le mode pilote",
                                "doit afficher les interfaces en mode pilote sous le format paysage."));
                Exigences.add(new Exigence("EF16", null, Exigence.Type.FUNCTIONAL, "Mode pilote",
                                "Mettre en veille l’application après 3 secondes sans données",
                                "doit se mettre en veille si une interruption de données survient et si elle dure plus de trois secondes."));
                Exigences.add(new Exigence("EF17", null, Exigence.Type.FUNCTIONAL, "Mode ingénieur de piste",
                                "Visualiser les alarmes et les capteurs sur l’interface ingénieur",
                                "doit afficher, sous forme de liste, les différents capteurs et alarmes que l’utilisateur décide d’inclure. Les capteurs et les alarmes sont affichés séparément, les alarmes se trouvant en haut de la liste. Lors de la présentation du prototype, le club formule ÉTS a précisé qu’ils souhaiteraient traiter les alarmes de la même façon que les différents capteurs donc les rendre modifiable sur la page principale."));
                Exigences.add(new Exigence("EF18", null, Exigence.Type.FUNCTIONAL, "Mode ingénieur de piste",
                                "Ajouter une alarme ou un capteur",
                                "doit permettre à l’utilisateur de sélectionner dans une liste une alarme ou un capteur à ajouter à la liste d’affichage. L’application doit aussi permettre de filtrer la liste des alarmes et des capteurs qui peuvent être ajoutés et d’y effectuer une recherche."));
                Exigences.add(new Exigence("EF19", null, Exigence.Type.FUNCTIONAL, "Mode ingénieur de piste",
                                "Changer l’ordre des alarmes et des capteurs affichés",
                                "doit permettre à l’utilisateur, une fois en mode édition de la liste, de réorganiser respectivement les alarmes et les capteurs entre eux."));
                Exigences.add(new Exigence("EF20", null, Exigence.Type.FUNCTIONAL, "Mode ingénieur de piste",
                                "Supprimer une alarme ou un capteur affiché",
                                "doit permettre à l’utilisateur, une fois en mode édition de la liste, de supprimer un capteur ou une alarme."));
                Exigences.add(new Exigence("EF21", null, Exigence.Type.FUNCTIONAL, "Mode ingénieur de piste",
                                "Afficher les détails de l’alarme ou du capteur",
                                "système doit permettre de cliquer sur un capteur ou une alarme affichés afin d’obtenir plus de détails. Lors de la présentation du prototype, cette exigence a été clarifiée. Le client désire avoir la possibilité de modifier l’affichage du widget pour d’autres formats ainsi qu’obtenir un historique des dernières données. Les informations apparaissent sous le widget principal avec la possibilité d’afficher l’historique en plein écran."));
                Exigences.add(new Exigence("EF22", null, Exigence.Type.FUNCTIONAL, "Mode ingénieur de piste",
                                "Gérer les cas d’erreurs de l’application",
                                "doit, en cas d’erreurs de l’application, afficher les dernières données reçues. Les cas d’erreurs peuvent être, par exemple, une erreur de transmission de données ou un message d’erreur reçu par une chaîne CAN du module Can2Ethernet."));
        }

        private static ArrayList<Exigence> toArray() {
                ArrayList<Exigence> exigences = new ArrayList<Exigence>();
                for (final Map.Entry<String, Exigence> entry : mapExigences.entrySet()) {
                        exigences.add(entry.getValue());
                }
                return exigences;
        }

        public static void toYaml() {
                ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
                // need to create a maven project to download new packages.
                // ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
                objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
                ArrayList<Exigence> exigences = Exigences.toArray();
                File file = new File("exigences.yml");
                try {
                        objectMapper.writeValue(file, exigences);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public static void fromYaml(String filename) {
                // ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                // File file = new File(classLoader.getResource(filename).getFile());

                // Instantiating a new ObjectMapper as a YAMLFactory
                ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
                objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

                try {
                        byte[] jsonData = Files.readAllBytes(Paths.get(filename));

                        List<Exigence> exigences = Arrays.asList(objectMapper.readValue(jsonData, Exigence[].class));
                        Exigences.clear();
                        for (final Exigence entry : exigences) {
                                Exigences.add(entry);
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

}