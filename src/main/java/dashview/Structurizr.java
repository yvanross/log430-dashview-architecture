/** Structurizr projet to generate architecture documentation on Structurizr web site 
 * 
*/
package dashview;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
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
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.model.Element;

import com.structurizr.view.*;

import dashview.Interfaces.ICancanRouter;
import dashview.Interfaces.IControllerEngineer;
import dashview.Interfaces.IControllerPilot;
import dashview.Requirements.Requirement;
import dashview.Requirements.Requirements;
import dashview.Requirements.Requirement.Type;
import dashview.Utils.Utils;

import org.ini4j.*;

/**
 * This is a simple example of how to get started with Structurizr for Java.
 * Documentation: https://github.com/structurizr/java
 */
public class Structurizr {
    private static long WORKSPACE_ID;
    private static String API_KEY;
    private static String API_SECRET;

    // Person pilot, engineer, optimisationEngineer;
    // SoftwareSystem vehiculeSystem, racingSystem, optimisationSystem;
    // Workspace workspace;
    // Model model;
    ViewSet views;
    // StructurizrDocumentationTemplate template;
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
        // Requirements.createAll();
        // Requirements.toYaml();
        Requirements.fromYaml("requirements.yml");
        // System.out.println(Requirements.keyTitles());

        views = workspace.getViews();
        applyViewsStyling();

        // template for documentation
        final StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
        File documentationRoot = new File("./documentation");

        /**
         * Persons
         */
        final Person pilot = model.addPerson("Pilot",
                "Le pilote contrôle le véhicule lors des essais sur piste et des compétitions. Il utilise l’application en mode pilote afin d’accéder aux données du véhicule ce qui permet d’avoir une meilleure compréhension des différents composants et d’améliorer sa conduite.");

        Requirements.addToElement((Element) pilot, "EF01", "EF02", "EF06");

        final Person engineer = model.addPerson("Engineer",
                "L'ingénieur de piste gère les alarmes et capteurs du véhicule et ajuste/optimise les paramètres logiciels du véhicule");

        final Person optimisationEngineer = model.addPerson("University Optimisation Engineer",
                "Un ingénieur spécialisé en course automobile qui analyse les données accumulé pour fournir les paramètres d'optimisation au véhicule de course.");
        Requirements.addToElement(optimisationEngineer, "EF16");
        Requirements.addToElement(optimisationEngineer, "EF11");

        /**
         * Software systems
         */

        final SoftwareSystem softwareSystemOptimisation = model.addSoftwareSystem("Optimisation Server",
                "Système distant permettant de récupérer les données d'un circuit et de faire l'analyse de ceux-ci pour fournire les paramètres du véhicule pour optimise le rendement de celui-ci durant la course.");
        softwareSystemOptimisation.addTags("REMOTE");
        Requirements.addToElement(softwareSystemOptimisation, "EF16", "ENF01", "ENF02");

        final SoftwareSystem softwareSystemRacing = model.addSoftwareSystem("Racing System",
                "Système de calcul sur site permettant de récupérer les données temps réel et d'envoyer des commandes aux véhicule pour la calibration de celui-ci.");
        Requirements.addToElement(softwareSystemRacing, "ENF03", "ENF04");

        final SoftwareSystem softwareSystemVehicule = model.addSoftwareSystem("Vehicule System",
                "Système déployé dans les véhicules FormuleETS pour permettre la communication avec le Racing Server et le pilote.");

        /**
         * create relations between Person and systems
         */

        pilot.uses(softwareSystemVehicule, "Consulte l'état du véhicule durant la course");
        engineer.uses(softwareSystemRacing, "Optimise la configuration du véhicule");
        optimisationEngineer.uses(softwareSystemOptimisation,
                "Analyse les données d'historique et fournie des données d'optimisation du véhicule");
        softwareSystemVehicule.uses(softwareSystemRacing, "Fournie les données temps réel");
        softwareSystemRacing.uses(softwareSystemOptimisation, "Fournie les données pour analyse et récupère les modèle d'optimisation");

        /**
         * Containers
         */

        final Container containerDisplay = softwareSystemVehicule.addContainer("display App",
                "Application cliente permettant d'affiche les informations au pilote durant la course", "IOS Mobile");

        final Container containerCancanEthernet = softwareSystemVehicule.addContainer("cancanEthernet",
                "Serveur permettrant d'accumuler les données des capteurs du réseau cancan, de les transmettre au serveur et de fournir l'information nécessaire à l'affichage du pilote ",
                "cancan bus web server");

        final Container containerEngineeringUI = softwareSystemRacing.addContainer("Racing System User interface",
                "User interface to control....", "React");
        
        final Container containerRacingServer = softwareSystemRacing.addContainer("Racing system Server",
                "server that provide an API to record vehicule state and update...", "Web server Golang");

        /** 
         * Relationship between containers
         */

