package pwcg.aar.inmission.phase3.reconcile.victories.singleplayer;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pwcg.aar.ui.events.model.ClaimDeniedEvent;
import pwcg.campaign.Campaign;
import pwcg.campaign.CampaignPersonnelManager;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.PlaneTypeFactory;
import pwcg.campaign.squadmember.SerialNumber;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ClaimDenierTest
{
    @Mock private PlayerVictoryDeclaration declaration;
    @Mock private Campaign campaign;
    @Mock private CampaignPersonnelManager personnelManager;
    @Mock private SquadronMembers playerMembers;
    @Mock private SquadronMember player;
    @Mock private SquadronMember pilot;
    @Mock private Squadron squadron;
    @Mock private PlaneTypeFactory planeFactory;
    @Mock private PlaneType planeType;
   
    private List<SquadronMember> players = new ArrayList<>();

    @BeforeEach
    public void setupTest() throws PWCGException
    {
        PWCGContext.setProduct(PWCGProduct.BOS);
        
        players = new ArrayList<>();
        players.add(player);

        Mockito.when(campaign.getPersonnelManager()).thenReturn(personnelManager);
        Mockito.when(personnelManager.getAnyCampaignMember(ArgumentMatchers.<Integer>any())).thenReturn(pilot);
    }

    @Test
    public void testClamAccepted() throws PWCGException
    {
        Mockito.when(declaration.isConfirmed()).thenReturn(true);

        ClaimDenier claimDenier = new ClaimDenier(campaign, planeFactory);
        ClaimDeniedEvent claimDeniedEvent = claimDenier.determineClaimDenied(SerialNumber.PLAYER_STARTING_SERIAL_NUMBER, declaration);
        Assertions.assertTrue (claimDeniedEvent == null);
    }
    
    @Test 
    public void testClaimDeniedPlane() throws PWCGException
    {

        Mockito.when(declaration.isConfirmed()).thenReturn(false);
        Mockito.when(declaration.getAircraftType()).thenReturn("Albatros D.III");
        Mockito.when(planeFactory.createPlaneTypeByAnyName(ArgumentMatchers.<String>any())).thenReturn(planeType);
        Mockito.when(planeType.getDisplayName()).thenReturn("Albatros D.III");
        
        ClaimDenier claimDenier = new ClaimDenier(campaign, planeFactory);
        ClaimDeniedEvent claimDeniedEvent = claimDenier.determineClaimDenied(SerialNumber.PLAYER_STARTING_SERIAL_NUMBER, declaration);
        Assertions.assertTrue (claimDeniedEvent.getType().equals("Albatros D.III"));
    }
    
    @Test 
    public void testClaimDeniedBalloon() throws PWCGException
    {

        Mockito.when(declaration.isConfirmed()).thenReturn(false);
        Mockito.when(declaration.getAircraftType()).thenReturn(PlaneType.BALLOON);
        
        ClaimDenier claimDenier = new ClaimDenier(campaign, planeFactory);
        ClaimDeniedEvent claimDeniedEvent = claimDenier.determineClaimDenied(SerialNumber.PLAYER_STARTING_SERIAL_NUMBER, declaration);
        Assertions.assertTrue (claimDeniedEvent.getType().equals(PlaneType.BALLOON));
    }
}
