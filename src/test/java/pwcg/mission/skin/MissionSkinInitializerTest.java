package pwcg.mission.skin;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;
import pwcg.mission.flight.FlightPlanes;
import pwcg.mission.flight.IFlight;
import pwcg.mission.flight.plane.PlaneMcu;

@ExtendWith(MockitoExtension.class)
public class MissionSkinInitializerTest
{
    @Mock
    private Campaign campaign;
    @Mock
    private Squadron squadron;
    @Mock
    private IFlight flight;
    @Mock
    private FlightPlanes flightPlanes;
    @Mock
    private ICountry country;

    private PlaneMcu plane1;
    private PlaneMcu plane2;
    private PlaneMcu plane3;

    private List<PlaneMcu> planes = new ArrayList<>();

    @BeforeEach
    public void setupTest() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        plane1 = new PlaneMcu(campaign);
        plane2 = new PlaneMcu(campaign);
        plane3 = new PlaneMcu(campaign);

        Mockito.when(flight.getCampaign()).thenReturn(campaign);

        Mockito.when(flight.getFlightPlanes()).thenReturn(flightPlanes);
        Mockito.when(flightPlanes.getPlanes()).thenReturn(planes);

        Mockito.when(flight.getSquadron()).thenReturn(squadron);
        Mockito.when(squadron.determineSquadronCountry(Mockito.any())).thenReturn(country);

        planes.clear();
        planes.add(plane1);
        planes.add(plane2);
        planes.add(plane3);
    }

    @Test
    public void buildMissionSkinSetForSummer() throws Exception
    {
        Mockito.when(squadron.getSquadronId()).thenReturn(20111003);
        Mockito.when(squadron.getCountry()).thenReturn(country);
        Mockito.when(country.getCountryName()).thenReturn("Germany");

        plane1.setType("bf109f4");
        plane2.setType("bf109f4");
        plane3.setType("bf109f2");

        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19420501"));

        MissionSkinSetBuilder skinSetBuilder = new MissionSkinSetBuilder(flight);
        MissionSkinSet missionSkinSet = skinSetBuilder.buildSummerMissionSkinSet();
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane1, DateUtils.getDateYYYYMMDD("19420501"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane2, DateUtils.getDateYYYYMMDD("19420501"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane3, DateUtils.getDateYYYYMMDD("19420501"));

        assert (plane1.getSkin().getSkinName().length() > 0);
        assert (plane2.getSkin().getSkinName().length() > 0);
        assert (plane3.getSkin().getSkinName().length() > 0);
    }

    @Test
    public void buildRussianMissionSkinSetForWinter() throws PWCGException
    {
        Mockito.when(squadron.getSquadronId()).thenReturn(10111011);
        Mockito.when(squadron.getCountry()).thenReturn(country);
        Mockito.when(country.getCountryName()).thenReturn("Russia");

        plane1.setType("lagg3s29");
        plane2.setType("lagg3s29");
        plane3.setType("lagg3s29");

        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19420101"));

        MissionSkinSetBuilder skinSetBuilder = new MissionSkinSetBuilder(flight);
        MissionSkinSet missionSkinSet = skinSetBuilder.buildSummerMissionSkinSet();
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane1, DateUtils.getDateYYYYMMDD("19420101"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane2, DateUtils.getDateYYYYMMDD("19420101"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane3, DateUtils.getDateYYYYMMDD("19420101"));

        assert (plane1.getSkin().getSkinName().length() > 0);
        assert (plane2.getSkin().getSkinName().length() > 0);
        assert (plane3.getSkin().getSkinName().length() > 0);
    }

    @Test
    public void buildGermanMissionSkinSetForWinter() throws PWCGException
    {
        Mockito.when(squadron.getSquadronId()).thenReturn(20111052);
        Mockito.when(squadron.getCountry()).thenReturn(country);
        Mockito.when(country.getCountryName()).thenReturn("Germany");

        plane1.setType("bf109f2");
        plane2.setType("bf109f2");
        plane3.setType("bf109f2");

        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19420101"));

        MissionSkinSetBuilder skinSetBuilder = new MissionSkinSetBuilder(flight);
        MissionSkinSet missionSkinSet = skinSetBuilder.buildSummerMissionSkinSet();
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane1, DateUtils.getDateYYYYMMDD("19420101"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane2, DateUtils.getDateYYYYMMDD("19420101"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane3, DateUtils.getDateYYYYMMDD("19420101"));

        assert (plane1.getSkin().getSkinName().length() > 0);
        assert (plane2.getSkin().getSkinName().length() > 0);
        assert (plane3.getSkin().getSkinName().length() > 0);
    }

    @Test
    public void buildMissionSkinSetForDiffentPlaneTypesInFlight() throws PWCGException
    {
        Mockito.when(squadron.getSquadronId()).thenReturn(20111003);
        Mockito.when(squadron.getCountry()).thenReturn(country);
        Mockito.when(country.getCountryName()).thenReturn("Germany");

        plane1.setType("bf109f4");
        plane2.setType("bf109f4");
        plane3.setType("bf109f2");

        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19420501"));

        MissionSkinSetBuilder skinSetBuilder = new MissionSkinSetBuilder(flight);
        MissionSkinSet missionSkinSet = skinSetBuilder.buildSummerMissionSkinSet();
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane1, DateUtils.getDateYYYYMMDD("19420501"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane2, DateUtils.getDateYYYYMMDD("19420501"));
        MissionSkinInitializer.intitializeSkin(missionSkinSet, squadron, plane3, DateUtils.getDateYYYYMMDD("19420501"));

        assert (plane1.getSkin().getSkinName().length() > 0);
        assert (plane2.getSkin().getSkinName().length() > 0);
        assert (plane3.getSkin().getSkinName().length() > 0);
    }
}
