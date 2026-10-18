package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCBlockStateExtension;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockState.class)
public abstract class BlockStateExtensionImplMixin implements SCBlockStateExtension {}
