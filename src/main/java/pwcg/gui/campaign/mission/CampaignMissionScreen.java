package pwcg.gui.campaign.mission;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import pwcg.aar.AARCoordinator;
import pwcg.campaign.Campaign;
import pwcg.campaign.plane.PwcgRole;
import pwcg.campaign.squadmember.SquadronMemberStatus;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.CampaignGuiContextManager;
import pwcg.gui.ScreenIdentifier;
import pwcg.gui.UiImageResolver;
import pwcg.gui.campaign.activity.CampaignLeaveScreen;
import pwcg.gui.campaign.home.CampaignHomeContext;
import pwcg.gui.campaign.home.CampaignHomeScreen;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.dialogs.HelpDialog;
import pwcg.gui.rofmap.brief.BriefingCoopPersonaChooser;
import pwcg.gui.rofmap.brief.BriefingRoleChooser;
import pwcg.gui.rofmap.brief.CampaignHomeGuiBriefingWrapper;
import pwcg.gui.rofmap.debrief.DebriefMissionDescriptionScreen;
import pwcg.gui.sound.ProceedWithMission;
import pwcg.gui.sound.SoundManager;
import pwcg.gui.utils.CommonUIActions;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.PWCGButtonFactory;
import pwcg.gui.utils.PWCGLabelFactory;
import pwcg.gui.utils.SpacerPanelFactory;
import pwcg.mission.MissionHumanParticipants;

public class CampaignMissionScreen extends ImageResizingPanel implements ActionListener
{
    public static final String CAMP_MISSION_ROLE = "CampMissionRole";
    public static final String CAMP_MISSION = "CampMission";
    
    private static final long serialVersionUID = 1L;
    private CampaignHomeScreen campaignHome = null;
    private CampaignHomeGuiBriefingWrapper campaignHomeGuiBriefingWrapper;

    public CampaignMissionScreen(CampaignHomeScreen campaignHome)
    {
        super();
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        this.campaignHome = campaignHome;
        campaignHomeGuiBriefingWrapper = new CampaignHomeGuiBriefingWrapper(campaignHome);
    }

	public void makePanels() throws PWCGException 
	{
        String imagePath = UiImageResolver.getImage(ScreenIdentifier.CampaignSimpleConfigurationScreen);
        this.setThemedImageFromName(CampaignHomeContext.getCampaign().getReferenceService(), imagePath);

        this.add(BorderLayout.WEST, makeNavigatePanel());
        this.add(BorderLayout.EAST, SpacerPanelFactory.makeDocumentSpacerPanel(1400));
	}

	private JPanel makeNavigatePanel() throws PWCGException
	{
        JPanel leftSidePanel = new JPanel(new BorderLayout());
        leftSidePanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        buttonPanel.setOpaque(false);

        buttonPanel.add(PWCGLabelFactory.makeDummyLabel());

        JButton finishedButton = PWCGButtonFactory.makeTranslucentMenuButton("Finished", CommonUIActions.FINISHED, "Finished with configuration changes", this);
        buttonPanel.add(finishedButton);

        buttonPanel.add(PWCGLabelFactory.makeDummyLabel());

        if (isDisplayMissionButton())
        {
            JButton missionButton = PWCGButtonFactory.makeTranslucentMenuButton("Mission", CAMP_MISSION, "Generate a mission", this);
            buttonPanel.add(missionButton);

            JButton missionButtonWithRoleSelect = PWCGButtonFactory.makeTranslucentMenuButton("Mission With Role", CAMP_MISSION_ROLE, "Generate a mission where you specify the role", this);
            buttonPanel.add(missionButtonWithRoleSelect);
        }
        else if (isDisplayLeaveButton())
        {
            JButton missionButton = PWCGButtonFactory.makeTranslucentMenuButton("Heal Wounds", "CampLeave", "Take leave to heal wounds", this);
            buttonPanel.add(missionButton);
        }
        
        if (isDisplayAARButton())
        {
            JButton aarButton = PWCGButtonFactory.makeTranslucentMenuButton("Combat Report", "CampFlowCombatReport", "File an After Action Report (AAR) for a mission", this);
            buttonPanel.add(aarButton);
        }


		leftSidePanel.add(buttonPanel, BorderLayout.NORTH);

		return leftSidePanel;
	}

