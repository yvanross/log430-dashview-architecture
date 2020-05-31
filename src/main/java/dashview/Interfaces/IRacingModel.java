package dashview.Interfaces;

import java.util.ArrayList;

/**
 * interface de manipulation des données des véhicules
 */
public interface IRacingModel {

  /**
   * get cancan table
   * @param vehiculeId identification du véhicule
   * @return   ArrayList<ISensorValue> , IsensorValue -> Timestamp,SensorId, valeur
   */
  public ArrayList<ISensorValue> getCanCanTable(Long vehiculeId);

  /**
   * add cancanData
   */

  


}
