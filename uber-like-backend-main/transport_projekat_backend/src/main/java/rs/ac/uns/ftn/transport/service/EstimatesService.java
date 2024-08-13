package rs.ac.uns.ftn.transport.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.Location;
import rs.ac.uns.ftn.transport.model.VehicleType;

@Service
public class EstimatesService {
    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    private final static double AVERAGE_SPEED_IN_KILOMETERS = 50;

    /**
     @return : distance in kilometers
     **/
    public double calculateDistance(Location departure, Location destination) {

        double latDistance = Math.toRadians(departure.getLatitude() - destination.getLatitude());
        double lngDistance = Math.toRadians(departure.getLongitude() - destination.getLongitude());

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(departure.getLatitude()))) *
                        (Math.cos(Math.toRadians(destination.getLatitude()))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return AVERAGE_RADIUS_OF_EARTH * c;
    }

    /**
     * @return Estimated time in minutes
     * */
    public double getEstimatedTime(double distance) {
        return distance / AVERAGE_SPEED_IN_KILOMETERS * 60;
    }

    /**
     * @return Estimated price in din
     * */
    public double getEstimatedPrice(VehicleType type, double distanceKm) {
        return type.getPricePerKm() + distanceKm * 120;
    }
}
