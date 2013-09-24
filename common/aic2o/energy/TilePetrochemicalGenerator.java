package aic2o.energy;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import buildcraft.api.core.Position;
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

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (addedToEnergyNet && engine != null && engine instanceof EnginePetrochemical) {
			EnginePetrochemical pengine = (EnginePetrochemical) engine;

			int output = Math.min(pengine.getMaxEnergyOutput(), (int) pengine.energy * MJ2EU);

			EnergyTileSourceEvent event = new EnergyTileSourceEvent(this, output);
			MinecraftForge.EVENT_BUS.post(event);

			pengine.energy -= (output - event.amount) / MJ2EU;
		}
	}

	@Override
	public void switchOrientation() {
		for (int i = orientation + 1; i <= orientation + 6; ++i) {
			ForgeDirection o = ForgeDirection.values()[i % 6];

			Position pos = new Position(xCoord, yCoord, zCoord, o);

			pos.moveForwards(1);

			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (tile instanceof IEnergyConductor || tile instanceof IEnergySink) {
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
