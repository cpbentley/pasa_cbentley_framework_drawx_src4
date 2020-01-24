package pasa.cbentley.framework.drawx.src4.ctx;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.ABOCtx;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.tech.ITechCtxSettings;
import pasa.cbentley.core.src4.memory.IMemory;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.coredraw.src4.ctx.CoreDrawCtx;
import pasa.cbentley.framework.coredraw.src4.interfaces.IFontFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImageFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechDrawer;
import pasa.cbentley.framework.drawx.src4.base.PngEncoder;
import pasa.cbentley.framework.drawx.src4.color.ColorFunctionFactory;
import pasa.cbentley.framework.drawx.src4.engine.RgbCache;
import pasa.cbentley.framework.drawx.src4.factories.BoxFactory;
import pasa.cbentley.framework.drawx.src4.factories.FigureFactory;
import pasa.cbentley.framework.drawx.src4.factories.FigureOperator;
import pasa.cbentley.framework.drawx.src4.factories.FilterFactory;
import pasa.cbentley.framework.drawx.src4.factories.FilterOperator;
import pasa.cbentley.framework.drawx.src4.factories.GradientFactory;
import pasa.cbentley.framework.drawx.src4.factories.MaskFactory;
import pasa.cbentley.framework.drawx.src4.factories.MaskOperator;
import pasa.cbentley.framework.drawx.src4.factories.MergeMaskFactory;
import pasa.cbentley.framework.drawx.src4.factories.MergeMaskOperator;
import pasa.cbentley.framework.drawx.src4.factories.PassDrawOperator;
import pasa.cbentley.framework.drawx.src4.factories.RgbImageFactory;
import pasa.cbentley.framework.drawx.src4.factories.RgbImageOperator;
import pasa.cbentley.framework.drawx.src4.factories.ScaleOperator;
import pasa.cbentley.framework.drawx.src4.factories.ScalerFactory;
import pasa.cbentley.framework.drawx.src4.factories.TblrFactory;
import pasa.cbentley.framework.drawx.src4.string.FxStringFactory;
import pasa.cbentley.framework.drawx.src4.string.FxStringOperator;
import pasa.cbentley.framework.drawx.src4.style.StyleFactory;
import pasa.cbentley.framework.drawx.src4.style.StyleOperator;
import pasa.cbentley.framework.drawx.src4.tech.ITechBlend;
import pasa.cbentley.layouter.src4.ctx.LayouterCtx;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;

/**
 * Context Object for the Draw Base Module.
 * <br>
 * <li> {@link IDrawCtx} the current user of the Draw engine module.
 * <li> {@link RgbCache} Creates the unique reference of {@link RgbCache} accross the whole {@link IAppli}.
 * <li> {@link FactoryDrw} links to the whole object factories for creating figures, mask, TBLR, Gradients
 * <li> {@link ModuleDrwBase} is the {@link BOModule} of this code base
 * @author Charles Bentley
 *
 */
public class DrwCtx extends ABOCtx {

   private final BOCtx          boc;

   private BoxFactory           bx;

   private RgbCache             cache;

   private ColorFunctionFactory colorFunctionFactory;

   private FigureFactory        facFig;

   private FigureOperator       figureOperator;

   private FilterFactory        filterFactory;

   private FilterOperator       filterOperator;

   private IImageFactory        fontImgCreator;

   private FxStringFactory      fxStringFactory;

   private FxStringOperator     fxStringOperator;

   private GradientFactory      grad;

   private LayouterCtx          lac;

   private MaskFactory          maskFactory;

   private MaskOperator         maskOperator;

   private MergeMaskFactory     me;

   private MergeMaskOperator    mergeMaskOperator;

   private BOModuleDrawx        module;

   private PassDrawOperator     passDrawOperator;

   private RgbImageFactory      rgbImageFactory;

   private RgbImageOperator     rgbImageOperator;

   private ScaleOperator        scaleOperator;

   private ScalerFactory        scalerFactory;

   private TblrFactory          tblrFactory;

   private int                  styleFlags;

   protected final CoreDrawCtx  cdc;

   private StyleOperator styleOperator;

   /**
    * Provides what this module needs in input to be usable
    * @param user
    * @param root
    */
   public DrwCtx(CoreDrawCtx cdc, LayouterCtx lac) {
      super(cdc.getBOC());
      //any settings in ctx manager is not loaded here

      this.cdc = cdc;
      this.boc = cdc.getBOC();
      this.lac = lac;
      //we need a sizer and root
      module = new BOModuleDrawx(this);

      cache = new RgbCache(this);
      facFig = new FigureFactory(this);
      bx = new BoxFactory(this);
      me = new MergeMaskFactory(this);
      grad = new GradientFactory(this);

    
   }

   /**
    * Tells whether {@link CoreDrawCtx} has feature support.
    * 
    * {@link ITechDrawer#SUP_ID_03_OPEN_GL}
    * {@link ITechDrawer#SUP_ID_04_ALIAS}
    * {@link ITechDrawer#SUP_ID_10_TRANSPARENT_BACKGROUND}
    * 
    * @param featureID
    * @return
    */
   public boolean hasFeatureSupport(int featureID) {
      return cdc.hasFeatureSupport(featureID);
   }
   
   /**
    * Called when code ctx settings have changed
    */
   public void applySettings() {
      cache.applySettings();
   }

