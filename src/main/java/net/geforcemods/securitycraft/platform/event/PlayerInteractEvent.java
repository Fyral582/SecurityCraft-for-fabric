package net.geforcemods.securitycraft.platform.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Minimal recreation of NeoForge's PlayerInteractEvent.RightClickBlock, fired by SecurityCraft's own mixins into
 * ServerPlayerGameMode and MultiPlayerGameMode.
 */
public final class PlayerInteractEvent {
	private PlayerInteractEvent() {}

	public static class RightClickBlock {
		private final Player player;
		private final InteractionHand hand;
		private final BlockPos pos;
		private final BlockHitResult hitVec;
		private TriState useBlock = TriState.DEFAULT;
		private TriState useItem = TriState.DEFAULT;
		private InteractionResult cancellationResult = InteractionResult.PASS;
		private boolean canceled;

		public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
			this.player = player;
			this.hand = hand;
			this.pos = pos;
			this.hitVec = hitVec;
		}

		public Player getEntity() {
			return player;
		}

		public InteractionHand getHand() {
			return hand;
		}

		public ItemStack getItemStack() {
			return player.getItemInHand(hand);
		}

		public BlockPos getPos() {
			return pos;
		}

		public Direction getFace() {
			return hitVec.getDirection();
		}

		public BlockHitResult getHitVec() {
			return hitVec;
		}

		public Level getLevel() {
			return player.level();
		}

		public TriState getUseBlock() {
			return useBlock;
		}

		public TriState getUseItem() {
			return useItem;
		}

		public void setUseBlock(TriState useBlock) {
			this.useBlock = useBlock;
		}

		public void setUseItem(TriState useItem) {
			this.useItem = useItem;
		}

		public InteractionResult getCancellationResult() {
			return cancellationResult;
		}

		public void setCancellationResult(InteractionResult result) {
			cancellationResult = result;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public void setCanceled(boolean canceled) {
			this.canceled = canceled;

			if (canceled) {
				useBlock = TriState.FALSE;
				useItem = TriState.FALSE;
			}
		}
	}
}
