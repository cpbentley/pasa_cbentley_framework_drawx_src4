package pasa.cbentley.framework.drawx.src4.string;

public interface IBOFxStrPara extends IBOFxStr {

   public static final int FXPARA_BASIC_SIZE               = FX_BASIC_SIZE + 4;

   /**
    * True when a inter line figure is defined
    */
   public static final int FXPARA_FLAG_1_INTERLINE_FIG     = 1 << 0;

   public static final int FXPARA_FLAG_2_INTERWORD_FIG     = 1 << 1;
}
