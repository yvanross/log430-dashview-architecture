/** Structurizr projet to generate architecture documentation on Structurizr web site 
 * 
* 
*/
package dashview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.ini4j.Wini;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.Decision;
import com.structurizr.documentation.Documentation;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.CodeElement;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Enterprise;
import com.structurizr.model.InteractionStyle;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DynamicView;
import com.structurizr.view.ElementView;
import com.structurizr.view.PaperSize;
import com.structurizr.view.RelationshipView;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

import dashview.Interfaces.ICancanRouter;
import dashview.Interfaces.ICtrlDisplay;
import dashview.Interfaces.ICtrlEngineer;
import dashview.Interfaces.ICtrlPilot;
import dashview.Interfaces.IRacingConfigurationUI;
import dashview.Interfaces.IRacingModel;
import dashview.Interfaces.IRacingServer;
import dashview.Requirements.Requirement;
import dashview.Requirements.Requirement.Type;
import dashview.Requirements.Requirements;
import dashview.Utils.JavadocToMarkdown;
import dashview.Utils.Property;
import dashview.Utils.Utils;

/**
 * This is a simple example of how to get started with Structurizr for Java.
 * Documentation: https://github.com/structurizr/java
 */
public class Structurizr {
  private static long WORKSPACE_ID;
  private static String API_KEY;
  private static String API_SECRET;
  Long decisionId = 0l;
  Documentation doc;
  ViewSet views;
  File documentationRoot;
  Relationship relationship;

