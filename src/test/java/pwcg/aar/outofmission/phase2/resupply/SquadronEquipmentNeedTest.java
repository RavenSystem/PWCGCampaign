package pwcg.aar.outofmission.phase2.resupply;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import pwcg.aar.data.AARPersonnelLosses;
import pwcg.campaign.Campaign;
import pwcg.campaign.CampaignEquipmentManager;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.context.PWCGProduct;
import pwcg.campaign.personnel.SquadronPersonnel;
import pwcg.campaign.plane.Equipment;
import pwcg.campaign.plane.EquippedPlane;
import pwcg.campaign.resupply.equipment.SquadronEquipmentNeed;
import pwcg.campaign.squadmember.SerialNumber;
import pwcg.campaign.squadron.Squadron;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
public class SquadronEquipmentNeedTest
{
    @Mock private Campaign campaign;
    @Mock private Squadron squadron;
    @Mock private CampaignEquipmentManager campaignEquipmentManager;
    @Mock private Equipment equipment;
    @Mock private AARPersonnelLosses lossesInMissionData;
    @Mock private SquadronPersonnel squadronPersonnel;
    @Mock private EquippedPlane equippedPlane;

    private Map<Integer, EquippedPlane> activeEquippedPlaneCollection = new HashMap<>();
    private Map<Integer, EquippedPlane> inactiveEquippedPlaneCollection = new HashMap<>();
    
    SerialNumber serialNumber = new SerialNumber();
    
    @BeforeEach
    public void setupTest() throws PWCGException
    {
        activeEquippedPlaneCollection.clear();
        inactiveEquippedPlaneCollection.clear();
        
        PWCGContext.setProduct(PWCGProduct.BOS);
        Mockito.when(campaign.getDate()).thenReturn(DateUtils.getDateYYYYMMDD("19420430"));
        Mockito.when(campaign.getEquipmentManager()).thenReturn(campaignEquipmentManager);
        Mockito.when(campaignEquipmentManager.getEquipmentForSquadron(ArgumentMatchers.any())).thenReturn(equipment);

        Mockito.when(equipment.getActiveEquippedPlanes()).thenReturn(activeEquippedPlaneCollection);
        Mockito.when(equipment.getRecentlyInactiveEquippedPlanes(ArgumentMatchers.any())).thenReturn(inactiveEquippedPlaneCollection);
     }

    @Test
    public void testResupplyWithNoEquipment() throws PWCGException
    {
        SquadronEquipmentNeed squadronTransferNeed = new SquadronEquipmentNeed(campaign, squadron);
        squadronTransferNeed.determineResupplyNeeded();
        Assertions.assertTrue (squadronTransferNeed.needsResupply() == true);
        
        for (int i = 0; i < Squadron.SQUADRON_EQUIPMENT_SIZE - 1; ++i)
        {
            squadronTransferNeed.noteResupply();
            Assertions.assertTrue (squadronTransferNeed.needsResupply() == true);
        }

        squadronTransferNeed.noteResupply();
        Assertions.assertTrue (squadronTransferNeed.needsResupply() == false);
    }
    

    @Test
    public void testResupplyWithActiveEquipment() throws PWCGException
    {
        for (int i = 0; i < 11; ++i)
        {
            activeEquippedPlaneCollection.put(serialNumber.getNextPlaneSerialNumber(), equippedPlane);
        }
        
        SquadronEquipmentNeed squadronResupplyNeed = new SquadronEquipmentNeed(campaign, squadron);
        squadronResupplyNeed.determineResupplyNeeded();
        Assertions.assertTrue (squadronResupplyNeed.needsResupply() == true);
        
        for (int i = 0; i < 2; ++i)
        {
            squadronResupplyNeed.noteResupply();
            Assertions.assertTrue (squadronResupplyNeed.needsResupply() == true);
        }

        squadronResupplyNeed.noteResupply();
        Assertions.assertTrue (squadronResupplyNeed.needsResupply() == false);
    }
    

    @Test
    public void testResupplyWithActiveAndInactiveEquipment() throws PWCGException
    {
        for (int i = 0; i < 9; ++i)
        {
            activeEquippedPlaneCollection.put(serialNumber.getNextPlaneSerialNumber(), equippedPlane);
        }
        
        for (int i = 0; i < 2; ++i)
        {
            inactiveEquippedPlaneCollection.put(serialNumber.getNextPlaneSerialNumber(), equippedPlane);
        }

        SquadronEquipmentNeed squadronResupplyNeed = new SquadronEquipmentNeed(campaign, squadron);
        squadronResupplyNeed.determineResupplyNeeded();
        Assertions.assertTrue (squadronResupplyNeed.needsResupply() == true);
        
        for (int i = 0; i < 2; ++i)
        {
            squadronResupplyNeed.noteResupply();
            Assertions.assertTrue (squadronResupplyNeed.needsResupply() == true);
        }

        squadronResupplyNeed.noteResupply();
        Assertions.assertTrue (squadronResupplyNeed.needsResupply() == false);
    }

    @Test
    public void testNoResupplyNeeded() throws PWCGException
    {
        for (int i = 0; i < 10; ++i)
        {
            activeEquippedPlaneCollection.put(serialNumber.getNextPlaneSerialNumber(), equippedPlane);
        }
        
        for (int i = 0; i < 4; ++i)
        {
            inactiveEquippedPlaneCollection.put(serialNumber.getNextPlaneSerialNumber(), equippedPlane);
        }

        SquadronEquipmentNeed squadronResupplyNeed = new SquadronEquipmentNeed(campaign, squadron);
        squadronResupplyNeed.determineResupplyNeeded();
        Assertions.assertTrue (squadronResupplyNeed.needsResupply() == false);
    }

}
