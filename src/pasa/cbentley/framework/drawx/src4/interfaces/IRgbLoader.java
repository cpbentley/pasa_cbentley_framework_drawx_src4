/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.interfaces;

import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.framework.drawx.src4.engine.RgbCache;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * Locates and loads the image
 * <br>
 * Plugs a generic in the {@link RgbCache} loaders.
 * <br>
 * Create an image based on. Enables the {@link RgbCache} to flush the rgb data
 * <br>
 * @author Charles-Philip
 *
 */
public interface IRgbLoader extends IStringable {

   public RgbImage loadImage();

   public String getLoaderStrID();
}