  public static void main(final String[] args) {
    try {
      new Structurizr().writeWorkspace();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public void loadWorkspace() throws Exception {
    _init_structurizr();
    StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
    structurizrClient.setMergeFromRemote(true);
    Workspace workspace = structurizrClient.getWorkspace(WORKSPACE_ID);
    Iterator<SoftwareSystem> iterator_sys = workspace.getModel().getSoftwareSystems().iterator();
    while (iterator_sys.hasNext()) {
      SoftwareSystem softwareSystem = iterator_sys.next();
      Set<Container> conts = softwareSystem.getContainers();
      Iterator<Container> iterator_cont = conts.iterator();
      while (iterator_cont.hasNext()) {
        Container cont = iterator_cont.next();
        System.out.println(cont.getCanonicalName());
      }

    }
    Documentation doc = workspace.getDocumentation();
    Iterator<Decision> itDecision = doc.getDecisions().iterator();
    while (itDecision.hasNext()) {
      Decision decision = itDecision.next();
      System.out.println(decision.getId() + "-" + decision.getElementId() + "-" + decision.getTitle() + "-"
          + decision.getContent() + "-" + decision.getStatus());
    }

  }

  public void writeWorkspace() throws Exception {
    _init_structurizr();
    // a Structurizr workspace is the wrapper for a software architecture model,
    // view and documentation
    final Workspace workspace = new Workspace("FormuleETS DashView project",
        "Représentation des systèmes nécessaires à la calibration du véhicule.");
    doc = workspace.getDocumentation();

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
     * sys Racing
     */

    final SoftwareSystem sysRacing = model.addSoftwareSystem("SystemRacing",
        "Système de calcul sur site permettant de récupérer les données temps réel et d'envoyer des commandes aux véhicule pour la calibration de celui-ci.");
    Requirements.add(sysRacing, "C01,C04,C06,C08,EF01,EF03,EF17,EF18,EF19,EF20,EF21,EF22,ENF07");

    /**
     * System racing conts
     */
    final Container contEngineeringUI = sysRacing.addContainer("EngineeringUI", "User interface to control....",
        "React");
    Requirements.add(contEngineeringUI, "C04,C08,EF17,EF18,EF19,EF20,EF21,EF22");

    final Container contRacingServer = sysRacing.addContainer("RacingServer",
        "server that provide an API to record vehicule state and update...", "Web server Golang");
    Requirements.add(contRacingServer, "C01,C06,EF01,EF03,ENF07");

    /**
     * System racing composants of contRacingServer
     */

    Component compRacingConfigurationUI = contRacingServer.addComponent("RacingConfigurationUi",
        IRacingConfigurationUI.class, "Interface usage de configuration du serveur et des véhicules", "Node.js");
    Component compRacingServer = contRacingServer.addComponent("racingServer", IRacingServer.class,
        "serveur d'acquisition de données", "Node.js");
    Component compRacingModel = contRacingServer.addComponent("racingModel", IRacingModel.class,
        "Modèle de donné servant à l'acquisition des données et à l'analyse de ceux-ci", "No SQL Database");

    /**
     * vehicule sys
     */

    final SoftwareSystem sysVehicule = model.addSoftwareSystem("SystemVehicule",
        "Système déployé dans les véhicules FormuleETS pour permettre la communication avec le Racing Server et le pilote.");
    Requirements.add(sysVehicule,
        "C02,C03,C04,C05,C07,C08,C09,EF02,EF03, EF04,EF05,EF06,EF07,EF08,EF09,EF10,EF11,EF12,EF13,EF14,EF15,EF16,ENF01,ENF02,ENF03,ENF04,ENF05,ENF06,ENF08");

    /**
     * sys vehicule Containers
     */

    final Container contDisplay = sysVehicule.addContainer("Display",
        "Application cliente permettant d'affiche les informations au pilote durant la course", "IOS Mobile");
    Requirements.add(contDisplay,
        "C02,C04,C07,C08,C09,EF02,EF03, EF04,EF05,EF06,EF07,EF08,EF09,EF15,EF16,ENF01,ENF02,ENF03,ENF04,ENF05,ENF06,ENF08");

    final Container contVolant = sysVehicule.addContainer("Volant",
        "Application cliente permettant d'envoye la sélection des boutons du volant", "Arduino WIFi");
    Requirements.add(contVolant, "EF10");

    final Container contCancanEthernet = sysVehicule.addContainer("cancanEthernet",
        "Serveur permettrant d'accumuler les données des capteurs du réseau cancan, de les transmettre au serveur et de fournir l'information nécessaire à l'affichage du pilote ",
        "cancan bus web server");
    Requirements.add(contCancanEthernet, "C03,C05,EF11,EF12,EF13,EF14,");

    /**
     * System vehicule Composants contDisplay
     */

    final Component compDisplayCtrl = contDisplay.addComponent("CtrlDisplay ",
        ICtrlDisplay.class, "Controlleur/routeur qui permet au pilote d'intéragir avec l'application",
        "Web server");
    Requirements.add(compDisplayCtrl, "C02,EF03,EF10,ENF03,ENF05");

    final Component compDisplay1 = contDisplay.addComponent("Pilote1",
        "Affiche la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute.",
        "page Application IOS");
    Requirements.add(compDisplay1, "EF04,EF06,EF07,EF09,EF11,ENF04");

    final Component compDisplay2 = contDisplay.addComponent("Pilote2",
        "Affiche la pression et la température des pneus, le biais de frein, l’antiroulis, l’odomètre, le voltage de la batterie, la température du moteur et un indicateur d’utilisation et l’angle du système de réduction de traînée.",
        "Page application IOS");
    Requirements.add(compDisplay2, "EF05,EF06,EF07,EF09,EF12,ENF04");

    final Component compDisplay3 = contDisplay.addComponent("Pilote3",
        "Affiche le temps total de course, le temps du tour courant, la différence de temps par rapport au meilleur temps, le meilleur temps de tour de piste et la différence de temps par rapport au dernier de tour de piste.",
        "Page application IOS");
    Requirements.add(compDisplay3, "EF09,EF13,ENF04");

    final Component compDisplay4 = contDisplay.addComponent("Pilote4",
        "Affiche un schéma de la piste de course et le déplacement de la voiture en temps réel, le temps du tour de piste, le meilleur temps et la différence par rapport au meilleur temps.",
        "Page application IOS");
    Requirements.add(compDisplay4, "EF09,EF14,ENF04");

    final Component compDisplayConfiguration = contDisplay.addComponent("Configuration",
        "Interface usagé utilisé par l'ingénieur pour configurer l'appliation mobile", "Page application IOS");
    Requirements.add(compDisplayConfiguration, "EF02,");

    /**
     * composants contCancanEthernet
     */

    final Component compCancanEthernet = contCancanEthernet.addComponent("cancanEthernet",
        ICancanRouter.class,
        "Routeur du Server web wifi controlant le bus CanCan pour l'acquisition/diffusion des données des capteurs",
        "CanCan bus Router");

    Relationship compDisplayCtrl2compCancanEthernet =  compDisplayCtrl.uses(compCancanEthernet, "requetes pour les interfaces usagé");
    compDisplayCtrl2compCancanEthernet.addTags("WIFI",
        "API");
    Requirements.add(compCancanEthernet, "C03,C05");

    final Component compSensors = contCancanEthernet.addComponent("Sensors",
        "Représente tout les capteurs du véhicule", "Capteurs compatible avec cancan");

    final Component compCtrlPilot = contCancanEthernet.addComponent("controllerPilot",
        ICtrlPilot.class, "Controleur pour la gestion des évènements pilote", "web server");
    Requirements.add(compCtrlPilot, "EF11,EF12,EF13,EF14");

    final Component compCtrlEngineer = contCancanEthernet.addComponent("controllerEngineer",
        ICtrlEngineer.class,
        "Controleur pour la gestion des évènements de configuration et de transmission des données", "CanCan bus");
    Requirements.add(compCtrlEngineer, "ENF07");

    /**
     * relation from User
     */
    Relationship pilot2contDisplay= pilot.uses(contDisplay, "Visualise les différents écran durant la course", "IOS application",
        InteractionStyle.Asynchronous);
    Relationship pilot2contVolant=pilot.uses(contVolant, "Appuie sur les boutons de sélection d'écran");

    Relationship engineer2contDisplay = engineer.uses(contDisplay, "Configuration manuel des paramètres du véhicule avant la course");
    Relationship engineer2sysRacing = engineer.uses(sysRacing, "Optimise la configuration du véhicule");
    Relationship engineer2contEngineeringUI = engineer.uses(contEngineeringUI, "Uses");

    /**
     * relation from sys
     */
    Relationship sysVehicule2contRacingServer = sysVehicule.uses(contRacingServer, "RacingSystemAPI");
    sysVehicule2contRacingServer.addTags("API");

    Relationship sysVehicule2sysRacing = sysVehicule.uses(sysRacing, "send Vehicule data and receive configuration files");
    sysVehicule2sysRacing.addTags("API");

    /**
     * Relationship between conts
     */
    Relationship contCancanEthernet2sysRacing = contCancanEthernet.uses(sysRacing,
        "Transmission des données de l'état des capteurs du véhicule");
        contCancanEthernet2sysRacing.addTags("UDP");

    Relationship contDisplay2contCancanEthernet = contDisplay.uses(contCancanEthernet,
        "Récupération des informations des capteurs du véhicule, Envoie des paramètres de configuration du véhicule");
    Relationship contEngineeringUI2contRacingServer =  contEngineeringUI.uses(contRacingServer, "RacingSystemAPI");

    contEngineeringUI2contRacingServer.addTags("API");
    
    Relationship contVolant2compDisplayCtrl = contVolant.uses(compDisplayCtrl, "Envoie les requète de changement d'écran à partir des boutons du volant");
    contVolant2compDisplayCtrl.addTags("API", "WIFI");
    
    Relationship contVolant2contDisplay= contVolant.uses(contDisplay, "Demande de changement d'écran");
    contVolant2contDisplay.addTags("WIFI");
    
    Relationship contDisplay2compCancanEthernet = contDisplay.uses(compCancanEthernet, "display data for pilot, get/set data for engineer");
    contDisplay2compCancanEthernet.addTags("WIFI","API");
    
    Relationship contEngineeringUI2compRacingServer = contEngineeringUI.uses(compRacingServer, "Configure les véhicules et le serveur");
    
    Relationship contCancanEthernet2compRacingServer = contCancanEthernet.uses(compRacingServer, "Envoie des données du vehicule");
    /**
     * define relationship between comps
     */

    Relationship compRacingConfigurationUI2compRacingServer = compRacingConfigurationUI.uses(compRacingServer, "Configure les véhicules");
    Relationship compRacingServer2compRacingModel = compRacingServer.uses(compRacingModel, "sauvegarde des données d'acquisition");
    Relationship compDisplayCtrl2pilot = compDisplayCtrl.delivers(pilot, "updated vehicule data");
    Relationship compDisplayCtrl2engineer = compDisplayCtrl.delivers(engineer, "updated vehicule configuration");
    Relationship compDisplayCtrl2compDisplay1 = compDisplayCtrl.uses(compDisplay1, "Affiche l'écran 1");
    Relationship compDisplayCtrl2compDisplay2 = compDisplayCtrl.uses(compDisplay2, "Affiche l'écran 2");
    Relationship compDisplayCtrl2compDisplay3 = compDisplayCtrl.uses(compDisplay3, "Affiche l'écran 3");
    Relationship compDisplayCtrl2compDisplay4 = compDisplayCtrl.uses(compDisplay4, "Affiche l'écran 4");
    Relationship compCancanEthernet2compSensors = compCancanEthernet.uses(compSensors,"Effectue la lecteur des senseurs au travers du réseau cancan");
    Relationship compCancanEthernet2compCtrlPilot = compCancanEthernet.uses(compCtrlPilot, "get data for the pilot");
    Relationship compCancanEthernet2compCtrlEngineer = compCancanEthernet.uses(compCtrlEngineer, "get/set data for engineer");

    /**
     * Set comp url to define interface
     */
    Iterator<Element> iteratorElement = model.getElements().iterator();
    while (iteratorElement.hasNext()) {
      Element element = iteratorElement.next();
      if (element instanceof Component) {
        Component comp = (Component) element;
        Iterator<CodeElement> itCodeElement = comp.getCode().iterator();
        while (itCodeElement.hasNext()) {
          CodeElement codeElement = itCodeElement.next();
          String path = codeElement.getType().replaceAll("\\.", File.separator);
          comp.setUrl(
              "https://github.com/yvanross/log430-dashview-architecture/blob/master/src/main/java/" + path + ".java");
        }
      }
    }

    /**
     * define perspectives REQUIREMENT
     */

    /**
     * C01
     */
    engineer.addPerspective("C01", "L'ingénieur est responsable de réaliser le fichier de configureation XML");
    sysRacing.addPerspective("C01",
        "Les configurations pour les catégories des alarmes et des capteurs doivent être définies dans un fichier XML.");
    contRacingServer.addPerspective("C01",
        "Usability\nLes configurations pour les catégories des alarmes et des capteurs doivent être définies dans un fichier XML.");
    /**
     * C02
     */
    sysVehicule.addPerspective("C02",
        "Usability\nLes configurations pour le mode par défaut et les couleurs de l’interface sont définies directement dans iOS.");
    contDisplay.addPerspective("C02",
        "Usability\nLes configurations pour le mode par défaut et les couleurs de l’interface sont définies directement dans iOS.");
    compDisplayCtrl.addPerspective("C02",
        "Usability\nLes configurations pour le mode par défaut et les couleurs de l’interface sont définies directement dans iOS.");
    /**
     * C03
     */
    sysRacing.addPerspective("C03", "Recoit les données du véhicule et archives celles-ci");
    contRacingServer.addPerspective("C03", "Recoit les données du véhicule");
    sysVehicule.addPerspective("C03",
        "can2Ethernet\nLa librairie Can2Ethernet développée par le club Formule ÉTS doit être utilisée.");
    contCancanEthernet.addPerspective("C03",
        "can2Ethernet\nLa librairie Can2Ethernet développée par le club Formule ÉTS doit être utilisée.");
    // relationship.addPerspective("C03", "Transfert vehicule data");
    // relation.addPerspective("C03", "Transfer les donnéeset configurations du véhicule au systèmeRacing");
    // relationship.addPerspective("C03", "Lecture du cancan bus pour récupérer les donneés des capteurs");
    compCancanEthernet.addPerspective("C03",
        "can2Ethernet\nLa librairie Can2Ethernet développée par le club Formule ÉTS doit être utilisée.");
    compSensors.addPerspective("C03", "Capteurs permettant l'acquision de l'état du véhicule");
    /**
     * C04
     */
    sysRacing.addPerspective("C04",
        "L’application doit être en Objective-C sous la plateforme iOS 7 et est destinée aux iPod Touch de 5e génération avec un écran de 4 pouces");
    contEngineeringUI.addPerspective("C04",
        "Objective-C\nL’application doit être en Objective-C sous la plateforme iOS 7 et est destinée aux iPod Touch de 5e génération avec un écran de 4 pouces");
    sysVehicule.addPerspective("C04",
        "Objective-C\nL’application doit être en Objective-C sous la plateforme iOS 7 et est destinée aux iPod Touch de 5e génération avec un écran de 4 pouces");
    contDisplay.addPerspective("C04",
        "Objective-C\nL’application doit être en Objective-C sous la plateforme iOS 7 et est destinée aux iPod Touch de 5e génération avec un écran de 4 pouces");
    /**
     * C05
     */
    sysVehicule.addPerspective("C05",
        "Wifi UDP\nLes données sont envoyées par Wi-Fi via le protocole UDP et le bus CAN.");
    contCancanEthernet.addPerspective("C05",
        "Wifi UDP\nLes données sont envoyées par Wi-Fi via le protocole UDP et le bus CAN.");
    compCancanEthernet.addPerspective("C05",
        "Wifi UDP\nLes données sont envoyées par Wi-Fi via le protocole UDP et le bus CAN.");

    /**
     * C06
     */
    sysRacing.addPerspective("C06", "Le port UDP est le port par défaut soit 1337.");
    contRacingServer.addPerspective("C06", "UDP port\nLe port UDP est le port par défaut soit 1337.");
    /**
     * C07
     */
    sysVehicule.addPerspective("C07",
        "Id et offset des capteurs\nLes « id » et les « offset » des capteurs doivent suivre la table CAN fournie.");
    contDisplay.addPerspective("C07",
        "Id et offset des capteurs\nLes « id » et les « offset » des capteurs doivent suivre la table CAN fournie.");
    /**
     * C08
     */
    sysRacing.addPerspective("C08", "Les textes de l’application doivent être en anglais.");
    contEngineeringUI.addPerspective("C08", "langue\nLes textes de l’application doivent être en anglais.");
    sysVehicule.addPerspective("C08", "langue\nLes textes de l’application doivent être en anglais.");
    contDisplay.addPerspective("C08", "langue\nLes textes de l’application doivent être en anglais.");
    /**
     * CO9
     */
    sysVehicule.addPerspective("C09",
        "deployment\nL’application doit être installée et exécutée sur un iPod Touch qui ne nécessite pas un « iOS jailbreaking » ");
    contDisplay.addPerspective("C09",
        "deployment\nL’application doit être installée et exécutée sur un iPod Touch qui ne nécessite pas un « iOS jailbreaking » ");
    /**
     * EF01
     */
    engineer.addPerspective("EF01", "Configuration de l’application avec un fichier XML");
    sysRacing.addPerspective("EF01", "Configuration de l’application avec un fichier XML");
    contRacingServer.addPerspective("EF01",
        "Configuration de l’application avec un fichier XML\nL’application doit utiliser un fichier de configuration, sous le format XML, pour déterminer les alarmes et capteurs disponibles. La liste des alarmes et des capteurs sont définis selon la table CAN fournie par la Formule ÉTS.");
    /**
     * EF02
     */
    pilot.addPerspective("EF02",
        "Le système doit permettre de changer quelques configurations directement dans les paramètres de l’application sur iOS. Les configurations doivent inclure, entre autres, le changement de mode entre pilote et ingénieur de piste ainsi que le changement des couleurs de l’interface de pâle à foncé.");
    sysVehicule.addPerspective("EF02",
        "Configuration de l’application dans les paramètres d’iOS\nLe système doit permettre de changer quelques configurations directement dans les paramètres de l’application sur iOS. Les configurations doivent inclure, entre autres, le changement de mode entre pilote et ingénieur de piste ainsi que le changement des couleurs de l’interface de pâle à foncé.");
    contDisplay.addPerspective("EF02",
        "Configuration de l’application dans les paramètres d’iOS\nLe système doit permettre de changer quelques configurations directement dans les paramètres de l’application sur iOS. Les configurations doivent inclure, entre autres, le changement de mode entre pilote et ingénieur de piste ainsi que le changement des couleurs de l’interface de pâle à foncé.");
    compDisplayConfiguration.addPerspective("EF02",
        "Configuration de l’application dans les paramètres d’iOS\nLe système doit permettre de changer quelques configurations directement dans les paramètres de l’application sur iOS. Les configurations doivent inclure, entre autres, le changement de mode entre pilote et ingénieur de piste ainsi que le changement des couleurs de l’interface de pâle à foncé.");
    /**
     * EF03
     */
    sysRacing.addPerspective("EF03",
        "Gérer les données reçues en temps réel\nL’application doit constamment recevoir des données du bus CAN via Wi-Fi à partir du module Can2Ethernet et, en comparant avec la table des messages CAN, associer ces données aux capteurs et alarmes pour les afficher.");
    contRacingServer.addPerspective("EF03",
        "Gérer les données reçues en temps réel\nL’application doit constamment recevoir des données du bus CAN via Wi-Fi à partir du module Can2Ethernet et, en comparant avec la table des messages CAN, associer ces données aux capteurs et alarmes pour les afficher.");
    sysVehicule.addPerspective("EF03",
        "Gérer les données reçues en temps réel\nL’application doit constamment recevoir des données du bus CAN via Wi-Fi à partir du module Can2Ethernet et, en comparant avec la table des messages CAN, associer ces données aux capteurs et alarmes pour les afficher.");
    contDisplay.addPerspective("EF03",
        "Gérer les données reçues en temps réel\nL’application doit constamment recevoir des données du bus CAN via Wi-Fi à partir du module Can2Ethernet et, en comparant avec la table des messages CAN, associer ces données aux capteurs et alarmes pour les afficher.");
    compDisplayCtrl.addPerspective("EF03",
        "Gérer les données reçues en temps réel\nL’application doit constamment recevoir des données du bus CAN via Wi-Fi à partir du module Can2Ethernet et, en comparant avec la table des messages CAN, associer ces données aux capteurs et alarmes pour les afficher.");
    /**
     * EF04
     */
    sysVehicule.addPerspective("EF04",
        "Afficher des couleurs spécifiques pour les RPM\nL’application doit afficher l’indicateur de RPM avec un code de couleur précis, soit de jaune à rouge en passant par une zone orange visible. De 3000 RPM à 15 000 RPM, l’indicateur doit être dans le spectre de jaune à rouge. À 15 000 RPM et plus, l’indicateur doit être rouge. De plus, le rouge doit changer selon un paramètre calculé par l’ACL lorsqu’un message correspondant au « id » de la table CAN de ce paramètre est reçu.");
    contDisplay.addPerspective("EF04",
        "Afficher des couleurs spécifiques pour les RPM\nL’application doit afficher l’indicateur de RPM avec un code de couleur précis, soit de jaune à rouge en passant par une zone orange visible. De 3000 RPM à 15 000 RPM, l’indicateur doit être dans le spectre de jaune à rouge. À 15 000 RPM et plus, l’indicateur doit être rouge. De plus, le rouge doit changer selon un paramètre calculé par l’ACL lorsqu’un message correspondant au « id » de la table CAN de ce paramètre est reçu.");
    compDisplay1.addPerspective("EF04",
        "Afficher des couleurs spécifiques pour les RPM\nL’application doit afficher l’indicateur de RPM avec un code de couleur précis, soit de jaune à rouge en passant par une zone orange visible. De 3000 RPM à 15 000 RPM, l’indicateur doit être dans le spectre de jaune à rouge. À 15 000 RPM et plus, l’indicateur doit être rouge. De plus, le rouge doit changer selon un paramètre calculé par l’ACL lorsqu’un message correspondant au « id » de la table CAN de ce paramètre est reçu.");
    /**
     * EF05
     */

    sysVehicule.addPerspective("EF05",
        "Afficher des couleurs spécifiques pour la température des pneus\nL’application doit afficher la température des trois capteurs de chaque pneu selon des couleurs spécifiques et avec des transitions fluides. Les capteurs sont situés à l’extérieur, au milieu et à l’intérieur de chacun des pneus. Lorsque la température est de 25°C et moins, la couleur est bleue. Entre 25°C et 65°C, la couleur passe de bleue à jaune. Entre 65°C et 95°C, l’indicateur passe de jaune à rouge. Finalement, en haut de 95°C, la couleur est rouge.");
    contDisplay.addPerspective("EF05",
        "Afficher des couleurs spécifiques pour la température des pneus\nL’application doit afficher la température des trois capteurs de chaque pneu selon des couleurs spécifiques et avec des transitions fluides. Les capteurs sont situés à l’extérieur, au milieu et à l’intérieur de chacun des pneus. Lorsque la température est de 25°C et moins, la couleur est bleue. Entre 25°C et 65°C, la couleur passe de bleue à jaune. Entre 65°C et 95°C, l’indicateur passe de jaune à rouge. Finalement, en haut de 95°C, la couleur est rouge.");
    compDisplay2.addPerspective("EF05",
        "Afficher des couleurs spécifiques pour la température des pneus\nL’application doit afficher la température des trois capteurs de chaque pneu selon des couleurs spécifiques et avec des transitions fluides. Les capteurs sont situés à l’extérieur, au milieu et à l’intérieur de chacun des pneus. Lorsque la température est de 25°C et moins, la couleur est bleue. Entre 25°C et 65°C, la couleur passe de bleue à jaune. Entre 65°C et 95°C, l’indicateur passe de jaune à rouge. Finalement, en haut de 95°C, la couleur est rouge.");
    /**
     * EF06
     */
    sysVehicule.addPerspective("EF06",
        "Afficher des couleurs spécifiques pour la température du moteur\nLe Dash Display doit afficher la température du moteur à l’aide de quatre couleurs. Lorsque la température est de 70°C et moins, l’indicateur doit être bleu. Entre 70°C et 90°C, la couleur utilisée est le vert. Entre 90°C et 100°C, l’indicateur doit être jaune et, finalement, il doit être rouge lorsque la température dépasse le 100°C.");
    contDisplay.addPerspective("EF06",
        "Afficher des couleurs spécifiques pour la température du moteur\nLe Dash Display doit afficher la température du moteur à l’aide de quatre couleurs. Lorsque la température est de 70°C et moins, l’indicateur doit être bleu. Entre 70°C et 90°C, la couleur utilisée est le vert. Entre 90°C et 100°C, l’indicateur doit être jaune et, finalement, il doit être rouge lorsque la température dépasse le 100°C.");
    compDisplay1.addPerspective("EF06",
        "Afficher des couleurs spécifiques pour la température du moteur\nLe Dash Display doit afficher la température du moteur à l’aide de quatre couleurs. Lorsque la température est de 70°C et moins, l’indicateur doit être bleu. Entre 70°C et 90°C, la couleur utilisée est le vert. Entre 90°C et 100°C, l’indicateur doit être jaune et, finalement, il doit être rouge lorsque la température dépasse le 100°C.");
    compDisplay2.addPerspective("EF06",
        "Afficher des couleurs spécifiques pour la température du moteur\nLe Dash Display doit afficher la température du moteur à l’aide de quatre couleurs. Lorsque la température est de 70°C et moins, l’indicateur doit être bleu. Entre 70°C et 90°C, la couleur utilisée est le vert. Entre 90°C et 100°C, l’indicateur doit être jaune et, finalement, il doit être rouge lorsque la température dépasse le 100°C.");
    /**
     * EF07
     */
    sysVehicule.addPerspective("EF07",
        "Afficher des couleurs spécifiques pour la puissance de la batterie\nLe système doit afficher la puissance de la batterie à l’aide de deux couleurs. Lorsque la batterie est à la puissance maximale (14V) jusqu’à 11.5V, le fond du capteur est vert. Lorsque la puissance atteint 11.5 V et en dessous, le fond devient rouge.");
    contDisplay.addPerspective("EF07",
        "Afficher des couleurs spécifiques pour la puissance de la batterie\nLe système doit afficher la puissance de la batterie à l’aide de deux couleurs. Lorsque la batterie est à la puissance maximale (14V) jusqu’à 11.5V, le fond du capteur est vert. Lorsque la puissance atteint 11.5 V et en dessous, le fond devient rouge.");
    compDisplay1.addPerspective("EF07",
        "Afficher des couleurs spécifiques pour la puissance de la batterie\nLe système doit afficher la puissance de la batterie à l’aide de deux couleurs. Lorsque la batterie est à la puissance maximale (14V) jusqu’à 11.5V, le fond du capteur est vert. Lorsque la puissance atteint 11.5 V et en dessous, le fond devient rouge.");
    compDisplay2.addPerspective("EF07",
        "Afficher des couleurs spécifiques pour la puissance de la batterie\nLe système doit afficher la puissance de la batterie à l’aide de deux couleurs. Lorsque la batterie est à la puissance maximale (14V) jusqu’à 11.5V, le fond du capteur est vert. Lorsque la puissance atteint 11.5 V et en dessous, le fond devient rouge.");
    /**
     * EF08
     */
    sysVehicule.addPerspective("EF08",
        "Afficher des couleurs spécifiques pour les alertes\nLe Dash Display doit afficher les alertes de deux façons selon leur statut. Dans tous les cas, elles sont affichées en rouge. Lorsqu’elles sont en cours, elles ont une opacité de 100 %. Sinon, l’opacité diminue à 30 %.");
    contDisplay.addPerspective("EF08",
        "Afficher des couleurs spécifiques pour les alertes\nLe Dash Display doit afficher les alertes de deux façons selon leur statut. Dans tous les cas, elles sont affichées en rouge. Lorsqu’elles sont en cours, elles ont une opacité de 100 %. Sinon, l’opacité diminue à 30 %.");
    compDisplay2.addPerspective("EF08",
        "Afficher des couleurs spécifiques pour les alertes\nLe Dash Display doit afficher les alertes de deux façons selon leur statut. Dans tous les cas, elles sont affichées en rouge. Lorsqu’elles sont en cours, elles ont une opacité de 100 %. Sinon, l’opacité diminue à 30 %.");
    /**
     * EF09
     */
    pilot.addPerspective("EF09",
        "Visualiser les alarmes et les capteurs sur l’interface pilote\nL’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement.");
    sysVehicule.addPerspective("EF09",
        "Visualiser les alarmes et les capteurs sur l’interface pilote\nL’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement.");
    contDisplay.addPerspective("EF09",
        "Visualiser les alarmes et les capteurs sur l’interface pilote\nL’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement.");
    compDisplay1.addPerspective("EF09",
        "Visualiser les alarmes et les capteurs sur l’interface pilote\nL’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement.");
    compDisplay2.addPerspective("EF09",
        "Visualiser les alarmes et les capteurs sur l’interface pilote\nL’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement.");
    compDisplay4.addPerspective("EF09",
        "Visualiser les alarmes et les capteurs sur l’interface pilote\nL’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement.");
    compDisplay3.addPerspective("EF09",
        "Visualiser les alarmes et les capteurs sur l’interface pilote\nL’application doit afficher, de manière claire, les informations nécessaires au pilote en provenance des différents capteurs du véhicule. De plus, les messages d’alarmes seront aussi affichés de façon évidente et de façon à ce que le pilote les remarques immédiatement.");
    /**
     * EF10
     */
    pilot.addPerspective("EF10",
        "Changer l’interface lors de l’appui sur le bouton du volant\n L’application doit changer l’interface affichée lorsque le pilote appuie sur un bouton du volant. Le bouton envoie un message CAN à l’application pour lui indiquer de changer. L’application en mode pilote affichera quatre interfaces différentes en boucle.");
    sysVehicule.addPerspective("EF10",
        "Changer l’interface lors de l’appui sur le bouton du volant\nL’application doit changer l’interface affichée lorsque le pilote appuie sur un bouton du volant. Le bouton envoie un message CAN à l’application pour lui indiquer de changer. L’application en mode pilote affichera quatre interfaces différentes en boucle.");
    contVolant.addPerspective("EF10",
        "Changer l’interface lors de l’appui sur le bouton du volant\nL’application doit changer l’interface affichée lorsque le pilote appuie sur un bouton du volant. Le bouton envoie un message CAN à l’application pour lui indiquer de changer. L’application en mode pilote affichera quatre interfaces différentes en boucle.");
    compDisplayCtrl.addPerspective("EF10",
        "Changer l’interface lors de l’appui sur le bouton du volant\nL’application doit changer l’interface affichée lorsque le pilote appuie sur un bouton du volant. Le bouton envoie un message CAN à l’application pour lui indiquer de changer. L’application en mode pilote affichera quatre interfaces différentes en boucle.");
    /**
     * EF11
     */
    pilot.addPerspective("EF11", "Le pilot doit pouvoir activer l'écran 1 a partir d'un bouton sur le volant");
    sysVehicule.addPerspective("EF11",
        "Contenu de la première interface\nL’application doit afficher, sur la première interface, les capteurs suivants : la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute.");
    compDisplay1.addPerspective("EF11",
        "Contenu de la première interface\nL’application doit afficher, sur la première interface, les capteurs suivants : la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute.");
    contCancanEthernet.addPerspective("EF11",
        "Contenu de la première interface\nL’application doit afficher, sur la première interface, les capteurs suivants : la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute.");
    compCtrlPilot.addPerspective("EF11",
        "Contenu de la première interface\nL’application doit afficher, sur la première interface, les capteurs suivants : la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute.");
    /**
     * EF12
     */
    contCancanEthernet.addPerspective("EF12",
        "Contenu de la deuxième interface\nLe Dash Display doit afficher, sur la deuxième interface, les capteurs suivants : la pression et la température des pneus, le biais de frein, l’antiroulis, l’odomètre, le voltage de la batterie, la température du moteur et un indicateur d’utilisation et l’angle du système de réduction de traînée.");
    compDisplay2.addPerspective("EF12",
        "Contenu de la deuxième interface\nLe Dash Display doit afficher, sur la deuxième interface, les capteurs suivants : la pression et la température des pneus, le biais de frein, l’antiroulis, l’odomètre, le voltage de la batterie, la température du moteur et un indicateur d’utilisation et l’angle du système de réduction de traînée.");
    pilot.addPerspective("EF12", "Le pilot doit pouvoir activer l'écran 2 a partir d'un bouton sur le volant");
    sysVehicule.addPerspective("EF12",
        "Contenu de la deuxième interface\nLe Dash Display doit afficher, sur la deuxième interface, les capteurs suivants : la pression et la température des pneus, le biais de frein, l’antiroulis, l’odomètre, le voltage de la batterie, la température du moteur et un indicateur d’utilisation et l’angle du système de réduction de traînée.");
    compCtrlPilot.addPerspective("EF12",
        "Contenu de la deuxième interface\nLe Dash Display doit afficher, sur la deuxième interface, les capteurs suivants : la pression et la température des pneus, le biais de frein, l’antiroulis, l’odomètre, le voltage de la batterie, la température du moteur et un indicateur d’utilisation et l’angle du système de réduction de traînée.");
    /**
     * EF13
     */
    contCancanEthernet.addPerspective("EF13",
        "Contenu de la troisième interface\nL’application doit afficher, sur la troisième interface, le temps total de course, le temps du tour courant, la différence de temps par rapport au meilleur temps, le meilleur temps de tour de piste et la différence de temps par rapport au dernier de tour de piste.");
    compDisplay3.addPerspective("EF13",
        "Contenu de la troisième interface\nL’application doit afficher, sur la troisième interface, le temps total de course, le temps du tour courant, la différence de temps par rapport au meilleur temps, le meilleur temps de tour de piste et la différence de temps par rapport au dernier de tour de piste.");
    pilot.addPerspective("EF13", "Le pilot doit pouvoir activer l'écran 3 a partir d'un bouton sur le volant");
    sysVehicule.addPerspective("EF13",
        "Contenu de la troisième interface\nL’application doit afficher, sur la troisième interface, le temps total de course, le temps du tour courant, la différence de temps par rapport au meilleur temps, le meilleur temps de tour de piste et la différence de temps par rapport au dernier de tour de piste.");
    compCtrlPilot.addPerspective("EF13",
        "Contenu de la troisième interface\nL’application doit afficher, sur la troisième interface, le temps total de course, le temps du tour courant, la différence de temps par rapport au meilleur temps, le meilleur temps de tour de piste et la différence de temps par rapport au dernier de tour de piste.");
    /**
     * EF14
     */
    contCancanEthernet.addPerspective("EF14",
        "Contenu de la quatrième interface\nDash Display doit afficher, sur la quatrième interface, un schéma de la piste de course et le déplacement de la voiture en temps réel. De plus, les temps suivants doivent être présents : le temps du tour de piste, le meilleur temps et la différence par rapport au meilleur temps.");
    compDisplay4.addPerspective("EF14",
        "Contenu de la quatrième interface\nDash Display doit afficher, sur la quatrième interface, un schéma de la piste de course et le déplacement de la voiture en temps réel. De plus, les temps suivants doivent être présents : le temps du tour de piste, le meilleur temps et la différence par rapport au meilleur temps.");
    sysVehicule.addPerspective("EF14",
        "Contenu de la quatrième interface\nDash Display doit afficher, sur la quatrième interface, un schéma de la piste de course et le déplacement de la voiture en temps réel. De plus, les temps suivants doivent être présents : le temps du tour de piste, le meilleur temps et la différence par rapport au meilleur temps.");
    compCtrlPilot.addPerspective("EF14",
        "Contenu de la quatrième interface\nDash Display doit afficher, sur la quatrième interface, un schéma de la piste de course et le déplacement de la voiture en temps réel. De plus, les temps suivants doivent être présents : le temps du tour de piste, le meilleur temps et la différence par rapport au meilleur temps.");
    /**
     * EF15
     */
    pilot.addPerspective("EF15", "Affichage en mode paysage pour le mode pilote");
    sysVehicule.addPerspective("EF15",
        "Affichage en mode paysage pour le mode pilote\ndoit afficher les interfaces en mode pilote sous le format paysage.");
    contDisplay.addPerspective("EF15",
        "Affichage en mode paysage pour le mode pilote\ndoit afficher les interfaces en mode pilote sous le format paysage.");
    /**
     * EF16
     */
    pilot.addPerspective("EF16", "Mettre en veille l’application après 3 secondes sans donnéesÉ");
    sysVehicule.addPerspective("EF16",
        "Mettre en veille l’application après 3 secondes sans données\ndoit se mettre en veille si une interruption de données survient et si elle dure plus de trois secondes.");
    contDisplay.addPerspective("EF16",
        "Mettre en veille l’application après 3 secondes sans données\ndoit se mettre en veille si une interruption de données survient et si elle dure plus de trois secondes.");
    /**
     * EF17
     */
    engineer.addPerspective("EF17", "Visualiser les alarmes et les capteurs sur l’interface ingénieur");
    sysRacing.addPerspective("EF17",
        "Visualiser les alarmes et les capteurs sur l’interface ingénieur\nDoit afficher, sous forme de liste, les différents capteurs et alarmes que l’utilisateur décide d’inclure. Les capteurs et les alarmes sont affichés séparément, les alarmes se trouvant en haut de la liste. Lors de la présentation du prototype, le club formule ÉTS a précisé qu’ils souhaiteraient traiter les alarmes de la même façon que les différents capteurs donc les rendre modifiable sur la page principale.");
    contEngineeringUI.addPerspective("EF17",
        "Visualiser les alarmes et les capteurs sur l’interface ingénieur\ndoit afficher, sous forme de liste, les différents capteurs et alarmes que l’utilisateur décide d’inclure. Les capteurs et les alarmes sont affichés séparément, les alarmes se trouvant en haut de la liste. Lors de la présentation du prototype, le club formule ÉTS a précisé qu’ils souhaiteraient traiter les alarmes de la même façon que les différents capteurs donc les rendre modifiable sur la page principale.");
    /**
     * EF18
     */
    engineer.addPerspective("EF18",
        "Ajouter une alarme ou un capteur\ndoit permettre à l’utilisateur de sélectionner dans une liste une alarme ou un capteur à ajouter à la liste d’affichage. L’application doit aussi permettre de filtrer la liste des alarmes et des capteurs qui peuvent être ajoutés et d’y effectuer une recherche.");
    // sysRacing.addPerspective("EF18","Ajouter une alarme ou un capteur\ndoit
    // permettre à l’utilisateur de sélectionner dans une liste une alarme ou un
    // capteur à ajouter à la liste d’affichage. L’application doit aussi permettre
    // de filtrer la liste des alarmes et des capteurs qui peuvent être ajoutés et
    // d’y effectuer une recherche.";
    contEngineeringUI.addPerspective("EF18",
        "Ajouter une alarme ou un capteur\ndoit permettre à l’utilisateur de sélectionner dans une liste une alarme ou un capteur à ajouter à la liste d’affichage. L’application doit aussi permettre de filtrer la liste des alarmes et des capteurs qui peuvent être ajoutés et d’y effectuer une recherche.");
    /**
     * EF19
     */
    engineer.addPerspective("EF19", "Changer l’ordre des alarmes et des capteurs affichés");
    sysRacing.addPerspective("EF19",
        "Changer l’ordre des alarmes et des capteurs affichés\ndoit permettre à l’utilisateur, une fois en mode édition de la liste, de réorganiser respectivement les alarmes et les capteurs entre eux.");
    contEngineeringUI.addPerspective("EF19",
        "Changer l’ordre des alarmes et des capteurs affichés\ndoit permettre à l’utilisateur, une fois en mode édition de la liste, de réorganiser respectivement les alarmes et les capteurs entre eux.");
    /**
     * ( EF20)
     */
    sysRacing.addPerspective("EF20",
        "Supprimer une alarme ou un capteur affiché\ndoit permettre à l’utilisateur, une fois en mode édition de la liste, de supprimer un capteur ou une alarme.");
    engineer.addPerspective("EF20", "Supprimer une alarme ou un capteur affiché");
    contEngineeringUI.addPerspective("EF20",
        "Supprimer une alarme ou un capteur affiché\ndoit permettre à l’utilisateur, une fois en mode édition de la liste, de supprimer un capteur ou une alarme.");
    /**
     * EF21
     */
    engineer.addPerspective("EF21", "Afficher les détails de l’alarme ou du capteur");
    sysRacing.addPerspective("EF21",
        "Afficher les détails de l’alarme ou du capteur\nsystème doit permettre de cliquer sur un capteur ou une alarme affichés afin d’obtenir plus de détails. Lors de la présentation du prototype, cette exigence a été clarifiée. Le client désire avoir la possibilité de modifier l’affichage du widget pour d’autres formats ainsi qu’obtenir un historique des dernières données. Les informations apparaissent sous le widget principal avec la possibilité d’afficher l’historique en plein écran.");
    sysRacing.addPerspective("EF22",
        "Gérer les cas d’erreurs de l’application\ndoit, en cas d’erreurs de l’application, afficher les dernières données reçues. Les cas d’erreurs peuvent être, par exemple, une erreur de transmission de données ou un message d’erreur reçu par une chaîne CAN du module Can2Ethernet.");
    contEngineeringUI.addPerspective("EF21",
        "Afficher les détails de l’alarme ou du capteur\nsystème doit permettre de cliquer sur un capteur ou une alarme affichés afin d’obtenir plus de détails. Lors de la présentation du prototype, cette exigence a été clarifiée. Le client désire avoir la possibilité de modifier l’affichage du widget pour d’autres formats ainsi qu’obtenir un historique des dernières données. Les informations apparaissent sous le widget principal avec la possibilité d’afficher l’historique en plein écran.");
    /**
     * EF22
     */
    engineer.addPerspective("EF22", "Gérer les cas d’erreurs de l’application");
    contEngineeringUI.addPerspective("EF22",
        "Gérer les cas d’erreurs de l’application\ndoit, en cas d’erreurs de l’application, afficher les dernières données reçues. Les cas d’erreurs peuvent être, par exemple, une erreur de transmission de données ou un message d’erreur reçu par une chaîne CAN du module Can2Ethernet.");
    /**
     * ENF01
     */
    sysVehicule.addPerspective("ENF01",
        "Utilisation du visuel de façon intuitive\nL’interface doit respecter le fonctionnement natif d’iOS lorsque les diverses actions sont effectuées dans le mode ingénieur de piste. Par exemple, l’ingénieur de piste doit entrer en mode édition pour supprimer ou déplacer une alarme ou un capteur.");
    contDisplay.addPerspective("ENF01",
        "Utilisation du visuel de façon intuitive\nL’interface doit respecter le fonctionnement natif d’iOS lorsque les diverses actions sont effectuées dans le mode ingénieur de piste. Par exemple, l’ingénieur de piste doit entrer en mode édition pour supprimer ou déplacer une alarme ou un capteur.");
    /**
     * ENF02
     */
    sysVehicule.addPerspective("ENF02",
        "Utilisation du mode pilote doit être très simple\nLes différentes interfaces du mode pilote doivent être simples, claires et précises. Lorsque la voiture est en piste, le pilote ne doit pas avoir à réfléchir pour comprendre et utiliser l’application. Les quatre interfaces disponibles doivent être toutes visibles en trois clics du bouton situé sur le volant puis continuer de cette façon en boucle.");
    contDisplay.addPerspective("ENF02",
        "Utilisation du mode pilote doit être très simple\nLes différentes interfaces du mode pilote doivent être simples, claires et précises. Lorsque la voiture est en piste, le pilote ne doit pas avoir à réfléchir pour comprendre et utiliser l’application. Les quatre interfaces disponibles doivent être toutes visibles en trois clics du bouton situé sur le volant puis continuer de cette façon en boucle.");
    /**
     * ENF03
     */
    sysVehicule.addPerspective("ENF03",
        "Démarrage simple et rapide dans le mode configuré\nLorsque le Dash Display démarre, il ne doit pas y avoir d’attente ou de commande à effectuer pour que l’application puisse être utilisée. Cette exigence est surtout importante pour le mode pilote qui ne peut pas utiliser l’écran tactile. De plus, le système doit utiliser le mode choisi dans les configurations de l’application directement dans iOS.");
    contDisplay.addPerspective("ENF03",
        "Démarrage simple et rapide dans le mode configuré\nLorsque le Dash Display démarre, il ne doit pas y avoir d’attente ou de commande à effectuer pour que l’application puisse être utilisée. Cette exigence est surtout importante pour le mode pilote qui ne peut pas utiliser l’écran tactile. De plus, le système doit utiliser le mode choisi dans les configurations de l’application directement dans iOS.");
    compDisplayCtrl.addPerspective("ENF03",
        "Démarrage simple et rapide dans le mode configuré\nLorsque le Dash Display démarre, il ne doit pas y avoir d’attente ou de commande à effectuer pour que l’application puisse être utilisée. Cette exigence est surtout importante pour le mode pilote qui ne peut pas utiliser l’écran tactile. De plus, le système doit utiliser le mode choisi dans les configurations de l’application directement dans iOS.");
    /**
     * ENF04
     */
    sysVehicule.addPerspective("ENF04",
        "Haut contraste dans les couleurs de l’interface\nLes couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.");
    contDisplay.addPerspective("ENF04",
        "Haut contraste dans les couleurs de l’interface\nLes couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.");
    compDisplay1.addPerspective("ENF04",
        "Haut contraste dans les couleurs de l’interface\nLes couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.");
    compDisplay2.addPerspective("ENF04",
        "Haut contraste dans les couleurs de l’interface\nLes couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.");
    compDisplay3.addPerspective("ENF04",
        "Haut contraste dans les couleurs de l’interface\nLes couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.");
    compDisplay4.addPerspective("ENF04",
        "Haut contraste dans les couleurs de l’interface\nLes couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.");
    /**
     * ENF05
     */
    sysVehicule.addPerspective("ENF05",
        "Rafraîchissement de l’écran à une cadence de 10 Hz\nLa fréquence de rafraîchissement des interfaces est de 10 Hz afin que les données affichées soient toujours à jour en temps réel. Avec cette cadence, le véhicule a le temps de transmettre les nouvelles données par Wi-Fi. Cette mesure signifie 10 fois par seconde donc le rafraîchissement est d’une fois à chaque 100 ms.");
    contDisplay.addPerspective("ENF05",
        "Rafraîchissement de l’écran à une cadence de 10 Hz\nLa fréquence de rafraîchissement des interfaces est de 10 Hz afin que les données affichées soient toujours à jour en temps réel. Avec cette cadence, le véhicule a le temps de transmettre les nouvelles données par Wi-Fi. Cette mesure signifie 10 fois par seconde donc le rafraîchissement est d’une fois à chaque 100 ms.");
    compDisplayCtrl.addPerspective("ENF05",
        "Rafraîchissement de l’écran à une cadence de 10 Hz\nLa fréquence de rafraîchissement des interfaces est de 10 Hz afin que les données affichées soient toujours à jour en temps réel. Avec cette cadence, le véhicule a le temps de transmettre les nouvelles données par Wi-Fi. Cette mesure signifie 10 fois par seconde donc le rafraîchissement est d’une fois à chaque 100 ms.");
    /**
     * ENF06
     */
    sysVehicule.addPerspective("ENF06",
        "Période d’utilisation d’au maximum 25 minutes\nLes courses ont une durée d’au maximum 25 minutes donc l’application doit être optimisée pour une utilisation sans problème pour ce délai de temps.");
    contDisplay.addPerspective("ENF06",
        "Période d’utilisation d’au maximum 25 minutes\nLes courses ont une durée d’au maximum 25 minutes donc l’application doit être optimisée pour une utilisation sans problème pour ce délai de temps.");
    /**
     * EFN07
     */
    compCtrlEngineer.addPerspective("ENF07",
        "Modification rapide des alarmes et des capteurs\nLa liste des alarmes et des capteurs peut facilement être modifiée en 5 minutes et moins. Cette modification est effectuée sur le fichier de configuration XML en fournissant le « id » et l’« offset » définis dans la table CAN. Cette exigence n’était pas précisée de la part du client, mais une précision concernant la table CAN qui peut être modifiée a été faite. Il est donc important que tout le logiciel soit facilement maintenable.");
    /**
     * EFN08
     */
    sysVehicule.addPerspective("ENF08",
        "Aucun redémarrage de l’application en cas d’erreur\nLorsqu’une erreur survient, l’application ne doit pas redémarrer seule. Il faut la redémarrer manuellement à chaque fois.");
    contDisplay.addPerspective("ENF08",
        "Aucun redémarrage de l’application en cas d’erreur\nLorsqu’une erreur survient, l’application ne doit pas redémarrer seule. Il faut la redémarrer manuellement à chaque fois.");

    /**
     * define sys views
     */

    final SystemLandscapeView landscapeSystemView = views.createSystemLandscapeView("landscapeSystemView",
        "Diagramme d'integration de tout les systèmes pour le projet FormuleETS DashView");
    landscapeSystemView.setPaperSize(PaperSize.A4_Landscape);
    landscapeSystemView.addAllSoftwareSystems();
    landscapeSystemView.addAllPeople();
    // landscatemView.enableAutomaticLayout();
    landscapeSystemView.setEnterpriseBoundaryVisible(true);

    /**
     * cont views
     */

    final ContainerView vehiculeContainersView = views.createContainerView(sysVehicule, "vehiculeContainersView",
        "Diagramme de décomposition du véhicule");
    vehiculeContainersView.setPaperSize(PaperSize.A4_Landscape);
    // vehiculeContainersView.enableAutomaticLayout();
    Iterator<Container> iteratorContainer = sysVehicule.getContainers().iterator();
    while (iteratorContainer.hasNext())
      vehiculeContainersView.addNearestNeighbours(iteratorContainer.next());
    // vehiculeContainersView.remove(engineerUsesRacingSystem);

    final ContainerView sysRacingContainerView = views.createContainerView(sysRacing, "sysRacingContainerView",
        "Diagramme de décompositino du système Racing");
    sysRacingContainerView.setPaperSize(PaperSize.A4_Landscape);
    iteratorContainer = sysRacing.getContainers().iterator();
    while (iteratorContainer.hasNext())
      sysRacingContainerView.addNearestNeighbours(iteratorContainer.next());
    // sysRacingContainerView.remove(engineerUsesRacingSystem);

    /**
     * Components views
     */

    final ComponentView displayComponentsView = views.createComponentView(contDisplay, "displayComponentsView",
        "Composants de l'application mobile du pilote");
    displayComponentsView.setPaperSize(PaperSize.A4_Landscape);
    displayComponentsView.enableAutomaticLayout();
    Iterator<Component> iterator = contDisplay.getComponents().iterator();
    while (iterator.hasNext())
      displayComponentsView.addNearestNeighbours(iterator.next());

    final ComponentView cancanEthernerComponentsView = views.createComponentView(contCancanEthernet,
        "cancanEthernerComponentsView", "Component of the cancan Eternet cont");
    cancanEthernerComponentsView.setPaperSize(PaperSize.A4_Landscape);
    cancanEthernerComponentsView.enableAutomaticLayout();
    iterator = contCancanEthernet.getComponents().iterator();
    while (iterator.hasNext())
      cancanEthernerComponentsView.addNearestNeighbours(iterator.next());

    final ComponentView racingServerComponentView = views.createComponentView(contRacingServer,
        "racingServerComponentView", "Vue de décomposition du cont RacingServer");
    racingServerComponentView.setPaperSize(PaperSize.A4_Landscape);
    iterator = contRacingServer.getComponents().iterator();
    while (iterator.hasNext())
      racingServerComponentView.addNearestNeighbours(iterator.next());

    /**
     * Dynamic views
     */

    final DynamicView dynamicView1 = views.createDynamicView(contCancanEthernet, "dynamicView1",
        "Diagramme pour démontrer comment l'appliation du pilot récupérer les données");
    dynamicView1.setPaperSize(PaperSize.A4_Landscape);
    dynamicView1.add(pilot, "Appuyuer sur le bouton interface 1", contDisplay);
    dynamicView1.add(contDisplay, "operation1", compCancanEthernet);
    dynamicView1.add(compCancanEthernet, "getDataInterace(1)", compCtrlPilot);
    // dynamicView1.enableAutomaticLayout();

    /**
     * record conception decisions In short, that Y-statement is as follows: In the
     * context of <use case/user story u>, facing <concern c> we decided for <option
     * o> to achieve <quality q>, accepting <downside d>.
     */

    // doc.addDecision(null, "1", new
    // SimpleDateFormat("yyyy-MM-dd").parse("2020-05-01"),"Outils de conception
    // d'architecture", DecisionStatus.Accepted, Format.Markdown,"Dans le contexte
    // du cours de LOG430, j'ai décidé d'utiliser l'outils Structurizr pour
    // documenter l'architecture du système DashView FormuleETS dans le cadre d'un
    // processus d'apprentissage à la conception d'architecture logiciels.");
    // doc.addDecision(sysVehicule, "2", new
    // SimpleDateFormat("yyyy-MM-dd").parse("2020-05-31"),"utiliser une architecture
    // mvc", DecisionStatus.Proposed, Format.Markdown,"Dans le contexte de @EF01,
    // nous avons décidé d'utiliser une architecture MVC pour ["+
    // contDisplay.getCanonicalName() + "] ce qui permet de supporter la
    // modifiabilité");

    /**
     * define documentation
     */

    try {
      /**
       * System of syss documentation
       */
      template.addContextSection(null, new File(documentationRoot, "context.md"));
      template.addSection("Table des éléments systèmes", Format.Markdown,
          toMarkdown(landscapeSystemView, Requirement.Type.FUNCTIONAL));
      template.addSection("Table de priorité des exigences", Format.Markdown, Requirements.priorityTable());

      /**
       * Racing sys documentation
       */
      template.addContextSection(sysRacing, new File(documentationRoot, "racingSystem-context.md"));
      template.addSection(sysRacing, "Diagramme de conteneurs du sys Racing", Format.Markdown,
          " ![](embed:sysRacingContainerView)\n ### Table des éléments du conteneur véhicule\n"
              + toMarkdown(sysRacingContainerView, Requirement.Type.FUNCTIONAL));

      template.addSection(sysRacing, "Diagramme de composant du serveur de course", Format.Markdown,
          " ![](embed:racingServerComponentView)\n ### Table des éléments du conteneur racingServer\n"
              + toMarkdown(racingServerComponentView, Requirement.Type.FUNCTIONAL));

      /** vehicule sys documentation */
      template.addContextSection(sysVehicule, new File(documentationRoot, "vehicule-context.md"));

      template.addSection(sysVehicule, "Diagramme de conteneurs", Format.Markdown,
          " ![](embed:vehiculeContainersView)\n ### Table des éléments du conteneur véhicule\n"
              + toMarkdown(vehiculeContainersView, Requirement.Type.FUNCTIONAL));

      template.addSection(sysVehicule, "Diagramme de composant du conteneur d'affichage", Format.Markdown,
          " ![](embed:displayComponentsView)\n" + toMarkdown(displayComponentsView, Requirement.Type.FUNCTIONAL));

      template.addSection(sysVehicule, "Diagramme de composant du serveur cancanEthernet", Format.Markdown,
          " ![](embed:cancanEthernerComponentsView)\n"
              + toMarkdown(cancanEthernerComponentsView, Requirement.Type.FUNCTIONAL));

      template.addSection(sysVehicule, "Diagramme dynamique 1", Format.Markdown,
          " ![](embed:dynamicView1)" + toMarkdown(dynamicView1, Requirement.Type.FUNCTIONAL));

      final JavadocToMarkdown javadocToMarkdown = new JavadocToMarkdown();
      String interfaces = "";
      interfaces += javadocToMarkdown
          .fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/ICancanRouter.java"), 3);
      interfaces += javadocToMarkdown
          .fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/ICtrlEngineer.java"), 3);
      interfaces += javadocToMarkdown
          .fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/ICtrlPilot.java"), 3);
      interfaces += javadocToMarkdown.fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/IExample.java"), 3);
      interfaces += javadocToMarkdown.fromJavadoc(Utils.readFile("src/main/java/dashview/Interfaces/IExampleV2.java"),
          3);
      template.addSection(sysVehicule, "Documentation des interfaces", Format.Markdown, interfaces);

      template.addSection(sysVehicule, "User interfaces", Format.Markdown, "### Login Page"
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

    // workspace.getModel().getElementWithCanonicalName(contDisplay.getCanonicalName())
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
    if (relationRequirement == null)
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