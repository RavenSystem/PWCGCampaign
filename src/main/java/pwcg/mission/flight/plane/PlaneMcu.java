package pwcg.mission.flight.plane;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import pwcg.campaign.Campaign;
import pwcg.campaign.api.ICountry;
import pwcg.campaign.api.IProductSpecificConfiguration;
import pwcg.campaign.context.PWCGContext;
import pwcg.campaign.factory.CountryFactory;
import pwcg.campaign.factory.ProductSpecificConfigurationFactory;
import pwcg.campaign.plane.EquippedPlane;
import pwcg.campaign.plane.PlaneType;
import pwcg.campaign.plane.PlaneTypeFactory;
import pwcg.campaign.plane.payload.IPayloadFactory;
import pwcg.campaign.plane.payload.IPlanePayload;
import pwcg.campaign.skin.Skin;
import pwcg.campaign.skin.TacticalCode;
import pwcg.campaign.skin.TacticalCodeBuilder;
import pwcg.campaign.squadmember.SquadronMember;
import pwcg.campaign.squadmember.SquadronMembers;
import pwcg.campaign.squadron.Squadron;
import pwcg.campaign.utils.IndexGenerator;
import pwcg.core.config.ConfigItemKeys;
import pwcg.core.config.ConfigManagerGlobal;
import pwcg.core.constants.AiSkillLevel;
import pwcg.core.constants.Callsign;
import pwcg.core.exception.PWCGException;
import pwcg.core.location.Coordinate;
import pwcg.core.location.Orientation;
import pwcg.core.utils.DateUtils;
import pwcg.core.utils.PWCGLogger;
import pwcg.core.utils.PWCGLogger.LogCategory;
import pwcg.core.utils.PWCGLogger.LogLevel;
import pwcg.mission.flight.FlightStartPosition;
import pwcg.mission.flight.IFlight;
import pwcg.mission.mcu.McuEvent;
import pwcg.mission.mcu.McuTREntity;
import pwcg.mission.mcu.group.FighterAttackCommand;
import pwcg.mission.mcu.group.IPlaneRemover;
import pwcg.mission.mcu.group.WingmanMcuGroup;

/**
 * Plane is an instance of a plane. It derives from plane type and adds a crew,
 * payload, fuel state, and other elements specific to an instance of a plane
 * 
 * @author Patrick Wilson
 *
 */
public class PlaneMcu extends EquippedPlane implements Cloneable
{
    private String name = "";
    private int index;
    private int linkTrId;
    private Coordinate position;
    private Orientation orientation;
    private Skin skin = null;
    private AiSkillLevel aiSkillLevel = AiSkillLevel.NOVICE;
    private int coopStart = 0;
    private int numberInFormation = 1;
    private int vulnerable = 1;
    private int engageable = 1;
    private int limitAmmo = 1;
    private Callsign callsign = Callsign.NONE;
    private int callnum = 0;
    private int startInAir;
    private int time = 60;
    private double fuel = .6;
    private int damageReport = 50;
    private ICountry country = CountryFactory.makeNeutralCountry();
    private int damageThreshold = 1;
    private int aiRTBDecision = 1;
    private int deleteAfterDeath = 1;
    private WingmanMcuGroup wingmanCommands;
    private FighterAttackCommand fighterAttackCommand;

    private IPlanePayload payload = null;

    private McuTREntity entity;

    private Campaign campaign;
    private SquadronMember pilot;

    public PlaneMcu()
    {
    }
    
    public PlaneMcu(Campaign campaign)
    {
        super();
        this.campaign = campaign;
        this.index = IndexGenerator.getInstance().getNextIndex();
        this.entity = new McuTREntity(index);
        this.linkTrId = entity.getIndex();
        fighterAttackCommand = new FighterAttackCommand(this.linkTrId);
    }

