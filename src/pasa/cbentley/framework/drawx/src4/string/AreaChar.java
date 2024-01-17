package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

/**
 * Area that can behave like a character.
 * 
 * <li> It has a Width and Height.
 * 
 * Either defines
 * <li> Fixed
 * <li> Implicit, Takes Height of the Lines it is on.
 * 
 * Anchor of the Area is as a character by default. Easiest to implement
 * 
 * Drawable
 * with same facilities
 * 
 * How to represent it in the char array ?
 * For images, we want to be able to load them from res folder
 * <li>[img src="" w="" h="f"]
 * <li>[img src="/image.png" w="40" h="40"]
 * <li>[img src="/image.png" w="0" h="0"] //use image size
 *  
 * @author Charles Bentley
 *
 */
public class AreaChar extends ObjectDrw {

   public AreaChar(DrwCtx drc) {
      super(drc);
   }

}
