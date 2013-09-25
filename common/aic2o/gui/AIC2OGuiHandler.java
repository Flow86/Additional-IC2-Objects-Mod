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

package aic2o.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import aic2o.energy.gui.GuiPetrochemicalGenerator;
import buildcraft.energy.TileEngine;
import buildcraft.energy.gui.ContainerEngine;
import cpw.mods.fml.common.network.IGuiHandler;

public class AIC2OGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z))
			return null;

		TileEntity tile = world.getBlockTileEntity(x, y, z);

		switch (ID) {
		case AIC2OGuiIds.ENGINE_PETROCHEMICAL:
			return new ContainerEngine(player.inventory, (TileEngine) tile);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z))
			return null;

		TileEntity tile = world.getBlockTileEntity(x, y, z);

		switch (ID) {
		case AIC2OGuiIds.ENGINE_PETROCHEMICAL:
			return new GuiPetrochemicalGenerator(player.inventory, (TileEngine) tile);
		}

		return null;
	}
}
