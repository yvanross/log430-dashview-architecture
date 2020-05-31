package dashview.Interfaces;

import java.sql.Timestamp;

/**
 * valeur pour un capteur à un moment précis
 */
public interface ISensorValue {
  
  /**
   * ajouter les valeur d'un capteur
   * @param timestamp marqueur de temps correspondant à la mesure du capteur
   * @param sensorId cancanId du capteur
   * @param Value valeur numérique mesuré par le capteur
   */
  public void add( Timestamp timestamp,Long sensorId, Double Value);

}