    public PlaneMcu(Campaign campaign, ICountry country, EquippedPlane equippedPlane, SquadronMember pilot) throws PWCGException
    {
        super();

        equippedPlane.copyTemplate(this);
        setDataForInGameType(equippedPlane);

        this.campaign = campaign;
        this.pilot = pilot;
        this.name = pilot.getNameAndRank();
        this.setCountry(country);
        this.startInAir = FlightStartPosition.START_IN_AIR;

        this.index = IndexGenerator.getInstance().getNextIndex();
        this.entity = new McuTREntity(index);
        this.linkTrId = entity.getIndex();
        fighterAttackCommand = new FighterAttackCommand(this.linkTrId);
        
        setDeleteAfterDeath();
    }

    private void setDataForInGameType(EquippedPlane equippedPlane) throws PWCGException
    {
        PlaneType actualPlaneType = null;
        if (equippedPlane.getSubstituteType().isEmpty())
        {
            actualPlaneType = equippedPlane;
        }
        else
        {
            PlaneTypeFactory planeTypeFactory = PWCGContext.getInstance().getPlaneTypeFactory();
            actualPlaneType = planeTypeFactory.createPlaneTypeByType(equippedPlane.getSubstituteType());        
        }
        
        this.type = actualPlaneType.getType();
        this.desc = actualPlaneType.getDesc();
        this.model = actualPlaneType.getModel();
        this.script = actualPlaneType.getScript();
    }

    private void setDeleteAfterDeath() throws PWCGException
    {
        int isDeleteAfterDeath = ConfigManagerGlobal.getInstance().getIntConfigParam(ConfigItemKeys.DeleteAfterDeathKey);
        if (isDeleteAfterDeath == 0) 
        {
            deleteAfterDeath = 0;
        }
    }

    public PlaneMcu copy()
    {
        PlaneMcu plane = new PlaneMcu();
        copyTemplate(plane);
        return plane;
    }

    private void copyTemplate(PlaneMcu plane)
    {
        super.copyTemplate(plane);
        
        plane.name = this.name;
        plane.position = this.position;
        plane.orientation = this.orientation;
        plane.skin = this.skin;
        plane.aiSkillLevel = this.aiSkillLevel;
        plane.coopStart = this.coopStart;
        plane.numberInFormation = this.numberInFormation;
        plane.vulnerable = this.vulnerable;
        plane.engageable = this.engageable;
        plane.limitAmmo = this.limitAmmo;
        plane.callsign = this.callsign;
        plane.callnum = this.callnum;
        plane.startInAir = this.startInAir;
        plane.time = this.time;
        plane.fuel = this.fuel;
        plane.damageReport = this.damageReport;
        plane.country = CountryFactory.makeCountryByCountry(this.country.getCountry());
        plane.damageThreshold = this.damageThreshold;
        plane.aiRTBDecision = this.aiRTBDecision;
        plane.deleteAfterDeath = this.deleteAfterDeath;
        plane.wingmanCommands = this.wingmanCommands;
        if (payload != null)
        {
            plane.payload = this.payload.copy();
        }
        else
        {
            plane.payload = null;
        }

        plane.index = IndexGenerator.getInstance().getNextIndex();
        plane.entity = this.entity.copy(plane.index);
        plane.linkTrId = plane.entity.getIndex();
        
        plane.fighterAttackCommand = new FighterAttackCommand(plane.linkTrId);
        
        plane.campaign = this.campaign;
        plane.pilot = this.pilot;
    }

    public void populateEntity(IFlight flight, PlaneMcu flightLeader)
    {
        if (flightLeader.getIndex() != index)
        {
            entity.setEntityTarget(flightLeader.getLinkTrId());
        }
    }

    public boolean isActivePlayerPlane() throws PWCGException
    {
        boolean isPlayerPlane = false;
        if (getAiLevel() == AiSkillLevel.PLAYER)
        {
            isPlayerPlane = true;
        }

        SquadronMembers squadronMembers = campaign.getPersonnelManager().getAllActivePlayers();
        if (squadronMembers.isSquadronMember(serialNumber))
        {
            isPlayerPlane = true;
        }

        return isPlayerPlane;
    }

