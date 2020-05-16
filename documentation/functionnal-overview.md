
### Pilot
Le pilote contrôle le véhicule lors des essais sur piste et des compétitions. Il utilise l’application en mode pilote afin d’accéder aux données du véhicule ce qui permet d’avoir une meilleure compréhension des différents composants et d’améliorer sa conduite.

|Parent|Key|Category|Title|
|--|--|--|--|
|null|EF01|Général|Configuration de l’application avec un fichier XML|
||||L’application doit utiliser un fichier de configuration, sous le format XML, pour déterminer les alarmes et capteurs disponibles. La liste des alarmes et des capteurs sont définis selon la table CAN fournie par la Formule ÉTS.|
|null|EF02|Générale|Configuration de l’application dans les paramètres d’iOS|
||||Le système doit permettre de changer quelques configurations directement dans les paramètres de l’application sur iOS. Les configurations doivent inclure, entre autres, le changement de mode entre pilote et ingénieur de piste ainsi que le changement des couleurs de l’interface de pâle à foncé.|
|null|EF06|Générale|Afficher des couleurs spécifiques pour la température du moteur|
||||Le Dash Display doit afficher la température du moteur à l’aide de quatre couleurs. Lorsque la température est de 70°C et moins, l’indicateur doit être bleu. Entre 70°C et 90°C, la couleur utilisée est le vert. Entre 90°C et 100°C, l’indicateur doit être jaune et, finalement, il doit être rouge lorsque la température dépasse le 100°C.|
|null|EF03|Générale|Gérer les données reçues en temps réel|
||||L’application doit constamment recevoir des données du bus CAN via Wi-Fi à partir du module Can2Ethernet et, en comparant avec la table des messages CAN, associer ces données aux capteurs et alarmes pour les afficher.|
|null|EF05|Générale|Afficher des couleurs spécifiques pour la température des pneus|
||||L’application doit afficher la température des trois capteurs de chaque pneu selon des couleurs spécifiques et avec des transitions fluides. Les capteurs sont situés à l’extérieur, au milieu et à l’intérieur de chacun des pneus. Lorsque la température est de 25°C et moins, la couleur est bleue. Entre 25°C et 65°C, la couleur passe de bleue à jaune. Entre 65°C et 95°C, l’indicateur passe de jaune à rouge. Finalement, en haut de 95°C, la couleur est rouge.|

### Engineer
L'ingénieur de piste gère les alarmes et capteurs du véhicule et ajuste/optimise les paramètres logiciels du véhicule

|Parent|Key|Category|Title|
|--|--|--|--|

### University Optimisation Engineer
Un ingénieur spécialisé en course automobile qui analyse les données accumulé pour fournir les paramètres d'optimisation au véhicule de course.

|Parent|Key|Category|Title|
|--|--|--|--|
|null|EF16|Mode pilote|Mettre en veille l’application après 3 secondes sans données|
||||doit se mettre en veille si une interruption de données survient et si elle dure plus de trois secondes.|

### Vehicule System
Système déployé dans les véhicules FormuleETS pour permettre la communication avec le Racing Server et le pilote.

|Parent|Key|Category|Title|
|--|--|--|--|
|null|EF15|Mode pilote|Affichage en mode paysage pour le mode pilote|
||||doit afficher les interfaces en mode pilote sous le format paysage.|

### Racing System
Système de calcul sur site permettant de récupérer les données temps réel et d'envoyer des commandes aux véhicule pour la calibration de celui-ci.

|Parent|Key|Category|Title|
|--|--|--|--|
|null|EF15|Mode pilote|Affichage en mode paysage pour le mode pilote|
||||doit afficher les interfaces en mode pilote sous le format paysage.|

### Optimisation Server
Système distant permettant de récupérer les données d'un circuit et de faire l'analyse de ceux-ci pour fournire les paramètres du véhicule pour optimise le rendement de celui-ci durant la course.

|Parent|Key|Category|Title|
|--|--|--|--|
|null|EF16|Mode pilote|Mettre en veille l’application après 3 secondes sans données|
||||doit se mettre en veille si une interruption de données survient et si elle dure plus de trois secondes.|
