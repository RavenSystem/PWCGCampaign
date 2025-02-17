package pwcg.aar.outofmission.phase1.elapsedtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pwcg.aar.inmission.phase2.logeval.missionresultentity.LogPlane;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.Victory;

public class OutOfMissionVictoryData
{
    private Map<Integer, List<Victory>> victoryAwardsBySquadronMember = new HashMap<>();
    private Map<Integer, SquadronMember> shotDownPilots = new HashMap<>();
    private Map<Integer, LogPlane> shotDownPlanes = new HashMap<>();
    
    public void merge(OutOfMissionVictoryData victoryData)
    {
        for (Integer pilotSerialNumber : victoryData.getVictoryAwardsBySquadronMember().keySet())
        {
            List<Victory> victories = victoryData.getVictoryAwardsBySquadronMember().get(pilotSerialNumber);
            for (Victory victory : victories)
            {
                addVictoryAwards(pilotSerialNumber, victory);
            }
        }
        
        for (SquadronMember shotDownPilot : victoryData.getShotDownPilots().values())
        {
            addShotDownPilot(shotDownPilot);
        }
        
        for (LogPlane shotDownPlane : victoryData.getShotDownPlanes().values())
        {
            addShotDownPlane(shotDownPlane);
        }
    }
    
    public void addVictoryAwards(Integer pilotSerialNumber, Victory victory)
    {
        if (!victoryAwardsBySquadronMember.containsKey(pilotSerialNumber))
        {
            List<Victory> victoriesForSquadronMember = new ArrayList<>();
            victoryAwardsBySquadronMember.put(pilotSerialNumber, victoriesForSquadronMember);
        }
        
        List<Victory> victoriesForSquadronMember = victoryAwardsBySquadronMember.get(pilotSerialNumber);
        victoriesForSquadronMember.add(victory);
    }

    public void addVictoryEvents(OutOfMissionVictoryData victoryEvents)
    {
        victoryAwardsBySquadronMember.putAll(victoryEvents.getVictoryAwardsBySquadronMember());
    }

    public void addShotDownPilot(SquadronMember shotDownPilot)
    {
        shotDownPilots.put(shotDownPilot.getSerialNumber(), shotDownPilot);
    }

    public void addShotDownPlane(LogPlane shotDownPlane)
    {
        shotDownPlanes.put(shotDownPlane.getPlaneSerialNumber(), shotDownPlane);
    }
    
    public Map<Integer, SquadronMember> getShotDownPilots()
    {
        return shotDownPilots;
    }

    public Map<Integer, List<Victory>> getVictoryAwardsBySquadronMember()
    {
        return victoryAwardsBySquadronMember;
    }

    public Map<Integer, LogPlane> getShotDownPlanes()
    {
        return shotDownPlanes;
    }
}
