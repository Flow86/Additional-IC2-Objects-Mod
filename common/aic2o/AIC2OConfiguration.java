/** 
 * Copyright (C) 2011-2013 Flow86
 * 
 * AdditionalBuildcraftObjects is open-source.
 *
 * It is distributed under the terms of my Open Source License. 
 * It grants rights to read, modify, compile or run the code. 
 * It does *NOT* grant the right to redistribute this software or its 
 * modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package aic2o;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

/**
 * @author Flow86
 * 
 */
public class AIC2OConfiguration extends Configuration {
	public AIC2OConfiguration(File file) {
		super(file);
	}

	@Override
	public void save() {
		Property versionProp = null;

		get(CATEGORY_GENERAL, "version", AIC2O.VERSION);

		super.save();
	}
}