   public CoreDrawCtx getCoreDrawCtx() {
      return cdc;
   }
   private PngEncoder pngEncoder;

   public PngEncoder getPngEncoder() {
      if (pngEncoder == null) {
         pngEncoder = new PngEncoder(this);
      }
      return pngEncoder;
   }

   /**
    * Create a color composer
    * @param mod
    * @param type
    * @param alpha
    * @return
    */
   public ByteObject getBlender(int type, int alpha) {
      ByteObject p = boc.getByteObjectFactory().createByteObject(IBOTypesDrw.TYPE_062_BLENDER, ITechBlend.BLEND_BASIC_SIZE);
      p.set2(ITechBlend.BLEND_OFFSET_03_TYPE2, type);
      p.set1(ITechBlend.BLEND_OFFSET_02_ALPHA1, alpha);
      return p;
   }

   public BOCtx getBOC() {
      return boc;
   }

   public BoxFactory getBoxEng() {
      return bx;
   }

   public RgbCache getCache() {
      return cache;
   }

   public ColorFunctionFactory getColorFunctionFactory() {
      if (colorFunctionFactory == null) {
         colorFunctionFactory = new ColorFunctionFactory(this);
      }
      return colorFunctionFactory;
   }

   public FigureFactory getFigureFactory() {
      return facFig;
   }

   public FigureOperator getFigureOperator() {
      if (figureOperator == null) {
         figureOperator = new FigureOperator(this);
      }
      return figureOperator;
   }

   public FilterFactory getFilterFactory() {
      if (filterFactory == null) {
         filterFactory = new FilterFactory(this);
      }
      return filterFactory;
   }

   public FilterOperator getFilterOperator() {
      if (filterOperator == null) {
         filterOperator = new FilterOperator(this);
      }
      return filterOperator;
   }

   public IFontFactory getFontFactory() {
      return cdc.getFontFactory();
   }

   public FxStringFactory getFxStringFactory() {
      if (fxStringFactory == null) {
         fxStringFactory = new FxStringFactory(this);
      }
      return fxStringFactory;
   }

   public FxStringOperator getFxStringOperator() {
      if (fxStringOperator == null) {
         fxStringOperator = new FxStringOperator(this);
      }
      return fxStringOperator;
   }
   
   public StyleOperator getStyleOperator() {
      if (styleOperator == null) {
         styleOperator = new StyleOperator(this);
      }
      return styleOperator;
   }
   
   private StyleFactory styleFactory;

   public StyleFactory getStyleFactory() {
      if (styleFactory == null) {
         styleFactory = new StyleFactory(this);
      }
      return styleFactory;
   }


   public GradientFactory getGradientFactory() {
      return grad;
   }

   public IImageFactory getImageFactory() {
      return cdc.getImageFactory();
   }

   public LayouterCtx getLAC() {
      return lac;
   }

   public MaskFactory getMaskFactory() {
      if (maskFactory == null) {
         maskFactory = new MaskFactory(this);
      }
      return maskFactory;
   }

   public MaskOperator getMaskOperator() {
      if (maskOperator == null) {
         maskOperator = new MaskOperator(this);
      }
      return maskOperator;
   }

   public IMemory getMem() {
      return uc.getMem();
   }

   public MergeMaskFactory getMergeMaskFactory() {
      return me;
   }

   public MergeMaskOperator getMergeMaskOperator() {
      if (mergeMaskOperator == null) {
         mergeMaskOperator = new MergeMaskOperator(this);
      }
      return mergeMaskOperator;
   }

   /**
    * {@link ITechCtxSettingsDrw} is a {@link ITechCtxSettings}
    * 
    * @return
    */
   public ByteObject getSettingsCtxDrw() {
      return getSettingsBO();
   }

   public PassDrawOperator getPassDrawOperator() {
      if (passDrawOperator == null) {
         passDrawOperator = new PassDrawOperator(this);
      }
      return passDrawOperator;
   }

   public Random getRandom() {
      return uc.getRandom();
   }

   public RgbCache getRgbCache() {
      return cache;
   }

   public RgbImageFactory getRgbImageFactory() {
      if (rgbImageFactory == null) {
         rgbImageFactory = new RgbImageFactory(this);
      }
      return rgbImageFactory;
   }

   public RgbImageOperator getRgbImageOperator() {
      if (rgbImageOperator == null) {
         rgbImageOperator = new RgbImageOperator(this);
      }
      return rgbImageOperator;
   }

   public ScaleOperator getScaleOperator() {
      if (scaleOperator == null) {
         scaleOperator = new ScaleOperator(this);
      }
      return scaleOperator;
   }

   public ScalerFactory getScalerFactory() {
      if (scalerFactory == null) {
         scalerFactory = new ScalerFactory(this);
      }
      return scalerFactory;
   }

   public LayoutOperator getSizer() {
      return lac.getLayoutOperator();
   }

   public boolean hasStyleFlag(int flag) {
      return BitUtils.hasFlag(styleFlags, flag);
   }

   public TblrFactory getTblrFactory() {
      if (tblrFactory == null) {
         tblrFactory = new TblrFactory(this);
      }
      return tblrFactory;
   }

   public int getBOSettingsCtxSize() {
      return ITechCtxSettingsDrw.MODSET_DRW_BASIC_SIZE;
   }

}
