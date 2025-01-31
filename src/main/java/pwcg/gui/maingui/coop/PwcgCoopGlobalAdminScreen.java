package pwcg.gui.maingui.coop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import pwcg.core.config.InternationalizationManager;
import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;
import pwcg.gui.CampaignGuiContextManager;
import pwcg.gui.PwcgThreePanelUI;
import pwcg.gui.ScreenIdentifier;
import pwcg.gui.UiImageResolver;
import pwcg.gui.colors.ColorMap;
import pwcg.gui.dialogs.ErrorDialog;
import pwcg.gui.dialogs.PWCGMonitorFonts;
import pwcg.gui.utils.ImageResizingPanel;
import pwcg.gui.utils.PWCGButtonFactory;
import pwcg.gui.utils.PWCGLabelFactory;

public class PwcgCoopGlobalAdminScreen extends ImageResizingPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;

    private PwcgThreePanelUI pwcgThreePanel;
    private ButtonGroup buttonGroup = new ButtonGroup();

    public PwcgCoopGlobalAdminScreen()
    {
        super();
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        this.pwcgThreePanel = new PwcgThreePanelUI(this);
    }
    
    public void makePanels() 
    {
        try
        {        	
            String imagePath = UiImageResolver.getImage(ScreenIdentifier.PwcgCoopGlobalAdminScreen);
            this.setImageFromName(imagePath);

            pwcgThreePanel.setLeftPanel(makeNavigatePanel());
            pwcgThreePanel.setRightPanel(makeCoopAdminActionSelectPanel());
            pwcgThreePanel.setCenterPanel(makeBlankCenterPanel());
        }
        catch (Throwable e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }

	public JPanel makeBlankCenterPanel() throws PWCGException  
    {       
        String imagePath = UiImageResolver.getImage(ScreenIdentifier.Document);
        ImageResizingPanel blankPanel = new ImageResizingPanel(imagePath);
        blankPanel.setLayout(new BorderLayout());
                
        return blankPanel;
    }

    public JPanel makeNavigatePanel() throws PWCGException  
    {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        buttonPanel.setOpaque(false);

        JButton finishedButton = PWCGButtonFactory.makeTranslucentMenuButton("Finished", "Finished", "Finished with coop player administration", this);
        buttonPanel.add(finishedButton);

        navPanel.add(buttonPanel, BorderLayout.NORTH);
        
        return navPanel;
    }

    public JPanel makeCoopAdminActionSelectPanel() throws PWCGException  
    {
        JPanel configPanel = new JPanel(new BorderLayout());
        configPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridLayout(0,1));
        buttonPanel.setOpaque(false);
        

        JLabel label = PWCGLabelFactory.makeMenuLabelLarge("Select Coop Admin Action");
        buttonPanel.add(label);

        JLabel spacer = PWCGLabelFactory.makeMenuLabelLarge("   ");
        buttonPanel.add(spacer);

        buttonPanel.add(makeActionSelectRadioButton("Show Coop Participant Information", "ShowCoopUserInfo"));
        buttonPanel.add(makeActionSelectRadioButton("Add Coop User", "AddCoopUser"));
        buttonPanel.add(makeActionSelectRadioButton("Remove Coop User", "RemoveCoopUser"));
        
        add (buttonPanel);

        configPanel.add(buttonPanel, BorderLayout.NORTH);
        
        return configPanel;
    }

    private JRadioButton makeActionSelectRadioButton(String buttonText, String commandText) throws PWCGException 
    {
        buttonText = InternationalizationManager.getTranslation(buttonText);
        
        Color fgColor = ColorMap.CHALK_FOREGROUND;
        Font font = PWCGMonitorFonts.getPrimaryFont();
        JRadioButton button = PWCGButtonFactory.makeRadioButton(buttonText, commandText, "", font, fgColor, false, this);
        buttonGroup.add(button);
        return button;
    }

    public void actionPerformed(ActionEvent ae)
    {
        try
        {
            String action = ae.getActionCommand();
            if (action.equalsIgnoreCase("Finished"))
            {
                CampaignGuiContextManager.getInstance().popFromContextStack();
                return;
            }

            if (action.equalsIgnoreCase("ShowCoopUserInfo"))
            {
                CoopPersonaInfoPanel coopPersonaInfoPanel = new CoopPersonaInfoPanel();
                coopPersonaInfoPanel.makePanels();                
                pwcgThreePanel.setCenterPanel(coopPersonaInfoPanel);
            }
            else if (action.equalsIgnoreCase("AddCoopUser"))
            {
                CoopCreateUserPanel coopCreateUser = new CoopCreateUserPanel();
                coopCreateUser.makePanels();
                pwcgThreePanel.setCenterPanel(coopCreateUser);
            }
            else if (action.contains("RemoveCoopUser"))
            {
                CoopUserRemovePanel coopUserRemove = new CoopUserRemovePanel();
                coopUserRemove.makePanels();
                coopUserRemove.loadPanels();
                pwcgThreePanel.setCenterPanel(coopUserRemove);
            }
        }
        catch (Throwable e)
        {
            PWCGLogger.logException(e);
            ErrorDialog.internalError(e.getMessage());
        }
    }
}


