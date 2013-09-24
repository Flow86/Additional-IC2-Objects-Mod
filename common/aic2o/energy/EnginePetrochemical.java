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
	public float extractEnergy(int min, int max, boolean doExtract) {
		return 0;
	}

	public int getMaxEnergyOutput() {
		return production;
	}
}
