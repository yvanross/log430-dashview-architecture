/** Structurizr projet to generate architecture documentation on Structurizr web site 
 * 
*/
package dashview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.DecisionStatus;
import com.structurizr.documentation.Documentation;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Enterprise;
import com.structurizr.model.InteractionStyle;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.model.Element;
import com.structurizr.view.*;
import dashview.Interfaces.ICancanRouter;
import dashview.Interfaces.IControllerDisplay;
import dashview.Interfaces.IControllerEngineer;
import dashview.Interfaces.IControllerPilot;
import dashview.Requirements.Requirement;
import dashview.Requirements.Requirements;
import dashview.Requirements.Requirement.Type;
import dashview.Utils.JavadocToMarkdown;
import dashview.Utils.Property;
import dashview.Utils.Utils;

import org.apache.commons.lang.ArrayUtils;
import org.ini4j.*;

/**
 * This is a simple example of how to get started with Structurizr for Java.
 * Documentation: https://github.com/structurizr/java
 */
public class Structurizr {
  private static long WORKSPACE_ID;
  private static String API_KEY;
  private static String API_SECRET;

  ViewSet views;
  File documentationRoot;

  public static void main(final String[] args) {
    try {
      new Structurizr().run();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public void run() throws Exception {
    _init_structurizr();
    // a Structurizr workspace is the wrapper for a software architecture model,
    // view and documentation
    final Workspace workspace = new Workspace("FormuleETS DashView project",
        "Représentation des systèmes nécessaires à la calibration du véhicule.");
    final Enterprise enterprise = new Enterprise("FormuleETS");
    final Model model = workspace.getModel();
    model.setEnterprise(enterprise);

    // initialisation des requis
    Requirements.fromYaml("requirements.yml");

    views = workspace.getViews();
    applyViewsStyling();

    // template for documentation
    final StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
    final File documentationRoot = new File("./documentation");

    /**
     * Persons
     */
    final Person pilot = model.addPerson("Pilot",
        "Le pilote contrôle le véhicule lors des essais sur piste et des compétitions. Il utilise l’application en mode pilote afin d’accéder aux données du véhicule ce qui permet d’avoir une meilleure compréhension des différents composants et d’améliorer sa conduite.");
    Requirements.add(pilot, "EF02,EF09,EF10,EF11,EF12,EF13,EF14,EF15,EF16");
    
    final Person engineer = model.addPerson("Engineer",
        "L'ingénieur de piste gère les alarmes et capteurs du véhicule et ajuste/optimise les paramètres logiciels du véhicule");
      Requirements.add(engineer, "EF01,EF17,EF18,EF19,EF20,EF21,EF22");

    
    
    /**
     * Software systems
     */

 
    final SoftwareSystem systemRacing = model.addSoftwareSystem("SystemRacing",
        "Système de calcul sur site permettant de récupérer les données temps réel et d'envoyer des commandes aux véhicule pour la calibration de celui-ci.");
    Requirements.add(systemRacing,"C01,C04,C06,C08,EF01,EF03,EF17,EF18,EF19,EF20,EF21,EF22,ENF07");

    final SoftwareSystem systemVehicule = model.addSoftwareSystem("SystemVehicule",
        "Système déployé dans les véhicules FormuleETS pour permettre la communication avec le Racing Server et le pilote.");
    Requirements.add(systemVehicule,"C02,C03,C04,C05,C07,C08,C09,EF02,EF03, EF04,EF05,EF06,EF07,EF08,EF09,EF10,EF11,EF12,EF13,EF14,EF15,EF16,ENF01,ENF02,ENF03,ENF04,ENF05,ENF06,ENF08");
    /**
     * create relations between Person and systems
     */

    pilot.uses(systemVehicule, "Consulte l'état du véhicule durant la course");
    Relationship engineerUsesRacingSystem = engineer.uses(systemRacing, "Optimise la configuration du véhicule");
    engineer.uses(systemVehicule, "Configure les paramêtre d'acquistion et de communication du véhicule");
    systemVehicule.uses(systemRacing, "Fournie les données temps réel");

    /**
     * Containers
     */

    final Container containerDisplay = systemVehicule.addContainer("Display",
        "Application cliente permettant d'affiche les informations au pilote durant la course", "IOS Mobile");
    Requirements.add(containerDisplay,"C02,C04,C07,C08,C09,EF02,EF03, EF04,EF05,EF06,EF07,EF08,EF09,EF15,EF16,ENF01,ENF02,ENF03,ENF04,ENF05,ENF06,ENF08");

    final Container containerVolant = systemVehicule.addContainer("Volant",
    "Application cliente permettant d'envoye la sélection des boutons du volant", "Arduino WIFi");
    Requirements.add(containerVolant,"EF10");
    pilot.uses(containerVolant,"Appuie sur les boutons de sélection d'écran");

    final Container containerCancanEthernet = systemVehicule.addContainer("cancanEthernet",
        "Serveur permettrant d'accumuler les données des capteurs du réseau cancan, de les transmettre au serveur et de fournir l'information nécessaire à l'affichage du pilote ",
        "cancan bus web server");
        Requirements.add(containerCancanEthernet,"C03,C05,EF11,EF12,EF13,EF14,");
    
    final Container containerEngineeringUI = systemRacing.addContainer("Racing System User interface",
        "User interface to control....", "React");
        Requirements.add(containerEngineeringUI,"C04,C08,EF17,EF18,EF19,EF20,EF21,EF22");

    final Container containerRacingServer = systemRacing.addContainer("Racing system Server",
        "server that provide an API to record vehicule state and update...", "Web server Golang");
        Requirements.add(containerRacingServer,"C01,C06,EF01,EF03,ENF07");

    /**
     * Relationship between containers
     */

    pilot.uses(containerDisplay, "Visualise les différents écran durant la course", "IOS application",
        InteractionStyle.Asynchronous);
    engineer.uses(containerDisplay, "Configuration manuel des paramètres du véhicule avant la course");
    containerDisplay.uses(containerCancanEthernet,
        "Récupération des informations des capteurs du véhicule, Envoie des paramètres de configuration du véhicule");
    containerCancanEthernet.uses(systemRacing, "Transmission des données de l'état des capteurs du véhicule")
        .addTags("UDP");
    engineer.uses(containerEngineeringUI, "Uses");
    containerEngineeringUI.uses(containerRacingServer, "RacingSystemAPI").addTags("API");
    systemVehicule.uses(containerRacingServer, "RacingSystemAPI").addTags("API");

    /**
     * Composants containerDisplay 
     */

     final Component componentDisplayController = containerDisplay.addComponent("ControllerDisplay ",IControllerDisplay.class,"Controlleur/routeur qui permet au pilote d'intéragir avec l'application","Web server");
     Requirements.add(componentDisplayController,"C02,EF03,EF10,ENF03,ENF05");
     componentDisplayController.setUrl(
      "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/dashview/Interfaces/IControllerDisplay.java");

     final Component componentDisplay1 = containerDisplay.addComponent("Pilote1","Affiche la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute.");
     Requirements.add(componentDisplay1,"EF04,EF06,EF07,EF09,EF11,ENF04");
    
    final Component componentDisplay2 = containerDisplay.addComponent("Pilote2","Affiche la pression et la température des pneus, le biais de frein, l’antiroulis, l’odomètre, le voltage de la batterie, la température du moteur et un indicateur d’utilisation et l’angle du système de réduction de traînée.");
    Requirements.add(componentDisplay2,"EF05,EF06,EF07,EF09,EF12,ENF04");
    
    final Component componentDisplay3 = containerDisplay.addComponent("Pilote3","Affiche le temps total de course, le temps du tour courant, la différence de temps par rapport au meilleur temps, le meilleur temps de tour de piste et la différence de temps par rapport au dernier de tour de piste.");
    Requirements.add(componentDisplay3,"EF09,EF13,ENF04");
    
    final Component componentDisplay4 = containerDisplay.addComponent("Pilote4","Affiche un schéma de la piste de course et le déplacement de la voiture en temps réel, le temps du tour de piste, le meilleur temps et la différence par rapport au meilleur temps.");
    Requirements.add(componentDisplay4,"EF09,EF14,ENF04");
  
    final Component componentDisplayConfiguration = containerDisplay.addComponent("Configuration","Interface usagé utilisé par l'ingénieur pour configurer l'appliation mobile");
    Requirements.add(componentDisplayConfiguration,"EF02,");
   
    /**
     * define relationship between components
     */
    containerVolant.uses(componentDisplayController,"Envoie les requète de changement d'écran à partir des boutons du volant").addTags("API","WIFI");
     containerVolant.uses(containerDisplay,"Demande de changement d'écran").addTags("WIFI");

    componentDisplayController.delivers(pilot, "updated vehicule data");
    componentDisplayController.delivers(engineer, "updated vehicule configuration");
    componentDisplayController.uses(componentDisplay1, "Affiche l'écran 1");
    componentDisplayController.uses(componentDisplay2, "Affiche l'écran 2");
    componentDisplayController.uses(componentDisplay3, "Affiche l'écran 3");
    componentDisplayController.uses(componentDisplay4, "Affiche l'écran 4");
    componentDisplayController.uses(componentDisplayConfiguration, "Affiche l'écran de configuration");
    
/**
 * composants containerCancanEthernet
 */

    final Component componentCancanRouter = containerCancanEthernet.addComponent("cancanRouter",ICancanRouter.class,
        "Routeur du Server web wifi controlant le bus CanCan pour l'acquisition/diffusion des données des capteurs", "CanCan bus Router");
    componentCancanRouter.setUrl(
          "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/dashview/Interfaces/ICancanRouter.java");
        componentDisplayController.uses(componentCancanRouter,"requetes pour les interfaces usagé").addTags("WIFI","API");;
        Requirements.add(componentCancanRouter,"C03,C05");
     
        final Component componentSensors = containerCancanEthernet.addComponent("Sensors", "Représente tout les capteurs du véhicule","Capteurs compatible avec cancan");
        
     
    final Component componentControllerPilot = containerCancanEthernet.addComponent("controllerPilot",
        IControllerPilot.class, "Controleur pour la gestion des évènements pilote",
        "web server");
    componentControllerPilot.setUrl(
        "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/dashview/Interfaces/IControllerPilot.java");

        Requirements.add(componentControllerPilot,"EF11,EF12,EF13,EF14");

        final Component componentControllerEngineer = containerCancanEthernet.addComponent("controllerEngineer",
        IControllerEngineer.class, "Controleur pour la gestion des évènements de configuration et de transmission des données",
        "CanCan bus");
    componentControllerEngineer.setUrl(
        "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/dashview/Interfaces/IControllerEngineer.java");
        Requirements.add(componentControllerEngineer,"ENF07");

    /**
     * relationship between components
     */

    componentCancanRouter.uses(componentSensors, "Effectue la lecteur des senseurs au travers du réseau cancan");

     containerDisplay.uses(componentCancanRouter, "display data for pilot, get/set data for engineer").addTags("WIFI","API");

    componentCancanRouter.uses(componentControllerPilot, "get data for the pilot");
    componentCancanRouter.uses(componentControllerEngineer, "get/set data for engineer");

    /**
     * define system views
     */

    final SystemLandscapeView landscapeSystemView = views.createSystemLandscapeView("landscapeSystemView",
        "Diagramme d'integration de tout les systèmes pour le projet FormuleETS DashView");
    landscapeSystemView.setPaperSize(PaperSize.A4_Landscape);
    landscapeSystemView.addAllSoftwareSystems();
    landscapeSystemView.addAllPeople();
    landscapeSystemView.enableAutomaticLayout();
    landscapeSystemView.setEnterpriseBoundaryVisible(true);

    final SystemContextView vehiculeSystemView = views.createSystemContextView(systemVehicule,
        "vehiculeSystemView", "Diagramme d'architecture du système dans le véhicule FormuleETS.");
    vehiculeSystemView.setPaperSize(PaperSize.A5_Landscape);
    vehiculeSystemView.addNearestNeighbours(systemVehicule);
    vehiculeSystemView.enableAutomaticLayout();

    final SystemContextView racingSystemView = views.createSystemContextView(systemRacing,
        "racingSystemView", "Diagramme d'architecture du système Racing.");
    racingSystemView.setPaperSize(PaperSize.A5_Landscape);
    racingSystemView.addNearestNeighbours(systemRacing);
    racingSystemView.enableAutomaticLayout();

   
    /**
     * container views
     */

    final ContainerView vehiculeContainersView = views.createContainerView(systemVehicule,
        "vehiculeContainersView", "Diagramme de décomposition du véhicule");
    vehiculeContainersView.setPaperSize(PaperSize.A5_Landscape);
    vehiculeContainersView.enableAutomaticLayout();
    Iterator<Container> iteratorContainer = systemVehicule.getContainers().iterator();
    while(iteratorContainer.hasNext())
      vehiculeContainersView.addNearestNeighbours(iteratorContainer.next());
    vehiculeContainersView.remove(engineerUsesRacingSystem);

    /**
     * Components views
     */

    final ComponentView displayComponentsView = views.createComponentView(containerDisplay,"displayComponentsView","Composants de l'application mobile du pilote");
    displayComponentsView.setPaperSize(PaperSize.A5_Landscape);
    displayComponentsView.enableAutomaticLayout();
    Iterator<Component> iterator = containerDisplay.getComponents().iterator();
    while(iterator.hasNext())
      displayComponentsView.addNearestNeighbours(iterator.next());

    

    final ComponentView cancanEthernerComponentsView = views.createComponentView(containerCancanEthernet,
        "cancanEthernerComponentsView", "Component of the cancan Eternet container");
    cancanEthernerComponentsView.setPaperSize(PaperSize.A5_Landscape);
    cancanEthernerComponentsView.enableAutomaticLayout();
    iterator = containerCancanEthernet.getComponents().iterator();
    while(iterator.hasNext())
      cancanEthernerComponentsView.addNearestNeighbours(iterator.next());

    /**
     * Dynamic views
     */

    final DynamicView dynamicView1 = views.createDynamicView(containerCancanEthernet, "dynamicView1",
        "Diagramme pour démontrer comment l'appliation du pilot récupérer les données");
    dynamicView1.setPaperSize(PaperSize.A5_Landscape);
    dynamicView1.add(pilot, "Appuyuer sur le bouton interface 1", containerDisplay);
    dynamicView1.add(containerDisplay, "operation1", componentCancanRouter);
    dynamicView1.add(componentCancanRouter, "getDataInterace(1)", componentControllerPilot);
    dynamicView1.enableAutomaticLayout();

    /**
     * define documentation
     */

    try {
      /** System of systems documentation */
      template.addContextSection(null, new File(documentationRoot, "context.md"));

      template.addSection("Table des éléments systèmes", Format.Markdown,
          toMarkdown(landscapeSystemView, Requirement.Type.FUNCTIONAL));

      template.addSection("Table de priorité des exigences", Format.Markdown, Requirements.priorityTable());


      /** Racing system documentation */
      template.addContextSection(systemRacing, new File(documentationRoot, "racingSystem-context.md"));
      template.addSection(systemRacing,"Table des éléments du système racing", Format.Markdown, toMarkdown(racingSystemView, Requirement.Type.FUNCTIONAL));


      /** vehicule system documentation */
      template.addContextSection(systemVehicule, new File(documentationRoot, "vehicule-context.md"));
      template.addSection(systemVehicule,"Table des éléments du système véhicule", Format.Markdown,
        toMarkdown(vehiculeSystemView, Requirement.Type.FUNCTIONAL));
  
      template.addSection(systemVehicule,"Diagramme de conteneurs", Format.Markdown, 
      " ![](embed:vehiculeContainersView)\n ### Table des éléments du conteneur véhicule\n" +
      toMarkdown(vehiculeContainersView, Requirement.Type.FUNCTIONAL));
 
      template.addSection(systemVehicule,"Diagramme de composant du conteneur d'affichage", Format.Markdown, 
      " ![](embed:displayComponentsView)\n" +
      toMarkdown(displayComponentsView, Requirement.Type.FUNCTIONAL));
 
      template.addSection(systemVehicule,"Diagramme de composant du serveur cancanEthernet", Format.Markdown, 
      " ![](embed:cancanEthernerComponentsView)\n" +
      toMarkdown(cancanEthernerComponentsView, Requirement.Type.FUNCTIONAL));
 
      template.addSection(systemVehicule,"Diagramme dynamique 1", Format.Markdown, 
      " ![](embed:dynamicView1)" +
      toMarkdown(dynamicView1, Requirement.Type.FUNCTIONAL));
      


      final JavadocToMarkdown javadocToMarkdown = new JavadocToMarkdown();
      String interfaces = "";
      interfaces += javadocToMarkdown
          .fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/ICancanRouter.java"), 3);
      interfaces += javadocToMarkdown
          .fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/IControllerEngineer.java"), 3);
      interfaces += javadocToMarkdown
          .fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/IControllerPilot.java"), 3);
      interfaces += javadocToMarkdown.fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/IExample.java"), 3);
      interfaces += javadocToMarkdown.fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/IExampleV2.java"),
          3);
      template.addSection(systemVehicule, "Documentation des interfaces", Format.Markdown, interfaces);

      template.addSection(systemVehicule, "User interfaces", Format.Markdown, "### Login Page"
          + "\n![Login Page](http://www.plantuml.com/plantuml/png/SoWkIImgAKxCAU6gvb9GyCbFpynJ088Q1INVIh_4t5GWMmae4P1ON5oUNvG2aj020g0mH2BQEJ4lEJKd5YWHhQ3WxmqKZkMgvN98pKi1cGe0)"
          + "\n### Pilot Interface #1\n"
          + "\n![Login Page](http://www.plantuml.com/plantuml/png/SoWkIImgAKxCAU6gvb9GyCbFpynJ088Q1INVIh_4t5GWMmae4P1ON5oUNvG2aj020g0mH2BQEJ4lEJKd5YWHhQ3WxmqKZkMgvN98pKi1cGe0)"
          + "\n### Pilot Interface #2\n"
          + "\n![Login Page](http://www.plantuml.com/plantuml/png/SoWkIImgAKxCAU6gvb9GyCbFpynJ088Q1INVIh_4t5GWMmae4P1ON5oUNvG2aj020g0mH2BQEJ4lEJKd5YWHhQ3WxmqKZkMgvN98pKi1cGe0)"
          + "\n### Pilot Interface #3\n"
          + "\n![Login Page](http://www.plantuml.com/plantuml/png/SoWkIImgAKxCAU6gvb9GyCbFpynJ088Q1INVIh_4t5GWMmae4P1ON5oUNvG2aj020g0mH2BQEJ4lEJKd5YWHhQ3WxmqKZkMgvN98pKi1cGe0)"
          + "\n### Pilot Interface #4\n"
          + "\n![Login Page](http://www.plantuml.com/plantuml/png/SoWkIImgAKxCAU6gvb9GyCbFpynJ088Q1INVIh_4t5GWMmae4P1ON5oUNvG2aj020g0mH2BQEJ4lEJKd5YWHhQ3WxmqKZkMgvN98pKi1cGe0)");

    } catch (final IOException e) {
      e.printStackTrace();
    }

    /**
     * Documenting decisions
     */
    final Documentation doc = workspace.getDocumentation();
    doc.addDecision(systemRacing, "1", new Date(), "Choix de la plateforme de conception",
        DecisionStatus.Proposed, Format.Markdown, "Utiliser Structurizr pour documenter l'architecture du système");

    uploadWorkspaceToStructurizr(workspace);
  }

  private void applyViewsStyling() {
    // add some styling
    final Styles styles = views.getConfiguration().getStyles();
    styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
    styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);

    // styles.addElementStyle(Tags.RELATIONSHIP).color("#Ff0000");
    styles.addRelationshipStyle("API").color("#0000FF");
    styles.addRelationshipStyle("UDP").color("#00ff00").dashed(true);
    styles.addRelationshipStyle("TCP").color("#00ff00").dashed(false);
    styles.addRelationshipStyle("WIFI").width(3);
  }

