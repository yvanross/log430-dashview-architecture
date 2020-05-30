package dashview.Interfaces;

/** 
 * this is a test for the cancanRouter 
 * @author Yvan Ross
 * */
public interface ICancanRouter {
  /** 
   * Temperature d'opération
   *  @param maximumOperationTemperature parametre
   * */
  void setMaximumOperationTemperature(double maximumOperationTemperature);
  /** 
   * Nombre de tours minues
   * @param maxRPM en tours par minutes
   * */
  void setMaximumRPM(double maxRPM);
  /**
   * temperature des pneu
   * @param maxTireTemperature parametre
   */
  void setMaximumTireTemperature(double maxTireTemperature);

/**
 * Temperature moteur
 * @param maxTemp Temperature maximum du moteur en degré celcius
 */
  void setMaximumMotorTemperature(double maxTemp);

}