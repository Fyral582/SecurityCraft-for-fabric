package net.geforcemods.securitycraft.platform.event;

import org.jetbrains.annotations.Nullable;

import net.geforcemods.securitycraft.SCEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Shared logic of the mixins into ServerPlayerGameMode and MultiPlayerGameMode that recreate NeoForge's block interaction
 * hooks (PlayerInteractEvent.RightClickBlock, IItemExtension#onItemUseFirst and UseItemOnBlockEvent)
 */
public final class InteractionHooks {
	private static final ThreadLocal<PlayerInteractEvent.RightClickBlock> CURRENT_EVENT = new ThreadLocal<>();

	private InteractionHooks() {}

	/**
	 * Called at the start of the interaction
	 *
	 * @return The result the interaction should return immediately, or null if the interaction should continue
	 */
	@Nullable
	public static InteractionResult onStart(Player player, InteractionHand hand, BlockHitResult hitResult) {
		PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, hitResult.getBlockPos(), hitResult);

		SCEventHandler.onRightClickBlockEvent(event);

		if (event.isCanceled())
			return event.getCancellationResult();

		CURRENT_EVENT.set(event);

		if (!player.isSpectator() && event.getUseItem() != TriState.FALSE) {
			ItemStack stack = player.getItemInHand(hand);
			InteractionResult result = stack.getItem().onItemUseFirst(stack, new UseOnContext(player, hand, hitResult));

			if (result != InteractionResult.PASS) {
				CURRENT_EVENT.remove();
				return result;
			}
		}

		return null;
	}

	public static void onEnd() {
		CURRENT_EVENT.remove();
	}

	/**
	 * Modifies whether the block interaction is skipped
	 */
	public static boolean shouldSkipBlock(boolean original) {
		PlayerInteractEvent.RightClickBlock event = CURRENT_EVENT.get();

		if (event == null)
			return original;
		else if (event.getUseBlock() == TriState.TRUE)
			return false;
		else if (event.getUseBlock() == TriState.FALSE)
			return true;

		return original;
	}

	/**
	 * Called instead of using the item on the block
	 *
	 * @return The result to return instead of using the item, or null if the item should be used normally
	 */
	@Nullable
	public static InteractionResult beforeItemUse(UseOnContext context) {
		PlayerInteractEvent.RightClickBlock event = CURRENT_EVENT.get();

		if (event != null && event.getUseItem() == TriState.FALSE)
			return InteractionResult.PASS;

		Player player = context.getPlayer();

		if (player != null) {
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos();

			return SCEventHandler.onUseItemOnBlock(player, level, pos, context.getItemInHand());
		}

		return null;
	}
}
