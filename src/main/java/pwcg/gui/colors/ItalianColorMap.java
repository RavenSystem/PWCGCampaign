package pwcg.gui.colors;

import java.awt.Color;

import pwcg.campaign.plane.PwcgRoleCategory;

public class ItalianColorMap extends ServiceColor implements IServiceColorMap
{
    public static final Color RECON_COLOR = new Color(120, 105, 90);
    public static final Color FIGHTER_COLOR = new Color(150, 140, 120);
    
    public Color getColorForRole(PwcgRoleCategory roleCategory)
    {
        if (roleCategory == PwcgRoleCategory.FIGHTER)
        {
            return FIGHTER_COLOR;            
        }
        
        return RECON_COLOR;
    }
}
