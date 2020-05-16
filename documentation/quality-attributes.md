
### Pilot
Le pilote contrôle le véhicule lors des essais sur piste et des compétitions. Il utilise l’application en mode pilote afin d’accéder aux données du véhicule ce qui permet d’avoir une meilleure compréhension des différents composants et d’améliorer sa conduite.

|Parent|Key|Category|Title|
|--|--|--|--|

### Engineer
L'ingénieur de piste gère les alarmes et capteurs du véhicule et ajuste/optimise les paramètres logiciels du véhicule

|Parent|Key|Category|Title|
|--|--|--|--|

### University Optimisation Engineer
Un ingénieur spécialisé en course automobile qui analyse les données accumulé pour fournir les paramètres d'optimisation au véhicule de course.

|Parent|Key|Category|Title|
|--|--|--|--|

### Optimisation Server
Système distant permettant de récupérer les données d'un circuit et de faire l'analyse de ceux-ci pour fournire les paramètres du véhicule pour optimise le rendement de celui-ci durant la course.

|Parent|Key|Category|Title|
|--|--|--|--|
|null|ENF01|Usability - Understandability|Utilisation du visuel de façon intuitive|
||||L’interface doit respecter le fonctionnement natif d’iOS lorsque les diverses actions sont effectuées dans le mode ingénieur de piste. Par exemple, l’ingénieur de piste doit entrer en mode édition pour supprimer ou déplacer une alarme ou un capteur.|
|null|ENF02|Usability - Learnability|Utilisation du mode pilote doit être très simple|
||||Les différentes interfaces du mode pilote doivent être simples, claires et précises. Lorsque la voiture est en piste, le pilote ne doit pas avoir à réfléchir pour comprendre et utiliser l’application. Les quatre interfaces disponibles doivent être toutes visibles en trois clics du bouton situé sur le volant puis continuer de cette façon en boucle.|

### Racing System
Système de calcul sur site permettant de récupérer les données temps réel et d'envoyer des commandes aux véhicule pour la calibration de celui-ci.

|Parent|Key|Category|Title|
|--|--|--|--|
|null|ENF03|Usability - Operability|Démarrage simple et rapide dans le mode configuré|
||||Lorsque le Dash Display démarre, il ne doit pas y avoir d’attente ou de commande à effectuer pour que l’application puisse être utilisée. Cette exigence est surtout importante pour le mode pilote qui ne peut pas utiliser l’écran tactile. De plus, le système doit utiliser le mode choisi dans les configurations de l’application directement dans iOS.|
|null|ENF04|Usability - User-friendliness|Haut contraste dans les couleurs de l’interface|
||||Les couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.|

### Vehicule System
Système déployé dans les véhicules FormuleETS pour permettre la communication avec le Racing Server et le pilote.

|Parent|Key|Category|Title|
|--|--|--|--|
