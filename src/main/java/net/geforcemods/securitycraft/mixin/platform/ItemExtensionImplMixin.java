package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCItemExtension;
import net.minecraft.world.item.Item;

@Mixin(Item.class)
public abstract class ItemExtensionImplMixin implements SCItemExtension {}