    public IPlanePayload buildPlanePayload(IFlight flight, Date date) throws PWCGException
    {
        IPayloadFactory payloadFactory = PWCGContext.getInstance().getPayloadFactory();
        payload = payloadFactory.createPlanePayload(type, date);
        payload.createWeaponsPayload(flight);
        return payload.copy();
    }

    public IPlanePayload buildStandardPlanePayload(Date date) throws PWCGException
    {
        IPayloadFactory payloadFactory = PWCGContext.getInstance().getPayloadFactory();
        payload = payloadFactory.createPlanePayload(type, date);
        payload.createStandardWeaponsPayload();
        return payload.copy();
    }

    public void setPlanePayload(IPlanePayload payload) throws PWCGException
    {
        if (payload != null)
        {
            this.payload = payload.copy();
        }
    }

    public IPlanePayload getPlanePayload() throws PWCGException
    {
        if (payload != null)
        {
            return this.payload.copy();
        }

        return null;
    }

    public void noOrdnance()
    {
        if (payload != null)
        {
            this.payload.selectNoOrdnancePayload();
        }
    }

    public void setPlaneSkinWithCheck(Skin newSkin, Date campaignDate)
    {
        if (!(campaignDate.before(newSkin.getStartDate())))
        {
            if (!(campaignDate.after(newSkin.getEndDate())))
            {
                if (newSkin.skinExists(Skin.PRODUCT_SKIN_DIR) || newSkin.isDefinedInGame())
                {
                    this.skin = newSkin;
                }
                else
                {
                    PWCGLogger.logByCategory(LogCategory.SKIN, "setPlaneSkin: skin rejected by skin exists");
                }
            }
            else
            {
                PWCGLogger.logByCategory(LogCategory.SKIN,
                        "setPlaneSkin: skin rejected by end date: " + DateUtils.getDateStringDashDelimitedYYYYMMDD(newSkin.getEndDate()));
            }
        }
        else
        {
            PWCGLogger.logByCategory(LogCategory.SKIN,
                    "setPlaneSkin: skin rejected by start date: " + DateUtils.getDateStringDashDelimitedYYYYMMDD(newSkin.getStartDate()));
        }
    }

    private void validateFuel()
    {
        if (fuel < .3)
        {
            fuel = .3;
        }

        if (fuel > 1.0)
        {
            fuel = 1.0;
        }
    }

