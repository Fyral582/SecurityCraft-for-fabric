package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCDataComponentHolderExtension;
import net.minecraft.core.component.DataComponentHolder;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderExtensionImplMixin extends SCDataComponentHolderExtension {}
