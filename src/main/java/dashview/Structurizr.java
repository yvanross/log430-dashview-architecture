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
import com.structurizr.model.Container;
import com.structurizr.model.Enterprise;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.model.Element;

import com.structurizr.view.*;

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

    Person pilot, engineer, optimisationEngineer;
    SoftwareSystem vehiculeSystem, racingSystem, optimisationSystem;
    Workspace workspace;
    Model model;
    ViewSet views;
    StructurizrDocumentationTemplate template;
    File documentationRoot;

    public static void main(final String[] args) {
        try {
            new Structurizr().run();
        } catch (final Exception e) {
            System.out.println("----------------");
            e.printStackTrace();
        }
    }

    /** read init file and configure access key for Structurizr API */
    private void _init_structurizr(){
        File iniFile = new File("dashview.ini");
        try {
            Wini ini = new Wini(iniFile);
            WORKSPACE_ID = ini.get("structurizr","WORKSPACE_ID",long.class);
            API_KEY = ini.get("structurizr","API_KEY",String.class);
            API_SECRET = ini.get("structurizr","API_SECRET",String.class);
          } catch(final Exception e){
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
        model = workspace.getModel();
        model.setEnterprise(enterprise);

        // initialisation des requis
        // Requirements.createAll();
        // Requirements.toYaml();
        Requirements.fromYaml("requirements.yml");

        views = workspace.getViews();
        applyViewsStyling();

        // template for documentation
        template = new StructurizrDocumentationTemplate(workspace);
        documentationRoot = new File("./documentation");

        createPersons();
        createSystems();
        racingSystemDecompositions();
        vehiculeSystemDecomposition();
        optimisationSystemDecomposition();

        systemLandscapeView();
        vehiculeSystemContextView();
        racingSystemContextView();
        optimisationSystemContextView();

        defineDecisions(workspace);

        uploadWorkspaceToStructurizr(workspace);
    }

    private void applyViewsStyling() {
        // add some styling
        final Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
        styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
        // styles.addElementStyle(Tags.RELATIONSHIP).color("#Ff0000");
        styles.addRelationshipStyle("API").color("#ff0000");
    }

    private void optimisationSystemContextView() {
        final SystemContextView _optimisationSystemContextView = views.createSystemContextView(optimisationSystem,
                "OptimisationSystemContextView",
                "Système permettant l'optimisation des paramètres du véhicule à partir des données temps réel et calculés.");
        _optimisationSystemContextView.setPaperSize(PaperSize.A5_Landscape);
        _optimisationSystemContextView.addNearestNeighbours(optimisationSystem);
        _optimisationSystemContextView.enableAutomaticLayout();

        try {
            template.addContextSection(optimisationSystem,
                    new File(documentationRoot, "optimisationSystem-context.md"));
            template.addFunctionalOverviewSection(optimisationSystem,
                    new File(documentationRoot, "optimisationSystem-functional-overview.md"));
            template.addQualityAttributesSection(optimisationSystem,
                    new File(documentationRoot, "optimisationSystem-quality-attributes.md"));
            template.addSection(optimisationSystem, "Task list",
                    new File(documentationRoot, "optimisationSystem-tasks-list.md"));

        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    private void optimisationSystemDecomposition() {
    }

    private void vehiculeSystemDecomposition() {
    }

    private void racingSystemDecompositions() {
        final Container engineeringUI = racingSystem.addContainer("Racing System User interface",
                "User interface to control....", "React");
        final Container racingSystemServer = racingSystem.addContainer("Racing system Server",
                "server that provide an API to record vehicule state and update...", "Web server Golang");

        engineer.uses(engineeringUI, "Uses");
        engineeringUI.uses(racingSystemServer, "RacingSystemAPI").addTags("API");
        vehiculeSystem.uses(racingSystemServer, "RacingSystemAPI").addTags("API");
        racingSystemServer.uses(optimisationSystem, "OptimisationSysTemAPI").addTags("API");

    }

    private static void uploadWorkspaceToStructurizr(final Workspace workspace) {
        final StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
        try {
            structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void createPersons() throws Exception {
        pilot = model.addPerson("Pilot",
                "Le pilote contrôle le véhicule lors des essais sur piste et des compétitions. Il utilise l’application en mode pilote afin d’accéder aux données du véhicule ce qui permet d’avoir une meilleure compréhension des différents composants et d’améliorer sa conduite.");

        Requirements.addToElement((Element) pilot, "EF01", "EF02", "EF06");
        pilot.setUrl("http://www.clemex.com");

        engineer = model.addPerson("Engineer",
                "L'ingénieur de piste gère les alarmes et capteurs du véhicule et ajuste/optimise les paramètres logiciels du véhicule");

        optimisationEngineer = model.addPerson("University Optimisation Engineer",
                "Un ingénieur spécialisé en course automobile qui analyse les données accumulé pour fournir les paramètres d'optimisation au véhicule de course.");
        Requirements.addToElement(optimisationEngineer, "EF16");
        Requirements.addToElement(optimisationEngineer, "EF11");

    }

    private void createSystems() throws Exception {
        optimisationSystem = model.addSoftwareSystem("Optimisation Server",
                "Système distant permettant de récupérer les données d'un circuit et de faire l'analyse de ceux-ci pour fournire les paramètres du véhicule pour optimise le rendement de celui-ci durant la course.");
        optimisationSystem.addTags("REMOTE");
        Requirements.addToElement(optimisationSystem, "EF16", "ENF01", "ENF02");

        racingSystem = model.addSoftwareSystem("Racing System",
                "Système de calcul sur site permettant de récupérer les données temps réel et d'envoyer des commandes aux véhicule pour la calibration de celui-ci.");
        Requirements.addToElement(racingSystem, "ENF03", "ENF04");

        vehiculeSystem = model.addSoftwareSystem("Vehicule System",
                "Système déployé dans les véhicules FormuleETS pour permettre la communication avec le Racing Server et le pilote.");

        // create relations between Person and systems
        pilot.uses(vehiculeSystem, "Consulte l'état du véhicule durant la course");
        engineer.uses(racingSystem, "Optimise la configuration du véhicule");
        optimisationEngineer.uses(optimisationSystem,
                "Analyse les données d'historique et fournie des données d'optimisation du véhicule");

        // create relations between systems
        vehiculeSystem.uses(racingSystem, "Fournie les données temps réel");
        racingSystem.uses(optimisationSystem, "Fournie les données pour analyse et récupère les modèle d'optimisation");

    }

    private void systemLandscapeView() {

        final SystemLandscapeView view = views.createSystemLandscapeView("system_landscape_view",
                "Integration de tout les systèmes pour le projet FormuleETS DashView");
        view.setPaperSize(PaperSize.A4_Landscape);
        view.addAllSoftwareSystems();
        view.addAllPeople();
        view.enableAutomaticLayout();
        // view.setTitle("This is the title of the view");
        view.setEnterpriseBoundaryVisible(true);

        try {
            template.addContextSection(null, new File(documentationRoot, "context.md"));

            final String data = "### Acteurs\n" + "#### " + pilot.getName() + "\n" + pilot.getDescription() + "\n"
                    + "#### " + engineer.getName() + "\n" + engineer.getDescription() + "\n" + "#### "
                    + optimisationEngineer.getName() + "\n" + optimisationEngineer.getDescription();

            template.addDataSection(null, Format.Markdown, data);

            template.addFunctionalOverviewSection(null,
                    writeRequirementsFile(view, Requirement.Type.FUNCTIONAL, "functionnal-overview.md"));
            template.addQualityAttributesSection(null,
                    writeRequirementsFile(view, Requirement.Type.QUALITY, "quality-attributes.md"));
            template.addConstraintsSection(null, new File(documentationRoot, "contraints.md"));

            // String result = Utils.readFile("target/site/apidocs/dashview/Interfaces/IExample.html");
            String java = Utils.readFile("src/main/java/dashview/Interfaces/IExample.java");
            template.addSection(vehiculeSystem, "Example de documentation d'interface - IExample.java",Format.AsciiDoc,java);

        } catch (final IOException e) {
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
            result += Requirements.toMarkdown(element.getElement(),type);
        }
        return result;
    }

    private void racingSystemContextView() {

        final SystemContextView _racingSystemContextView = views.createSystemContextView(racingSystem,
                "RacingSystemContextView", "Diagramme d'architecture du système Racing.");
        _racingSystemContextView.setPaperSize(PaperSize.A5_Landscape);
        _racingSystemContextView.addNearestNeighbours(racingSystem);
        _racingSystemContextView.enableAutomaticLayout();
        try {
            template.addContextSection(racingSystem, new File(documentationRoot, "racingSystem-context.md"));
            template.addFunctionalOverviewSection(racingSystem,
                    new File(documentationRoot, "racingSystem-functional-overview.md"));
            template.addQualityAttributesSection(racingSystem,
                    new File(documentationRoot, "racingSystem-quality-attributes.md"));
            template.addSection(racingSystem, "Task list", new File(documentationRoot, "racingSystem-tasks-list.md"));
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    private void vehiculeSystemContextView() {
        final SystemContextView _vehiculeSystemContextView = views.createSystemContextView(vehiculeSystem,
                "VehiculeContextView", "Diagramme d'architecture du système dans le véhicule FormuleETS.");
        _vehiculeSystemContextView.setPaperSize(PaperSize.A5_Landscape);
        _vehiculeSystemContextView.addNearestNeighbours(vehiculeSystem);
        _vehiculeSystemContextView.enableAutomaticLayout();

        try {
            template.addContextSection(vehiculeSystem, new File(documentationRoot, "vehicule-context.md"));
            template.addFunctionalOverviewSection(vehiculeSystem,
                    new File(documentationRoot, "vehicule-functional-overview.md"));
            template.addQualityAttributesSection(vehiculeSystem,
                    new File(documentationRoot, "vehicule-quality-attributes.md"));
            template.addSection(vehiculeSystem, "My context view3", Format.Markdown, " ![](#SystemContext)");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void defineDecisions(final Workspace workspace) {
        final Documentation doc = workspace.getDocumentation();
        doc.addDecision(racingSystem, "1", new Date(), "Choix de la plateforme de conception", DecisionStatus.Proposed,
                Format.Markdown, "Utiliser Structurizr pour documenter l'architecture du système");

    }

}

// @Component
// @UsesSoftwareSystem(name = "Mail Server", description = "Send emails.")
// public class MailComponent {
// }

// @Component
// public class TwitterComponent {
// @UsesComponent(description = "Send an email about a Twitter update")
// private MailComponent mailComponent;
// }

// @Component
// public class FacebookComponent {
// @UsesComponent(description = "Send an email about a Facebook update")
// private MailComponent mailComponent;
// }