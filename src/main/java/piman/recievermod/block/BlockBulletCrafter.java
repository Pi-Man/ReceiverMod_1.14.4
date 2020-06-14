package piman.recievermod.block;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import piman.recievermod.tileentity.TileEntityBulletCrafter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBulletCrafter extends ContainerBlock {
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public BlockBulletCrafter(Block.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ACTIVE, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, ACTIVE);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	public static void setState(boolean crafting, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		state = state.with(ACTIVE, crafting);
		world.setBlockState(pos, state);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos, this.getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()));
	}

	@Override
	public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) worldIn.getTileEntity(pos));
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

//	@Override
//	public int getMetaFromState(IBlockState state) {
//		return state.getValue(FACING).getHorizontalIndex() + (state.getValue(ACTIVE) ? 0 : 4);
//	}
//
//	@Override
//	public IBlockState getStateFromMeta(int meta) {
//		return this.getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta%4]).withProperty(ACTIVE, meta/4 > 1 ? true : false);
//	}

//	@Override
//	public IBlockState withRotation(IBlockState state, Rotation rot) {
//		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
//	}
//
//	@Override
//	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
//		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
//	}


	@Nonnull
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isSolid(BlockState state) {
		return false;
	}

//	@Override
//	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
//		return new VoxelShape();
//	}
//
//	@Override
//	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
//		return new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
//	}


	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		if (!worldIn.isRemote) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityBulletCrafter) {
				player.openContainer((INamedContainerProvider) tileEntity);
			}
		}

		return true;

	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
		return new TileEntityBulletCrafter();
	}
}
