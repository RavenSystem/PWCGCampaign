package pwcg.mission;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pwcg.campaign.Campaign;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.skirmish.Skirmish;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.CoordinateBox;
import pwcg.core.utils.DateUtils;
import pwcg.mission.flight.FlightTypes;
import pwcg.testutils.SquadronTestProfile;
import pwcg.testutils.TestCampaignFactoryBuilder;
import pwcg.testutils.TestMissionBuilderUtility;

public class MissionCenterBuilderSkirmishTest
{    
    public MissionCenterBuilderSkirmishTest() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
    }
    
    @Test
    public void testNoSkirmish() throws PWCGException
    {
        Campaign campaign = TestCampaignFactoryBuilder.makeCampaign(this.getClass().getCanonicalName(), SquadronTestProfile.JG_51_PROFILE_MOSCOW);

        List<Skirmish> skirmishes = PWCGContext.getInstance().getMap(campaign.getCampaignMap()).getSkirmishManager().getSkirmishesForDate(campaign, TestMissionBuilderUtility.buildTestParticipatingHumans(campaign));
        Assertions.assertTrue (skirmishes.size() == 0);
    }

    @Test
    public void singlePlayerMissionBoxArnhemEarlyTest() throws PWCGException
    {
        Campaign campaign = TestCampaignFactoryBuilder.makeCampaign(this.getClass().getCanonicalName(), SquadronTestProfile.FG_362_PROFILE);
        campaign.setDate(DateUtils.getDateYYYYMMDD("19440917"));

        createMissionAtSkirmish(campaign);
    }

    @Test
    public void singlePlayerMissionBoxArnhemLateTest() throws PWCGException
    {
        Campaign campaign = TestCampaignFactoryBuilder.makeCampaign(this.getClass().getCanonicalName(), SquadronTestProfile.FG_362_PROFILE);
        campaign.setDate(DateUtils.getDateYYYYMMDD("19440928"));

        createMissionAtSkirmish(campaign);
    }

    private void createMissionAtSkirmish(Campaign campaign) throws PWCGException
    {
        MissionHumanParticipants participatingPlayers = makeParticipatingPlayers(campaign);
                
        List<Skirmish> skirmishes = PWCGContext.getInstance().getMap(campaign.getCampaignMap()).getSkirmishManager().getSkirmishesForDate(campaign, TestMissionBuilderUtility.buildTestParticipatingHumans(campaign));
        Assertions.assertTrue (skirmishes.size() > 0);
        
        Squadron playerSquadron = participatingPlayers.getAllParticipatingPlayers().get(0).determineSquadron();
        MissionSquadronFlightTypes playerFlightTypes = MissionSquadronFlightTypes.buildPlayerFlightType(FlightTypes.PATROL, playerSquadron);

        MissionBorderBuilder missionBorderBuilder = new MissionBorderBuilder(campaign, participatingPlayers, skirmishes.get(0), playerFlightTypes);

        CoordinateBox missionBorders = missionBorderBuilder.buildCoordinateBox();
        Coordinate missionBoxCenter = missionBorders.getCenter();
                
        assert(skirmishes.get(0).getCoordinateBox().isInBox(missionBoxCenter));
    }

    private MissionHumanParticipants makeParticipatingPlayers(Campaign campaign) throws PWCGException
    {
        MissionHumanParticipants participatingPlayers = new MissionHumanParticipants();
        SquadronMember player = campaign.findReferencePlayer();
        participatingPlayers.addSquadronMember(player);
        return participatingPlayers;
    }

}
