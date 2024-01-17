package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

public interface IBOFigBorder extends IBOFigure {

   /**
    * 1 byte for border flag
    * 1 byte for size, 
    * 2 bytes for arcw and arch
    * 4 bytes for secondary color
    * 1 byte gradient position
    */
   int FIG_BORDER_BASIC_SIZE              = IBOFigure.FIG__BASIC_SIZE + 3;
   /**
    * around the define boundary. default is inside
    */
   int FIG_BORDER_FLAG_1OUTER             = 1;
   /**
    * Are coins defined
    */
   int FIG_BORDER_FLAG_4COIN              = 8;
   int FIG_BORDER_FLAG_5FIGURE            = 16;
   /**
    * Flag set when the 8 first ByteObject are the 8 figures
    */
   int FIG_BORDER_FLAG_8FIGURES           = 128;
   int FIG_BORDER_OFFSET_1FLAG            = IBOFigure.FIG__BASIC_SIZE;
   /** 
    * The pixel shift applied at the 4 corners.<br>
    * Size of border depends on a ByteObject TBLR.
    * Shift reduce that value
    */
   int FIG_BORDER_OFFSET_2CORNER_SHIFT1   = IBOFigure.FIG__BASIC_SIZE + 1;
   /**
    * <li>  {@link ITechFigure#STROKE_0_SOLID}
    * <li>  {@link ITechFigure#STROKE_1_SIMPLE_DOTS}
    */
   int FIG_BORDER_OFFSET_3STROKE_STYLE1   = IBOFigure.FIG__BASIC_SIZE + 2;

}
