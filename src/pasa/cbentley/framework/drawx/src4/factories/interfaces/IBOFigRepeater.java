package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOFigRepeater extends IBOFigure {

   /**
    * 1 byte flag
    * 2 bytes w
    * 2 bytes h
    */
   int FIG_REPEATER_BASIC_SIZE            = IBOFigure.FIG__BASIC_SIZE + 5;
   int FIG_REPEATER_FLAG_1_FORCECOPYAREA  = 1;
   /**
    * Else transparent
    */
   int FIG_REPEATER_FLAG_2_USE_BGCOLOR    = 2;
   int FIG_REPEATER_OFFSET_1_FLAG         = IBOFigure.FIG__BASIC_SIZE;
   /**
    * Width for the Unit
    */
   int FIG_REPEATER_OFFSET_2_W2           = IBOFigure.FIG__BASIC_SIZE + 1;
   int FIG_REPEATER_OFFSET_3_H2           = IBOFigure.FIG__BASIC_SIZE + 3;

}
