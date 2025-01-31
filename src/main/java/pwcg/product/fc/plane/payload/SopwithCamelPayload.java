package pwcg.product.fc.plane.payload;

import java.util.Date;

import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.plane.payload.PayloadElement;
import pwcg.campaign.plane.payload.PlanePayload;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;

public class SopwithCamelPayload extends PlanePayload implements IPlanePayload
{
    public SopwithCamelPayload(PlaneType planeType, Date date)
    {
        super(planeType, date);
        setNoOrdnancePayloadId(0);
    }

    protected void initialize()
    {
        setAvailablePayload(-2, "100", PayloadElement.WING_CUTOUT);
        setAvailablePayload(-1, "10", PayloadElement.ALDIS_SIGHT);

        setAvailablePayload(0, "1", PayloadElement.STANDARD);
        setAvailablePayload(2, "10001", PayloadElement.BOMBS);
    }

    @Override
    public IPlanePayload copy()
    {
        SopwithCamelPayload clone = new SopwithCamelPayload(getPlaneType(), getDate());
        return super.copy(clone);
    }

    protected int createWeaponsPayloadForPlane(IFlight flight)
    {
        int selectedPayloadId = 0;
        if (FlightTypes.isGroundAttackFlight(flight.getFlightType()))
        {
            selectedPayloadId = selectGroundAttackPayload(flight);
        }
        return selectedPayloadId;
    }

    protected int selectGroundAttackPayload(IFlight flight)
    {
        return 2;
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
