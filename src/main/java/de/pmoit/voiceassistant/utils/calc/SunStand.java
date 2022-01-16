package de.pmoit.voiceassistant.utils.calc;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class SunStand {
    private final double LATITUDE;
    private final double LONGITUDE;
    private final double K = calcK();
    private final Calendar GREGCAL;

    public SunStand(double latitude, double longitude) {
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.GREGCAL = new GregorianCalendar();
    }

    /**
     * Constructor for testing. Insert a custom calendar object.
     */
    public SunStand(double latitude, double longitude, Calendar cal) {
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.GREGCAL = cal;
    }

    public SunStand() {
        this.LATITUDE = 50.254703;
        this.LONGITUDE = 8.750000;
        this.GREGCAL = new GregorianCalendar();
    }

    /**
     * Calcs the hight of the sun in degree.
     */
    public double getSunHight() {
        return Math.asin(calcSunX()) / K;
    }

    /**
     * Calcs the direction of the sun in degree.
     */
    public double getSunAzimut() {
        double leftEquation = getCurrentHour() + getCurrentMinute() / 60;
        double rightEquation = ( 12 + ( 15 - LONGITUDE ) / 15 - ( calcTimeEquation() / 60 ) );
        if (leftEquation <= rightEquation) {
            return Math.acos(calcSunY()) / K;
        }
        return 360 - Math.acos(calcSunY()) / K;
    }

    private double calcSunX() {
        return Math.sin(K * LATITUDE) * Math.sin(K * calcDeclination()) + Math.cos(K * LATITUDE) * Math.cos(K
            * calcDeclination()) * Math.cos(K * calcHourAngle());
    }

    private double calcSunY() {
        return - ( Math.sin(K * LATITUDE) * calcSunX() - Math.sin(K * calcDeclination()) ) / ( Math.cos(K * LATITUDE) * Math
            .sin(Math.acos(calcSunX())) );
    }

    private double calcHourAngle() {
        return 15 * ( getCurrentHour() + getCurrentMinute() / 60 - ( 15.0 - LONGITUDE ) / 15.0 - 12 + calcTimeEquation()
            / 60 );
    }

    private double calcTimeEquation() {
        return 60 * ( - 0.171 * Math.sin(0.0337 * getCurrentDay() + 0.465) - 0.1299 * Math.sin(0.01787 * getCurrentDay()
            - 0.168) );
    }

    private double calcDeclination() {
        return - 23.45 * Math.cos(K * 360 * ( getCurrentDay() + 10 ) / 365);
    }

    private double calcK() {
        return Math.PI / 180;
    }

    private double getCurrentDay() {
        return GREGCAL.get(Calendar.DAY_OF_YEAR);
    }

    private double getCurrentHour() {
        return GREGCAL.get(Calendar.HOUR_OF_DAY);
    }

    private double getCurrentMinute() {
        return GREGCAL.get(Calendar.MINUTE);
    }
}
