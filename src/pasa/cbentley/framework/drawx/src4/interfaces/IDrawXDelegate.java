package pasa.cbentley.framework.drawx.src4.interfaces;

import pasa.cbentley.framework.drawx.src4.engine.DrawXContext;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface IDrawXDelegate {

   /**
    * Draw what's its supposed to draw.
    * <br>
    * <br>
    * The {@link DrawXContext} provides the Area and parameters
    * @param g
    * @param dc {@link DrawXContext}
    */
   public void drawX(GraphicsX g, DrawXContext dc);
}
