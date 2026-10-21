package net.geforcemods.securitycraft.platform.fluid;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * Fabric replacement for NeoForge's BaseFlowingFluid. Fluid behavior that NeoForge defines through FluidTypes (swimming,
 * drowning etc.) is achieved through the vanilla water/lava fluid tags on Fabric.
 */
public abstract class BaseFlowingFluid extends FlowingFluid {
	private final Supplier<? extends Fluid> flowing;
	private final Supplier<? extends Fluid> still;
	private final Supplier<? extends Item> bucket;
	private final Supplier<? extends LiquidBlock> block;

	protected BaseFlowingFluid(Properties properties) {
		flowing = properties.flowing;
		still = properties.still;
		bucket = properties.bucket;
		block = properties.block;
	}

	@Override
	public Fluid getFlowing() {
		return flowing.get();
	}

	@Override
	public Fluid getSource() {
		return still.get();
	}

	@Override
	public Item getBucket() {
		return bucket == null ? net.minecraft.world.item.Items.AIR : bucket.get();
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == still.get() || fluid == flowing.get();
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !isSame(fluid);
	}

	/**
	 * Position aware version of {@link #canConvertToSource(ServerLevel)}
	 */
	public boolean canConvertToSource(FluidState state, ServerLevel level, BlockPos pos) {
		return canConvertToSource(level);
	}

	public static class Properties {
		private final Supplier<? extends Fluid> still;
		private final Supplier<? extends Fluid> flowing;
		private Supplier<? extends Item> bucket;
		private Supplier<? extends LiquidBlock> block;

		public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
			this.still = still;
			this.flowing = flowing;
		}

		public Properties bucket(Supplier<? extends Item> bucket) {
			this.bucket = bucket;
			return this;
		}

		public Properties block(Supplier<? extends LiquidBlock> block) {
			this.block = block;
			return this;
		}
	}
}
