package net.geforcemods.securitycraft.platform.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Minimal recreation of NeoForge's EntityTeleportEvent, fired by SecurityCraft's own teleportation mixins
 */
public class EntityTeleportEvent {
	private final Entity entity;
	private final double prevX, prevY, prevZ;
	private double targetX, targetY, targetZ;
	private boolean canceled;

	public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
		this.entity = entity;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		prevX = entity.getX();
		prevY = entity.getY();
		prevZ = entity.getZ();
	}

	public Entity getEntity() {
		return entity;
	}

	public double getTargetX() {
		return targetX;
	}

	public double getTargetY() {
		return targetY;
	}

	public double getTargetZ() {
		return targetZ;
	}

	public Vec3 getTarget() {
		return new Vec3(targetX, targetY, targetZ);
	}

	public double getPrevX() {
		return prevX;
	}

	public double getPrevY() {
		return prevY;
	}

	public double getPrevZ() {
		return prevZ;
	}

	public Vec3 getPrev() {
		return new Vec3(prevX, prevY, prevZ);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public static class TeleportCommand extends EntityTeleportEvent {
		public TeleportCommand(Entity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
		}
	}

	public static class SpreadPlayersCommand extends EntityTeleportEvent {
		public SpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
		}
	}

	public static class EnderEntity extends EntityTeleportEvent {
		private final LivingEntity entityLiving;

		public EnderEntity(LivingEntity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
			entityLiving = entity;
		}

		public LivingEntity getEntityLiving() {
			return entityLiving;
		}
	}

	public static class EnderPearl extends EntityTeleportEvent {
		public EnderPearl(Entity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
		}
	}

	public static class ItemConsumption extends EntityTeleportEvent {
		public ItemConsumption(LivingEntity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
		}
	}
}