        pilot.uses(containerDisplay, "Affichage des paramètre du véhicule durant la course");
        engineer.uses(containerDisplay, "Configuration manuel des paramètres du véhicule avant la course");
        containerDisplay.uses(containerCancanEthernet,
                "Récupération des informations des capteurs du véhicule, Envoie des paramètres de configuration du véhicule");
        containerCancanEthernet.uses(softwareSystemRacing, "Transmission des données de l'état des capteurs du véhicule")
                .addTags("UDP");
        engineer.uses(containerEngineeringUI, "Uses");
        containerEngineeringUI.uses(containerRacingServer, "RacingSystemAPI").addTags("API");
        softwareSystemVehicule.uses(containerRacingServer, "RacingSystemAPI").addTags("API");
        containerRacingServer.uses(softwareSystemOptimisation, "OptimisationSysTemAPI").addTags("API");

        /**
         * Composants
         */

        final Component componentCancanRouter = containerCancanEthernet.addComponent("cancanRouter", ICancanRouter.class,
                "Server controlant le bus CanCan pour l'acquisition des données des capteurs", "CanCan bus");
        componentCancanRouter.setUrl(
                "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/dashview/Interfaces/ICancanRouter.java");

        final Component componentControllerPilot = containerCancanEthernet.addComponent("controllerPilot",
                IControllerPilot.class, "Server controlant le bus CanCan pour l'acquisition des données des capteurs",
                "CanCan bus");
        componentControllerPilot.setUrl(
                "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/dashview/Interfaces/IControllerPilot.java");

  

        final Component componentControllerEngineer = containerCancanEthernet.addComponent("controllerEngineer",
                IControllerEngineer.class,
                "Server controlant le bus CanCan pour l'acquisition des données des capteurs", "CanCan bus");
        componentControllerEngineer.setUrl(
                "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/dashview/Interfaces/IControllerEngineer.java");


        /** 
         * relationship between components 
         * */

        componentControllerPilot.delivers(pilot, "delivers new user interface version 1");
        containerDisplay.uses(componentCancanRouter, "display data for pilot, get/set data for engineer");
        containerDisplay.uses(componentCancanRouter, "get data to display");
        componentCancanRouter.uses(componentControllerPilot, "get data for the pilot");
        componentCancanRouter.uses(componentControllerEngineer, "get/set data for engineer");

        /**
         * define views
         */

        final SystemLandscapeView viewSystemLandscape = views.createSystemLandscapeView("system_landscape_view",
        "Integration de tout les systèmes pour le projet FormuleETS DashView");
        viewSystemLandscape.setPaperSize(PaperSize.A4_Landscape);
        viewSystemLandscape.addAllSoftwareSystems();
        viewSystemLandscape.addAllPeople();
        viewSystemLandscape.enableAutomaticLayout();
        viewSystemLandscape.setEnterpriseBoundaryVisible(true);

        final SystemContextView viewSystemVehicule = views.createSystemContextView(softwareSystemVehicule,
                "VehiculeContextView", "Diagramme d'architecture du système dans le véhicule FormuleETS.");
        viewSystemVehicule.setPaperSize(PaperSize.A5_Landscape);
        viewSystemVehicule.addNearestNeighbours(softwareSystemVehicule);
        viewSystemVehicule.enableAutomaticLayout();

        final SystemContextView viewSystemRacing = views.createSystemContextView(softwareSystemRacing,
                "RacingSystemContextView", "Diagramme d'architecture du système Racing.");
        viewSystemRacing.setPaperSize(PaperSize.A5_Landscape);
        viewSystemRacing.addNearestNeighbours(softwareSystemRacing);
        viewSystemRacing.enableAutomaticLayout();

        final SystemContextView viewSystemOptimisation = views.createSystemContextView(softwareSystemOptimisation,
                "OptimisationSystemContextView",
                "Système permettant l'optimisation des paramètres du véhicule à partir des données temps réel et calculés.");
        viewSystemOptimisation.setPaperSize(PaperSize.A5_Landscape);
        viewSystemOptimisation.addNearestNeighbours(softwareSystemOptimisation);
        viewSystemOptimisation.enableAutomaticLayout();

        /** 
         * container views 
         * */

        final ContainerView viewContainersVehicule = views.createContainerView(softwareSystemVehicule, "vehiculeContainersView",
                "Vehicule System Containers view");
        viewContainersVehicule.setPaperSize(PaperSize.A5_Landscape);
        viewContainersVehicule.addNearestNeighbours(containerDisplay);
        viewContainersVehicule.addNearestNeighbours(containerCancanEthernet);
        viewContainersVehicule.enableAutomaticLayout();


        /** 
         * Components views
         */
        
        final ComponentView viewComponentsCanCanEthernet = views.createComponentView(containerCancanEthernet,
                "cancanRouterComponentsView", "Component of the cancan Eternet container");
        viewComponentsCanCanEthernet.setPaperSize(PaperSize.A5_Landscape);
        viewComponentsCanCanEthernet.addNearestNeighbours(componentCancanRouter);
        viewComponentsCanCanEthernet.addNearestNeighbours(componentControllerPilot);
        viewComponentsCanCanEthernet.addNearestNeighbours(componentControllerEngineer);
        viewComponentsCanCanEthernet.enableAutomaticLayout();


        /** 
         * Dynamic views
         */

