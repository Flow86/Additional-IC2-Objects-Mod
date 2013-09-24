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
