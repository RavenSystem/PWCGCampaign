package pwcg.gui.colors;

import java.awt.Color;

import pwcg.campaign.plane.PwcgRoleCategory;

public class BelgianColorMap extends ServiceColor implements IServiceColorMap
{
    public static final Color BOMBER_COLOR = new Color(200, 200, 70);
    public static final Color RECON_COLOR = new Color(230, 230, 80);
    public static final Color FIGHTER_COLOR = new Color(240, 240, 150);
    
    @Override
    public Color getColorForRole(PwcgRoleCategory roleCategory)
    {
        if (roleCategory == PwcgRoleCategory.BOMBER)
        {
            return BOMBER_COLOR;
        }
        if (roleCategory == PwcgRoleCategory.FIGHTER)
        {
            return FIGHTER_COLOR;            
        }
        
        return RECON_COLOR;
    }
}
