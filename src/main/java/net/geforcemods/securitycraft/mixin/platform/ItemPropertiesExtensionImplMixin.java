package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCItemPropertiesExtension;
import net.minecraft.world.item.Item;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesExtensionImplMixin implements SCItemPropertiesExtension {}
