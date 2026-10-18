package net.geforcemods.securitycraft.mixin.platform;

import org.spongepowered.asm.mixin.Mixin;

import net.geforcemods.securitycraft.platform.ext.SCBlockEntityExtension;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public abstract class BlockEntityExtensionImplMixin implements SCBlockEntityExtension {}
