/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.byteobjects.src4.objects.color.IBOFilter;
import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMergeMask;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.core.src4.utils.interfaces.IColorsStatic;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOMask;
import pasa.cbentley.framework.drawx.src4.string.FxStringFactory;
import pasa.cbentley.framework.drawx.src4.string.StringFx;
import pasa.cbentley.framework.drawx.src4.string.StringStyleApplicatorIndex;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * 
 * {@link IBOTypesDrawX#TYPE_DRWX_08_CONTENT_FX}
 * 
 * @author Charles-Philip Bentley
 *
 */
public interface IBOContentFx extends IByteObject {

   /**
    * 1 byte for flag
    * 1 byte for type scope
    */
   public static final int CONTENT_BASIC_SIZE                   = A_OBJECT_BASIC_SIZE + 15;

   public static final int CONTENT_FLAG_1_                      = 1 << 0;

   /**
    */
   public static final int CONTENT_FLAG_2_                      = 1 << 1;

   /**
    * 
    */
   public static final int CONTENT_FLAG_3_                      = 1 << 2;

   /**
    */
   public static final int CONTENT_FLAG_4_                      = 1 << 3;

   public static final int CONTENT_FLAG_5_                      = 1 << 4;

   public static final int CONTENT_FLAG_6_                      = 1 << 5;

   public static final int CONTENT_FLAG_7_                      = 1 << 6;

   public static final int CONTENT_FLAG_8_                      = 1 << 7;

   /**
    * When set font face definition is transparent and value should be ignored.
    */
   public static final int CONTENT_FLAGX_1_UNDEFINED_FONT_FACE  = 1 << 0;

   /**
    * When set font style definition is transparent and value should be ignored.
    */
   public static final int CONTENT_FLAGX_2_UNDEFINED_FONT_STYLE = 1 << 1;

   /**
    * When set font size definition is transparent and value should be ignored.
    */
   public static final int CONTENT_FLAGX_3_UNDEFINED_FONT_SIZE  = 1 << 2;

   /**
    * When set font color definition is transparent and value should be ignored.
    */
   public static final int CONTENT_FLAGX_4_UNDEFINED_COLOR      = 1 << 3;

   public static final int CONTENT_FLAGX_5_UNDEFINED            = 1 << 4;

   /**
    * 
    */
   public static final int CONTENT_FLAGX_6_UNDEFINED            = 1 << 5;

   /**
    * 
    */
   public static final int CONTENT_FLAGX_7_UNDEFINED            = 1 << 6;

   /**
    * Flag telling this object is semi transparent for the purpose of merges.
    * <p>
    * This avoid the use of {@link IBOMergeMask}s which are cumbersome. StringFx uses
    * a lot of transparent font/color combos.
    * </p>
    * 
    * <li> {@link IBOContentFx#CONTENT_FLAGX_1_UNDEFINED_FONT_FACE} 
    * <li> {@link IBOContentFx#CONTENT_FLAGX_2_UNDEFINED_FONT_STYLE} 
    * <li> {@link IBOContentFx#CONTENT_FLAGX_3_UNDEFINED_FONT_SIZE} 
    * <li> {@link IBOContentFx#CONTENT_FLAGX_4_UNDEFINED_COLOR} 
    * <li> {@link IBOContentFx#CONTENT_FLAGX_5_UNDEFINED_SCOPE} 
    * 
    */
   public static final int CONTENT_FLAGX_8_INCOMPLETE           = 1 << 7;

   /**
    * {@link IBOFilter} is defined.
    */
   public static final int CONTENT_FLAGY_1_COLOR_FILTER         = 1;

   /**
    * Sets when a Figure object is paramed. it will be used a simple background figure
    * for the scope of the text effect
    */
   public static final int CONTENT_FLAGY_2_FIGURE               = 1 << 1;

   /**
    * {@link IBOMask} is defined.
    * 
    */
   public static final int CONTENT_FLAGY_3_MASK                 = 1 << 2;

   /**
    * Filter post mask
    */
   public static final int CONTENT_FLAGY_4_MIDDLE_FILTER        = 1 << 3;

   /**
    * Decoration post 1st filter
    * 
    */
   public static final int CONTENT_FLAGY_5_DECORATION           = 1 << 4;

   /**
    * Filter applied after the decoration figure
    */
   public static final int CONTENT_FLAGY_6_LAST_FILTER          = 1 << 5;

   /**
    */
   public static final int CONTENT_FLAGY_7_                     = 1 << 6;

   /**
    */
   public static final int CONTENT_FLAGY_8_                     = 1 << 7;

   /**
    * <li> {@link IBOContentFx#CONTENT_FLAG_1_}
    * <li> {@link IBOContentFx#CONTENT_FLAG_2_}
    * <li> {@link IBOContentFx#CONTENT_FLAG_3_}
    * <li> {@link IBOContentFx#CONTENT_FLAG_4_}
    * <li> {@link IBOContentFx#CONTENT_FLAG_5_}
    * <li> {@link IBOContentFx#CONTENT_FLAG_6_}
    * <li> {@link IBOContentFx#CONTENT_FLAG_7_}
    * <li> {@link IBOContentFx#CONTENT_FLAG_8_}
    * 
    */
   public static final int CONTENT_OFFSET_01_FLAG               = A_OBJECT_BASIC_SIZE + 0;

   /**
    * <li> {@link IBOContentFx#CONTENT_FLAGX_1_UNDEFINED_FONT_FACE}
    * <li> {@link IBOContentFx#CONTENT_FLAGX_2_UNDEFINED_FONT_STYLE}
    * <li> {@link IBOContentFx#CONTENT_FLAGX_3_UNDEFINED_FONT_SIZE}
    * <li> {@link IBOContentFx#CONTENT_FLAGX_4_UNDEFINED_COLOR}
    * <li> {@link IBOContentFx#CONTENT_FLAGX_5_UNDEFINED_SCOPE}
    * <li> {@link IBOContentFx#CONTENT_FLAGX_6_UNDEFINED}
    * <li> {@link IBOContentFx#CONTENT_FLAGX_7_UNDEFINED}
    * <li> {@link IBOContentFx#CONTENT_FLAGX_8_INCOMPLETE}
    */
   public static final int CONTENT_OFFSET_02_FLAGX              = A_OBJECT_BASIC_SIZE + 1;

   /**
    * <li> {@link IBOContentFx#CONTENT_FLAGY_1_COLOR_FILTER}
    * <li> {@link IBOContentFx#CONTENT_FLAGY_2_FIGURE}
    * <li> {@link IBOContentFx#CONTENT_FLAGY_3_MASK}
    * <li> {@link IBOContentFx#CONTENT_FLAGY_4_MIDDLE_FILTER}
    * <li> {@link IBOContentFx#CONTENT_FLAGY_5_DECORATION}
    * <li> {@link IBOContentFx#CONTENT_FLAGY_6_LAST_FILTER}
    * <li> {@link IBOContentFx#CONTENT_FLAGY_7_STYLE}
    * <li> {@link IBOContentFx#CONTENT_FLAGY_8_APPLICATOR}
    */
   public static final int CONTENT_OFFSET_03_FLAGY              = A_OBJECT_BASIC_SIZE + 2;

   /**
    * ID To a set of fonts defined by driver.
    * 
    * {@link ITechFigure}
    * 
    * <li> {@link ITechFont#FACE_MONOSPACE}
    * <li> {@link ITechFont#FACE_PROPORTIONAL}
    * <li> {@link ITechFont#FACE_SYSTEM}
    * 
    * <p>
    * MergeMask: flag 1 of offset 6.
    * </p>
    */
   public static final int CONTENT_OFFSET_06_FONT_FACE1         = A_OBJECT_BASIC_SIZE + 6;

   /**
    * 
    * MM: flag 2 of offset 6.
    * 
    * 
    * <li> {@link ITechFont#STYLE_BOLD}
    * <li> {@link ITechFont#STYLE_ITALIC}
    * <li> {@link ITechFont#STYLE_PLAIN}
    * <li> {@link ITechFont#STYLE_UNDERLINED}
    */
   public static final int CONTENT_OFFSET_07_FONT_STYLE1        = A_OBJECT_BASIC_SIZE + 7;

   /**
    * MM: flag 3 of offset 6.
    * 
    * Relation to {@link IBOFigString#FIG_STRING_OFFSET_05_SIZE1}
    * 
    * <li> {@link ITechFont#SIZE_0_DEFAULT}
    * <li> {@link ITechFont#SIZE_1_TINY}
    * <li> {@link ITechFont#SIZE_2_SMALL}
    * <li> {@link ITechFont#SIZE_3_MEDIUM}
    * <li> {@link ITechFont#SIZE_4_LARGE}
    * <li> {@link ITechFont#SIZE_5_HUGE}
    */
   public static final int CONTENT_OFFSET_08_FONT_SIZE1         = A_OBJECT_BASIC_SIZE + 8;

   /**
    * Fx base color
    * 
    * <li> {@link IColorsStatic}
    * <li> {@link IColors}
    */
   public static final int CONTENT_OFFSET_04_COLOR4             = A_OBJECT_BASIC_SIZE + 9;

}
