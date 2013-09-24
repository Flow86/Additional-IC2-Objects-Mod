/** 
 * Copyright (C) 2011-2013 Flow86
 * 
 * AdditionalIC2Objects is open-source.
 *
 * It is distributed under the terms of my Open Source License. 
 * It grants rights to read, modify, compile or run the code. 
 * It does *NOT* grant the right to redistribute this software or its 
 * modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package aic2o.proxy;

import net.minecraft.network.packet.Packet;
import aic2o.energy.TilePetrochemicalGenerator;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author Flow86
 * 
 */
public class AIC2OProxy {

	@SidedProxy(clientSide = "aic2o.proxy.AIC2OProxyClient", serverSide = "aic2o.proxy.AIC2OProxy")
	public static AIC2OProxy proxy;

	public void registerPipe(int itemID) {
	}

	public void sendToServer(Packet packet) {
	}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TilePetrochemicalGenerator.class, "net.minecraft.src.aic2o.energy.petrochemicalGenerator");
	}

	public void registerBlockRenderers() {
	}
}
