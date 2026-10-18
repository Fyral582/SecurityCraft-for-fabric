package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCMenuProviderExtension;
import net.minecraft.world.MenuProvider;

@Mixin(MenuProvider.class)
public interface MenuProviderExtensionImplMixin extends SCMenuProviderExtension {}