        final DynamicView viewDynamic1 = views.createDynamicView(containerCancanEthernet, "dynamicView1",
                "Diagramme pour démontrer comment l'appliation du pilot récupérer les données");
        viewDynamic1.setPaperSize(PaperSize.A5_Landscape);
        viewDynamic1.add(pilot, "Appuyuer sur le bouton interface 1", containerDisplay);
        viewDynamic1.add(containerDisplay, "operation1", componentCancanRouter);
        viewDynamic1.add(componentCancanRouter, "getDataInterace(1)", componentControllerPilot);
        viewDynamic1.enableAutomaticLayout();

    
        /**
         * define documentation
         */

        try {
            /** System of systems documentation */
            template.addContextSection(null, new File(documentationRoot, "context.md"));

            final String data = "### Acteurs\n" + "#### " + pilot.getName() + "\n" + pilot.getDescription() + "\n"
                    + "#### " + engineer.getName() + "\n" + engineer.getDescription() + "\n" + "#### "
                    + optimisationEngineer.getName() + "\n" + optimisationEngineer.getDescription();

            template.addDataSection(null, Format.Markdown, data);

            template.addFunctionalOverviewSection(null,
                    writeRequirementsFile(viewSystemLandscape, Requirement.Type.FUNCTIONAL, "functionnal-overview.md"));
            template.addQualityAttributesSection(null,
                    writeRequirementsFile(viewSystemLandscape, Requirement.Type.QUALITY, "quality-attributes.md"));
            template.addConstraintsSection(null, new File(documentationRoot, "contraints.md"));

            /** Racing system documentation */
            template.addContextSection(softwareSystemRacing, new File(documentationRoot, "racingSystem-context.md"));
            template.addFunctionalOverviewSection(softwareSystemRacing,
                    new File(documentationRoot, "racingSystem-functional-overview.md"));
            template.addQualityAttributesSection(softwareSystemRacing,
                    new File(documentationRoot, "racingSystem-quality-attributes.md"));
            template.addSection(softwareSystemRacing, "Task list", new File(documentationRoot, "racingSystem-tasks-list.md"));
            
            /** optimisation system documentaion */
            template.addContextSection(softwareSystemOptimisation,
                new File(documentationRoot, "optimisationSystem-context.md"));
            template.addFunctionalOverviewSection(softwareSystemOptimisation,
                new File(documentationRoot, "optimisationSystem-functional-overview.md"));
            template.addQualityAttributesSection(softwareSystemOptimisation,
                new File(documentationRoot, "optimisationSystem-quality-attributes.md"));
            template.addSection(softwareSystemOptimisation, "Task list",
            new File(documentationRoot, "optimisationSystem-tasks-list.md"));
            
        
            /** vehicule system documentation*/
            template.addContextSection(softwareSystemVehicule, new File(documentationRoot, "vehicule-context.md"));
            template.addFunctionalOverviewSection(softwareSystemVehicule,
                    new File(documentationRoot, "vehicule-functional-overview.md"));
            template.addQualityAttributesSection(softwareSystemVehicule,
                    new File(documentationRoot, "vehicule-quality-attributes.md"));
            template.addSection(softwareSystemVehicule, "My context view3", Format.Markdown, " ![](#SystemContext)");
             template.addContainersSection(softwareSystemVehicule, Format.Markdown,
                "### vehicule containers view  \n ![](embed:vehiculeContainersView)");
            template.addComponentsSection(containerDisplay, Format.Markdown,
            "###vehicule dynamic view  \n ![](embed:dynamicView1)");
            final String java = Utils.readFile("src/main/java/dashview/Interfaces/IExample.java");
            template.addDataSection(softwareSystemVehicule,  Format.AsciiDoc,
                        java);
    
          
        } catch (final IOException e) {
            e.printStackTrace();
        }

        /** 
         * Documenting decisions
         */
        final Documentation doc = workspace.getDocumentation();
        doc.addDecision(softwareSystemRacing, "1", new Date(), "Choix de la plateforme de conception", DecisionStatus.Proposed,
        Format.Markdown, "Utiliser Structurizr pour documenter l'architecture du système");

        uploadWorkspaceToStructurizr(workspace);
    }

    private void applyViewsStyling() {
        // add some styling
        final Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
        styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);

        // styles.addElementStyle(Tags.RELATIONSHIP).color("#Ff0000");
        styles.addRelationshipStyle("API").color("#ff0000");
        styles.addRelationshipStyle("UDP").color("#00ff00").dashed(true);
        styles.addRelationshipStyle("TCP").color("#00ff00").dashed(false);
    }

    private static void uploadWorkspaceToStructurizr(final Workspace workspace) {
        final StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
        try {
            structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private File writeRequirementsFile(final StaticView view, final Type type, final String filename) {
        final File file = new File(documentationRoot, filename);
        try {
            final FileWriter functional = new FileWriter(file);
            functional.write(toMarkdown(view, type));
            functional.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String toMarkdown(final StaticView view, final Type type) {
        String result = "";
        final Set<ElementView> elements = view.getElements();
        for (final ElementView element : elements) {
            result += Requirements.toMarkdown(element.getElement(), type);
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