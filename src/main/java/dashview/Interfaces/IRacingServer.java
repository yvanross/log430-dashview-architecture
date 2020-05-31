package dashview.Interfaces;

/**
 * interface du server d'acquisition de données provenant du véhicule
 */
public interface IRacingServer {

  /**
   * initialise le système en mode de course
   */
  public void startRacing();

  /**
   * Initialise le système en mode d'analyse de performance pour la préparation à la course.
   */
  public void startTraining();

  /**
   * record cancan data from vehicule
   * @param cancanData json format
   * TODO: add json example
   */
  public void recordCanCanData(String cancanDataInJson);

  /**
   * save vehicule configuration
   */
  public void saveVehiculeConfiguration(String configurationInJson);
}
