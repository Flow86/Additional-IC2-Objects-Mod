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

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import buildcraft.api.core.Position;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.energy.Engine;
import buildcraft.energy.TileEngine;

public class TilePetrochemicalGenerator extends TileEngine implements IEnergySource {

	public static final int MJ2EU = 2 / 1;

	private boolean addedToEnergyNet = false;

	protected void attachToEnergyNet() {
		if (!worldObj.isRemote && !addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));

			if (engine != null) {
				Position pos = new Position(xCoord, yCoord, zCoord, engine.orientation);
				worldObj.markBlockForUpdate((int) pos.x, (int) pos.y, (int) pos.z);
			}

			addedToEnergyNet = true;
		}
	}

	protected void detachFromEnergyNet() {
		if (!worldObj.isRemote && addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));

			if (engine != null) {
				Position pos = new Position(xCoord, yCoord, zCoord, engine.orientation);
				worldObj.markBlockForUpdate((int) pos.x, (int) pos.y, (int) pos.z);
			}

			addedToEnergyNet = false;
		}
	}

	public boolean isEnergyTile(TileEntity tile) {
		return tile instanceof IEnergyConductor || tile instanceof IEnergySink;
	}

	@Override
	public void initialize() {
		super.initialize();
		attachToEnergyNet();
	}

	@Override
	public void delete() {
		detachFromEnergyNet();
		super.delete();
	}

	public void setActive(boolean active) {
		Method method;
		try {
			method = TileEngine.class.getDeclaredMethod("setActive", boolean.class);
			method.setAccessible(true);
			method.invoke(this, active);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateEntity() {

		// hack stupid TileEngine code
		Engine e = engine;
		engine = null;

		super.updateEntity();

		engine = e;

		// now this is copied from TileEngine, due to the fact that we don't get an animation otherwise ...

		if (engine == null)
			return;

		if (CoreProxy.proxy.isRenderWorld(worldObj)) {
			if (progressPart != 0) {
				engine.progress += serverPistonSpeed;

				if (engine.progress > 1) {
					progressPart = 0;
					engine.progress = 0;
				}
			} else if (this.isActive) {
				progressPart = 1;
			}

			return;
		}

		engine.update();

		float newPistonSpeed = engine.getPistonSpeed();
		if (newPistonSpeed != serverPistonSpeed) {
			serverPistonSpeed = newPistonSpeed;
			sendNetworkUpdate();
		}

		if (progressPart != 0) {
			engine.progress += engine.getPistonSpeed();

			if (engine.progress > 0.5 && progressPart == 1) {
				progressPart = 2;

				Position pos = new Position(xCoord, yCoord, zCoord, engine.orientation);
				pos.moveForwards(1.0);
				TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			} else if (engine.progress >= 1) {
				engine.progress = 0;
				progressPart = 0;
			}
		} else if (isRedstonePowered && engine.isActive()) {

			Position pos = new Position(xCoord, yCoord, zCoord, engine.orientation);
			pos.moveForwards(1.0);
			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (isEnergyTile(tile)) {
				if (engine.extractEnergy(1, 1, false) > 0) {
					progressPart = 1;
					setActive(true);
				} else {
					setActive(false);
				}
			} else {
				setActive(false);
			}

		} else {
			setActive(false);
		}

		engine.burn();

		if (addedToEnergyNet && engine != null && engine instanceof EnginePetrochemical) {
			EnginePetrochemical pengine = (EnginePetrochemical) engine;

			if (pengine.energy * MJ2EU > pengine.getMaxEnergyOutput()) {
				int output = Math.min(pengine.getMaxEnergyOutput(), (int) pengine.energy * MJ2EU);

				if (output > 0) {
					EnergyTileSourceEvent event = new EnergyTileSourceEvent(this, output);
					MinecraftForge.EVENT_BUS.post(event);

					pengine.energy -= (output - event.amount) / MJ2EU;
				}
			}
		}
	}

	@Override
	public void switchOrientation() {
		for (int i = orientation + 1; i <= orientation + 6; ++i) {
			ForgeDirection o = ForgeDirection.values()[i % 6];

			Position pos = new Position(xCoord, yCoord, zCoord, o);

			pos.moveForwards(1);

			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (isEnergyTile(tile)) {
				detachFromEnergyNet();

				if (engine != null) {
					engine.orientation = o;
				}

				orientation = o.ordinal();

				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));

				attachToEnergyNet();
				break;
			}
		}
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		return (engine != null && direction.toForgeDirection() == engine.orientation);
	}

	@Override
	public boolean isAddedToEnergyNet() {
		return addedToEnergyNet;
	}

	@Override
	public int getMaxEnergyOutput() {
		return ((EnginePetrochemical) engine).getMaxEnergyOutput();
	}

	@Override
	public Engine newEngine(int meta) {
		if (meta == 4)
			return new EnginePetrochemical(this);

		return super.newEngine(meta);
	}
}
