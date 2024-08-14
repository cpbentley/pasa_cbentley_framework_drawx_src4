/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

public interface ITechStyle {
   /**
    * Globally disable the Gradients
    */
   public static final int STYLES_1_GRADIENT_DISABLED = 1;
   /**
    * Default anchoring for style layers.
    * <br>
    * That layers will draw starting at the border position
    */
   int STYLE_ANC_0_BORDER               = 0;
   /**
    * That layers will draw starting at the margin position
    */
   int STYLE_ANC_1_MARGIN               = 1;
   /**
    * That layers will draw starting at the content position
    */
   int STYLE_ANC_2_CONTENT              = 2;
   /**
    * That layers will draw starting at the padding position
    */
   int STYLE_ANC_3_PADDING              = 3;

}
