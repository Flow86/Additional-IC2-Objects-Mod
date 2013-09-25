/** 
 * Copyright (C) 2013 Flow86
 * 
 * AdditionalIC2Objects is open-source.
 *
 * It is distributed under the terms of my Open Source License. 
 * It grants rights to read, modify, compile or run the code. 
 * It does *NOT* grant the right to redistribute this software or its 
 * modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package aic2o.energy;

import buildcraft.energy.EngineIron;
import buildcraft.energy.TileEngine;

public class EnginePetrochemical extends EngineIron {

	private final int production;

	public EnginePetrochemical(TileEngine tile) {
		super(tile);

		production = 32; // Eu/t

		maxEnergy = 4000000;
		maxEnergyExtracted = 0; // don't use buildcraft energy output
	}

	@Override
	public String getTextureFile() {
		return "/gfx/aic2o/blocks/base_generator.png";
	}

	@Override
	public float extractEnergy(int min, int max, boolean doExtract) {
		return doExtract ? 0 : (currentOutput > 0 ? 1 : 0);
	}

	public int getMaxEnergyOutput() {
		return production;
	}
}
