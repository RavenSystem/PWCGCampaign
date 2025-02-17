package pwcg.product.fc.plane.payload;

import java.util.Date;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.mission.flight.IFlight;

public class Nieuport11Payload extends PlanePayload implements IPlanePayload
{
    public Nieuport11Payload(PlaneType planeType, Date date)
    {
        super(planeType, date);
        setNoOrdnancePayloadId(0);
    }

    protected void initialize()
    {
        setAvailablePayload(-7, "100000000", PayloadElement.COCKPIT_LIGHT);
        setAvailablePayload(-6, "10000000", PayloadElement.CLOCK_GUAGE);
        setAvailablePayload(-5, "1000000", PayloadElement.COMPASS_GUAGE);
        setAvailablePayload(-4, "1000000", PayloadElement.ALTITUDE_GUAGE);
        setAvailablePayload(-3, "100000", PayloadElement.SPEED_GUAGE);
        setAvailablePayload(-2, "10000", PayloadElement.LE_CHRETIAN_SIGHT);
        setAvailablePayload(-1, "1000", PayloadElement.ALDIS_SIGHT);
        
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
        setAvailablePayload(1, "11", PayloadElement.TWIN_LEWIS);
        setAvailablePayload(2, "101", PayloadElement.LE_PRIEUR_ROCKETS);
    }

    @Override
    public IPlanePayload copy()
    {
        Nieuport11Payload clone = new Nieuport11Payload(getPlaneType(), getDate());
        return super.copy(clone);
    }

    protected int createWeaponsPayloadForPlane(IFlight flight)
    {
        int selectedPayloadId = 0;
        return selectedPayloadId;
    }

    @Override
    public boolean isOrdnance()
    {
        return false;
    }
}
