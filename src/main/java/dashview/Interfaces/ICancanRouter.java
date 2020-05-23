package dashview.Interfaces;

/** 
 * this is a test for the cancanRouter 
 * @author Yvan Ross
 * */
public interface ICancanRouter {
  /** 
   * operation 
   * 1 */
  void setMaximumOperationTemperature(double maximumOperationTemperature);
  /** 
   * operation 2 
   * */
  void setMaximumRPM(double maxRPM);
  /**
   * operation 2
   * @param maxTireTemperature parametre
   */
  void setMaximumTireTemperature(double maxTireTemperature);

  
}