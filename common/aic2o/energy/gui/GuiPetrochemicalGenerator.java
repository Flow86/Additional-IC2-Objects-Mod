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

package aic2o.energy.gui;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import aic2o.energy.EnginePetrochemical;
import aic2o.energy.TilePetrochemicalGenerator;
import buildcraft.BuildCraftCore;
import buildcraft.core.CoreIconProvider;
import buildcraft.core.DefaultProps;
import buildcraft.core.gui.GuiBuildCraft;
import buildcraft.core.utils.StringUtils;
import buildcraft.energy.Engine;
import buildcraft.energy.TileEngine;
import buildcraft.energy.gui.ContainerEngine;

public class GuiPetrochemicalGenerator extends GuiBuildCraft {

	protected class EngineLedger extends Ledger {

		Engine engine;
		int headerColour = 0xe1c92f;
		int subheaderColour = 0xaaafb8;
		int textColour = 0x000000;

		public EngineLedger(Engine engine) {
			this.engine = engine;
			maxHeight = 94;
			overlayColor = 0xd46c1f;
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			Minecraft.getMinecraft().renderEngine.bindTexture("/gui/items.png");
			drawIcon(BuildCraftCore.iconProvider.getIcon(CoreIconProvider.ENERGY), x + 3, y + 4);

			if (!isFullyOpened())
				return;

			fontRenderer.drawStringWithShadow(StringUtils.localize("gui.energy"), x + 22, y + 8, headerColour);
			fontRenderer.drawStringWithShadow(StringUtils.localize("gui.currentOutput") + ":", x + 22, y + 20, subheaderColour);
			fontRenderer.drawString(String.format("%.1f Eu/t", engine.getCurrentOutput() * TilePetrochemicalGenerator.MJ2EU), x + 22, y + 32, textColour);
			fontRenderer.drawStringWithShadow(StringUtils.localize("gui.stored") + ":", x + 22, y + 44, subheaderColour);
			fontRenderer.drawString(String.format("%.1f Eu", engine.getEnergyStored() * TilePetrochemicalGenerator.MJ2EU), x + 22, y + 56, textColour);
			fontRenderer.drawStringWithShadow(StringUtils.localize("gui.heat") + ":", x + 22, y + 68, subheaderColour);
			fontRenderer.drawString(String.format("%.2f \u00B0C", (engine.getHeat() / 100.0) + 20.0), x + 22, y + 80, textColour);

		}

		@Override
		public String getTooltip() {
			return (engine.getCurrentOutput() * TilePetrochemicalGenerator.MJ2EU) + " Eu/t";
		}
	}

	@Override
	protected void initLedgers(IInventory inventory) {
		super.initLedgers(inventory);
		ledgerManager.add(new EngineLedger(((TileEngine) tile).engine));
	}

	public GuiPetrochemicalGenerator(InventoryPlayer inventoryplayer, TileEngine tileEngine) {
		super(new ContainerEngine(inventoryplayer, tileEngine), tileEngine);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		String title = StringUtils.localize("tile.PetrochemicalGenerator");
		fontRenderer.drawString(title, getCenteredOffset(title), 6, 0x404040);
		fontRenderer.drawString(StringUtils.localize("gui.inventory"), 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(DefaultProps.TEXTURE_PATH_GUI + "/combustion_engine_gui.png");
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

		TileEngine engine = (TileEngine) tile;
		EnginePetrochemical enginePetrochemical = ((EnginePetrochemical) engine.engine);

		if (engine.getScaledBurnTime(58) > 0) {
			displayGauge(j, k, 19, 104, engine.getScaledBurnTime(58), enginePetrochemical.getFuel());
		}

		if (enginePetrochemical.getScaledCoolant(58) > 0) {
			displayGauge(j, k, 19, 122, enginePetrochemical.getScaledCoolant(58), enginePetrochemical.getCoolant());
		}
	}

	private void displayGauge(int j, int k, int line, int col, int squaled, LiquidStack liquid) {
		if (liquid == null) {
			return;
		}
		int start = 0;

		Icon liquidIcon;
		String textureSheet;
		if (liquid.canonical() != null && liquid.canonical().getRenderingIcon() != null) {
			textureSheet = liquid.canonical().getTextureSheet();
			liquidIcon = liquid.canonical().getRenderingIcon();
		} else {
			if (liquid.itemID < Block.blocksList.length && Block.blocksList[liquid.itemID].blockID > 0) {
				liquidIcon = Block.blocksList[liquid.itemID].getBlockTextureFromSide(0);
				textureSheet = "/terrain.png";
			} else {
				liquidIcon = Item.itemsList[liquid.itemID].getIconFromDamage(liquid.itemMeta);
				textureSheet = "/gui/items.png";
			}
		}
		mc.renderEngine.bindTexture(textureSheet);

		while (true) {
			int x = 0;

			if (squaled > 16) {
				x = 16;
				squaled -= 16;
			} else {
				x = squaled;
				squaled = 0;
			}

			drawTexturedModelRectFromIcon(j + col, k + line + 58 - x - start, liquidIcon, 16, 16 - (16 - x));
			start = start + 16;

			if (x == 0 || squaled == 0) {
				break;
			}
		}

		mc.renderEngine.bindTexture(DefaultProps.TEXTURE_PATH_GUI + "/combustion_engine_gui.png");
		drawTexturedModalRect(j + col, k + line, 176, 0, 16, 60);
	}

}
