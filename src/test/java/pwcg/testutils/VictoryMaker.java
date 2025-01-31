package pwcg.testutils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pwcg.campaign.squadmember.SquadronMemberStatus;
import pwcg.campaign.squadmember.Victory;
import pwcg.campaign.squadmember.VictoryEntity;
import pwcg.product.fc.plane.FCPlaneAttributeMapping;

public class VictoryMaker
{
    public static List<Victory> makeMultipleAlliedVictories(int numVictories, Date date)
    {
        List<Victory> victories = new ArrayList<>();
        for (int i = 0; i < numVictories; ++i)
        {
            Victory victory  = frenchVictorGermanVictim(date);
            victories.add(victory);
        }
        
        return victories;
    }
    
    public static List<Victory> makeMultipleCentralVictories(int numVictories, Date date)
    {
        List<Victory> victories = new ArrayList<>();
        for (int i = 0; i < numVictories; ++i)
        {
            Victory victory  = germanVictorFrenchVictim(date);
            victories.add(victory);
        }
        
        return victories;
    }
    
    public static List<Victory> makeMultipleAirGroundVictories(int numVictories, Date date)
    {
        List<Victory> victories = new ArrayList<>();
        for (int i = 0; i < numVictories; ++i)
        {
            Victory victory  = airGroundVictory(date);
            victories.add(victory);
        }
        
        return victories;
    }
    
    public static Victory frenchVictorGermanVictim(Date date)
    {
        Victory victory = new Victory();
        victory.setCrashedInSight(true);
        victory.setDate(date);
        victory.setLocation("Near something");
        
        VictoryEntity victor = new VictoryEntity();
        victor.setAirOrGround(Victory.AIRCRAFT);
        victor.setPilotStatus(SquadronMemberStatus.STATUS_ACTIVE);
        victor.setSquadronName("Esc 3");
        victor.setType(FCPlaneAttributeMapping.SPAD13.getPlaneType());
        victory.setVictor(victor);

        VictoryEntity victim = new VictoryEntity();
        victim.setAirOrGround(Victory.AIRCRAFT);
        victim.setPilotStatus(SquadronMemberStatus.STATUS_KIA);
        victim.setSquadronName("Jasta 2");
        victim.setType(FCPlaneAttributeMapping.ALBATROSD5.getPlaneType());
        victory.setVictim(victim);
        
        return victory;
    }
    
    
    public static Victory germanVictorFrenchVictim(Date date)
    {
        Victory victory = new Victory();
        victory.setCrashedInSight(true);
        victory.setDate(date);
        victory.setLocation("Near something");
        
        VictoryEntity victor = new VictoryEntity();
        victor.setAirOrGround(Victory.AIRCRAFT);
        victor.setPilotStatus(SquadronMemberStatus.STATUS_ACTIVE);
        victor.setSquadronName("Jasta 2");
        victor.setType(FCPlaneAttributeMapping.ALBATROSD5.getPlaneType());
        victory.setVictor(victor);
        
        VictoryEntity victim = new VictoryEntity();
        victim.setAirOrGround(Victory.AIRCRAFT);
        victim.setPilotStatus(SquadronMemberStatus.STATUS_KIA);
        victim.setSquadronName("Esc 3");
        victim.setType(FCPlaneAttributeMapping.SPAD13.getPlaneType());
        victory.setVictim(victim);
        
        return victory;
    }
    
    public static Victory airGroundVictory(Date date)
    {
        Victory victory = new Victory();
        victory.setCrashedInSight(true);
        victory.setDate(date);
        victory.setLocation("Near something");
        
        VictoryEntity victor = new VictoryEntity();
        victor.setAirOrGround(Victory.AIRCRAFT);
        victor.setPilotStatus(SquadronMemberStatus.STATUS_ACTIVE);
        victor.setSquadronName("Esc 3");
        victor.setType(FCPlaneAttributeMapping.SPAD13.getPlaneType());
        victory.setVictor(victor);

        VictoryEntity victim = new VictoryEntity();
        victim.setAirOrGround(Victory.VEHICLE);
        victim.setPilotStatus(SquadronMemberStatus.STATUS_KIA);
        victim.setSquadronName("Jasta 2");
        victim.setType("truck");
        victory.setVictim(victim);
        
        return victory;
    }
}
