package pwcg.mission.flight.artySpot;

import java.util.ArrayList;
import java.util.List;

import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.mission.flight.FlightBuildInformation;
import pwcg.mission.flight.FlightInformation;
import pwcg.mission.flight.FlightInformationFactory;
import pwcg.mission.flight.FlightTypes;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.IFlightPackage;
import pwcg.mission.target.GroundTargetDefinitionFactory;
import pwcg.mission.target.TargetDefinition;

public class ArtillerySpotPackage implements IFlightPackage
{
    private FlightInformation flightInformation;
    private TargetDefinition targetDefinition;
    private List<IFlight> packageFlights = new ArrayList<>();

    public ArtillerySpotPackage()
    {
    }

    @Override
    public List<IFlight> createPackage (FlightBuildInformation flightBuildInformation) throws PWCGException 
    {
        this.flightInformation = FlightInformationFactory.buildFlightInformation(flightBuildInformation, FlightTypes.ARTILLERY_SPOT);
        this.targetDefinition = buildTargetDefintion();
        
        IFlight artySpot = createFlight();
        packageFlights.add(artySpot);
		return packageFlights;
	}

    private IFlight createFlight() throws PWCGException
    {        
        TargetDefinition selectedTarget = createGroundUnitsForFlight();
		IFlight artySpot = null;
		if (flightInformation.isPlayerFlight())
		{
		    throw new PWCGException("Player artillery spot not supported");
		}
		else
		{
            artySpot = createAiFlight(selectedTarget.getPosition());
		}

        return artySpot;
    }

    private TargetDefinition createGroundUnitsForFlight() throws PWCGException
    {
        TargetDefinition selectedTarget  = GroundTargetDefinitionFactory.buildTargetDefinition(flightInformation);
        return selectedTarget;
    }

    private IFlight createAiFlight(Coordinate targetCoordinates) throws PWCGException
    {
        IFlight artySpot;        
        ArtillerySpotFlight artySpotAI = new ArtillerySpotFlight (flightInformation, targetDefinition);
        artySpotAI.createFlight();
        artySpot = artySpotAI;
        return artySpot;
    }

    private TargetDefinition buildTargetDefintion() throws PWCGException
    {
        return GroundTargetDefinitionFactory.buildTargetDefinition(flightInformation);
    }
}
