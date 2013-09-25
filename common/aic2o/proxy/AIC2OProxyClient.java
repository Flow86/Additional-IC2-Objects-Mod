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

package aic2o.proxy;

import net.minecraft.network.packet.Packet;
import aic2o.AIC2O;
import buildcraft.core.DefaultProps;
import buildcraft.core.render.RenderingEntityBlocks;
import buildcraft.core.render.RenderingEntityBlocks.EntityRenderIndex;
import buildcraft.energy.render.RenderEngine;
import cpw.mods.fml.client.FMLClientHandler;

/**
 * @author Flow86
 * 
 */
public class AIC2OProxyClient extends AIC2OProxy {
	@Override
	public void sendToServer(Packet packet) {
		FMLClientHandler.instance().getClient().getNetHandler().addToSendQueue(packet);
	}

	@Override
	public void registerBlockRenderers() {
		RenderingEntityBlocks.blockByEntityRenders.put(new EntityRenderIndex(AIC2O.blockPetrochemicalGenerator, 4), new RenderEngine(
				DefaultProps.TEXTURE_PATH_BLOCKS + "/base_iron.png"));
	}
}