    public void write(BufferedWriter writer) throws PWCGException
    {
        try
        {
            writer.write("Plane");
            writer.newLine();
            writer.write("{");
            writer.newLine();

            writer.write("  Script = \"" + script + "\";");
            writer.newLine();
            writer.write("  Model = \"" + model + "\";");
            writer.newLine();
            writer.write("  Desc = \"" + desc + "\";");
            writer.newLine();

            writer.write("  Name = \"\u0001" + name + "\";");
            writer.newLine();
            writer.write("  Index = " + index + ";");
            writer.newLine();
            writer.write("  LinkTrId = " + linkTrId + ";");
            writer.newLine();

            position.write(writer);
            orientation.write(writer);

            country.writeAdjusted(writer);

            String skinName = "";
            if (skin != null)
            {
                skinName = type + "\\" + skin.getSkinName();
                skinName = skinName.toLowerCase();
                if (!skinName.contains(".dds"))
                {
                    skinName += ".dds";
                }
            }

            writer.write("  Skin = \"" + skinName + "\";");
            writer.newLine();
            writer.write("  AILevel = " + aiSkillLevel.getAiSkillLevel() + ";");
            writer.newLine();
            writer.write("  CoopStart = " + coopStart + ";");
            writer.newLine();
            writer.write("  NumberInFormation = " + numberInFormation + ";");
            writer.newLine();
            writer.write("  Vulnerable = " + vulnerable + ";");
            writer.newLine();
            writer.write("  Engageable = " + engageable + ";");
            writer.newLine();
            writer.write("  LimitAmmo = " + limitAmmo + ";");
            writer.newLine();
            writer.write("  StartInAir = " + startInAir + ";");
            writer.newLine();
            writer.write("  Time = " + time + ";");
            writer.newLine();
            writer.write("  DamageReport = " + damageReport + ";");
            writer.newLine();
            writer.write("  DamageThreshold = " + damageThreshold + ";");
            writer.newLine();
            writer.write("  PayloadId = " + payload.getSelectedPayloadId() + ";");
            writer.newLine();
            writer.write("  AiRTBDecision = " + aiRTBDecision + ";");
            writer.newLine();
            writer.write("  DeleteAfterDeath = " + deleteAfterDeath + ";");
            writer.newLine();
            writer.write("  Fuel = " + fuel + ";");
            writer.newLine();

            // BoS specific parameters
            IProductSpecificConfiguration productSpecificConfiguration = ProductSpecificConfigurationFactory.createProductSpecificConfiguration();
            if (productSpecificConfiguration.useCallSign())
            {
                writer.write("  Callsign = " + callsign.getNum(country.getCountry()) + ";");
                writer.newLine();
                writer.write("  Callnum = " + callnum + ";");
                writer.newLine();
            }

            writer.write("  WMMask = " + payload.generateFullModificationMask() + ";");
            writer.newLine();

            writeTacticalCode(writer);

            writer.write("}");
            writer.newLine();
            writer.newLine();
            writer.newLine();

            entity.write(writer);
            
            fighterAttackCommand.write(writer);
            
            if (wingmanCommands != null)
            {
                wingmanCommands.write(writer);
            }
        }
        catch (IOException e)
        {
            PWCGLogger.logException(e);
            throw new PWCGException(e.getMessage());
        }
    }

    private void writeTacticalCode(BufferedWriter writer) throws PWCGException, IOException
    {
        if (skin == null)
        {
            PWCGLogger.log(LogLevel.INFO, "No skin designasated for plane " + type);
            return;
        }
        
        Squadron squadron = PWCGContext.getInstance().getSquadronManager().getSquadron(squadronId);
        if (skin.isUseTacticalCodes())
        {
            TacticalCode tacticalCode = TacticalCodeBuilder.buildTacticalCode(campaign, squadron, this);
            if (tacticalCode != null)
            {
                writer.write("  TCode = \"" + tacticalCode.formCodeString() + "\";");
                writer.newLine();
                writer.write("  TCodeColor = \"" + tacticalCode.formCodeColorString() + "\";");
                writer.newLine();
            }
        }
    }

    public boolean getStartInAir()
    {
        int startInAirVal = FlightStartPosition.START_IN_AIR;
        if (startInAir == startInAirVal)
        {
            return true;
        }

        return false;
    }

