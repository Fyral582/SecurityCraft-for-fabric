package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCBlockExtension;
import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public abstract class BlockExtensionImplMixin implements SCBlockExtension {}
