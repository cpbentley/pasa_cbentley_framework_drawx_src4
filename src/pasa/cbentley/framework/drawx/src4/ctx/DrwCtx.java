/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import java.util.Random;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.ABOCtx;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.ctx.IConfigBO;
import pasa.cbentley.byteobjects.src4.extra.MergeMaskFactory;
import pasa.cbentley.byteobjects.src4.tech.ITechCtxSettings;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.memory.IMemory;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.coredraw.src4.ctx.CoreDrawCtx;
import pasa.cbentley.framework.coredraw.src4.interfaces.IFontFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImageFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechDrawer;
import pasa.cbentley.framework.drawx.src4.base.PngEncoder;
import pasa.cbentley.framework.drawx.src4.base.RgbImageRotateUtils;
import pasa.cbentley.framework.drawx.src4.color.ColorFunctionFactory;
import pasa.cbentley.framework.drawx.src4.engine.RgbCache;
import pasa.cbentley.framework.drawx.src4.factories.AnchorFactory;
import pasa.cbentley.framework.drawx.src4.factories.BoxFactory;
import pasa.cbentley.framework.drawx.src4.factories.FigureFactory;
import pasa.cbentley.framework.drawx.src4.factories.FigureOperator;
import pasa.cbentley.framework.drawx.src4.factories.FilterFactory;
import pasa.cbentley.framework.drawx.src4.factories.FilterOperator;
import pasa.cbentley.framework.drawx.src4.factories.GradientFactory;
import pasa.cbentley.framework.drawx.src4.factories.GradientOperator;
import pasa.cbentley.framework.drawx.src4.factories.MaskFactory;
import pasa.cbentley.framework.drawx.src4.factories.MaskOperator;
import pasa.cbentley.framework.drawx.src4.factories.PassDrawOperator;
import pasa.cbentley.framework.drawx.src4.factories.RgbImageFactory;
import pasa.cbentley.framework.drawx.src4.factories.RgbImageOperator;
import pasa.cbentley.framework.drawx.src4.factories.ScaleOperator;
import pasa.cbentley.framework.drawx.src4.factories.ScalerFactory;
import pasa.cbentley.framework.drawx.src4.factories.ScalerOperatorTests;
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

   public static final int      CTX_ID = 485;

   private final BOCtx          boc;

   private BoxFactory           bx;

   private RgbCache             cache;

   protected final CoreDrawCtx  cdc;

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


   private BOModuleDrawx        module;

   private PassDrawOperator     passDrawOperator;

   private PngEncoder           pngEncoder;

   private RgbImageFactory      rgbImageFactory;

   private RgbImageOperator     rgbImageOperator;

   private RgbImageRotateUtils  rotator;

   private ScaleOperator        scaleOperator;

   private ScalerOperatorTests  scaleOpTest;

   private ScalerFactory        scalerFactory;

   private StyleFactory         styleFactory;

   private int                  styleFlags;

   private StyleOperator        styleOperator;

   private TblrFactory          tblrFactory;

   public DrwCtx(CoreDrawCtx cdc, LayouterCtx lac) {
      this(new ConfigDrawXDefault(cdc.getUCtx()), cdc, lac);
   }

   private AnchorFactory anchorFactory;

   public AnchorFactory getAnchorFactory() {
      if (anchorFactory == null) {
         anchorFactory = new AnchorFactory(this);
      }
      return anchorFactory;
   }

   /**
    * Provides what this module needs in input to be usable
    * @param user
    * @param root
    */
   public DrwCtx(IConfigDrawx config, CoreDrawCtx cdc, LayouterCtx lac) {
      super(config, cdc.getBOC());
      //any settings in ctx manager is not loaded here

      //#debug
      toStringSetToStringFlag(config.getFlagsDrw());

      this.cdc = cdc;
      this.boc = cdc.getBOC();
      this.lac = lac;
      //we need a sizer and root
      module = new BOModuleDrawx(this);

      cache = new RgbCache(this);
      facFig = new FigureFactory(this);
      bx = new BoxFactory(this);
      grad = new GradientFactory(this);

      if (this.getClass() == DrwCtx.class) {
         a_Init();
      }
   }

   public void a_Init() {
      super.a_Init();
   }

   protected void applySettings(ByteObject settingsNew, ByteObject settingsOld) {
      cache.applySettings(settingsNew);
   }

   private GradientOperator gradientOperator;

   public GradientOperator getGradientOperator() {
      if (gradientOperator == null) {
         gradientOperator = new GradientOperator(this);
      }
      return gradientOperator;
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

   public int getBOCtxSettingSize() {
      return ITechCtxSettingsDrwx.CTX_DRW_BASIC_SIZE;
   }

   public BoxFactory getBoxFactory() {
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

   public CoreDrawCtx getCoreDrawCtx() {
      return cdc;
   }

   public int getCtxID() {
      return CTX_ID;
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
      return boc.getMergeMaskFactory();
   }


   public PassDrawOperator getPassDrawOperator() {
      if (passDrawOperator == null) {
         passDrawOperator = new PassDrawOperator(this);
      }
      return passDrawOperator;
   }

   public PngEncoder getPngEncoder() {
      if (pngEncoder == null) {
         pngEncoder = new PngEncoder(this);
      }
      return pngEncoder;
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

   public RgbImageRotateUtils getRgbImageRotateUtils() {
      if (rotator == null) {
         rotator = new RgbImageRotateUtils(this);
      }
      return rotator;
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

   public ScalerOperatorTests getScalerOperatorTests() {
      if (scaleOpTest == null) {
         scaleOpTest = new ScalerOperatorTests(this);
      }
      return scaleOpTest;
   }

   /**
    * {@link ITechCtxSettingsDrwx} is a {@link ITechCtxSettings}
    * 
    * @return
    */
   public ByteObject getSettingsCtxDrw() {
      return getSettingsBO();
   }

   public LayoutOperator getSizer() {
      return lac.getLayoutOperator();
   }

   public StyleFactory getStyleFactory() {
      if (styleFactory == null) {
         styleFactory = new StyleFactory(this);
      }
      return styleFactory;
   }

   public StyleOperator getStyleOperator() {
      if (styleOperator == null) {
         styleOperator = new StyleOperator(this);
      }
      return styleOperator;
   }

   public TblrFactory getTblrFactory() {
      if (tblrFactory == null) {
         tblrFactory = new TblrFactory(this);
      }
      return tblrFactory;
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

   public boolean hasStyleFlag(int flag) {
      return BitUtils.hasFlag(styleFlags, flag);
   }

   protected void matchConfig(IConfigBO config, ByteObject settings) {

   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, "DrwCtx");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "DrwCtx");
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug

}