    public void setWingman(WingmanMcuGroup wingmanCommands)
    {
        this.wingmanCommands = wingmanCommands;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getIndex()
    {
        return index;
    }

    public int getLinkTrId()
    {
        return linkTrId;
    }

    public Coordinate getPosition()
    {
        return position.copy();
    }

    public void setPosition(Coordinate position)
    {
        this.position = position;

        if (entity != null)
        {
            entity.setPosition(position.copy());
        }

        if (fighterAttackCommand != null)
        {
            fighterAttackCommand.setPosition(position.copy());
        }
    }

    public Orientation getOrientation()
    {
        return orientation;
    }

    public void setOrientation(Orientation orientation)
    {
        this.orientation = orientation;

        if (entity != null)
        {
            entity.setOrientation(orientation.copy());
        }
        

        if (fighterAttackCommand != null)
        {
            fighterAttackCommand.setOrientation(orientation.copy());
        }
    }

    public Skin getPlaneSkin()
    {
        return skin;
    }

    public void setPlaneSkin(Skin newSkin)
    {
        if (newSkin != null)
        {
            this.skin = newSkin;
        }
        else
        {
            System.out.print("Null skin for aircraft");
        }
    }

    public AiSkillLevel getAiLevel()
    {
        return aiSkillLevel;
    }

    public void setAiLevel(AiSkillLevel aiLevel)
    {
        this.aiSkillLevel = aiLevel;
    }

    public int getCoopStart()
    {
        return coopStart;
    }

    public void setCoopStart(int coopStart)
    {
        this.coopStart = coopStart;
    }

    public int getNumberInFormation()
    {
        return numberInFormation;
    }

    public void setNumberInFormation(int numberInFormation)
    {
        this.numberInFormation = numberInFormation;
    }

    public int getVulnerable()
    {
        return vulnerable;
    }

    public void setVulnerable(int vulnerable)
    {
        this.vulnerable = vulnerable;
    }

    public int getEngageable()
    {
        return engageable;
    }

    public void setEngageable(int engageable)
    {
        this.engageable = engageable;
    }

    public int getLimitAmmo()
    {
        return limitAmmo;
    }

    public void setLimitAmmo(int limitAmmo)
    {
        this.limitAmmo = limitAmmo;
    }

    public void setStartInAir(int startInAir)
    {
        this.startInAir = startInAir;
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
    {
        this.time = time;
    }

    public int getDamageReport()
    {
        return damageReport;
    }

    public void setDamageReport(int damageReport)
    {
        this.damageReport = damageReport;
    }

    public int getDamageThreshold()
    {
        return damageThreshold;
    }

    public void setDamageThreshold(int damageThreshold)
    {
        this.damageThreshold = damageThreshold;
    }

    public ICountry getCountry()
    {
        return country;
    }

    public void setCountry(ICountry country)
    {
        this.country = country;
        this.side = country.getSide();
    }

    public double getFuel()
    {
        return fuel;
    }

    public void setFuel(double fuel)
    {
        this.fuel = fuel;

        validateFuel();
    }

    public SquadronMember getPilot()
    {
        return pilot;
    }

    public void replacePilot(SquadronMember newPilot)
    {
        this.pilot = newPilot;
    }

    public Callsign getCallsign()
    {
        return callsign;
    }

    public void setCallsign(Callsign callsign)
    {
        this.callsign = callsign;
    }

    public int getCallnum()
    {
        return callnum;
    }

    public void setCallnum(int callnum)
    {
        this.callnum = callnum;
    }

    public void setLinkTrId(int linkTrId)
    {
        this.linkTrId = linkTrId;
    }

    public IPlaneRemover getPlaneRemover()
    {
        // Removed because it deletes planes accidentally
        return null;
    }

    public int getAiRTBDecision()
    {
        return aiRTBDecision;
    }

    public void setAiRTBDecision(int aiRTBDecision)
    {
        this.aiRTBDecision = aiRTBDecision;
    }

    public Skin getSkin()
    {
        return skin;
    }

    public void setPlaneEntityTarget(int target)
    {
        entity.setEntityTarget(target);
    }

    public void copyEntityIndexFromPlane(PlaneMcu flightLeaderPlaneMcu)
    {
        entity.setIndex(flightLeaderPlaneMcu.entity.getIndex());
        linkTrId = entity.getIndex();
    }

    public void enable(boolean enable)
    {
        if (enable)
        {
            entity.enableEntity();
        }
        else
        {
            entity.disableEntity();
        }
    }

    public void setOnMessages(int message, int takeoffIndex, int waypointIndex)
    {
        entity.setOnMessages(
                message,
                takeoffIndex,
                waypointIndex);
    }

    public void addEvent(McuEvent event)
    {
        entity.addEvent(event);
    }

    public void resetTarget(int flightLeaderLinkTrId)
    {
        entity.clearTargets();
        entity.setEntityTarget(flightLeaderLinkTrId);
    }

    public McuTREntity getEntity()
    {
        return entity;
    }
    
    public void addTargets(List<Integer> targets)
    {
        fighterAttackCommand.addTargets(targets);
    }
    
    public int getFighterAttackTarget()
    {
        return fighterAttackCommand.getFighterAttackTarget();
    }    
}
