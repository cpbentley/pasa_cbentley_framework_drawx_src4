package pasa.cbentley.framework.drawx.src4.interfaces;

import pasa.cbentley.byteobjects.src4.core.ByteObject;


/**
 * Directly in the {@link ByteObject} constructor
 * 
 * Since {@link IDrawable} framework heavily uses ByteObject for styles, it implements the {@link IBODrwUser} interface.
 * with automatic registration.
 * So the {@link ByteObjectRepository} does not garbage
 * 
 * 
 * @author Charles-Philip Bentley
 *
 */
public interface IBODrwUser {

   
   /**
    * Returns all reference IDs of {@link ByteObject} used by the {@link IBODrwUser}
    * @return
    */
   public int[] getUsedRefIDs();
}
