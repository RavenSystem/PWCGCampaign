package pwcg.product.fc.plane.payload;

import java.util.Date;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;

public class BristolF2B3Payload extends PlanePayload implements IPlanePayload
{
    public BristolF2B3Payload(PlaneType planeType, Date date)
    {
        super(planeType, date);
        setNoOrdnancePayloadId(0);
    }

    protected void initialize()
    {
        setAvailablePayload(-5, "100000", PayloadElement.COCKPIT_LIGHT);
        setAvailablePayload(-4, "10000", PayloadElement.FUEL_GUAGE);
        
        setAvailablePayload(-3, "1000", PayloadElement.ALDIS_SIGHT);
        setAvailablePayload(-2, "100", PayloadElement.TWIN_GUN_TURRET);
        setAvailablePayload(-1, "10", PayloadElement.LEWIS_TOP);
        
        setAvailablePayload(0, "1", PayloadElement.STANDARD);
        setAvailablePayload(2, "1000001", PayloadElement.BOMBS);
        setAvailablePayload(1, "10000001", PayloadElement.CAMERA);
        setAvailablePayload(3, "100000001", PayloadElement.RADIO);
    }

    @Override
    public IPlanePayload copy()
    {
        BristolF2B2Payload clone = new BristolF2B2Payload(getPlaneType(), getDate());
        return super.copy(clone);
    }

    @Override
    protected int createWeaponsPayloadForPlane(IFlight flight)
    {
        int selectedPayloadId = 0;
        if (FlightTypes.isBombingFlight(flight.getFlightType()))
        {
            selectedPayloadId = selectPayload(flight);
        }
        else if (flight.getFlightType() == FlightTypes.RECON)
        {
            selectedPayloadId = selectReconPayload(flight);
        }
        else if (flight.getFlightType() == FlightTypes.ARTILLERY_SPOT)
        {
            selectedPayloadId = selectArtillerySpotPayload(flight);
        }
        return selectedPayloadId;
    }

    protected int selectPayload(IFlight flight)
    {
        return 2;
    }

    protected int selectReconPayload(IFlight flight)
    {
        return 1;
    }

    protected int selectArtillerySpotPayload(IFlight flight)
    {
        return 3;
    }

    @Override
    public boolean isOrdnance()
    {
        int selectedPayloadId = this.getSelectedPayload();
        if (selectedPayloadId == 2)
        {
            return true;
        }

        return false;
    }
}
