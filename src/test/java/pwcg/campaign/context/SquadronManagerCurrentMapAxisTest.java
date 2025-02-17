package pwcg.campaign.context;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.Side;
import pwcg.campaign.plane.PwcgRole;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.mission.MissionSquadronRegistry;
import pwcg.mission.flight.escort.EscortSquadronSelector;
import pwcg.testutils.SquadronTestProfile;
import pwcg.testutils.TestCampaignFactoryBuilder;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SquadronManagerCurrentMapAxisTest
{
    Campaign campaign;
    
    @BeforeAll
    public void setupSuite() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        campaign = TestCampaignFactoryBuilder.makeCampaign(this.getClass().getCanonicalName(), SquadronTestProfile.JG_51_PROFILE_MOSCOW);
    }

    @Test
    public void getEscortOrEscortedSquadronAxisTest() throws PWCGException
    {
        Squadron squadron = PWCGContext.getInstance().getSquadronManager().getSquadron(SquadronTestProfile.JG_51_PROFILE_MOSCOW.getSquadronId());
                 
        Squadron nearbySquadron = EscortSquadronSelector.getEscortSquadron(campaign, squadron, squadron.determineCurrentPosition(campaign.getDate()), new MissionSquadronRegistry());
        assert(nearbySquadron != null);
        assert(nearbySquadron.determineSide() == Side.AXIS);
        assert(nearbySquadron.getSquadronRoles().isSquadronThisRole(campaign.getDate(), PwcgRole.ROLE_FIGHTER) == true);
    }
}
