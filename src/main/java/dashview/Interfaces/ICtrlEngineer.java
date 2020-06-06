package dashview.Interfaces;

/**
 * control engineer functions
 * 
 * 
 * */
public interface ICtrlEngineer {
    /**
     * maximum operation temperature of the vehicule
     * @param maximumOperationTemperature vehicule temp
     */
    void setMaximumOperationTemperature(double maximumOperationTemperature);
    
    /**
     * maximum rpm of the vehicule
     * @param maxRPM max rpm in km/h
     */
    void setMaximumRPM(double maxRPM);
    /**
     * max tire temperature
     * @param maxTireTemperature in degre celcius
     */
    void setMaximumTireTemperature(double maxTireTemperature);
    /**
     * maximum battery power
     * @param maxBatteryPower in watts
     */
    void setMaximumBatteryPower(double maxBatteryPower);
    /**
     * maximum motor temperature
     * @param maxMotorTemperature in celcius
     */
    void setMaximumMotorTemperature(double maxMotorTemperature);
    
}