  private static void uploadWorkspaceToStructurizr(final Workspace workspace) {
    final StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
    try {
      structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }


  private void propagateRelationshipRequirements(final Element element, final Relationship relationship) {
    final String relationRequirement = relationship.getProperties().get(Property.REQUIREMENTS.toString());
    final String elementRequirement = element.getProperties().get(Property.REQUIREMENTS.toString());
    if(relationRequirement == null )
     return;

    final String[] relationKeys = relationRequirement.split(",");
    final String[] elementKeys = elementRequirement != null ? elementRequirement.split(",") : new String[0];

    final String[] merged = (String[]) ArrayUtils.addAll(elementKeys, relationKeys);
    final Set<String> result = new LinkedHashSet<String>(Arrays.asList(merged));
    element.addProperty(Property.REQUIREMENTS.toString(), String.join(",", result));
  }

  private List<Element> getElements(final View view) {
    final List<Element> elements = new ArrayList<Element>();
    final Iterator<ElementView> iterator = view.getElements().iterator();
    while (iterator.hasNext()) {
      elements.add(iterator.next().getElement());
    }
    return elements;
  }

  private List<Relationship> getRelations(final View view) {
    final List<Relationship> relations = new ArrayList<Relationship>();
    final Iterator<RelationshipView> iterator = view.getRelationships().iterator();
    while (iterator.hasNext()) {
      relations.add(iterator.next().getRelationship());
    }
    return relations;
  }

  private void progagateRequirements(List<Element> elements, List<Relationship> relations) {
    final Iterator<Relationship> iteratorRelationship = relations.iterator();
    while (iteratorRelationship.hasNext()) {
      final Relationship relationship = iteratorRelationship.next();
      final Element source = relationship.getSource();
      final Element destination = relationship.getDestination();
      if (elements.contains(source) && elements.contains(destination)) {
        propagateRelationshipRequirements(source, relationship);
        propagateRelationshipRequirements(destination, relationship);
      }
    }
  }

  private String toMarkdown(final View view, final Type type) {
    String result = "";
    final List<Element> elements = getElements(view);
    final List<Relationship> relations = getRelations(view);

    progagateRequirements(elements, relations);

    final Iterator<Element> iteratorElement = elements.iterator();
    while (iteratorElement.hasNext()) {
      final Element element = iteratorElement.next();
      result += Requirements.toMarkdown(element, type);
    }

    return result;
  }

  /** read init file and configure access key for Structurizr API */
  private void _init_structurizr() {
    final File iniFile = new File("dashview.ini");
    try {
      final Wini ini = new Wini(iniFile);
      WORKSPACE_ID = ini.get("structurizr", "WORKSPACE_ID", long.class);
      API_KEY = ini.get("structurizr", "API_KEY", String.class);
      API_SECRET = ini.get("structurizr", "API_SECRET", String.class);
    } catch (final Exception e) {
      e.printStackTrace();
    }

  }

}