    public void actionPerformed(ActionEvent ae)
	{
		try
		{
            String action = ae.getActionCommand();
            if (action.equalsIgnoreCase(CAMP_MISSION) || action.equalsIgnoreCase(CAMP_MISSION_ROLE))
            {
                boolean shouldProceed = ProceedWithMission.shouldProceedWithMission(CampaignHomeContext.getCampaign(), "Existing mission results detected.  Creating a mission will prevent an AAR.  Proceed?");
                if (!shouldProceed)
                {
                    return;
                }
                
                if (CampaignHomeContext.getCampaign().isCoop() && CampaignHomeContext.getCampaign().getCurrentMission() == null)
                {
                    createCoopMission(action);
                }
                else
                {
                    createSinglePlayerMission(action);
                }
            }
            else if (action.equalsIgnoreCase("CampFlowCombatReport"))
            {
                showAAR();
            }
            else if (action.equalsIgnoreCase("CampLeave"))
            {
                showLeavePage();
            }
            else if (action.equals(CommonUIActions.FINISHED))
            {
                CampaignGuiContextManager.getInstance().popFromContextStack();
            }
		}
		catch (Exception e)
		{
			PWCGLogger.logException(e);
			ErrorDialog.internalError(e.getMessage());
		}
	}

    private void createSinglePlayerMission(String action) throws PWCGException
    {
        if (action.contentEquals(CAMP_MISSION))
        {
            boolean isLoneWolf = false;
            generateMission(isLoneWolf);
        }
        else
        {
            generateMissionWithRoleOverride();
        }
    }
    
    private void createCoopMission(String missionChoice) throws PWCGException 
    {
        boolean overrideRole = false;
        if (missionChoice.contentEquals(CAMP_MISSION_ROLE))
        {
            overrideRole = true;
        }

        BriefingCoopPersonaChooser coopPersonaChooser = new BriefingCoopPersonaChooser(missionChoice, campaignHomeGuiBriefingWrapper, overrideRole);
        coopPersonaChooser.makePanels();
        CampaignGuiContextManager.getInstance().pushToContextStack(coopPersonaChooser);
    }

    private void generateMission(boolean isLoneWolf) throws PWCGException
    {
        Map<Integer, PwcgRole> squadronRoleOverride = new HashMap<>();
        MissionHumanParticipants participatingPlayers = MissionGeneratorHelper.buildParticipatingPlayersSinglePlayer(CampaignHomeContext.getCampaign());
        MissionGeneratorHelper.showBriefingMap(CampaignHomeContext.getCampaign(), campaignHomeGuiBriefingWrapper, participatingPlayers, squadronRoleOverride);
    }

    private void generateMissionWithRoleOverride() throws PWCGException
    {
        SoundManager.getInstance().playSound("Typewriter.WAV");
        MissionHumanParticipants participatingPlayers = MissionGeneratorHelper.buildParticipatingPlayersSinglePlayer(CampaignHomeContext.getCampaign());
        BriefingRoleChooser briefingRoleChooser = new BriefingRoleChooser(campaignHomeGuiBriefingWrapper, participatingPlayers);
        briefingRoleChooser.makePanels();
        CampaignGuiContextManager.getInstance().pushToContextStack(briefingRoleChooser);
    }

    private void showAAR() throws PWCGException 
    {
        SoundManager.getInstance().playSound("Typewriter.WAV");
        
        try
        {
            AARCoordinator.getInstance().aarPreliminary(CampaignHomeContext.getCampaign());
            
            DebriefMissionDescriptionScreen combatReportDisplay = new DebriefMissionDescriptionScreen(CampaignHomeContext.getCampaign(), campaignHome);
            combatReportDisplay.makePanels();
            CampaignGuiContextManager.getInstance().pushToContextStack(combatReportDisplay);
        }
        catch (PWCGException e)
        {
            new  HelpDialog("PWCG Could not perform AAR.  " + e.getMessage());
        }
    }

    private void showLeavePage() throws PWCGException 
    {
        SoundManager.getInstance().playSound("Typewriter.WAV");

        CampaignLeaveScreen leaveDisplay = new CampaignLeaveScreen(campaignHome);
        leaveDisplay.makePanels();
        
        CampaignGuiContextManager.getInstance().pushToContextStack(leaveDisplay);
    }

    private boolean isDisplayMissionButton() throws PWCGException
    {
        Campaign campaign = CampaignHomeContext.getCampaign();
        if (!campaign.isCampaignActive())
        {
            return false;
        }

        if (!campaign.isCampaignCanFly())
        {
            return false;
        }

        if (campaign.findReferencePlayer() == null)
        {
            return false;
        }

        if (campaign.findReferencePlayer().getPilotActiveStatus() != SquadronMemberStatus.STATUS_ACTIVE)
        {
            return false;
        }

        return true;
    }

    private boolean isDisplayLeaveButton() throws PWCGException
    {
        if (CampaignHomeContext.getCampaign().findReferencePlayer().getPilotActiveStatus() == SquadronMemberStatus.STATUS_WOUNDED ||
            CampaignHomeContext.getCampaign().findReferencePlayer().getPilotActiveStatus() == SquadronMemberStatus.STATUS_SERIOUSLY_WOUNDED)
        {
            return true;
        }
        
        return false;
    }

    private boolean isDisplayAARButton() throws PWCGException
    {
        if (!CampaignHomeContext.getCampaign().isCampaignActive())
        {
            return false;
        }

        return true;
    }
}

