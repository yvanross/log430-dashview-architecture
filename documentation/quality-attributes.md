
### Usability - Understandability

#### ENF01 - Utilisation du visuel de façon intuitive
L’interface doit respecter le fonctionnement natif d’iOS lorsque les diverses actions sont effectuées dans le mode ingénieur de piste. Par exemple, l’ingénieur de piste doit entrer en mode édition pour supprimer ou déplacer une alarme ou un capteur.

### Usability - Learnability

#### ENF02 - Utilisation du mode pilote doit être très simple
Les différentes interfaces du mode pilote doivent être simples, claires et précises. Lorsque la voiture est en piste, le pilote ne doit pas avoir à réfléchir pour comprendre et utiliser l’application. Les quatre interfaces disponibles doivent être toutes visibles en trois clics du bouton situé sur le volant puis continuer de cette façon en boucle.

### Usability - Operability

#### ENF03 - Démarrage simple et rapide dans le mode configuré
Lorsque le Dash Display démarre, il ne doit pas y avoir d’attente ou de commande à effectuer pour que l’application puisse être utilisée. Cette exigence est surtout importante pour le mode pilote qui ne peut pas utiliser l’écran tactile. De plus, le système doit utiliser le mode choisi dans les configurations de l’application directement dans iOS.

### Usability - User-friendliness

#### ENF04 - Haut contraste dans les couleurs de l’interface
Les couleurs de l’interface doivent avoir de très haut contraste. De plus, deux versions de couleurs doivent être disponibles dans la configuration de l’application sur iOS : un mode foncé et un mode pâle.

### Efficiency - Time behavior

#### ENF05 - Rafraîchissement de l’écran à une cadence de 10 Hz
La fréquence de rafraîchissement des interfaces est de 10 Hz afin que les données affichées soient toujours à jour en temps réel. Avec cette cadence, le véhicule a le temps de transmettre les nouvelles données par Wi-Fi. Cette mesure signifie 10 fois par seconde donc le rafraîchissement est d’une fois à chaque 100 ms.

### Efficiency - Resource behavior

#### ENF06 - Période d’utilisation d’au maximum 25 minutes
Les courses ont une durée d’au maximum 25 minutes donc l’application doit être optimisée pour une utilisation sans problème pour ce délai de temps.

### Usability - Customizability

#### ENF07 - Modification rapide des alarmes et des capteurs
La liste des alarmes et des capteurs peut facilement être modifiée en 5 minutes et moins. Cette modification est effectuée sur le fichier de configuration XML en fournissant le « id » et l’« offset » définis dans la table CAN. Cette exigence n’était pas précisée de la part du client, mais une précision concernant la table CAN qui peut être modifiée a été faite. Il est donc important que tout le logiciel soit facilement maintenable.

### Reliability - Fault tolerance

#### ENF08 - Aucun redémarrage de l’application en cas d’erreur
Lorsqu’une erreur survient, l’application ne doit pas redémarrer seule. Il faut la redémarrer manuellement à chaque fois.
