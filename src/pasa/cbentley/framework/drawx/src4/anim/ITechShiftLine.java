package pasa.cbentley.framework.drawx.src4.anim;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;

public interface ITechShiftLine extends IByteObject {
   public static final int TECH_BASIC_SIZE           = A_OBJECT_BASIC_SIZE + 4;

   /**
    * Shift lines up/down instead of left/right
    */
   public static final int TECH_FLAG_1UP_AND_DOWN    = 1;

   /**
    * Right to Left instead Left to Right.
    * <br>
    * or
    * <br>
    * Down to Up instead of Up to Down.
    */
   public static final int TECH_FLAG_2REVERSE        = 2;

   /**
    * Should the buffer be drawn at the 
    */
   public static final int TECH_FLAG_3SHIFT_PAINT    = 4;

   /**
    * Move is a multiply instead of pixel move
    */
   public static final int TECH_FLAG_4MULTIPLY       = 8;

   /**
    * When not clipped, animation shift-moves.
    * The whole buffer is drawn on {@link GraphicsX}. 
    */
   public static final int TECH_FLAG_5CLIP           = 32;

   public static final int TECH_OFFSET_1_FLAG        = A_OBJECT_BASIC_SIZE;

   public static final int TECH_OFFSET_2_FLAGX       = A_OBJECT_BASIC_SIZE + 1;

   public static final int TECH_OFFSET_3_SHIFT_SIZE2 = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Start by Top/Bottom Line and each step, add a new line
    * -random
    * -neighbour not added already
    */
   public static final int TYPE_0START               = 0;

   /**
    * Iterate, choose random
    */
   public static final int TYPE_1RANDOM              = 1;

   /**
    * One line is choosen, then all lines.
    * 
    */
   public static final int TYPE_2RANDOMFIRST         = 2;

   /**
    * Function pattern defines at which step
    * This allows to move a half sinus.
    * All lines move with the same shift step. it is just the timing of the start
    * that shapes the shift figure.
    */
   public static final int TYPE_3PATTERN             = 3;
}
