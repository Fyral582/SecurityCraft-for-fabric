package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ProjectorItem extends BlockItem {
	private static final MutableComponent TOOLTIP = Component.translatable("tooltip.securitycraft:projector").setStyle(Utils.GRAY_STYLE);

	public ProjectorItem(Item.Properties properties) {
		super(SCContent.PROJECTOR.get(), properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(TOOLTIP);
	}
}
