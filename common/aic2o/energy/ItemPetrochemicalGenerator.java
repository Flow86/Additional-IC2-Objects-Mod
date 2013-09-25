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

import net.minecraft.item.ItemStack;
import buildcraft.energy.ItemEngine;

public class ItemPetrochemicalGenerator extends ItemEngine {

	public ItemPetrochemicalGenerator(int i) {
		super(i);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if (itemstack.getItemDamage() == 4)
			return "tile.PetrochemicalGenerator";

		return super.getUnlocalizedName(itemstack);
	}
}
