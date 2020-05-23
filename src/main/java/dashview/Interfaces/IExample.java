package dashview.Interfaces;

import dashview.Requirements.Requirement;

/**
 * Iexample pour la documentation détaillé d'une interface que je veux lier à un
 * lien dans Structurizr
 * 
 */
public interface IExample {
    /** 
     * definition d'une constante 
     * */
    public static final double CONSTANT_PI = 3.1416;

    /** 
     * getPilotName
     * @return Get the name of the pilot 
     * */
    public String getPilotName();

    /** 
     * getPilotWeight
     * @return weight of the pilot
     */
    public String getPilotWeight();

    /** 
     * setPilotWeightLB in livre
     * @param weightInLbs of the pilot [EF01] @EF01 related to requirement
     * @throws ExceptionPilotOverWeight pilot in excess of weight
     */
    public void setPilotWeightLB(float weightInLbs) throws ExceptionPilotOverWeight;

    /** 
     * set related exigenceexigence
     * 
     * @param requirement is an instance of dashview.Requirement 
     */
    public void setExigence(Requirement requirement);
    
}

