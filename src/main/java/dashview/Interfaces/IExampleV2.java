package dashview.Interfaces;

import dashview.Requirements.Requirement;

/**
 * Iexample pour la documentation détaillé d'une interface que je veux lier à un
 * lien dans Structurizr
 * 
 */
public interface IExampleV2  {
    /** definition d'une constante */
    public static final double CONSTANT_PI2 = 6.28;

    /** getPilotName
     * @return Get the name of the pilot */
    public String getPilotFirstName();

    /** getPilotWeight
     * @return weight of the pilot in kilogram
     */
    public String getPilotWeightV2();

    /** setPilotWeight
     * @param weightInLbs of the pilot
     * IExampleV2 extand an expection is generated if pilot weight is over 200 lb
     * @throws dashview.Interfaces.ExceptionPilotOverWeight pilote dépasse le poids reglemenntaire
     */
    public void setPilotWeightV2(float weightInLbs) throws ExceptionPilotOverWeight;

    /** set related exigenceexigence
     * 
     * @param requirement is an instance of dashview.Requirement 
     */
    public void setExigenceV2(Requirement requirement);
    
}

