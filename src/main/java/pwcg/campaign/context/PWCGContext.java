package pwcg.campaign.context;

import pwcg.core.exception.PWCGException;
import pwcg.core.utils.PWCGLogger;

public class PWCGContext 
{
    protected static BoSContext bosContextManager = null;
    protected static FCContext fcContextManager = null;
    protected static PWCGProduct product = PWCGProduct.NONE;

	protected PWCGContext()
    {
    }

    public static IPWCGContextManager getInstance() 
    {
        try
        {
            return buildProductContext(product);
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
        }
        
        return null;
    }

    public static IPWCGContextManager getInstance(PWCGProduct requestedProduct) 
    {
        try
        {
            product = requestedProduct;
            return buildProductContext(product);
        }
        catch (Exception e)
        {
            PWCGLogger.logException(e);
        }
        
        return null;
    }


    public static PWCGProduct getProduct()
    {
        return product;
    }

    public static void setProduct(PWCGProduct product) throws PWCGException
    {
        if (PWCGContext.product != product)
        {
            bosContextManager = null;
            fcContextManager = null;
            PWCGContext.product = product;
            buildProductContext(product);
        }
    }

    private static IPWCGContextManager buildProductContext(PWCGProduct product) throws PWCGException
    {
        if (product == PWCGProduct.BOS)
        {
            if (PWCGContext.bosContextManager == null)
            {
                PWCGContext.bosContextManager = new BoSContext();
                PWCGContext.bosContextManager.initialize();
            }
            
            return PWCGContext.bosContextManager;
        }
        else if (product == PWCGProduct.FC)
        {
            if (PWCGContext.fcContextManager == null)
            {
                PWCGContext.fcContextManager = new FCContext();
                PWCGContext.fcContextManager.initialize();
            }
            
            return PWCGContext.fcContextManager;
        }
        else
        {
            throw new PWCGException("No product defined");
        }
    }
}
