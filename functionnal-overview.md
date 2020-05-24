
### Pilot
Le pilote contrôle le véhicule lors des essais sur piste et des compétitions. Il utilise l’application en mode pilote afin d’accéder aux données du véhicule ce qui permet d’avoir une meilleure compréhension des différents composants et d’améliorer sa conduite.

|Parent|Key|Category|Title|Priority|
|--|--|--|--|--|
|null|EF02|Générale|Configuration de l’application dans les paramètres d’iOS|2|
||||Le système doit permettre de changer quelques configurations directement dans les paramètres de l’application sur iOS. Les configurations doivent inclure, entre autres, le changement de mode entre pilote et ingénieur de piste ainsi que le changement des couleurs de l’interface de pâle à foncé.|
|null|EF06|Générale|Afficher des couleurs spécifiques pour la température du moteur|9|
||||Le Dash Display doit afficher la température du moteur à l’aide de quatre couleurs. Lorsque la température est de 70°C et moins, l’indicateur doit être bleu. Entre 70°C et 90°C, la couleur utilisée est le vert. Entre 90°C et 100°C, l’indicateur doit être jaune et, finalement, il doit être rouge lorsque la température dépasse le 100°C.|

### Engineer
L'ingénieur de piste gère les alarmes et capteurs du véhicule et ajuste/optimise les paramètres logiciels du véhicule

|Parent|Key|Category|Title|Priority|
|--|--|--|--|--|

### University Optimisation Engineer
Un ingénieur spécialisé en course automobile qui analyse les données accumulé pour fournir les paramètres d'optimisation au véhicule de course.

|Parent|Key|Category|Title|Priority|
|--|--|--|--|--|
|null|EF16|Mode pilote|Mettre en veille l’application après 3 secondes sans données|2|
||||doit se mettre en veille si une interruption de données survient et si elle dure plus de trois secondes.|
|null|EF11|Mode pilote|Contenu de la première interface|6|
||||L’application doit afficher, sur la première interface, les capteurs suivants : la température du moteur, le voltage de la batterie, l’indicateur d’utilisation et d’angle du système de réduction de traînée, les différentes alarmes, la boîte de messages, l’indicateur de vitesse et l’indicateur de révolutions par minute.|

### Optimisation Server
Système distant permettant de récupérer les données d'un circuit et de faire l'analyse de ceux-ci pour fournire les paramètres du véhicule pour optimise le rendement de celui-ci durant la course.

|Parent|Key|Category|Title|Priority|
|--|--|--|--|--|
|null|EF16|Mode pilote|Mettre en veille l’application après 3 secondes sans données|2|
||||doit se mettre en veille si une interruption de données survient et si elle dure plus de trois secondes.|

### Racing System
Système de calcul sur site permettant de récupérer les données temps réel et d'envoyer des commandes aux véhicule pour la calibration de celui-ci.

|Parent|Key|Category|Title|Priority|
|--|--|--|--|--|

### Vehicule System
Système déployé dans les véhicules FormuleETS pour permettre la communication avec le Racing Server et le pilote.

|Parent|Key|Category|Title|Priority|
|--|--|--|--|--|
