package net.geforcemods.securitycraft.platform.nbt;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

/**
 * Backport of Minecraft 1.21.6's ValueOutput to 1.21.4, implemented on top of {@link CompoundTag}. This allows the save/load
 * code of SecurityCraft to stay the same across versions.
 */
public final class ValueOutput {
	private final CompoundTag tag;
	private final HolderLookup.Provider lookup;
	private final DynamicOps<Tag> ops;

	private ValueOutput(CompoundTag tag, HolderLookup.Provider lookup) {
		this.tag = tag;
		this.lookup = lookup;
		ops = lookup == null ? NbtOps.INSTANCE : lookup.createSerializationContext(NbtOps.INSTANCE);
	}

	public static ValueOutput of(CompoundTag tag, HolderLookup.Provider lookup) {
		return new ValueOutput(tag, lookup);
	}

	public CompoundTag tag() {
		return tag;
	}

	public HolderLookup.Provider lookup() {
		return lookup;
	}

	public <T> void store(String key, Codec<T> codec, T value) {
		codec.encodeStart(ops, value).resultOrPartial().ifPresent(t -> tag.put(key, t));
	}

	public <T> void storeNullable(String key, Codec<T> codec, @Nullable T value) {
		if (value != null)
			store(key, codec, value);
	}

	public <T> void store(MapCodec<T> codec, T value) {
		codec.codec().encodeStart(ops, value).resultOrPartial().ifPresent(t -> {
			if (t instanceof CompoundTag compound)
				tag.merge(compound);
		});
	}

	public void putBoolean(String key, boolean value) {
		tag.putBoolean(key, value);
	}

	public void putByte(String key, byte value) {
		tag.putByte(key, value);
	}

	public void putShort(String key, short value) {
		tag.putShort(key, value);
	}

	public void putInt(String key, int value) {
		tag.putInt(key, value);
	}

	public void putLong(String key, long value) {
		tag.putLong(key, value);
	}

	public void putFloat(String key, float value) {
		tag.putFloat(key, value);
	}

	public void putDouble(String key, double value) {
		tag.putDouble(key, value);
	}

	public void putString(String key, String value) {
		tag.putString(key, value);
	}

	public void putIntArray(String key, int[] value) {
		tag.putIntArray(key, value);
	}

	public ValueOutput child(String key) {
		CompoundTag child = new CompoundTag();

		tag.put(key, child);
		return new ValueOutput(child, lookup);
	}

	public ValueOutputList childrenList(String key) {
		ListTag list = new ListTag();

		tag.put(key, list);
		return new ValueOutputList(list, lookup);
	}

	public <T> TypedOutputList<T> list(String key, Codec<T> codec) {
		ListTag list = new ListTag();

		tag.put(key, list);
		return new TypedOutputList<>(list, codec, ops);
	}

	public void discard(String key) {
		tag.remove(key);
	}

	public boolean isEmpty() {
		return tag.isEmpty();
	}

	public static final class TypedOutputList<T> {
		private final ListTag list;
		private final Codec<T> codec;
		private final DynamicOps<Tag> ops;

		private TypedOutputList(ListTag list, Codec<T> codec, DynamicOps<Tag> ops) {
			this.list = list;
			this.codec = codec;
			this.ops = ops;
		}

		public void add(T value) {
			codec.encodeStart(ops, value).resultOrPartial().ifPresent(list::add);
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}
	}

	public static final class ValueOutputList {
		private final ListTag list;
		private final HolderLookup.Provider lookup;

		private ValueOutputList(ListTag list, HolderLookup.Provider lookup) {
			this.list = list;
			this.lookup = lookup;
		}

		public ValueOutput addChild() {
			CompoundTag child = new CompoundTag();

			list.add(child);
			return new ValueOutput(child, lookup);
		}

		public void discardLast() {
			list.removeLast();
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}
	}
}
