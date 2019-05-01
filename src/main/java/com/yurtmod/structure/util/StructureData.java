package com.yurtmod.structure.util;

import com.yurtmod.block.BlockTentDoorHGM;
import com.yurtmod.block.BlockTentDoorSML;
import com.yurtmod.block.TileEntityTentDoor;
import com.yurtmod.init.Content;
import com.yurtmod.init.TentConfig;
import com.yurtmod.item.ItemTent;
import com.yurtmod.structure.StructureBase;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StructureData implements net.minecraftforge.common.util.INBTSerializable<NBTTagCompound> {
	
	////// String keys for NBT //////
	public static final String KEY_TENT_CUR = "StructureTentType";
	public static final String KEY_WIDTH_PREV = "StructureWidthPrevious";
	public static final String KEY_WIDTH_CUR = "StructureWidthCurrent";
	public static final String KEY_DEPTH_PREV = "StructureDepthPrevious";
	public static final String KEY_DEPTH_CUR = "StructureDepthCurrent";
	public static final String KEY_OFFSET_X = "StructureOffsetX";
	public static final String KEY_OFFSET_Z = "StructureOffsetZ";
	
	////// Important fields and default values //////
	private StructureTent tent = StructureTent.getById((short)0);
	private StructureWidth width = StructureWidth.getById((short)0);
	private StructureDepth depth = StructureDepth.getById((short)0);
	private StructureWidth prevWidth = StructureWidth.getById((short)0);
	private StructureDepth prevDepth = StructureDepth.getById((short)0);
	private int offsetX = ItemTent.ERROR_TAG;
	private int offsetZ = ItemTent.ERROR_TAG;
	
	public StructureData() {
		// empty constructor (uses defaults)
	}
	
	public StructureData(final NBTTagCompound nbt) {
		this();
		if(nbt != null) {
			this.deserializeNBT(nbt);
		}
	}
	
	public StructureData(final ItemStack tentStack) {
		this(tentStack.getOrCreateSubCompound(ItemTent.TENT_DATA));
	}
	
	public StructureData setBoth(final StructureTent tentIn, final StructureWidth widthIn, final StructureDepth depthIn) {
		this.setCurrent(tentIn, widthIn, depthIn);
		this.setPrev(widthIn, depthIn);
		return this;
	}
	
	public StructureData setCurrent(final StructureTent tentCur, final StructureWidth widthCur, final StructureDepth depthCur) {
		this.tent = tentCur;
		this.width = widthCur;
		this.depth = depthCur;
		return this;
	}
	
	public StructureData setPrev(final StructureWidth widthPrev, final StructureDepth depthPrev) {
		this.prevWidth = widthPrev;
		this.prevDepth = depthPrev;
		return this;
	}

	public StructureData setOffsets(final int x, final int z) {
		this.offsetX = x;
		this.offsetZ = z;
		return this;
	}
	
	public StructureData prevData() {
		return new StructureData()
				.setCurrent(tent, prevWidth, prevDepth)
				.setPrev(prevWidth, prevDepth)
				.setOffsets(offsetX, offsetZ);
	}
	
	public StructureData copy() {
		return new StructureData()
				.setCurrent(tent, width, depth)
				.setPrev(prevWidth, prevDepth)
				.setOffsets(offsetX, offsetZ);
	}
	
	//////////////////////////////////
	////// GETTERS AND SETTERS ///////
	//////////////////////////////////

	public StructureTent getTent() {
		return this.tent;
	}

	public StructureWidth getWidth() {
		return this.width;
	}
	
	public StructureDepth getDepth() {
		return this.depth;
	}
	
	public StructureWidth getPrevWidth() {
		return this.prevWidth;
	}
	
	public StructureDepth getPrevDepth() {
		return this.prevDepth;
	}
	
	public int getOffsetX() {
		return this.offsetX;
	}
	
	public int getOffsetZ() {
		return this.offsetZ;
	}
	
	public void setTent(final StructureTent tentIn) {
		this.tent = tentIn;
	}

	public void setWidth(final StructureWidth widthIn) {
		this.width = widthIn;
	}
	
	public void setDepth(final StructureDepth depthIn) {
		this.depth = depthIn;
	}

	public void setPrevWidth(final StructureWidth widthIn) {
		this.prevWidth = widthIn;
	}
	
	public void setPrevDepth(final StructureDepth depthIn) {
		this.prevDepth = depthIn;
	}
	
	public void setOffsetX(final int toSet) {
		this.offsetX = toSet;
	}
	
	public void setOffsetZ(final int toSet) {
		this.offsetZ = toSet;
	}
	
	//////////////////////////////////
	//////// STRUCTURE BLOCKS ////////
	//////////////////////////////////
	
	/** @return the Tent Door instance used for this structure **/
	public IBlockState getDoorBlock() {
		final boolean xl = this.getWidth().isXL();
		final Block block = getDoorBlockRaw(xl);
		final PropertyEnum sizeEnum = xl ? BlockTentDoorHGM.SIZE :  BlockTentDoorSML.SIZE;
		return block.getDefaultState().withProperty(sizeEnum, this.getWidth());
	}
	
	private Block getDoorBlockRaw(boolean isXL) {
		switch (this.getTent()) {
		case YURT:		return isXL ? Content.YURT_DOOR_HGM : Content.YURT_DOOR_SML;
		case TEPEE:		return isXL ? Content.TEPEE_DOOR_HGM : Content.TEPEE_DOOR_SML;
		case BEDOUIN:	return isXL ? Content.BEDOUIN_DOOR_HGM : Content.BEDOUIN_DOOR_SML;
		case INDLU:		return isXL ? Content.INDLU_DOOR_HGM : Content.INDLU_DOOR_SML;
		}
		return Content.YURT_DOOR_SML;
	}
	
	/** @return the specific Roof block for this tent type **/
	public IBlockState getRoofBlock(final int dimID) {
		return this.tent.getRoofBlock(dimID);
	}

	/** @return the specific Frame for this structure type. May be different for walls and roofs **/
	public IBlockState getFrameBlock(final boolean isRoof) {
		return this.tent.getFrameBlock(isRoof);
	}
	
	/** @return the main building block for this tent type. May be different inside tent. **/
	public IBlockState getWallBlock(final int dimID) {
		return this.tent.getWallBlock(dimID);
	}

	///////////////////////////////
	/////// OTHER HELPFUL /////////
	///////////////////////////////
	
	public void resetPrevData() {
		this.prevWidth = this.width;
		this.prevDepth = this.depth;
	}
	
	public boolean needsUpdate() {
		return  this.depth != this.prevDepth ||
				this.width != this.prevWidth;
	}
	
	public static void applyToTileEntity(final EntityPlayer player, final ItemStack stack, final TileEntityTentDoor te) {
		if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(ItemTent.TENT_DATA)) {
			System.out.println("[StructureType] ItemStack did not have any NBT information to pass to the TileEntity!");
			te.getWorld().removeTileEntity(te.getPos());
			return;
		}
		
		te.setTentData(new StructureData(stack));
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
		te.setPrevFacing(player.rotationYaw);
		if(TentConfig.general.OWNER_ENTRANCE || TentConfig.general.OWNER_PICKUP) {
			te.setOwner(EntityPlayer.getOfflineUUID(player.getName()));
		}
	}

	/** @return an NBT-tagged ItemStack based on the passed values **/
	public ItemStack getDropStack() {
		ItemStack stack = new ItemStack(Content.ITEM_TENT, 1);
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setTag(ItemTent.TENT_DATA, this.serializeNBT());
		return stack;
	}

	/** Note: the StructureBase only contains a COPY of this StructureData **/
	public StructureBase makePrevStructure() {
		return this.tent.makeStructure(this.prevData().copy());
	}
	
	/** Note: the StructureBase only contains a COPY of this StructureData **/
	public StructureBase makeStructure() {
		return this.tent.makeStructure(this.copy());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// only write if non-null
		if(this.tent != null) {
			// 'Current' values
			nbt.setShort(KEY_TENT_CUR, this.tent.getId());
			nbt.setShort(KEY_WIDTH_CUR, this.width.getId());
			nbt.setShort(KEY_DEPTH_CUR, this.depth.getId());
			// 'Previous' values
			nbt.setShort(KEY_WIDTH_PREV, this.prevWidth.getId());
			nbt.setShort(KEY_DEPTH_PREV, this.prevDepth.getId());
			// Offsets (tent location)
			nbt.setInteger(KEY_OFFSET_X, this.offsetX);
			nbt.setInteger(KEY_OFFSET_Z, this.offsetZ);
		}
		
		return nbt;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound nbt) {
		this.tent = StructureTent.getById(nbt.getShort(KEY_TENT_CUR));
		this.width = StructureWidth.getById(nbt.getShort(KEY_WIDTH_CUR));
		this.depth = StructureDepth.getById(nbt.getShort(KEY_DEPTH_CUR));
		this.prevWidth = StructureWidth.getById(nbt.getShort(KEY_WIDTH_PREV));
		this.prevDepth = StructureDepth.getById(nbt.getShort(KEY_DEPTH_PREV));
		this.offsetX = nbt.getInteger(KEY_OFFSET_X);
		this.offsetZ = nbt.getInteger(KEY_OFFSET_Z);
	}
	
	@Override
	public String toString() {
		return "StructureData: [TENT = " + tent.getName() + "; WIDTH = " 
				+ width.getName() + "; DEPTH = " + depth.getName() + 
				"; PREV_WIDTH = " + prevWidth.getName() + "; PREV_DEPTH = " 
				+ prevDepth.getName() + "; OFFSET_X = " + offsetX
				+ "; OFFSET_Z = " + offsetZ + "]";
	}
}
