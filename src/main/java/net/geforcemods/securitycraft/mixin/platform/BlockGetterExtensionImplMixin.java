package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCBlockGetterExtension;
import net.minecraft.world.level.BlockGetter;

@Mixin(BlockGetter.class)
public interface BlockGetterExtensionImplMixin extends SCBlockGetterExtension {}
