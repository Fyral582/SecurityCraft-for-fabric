package net.geforcemods.securitycraft.platform.nbt;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

/**
 * Backport of Minecraft 1.21.6's ValueInput to 1.21.4, implemented on top of {@link CompoundTag}. This allows the save/load
 * code of SecurityCraft to stay the same across versions.
 */
public final class ValueInput {
	private final CompoundTag tag;
	private final HolderLookup.Provider lookup;
	private final DynamicOps<Tag> ops;

	private ValueInput(CompoundTag tag, HolderLookup.Provider lookup) {
		this.tag = tag;
		this.lookup = lookup;
		ops = lookup == null ? NbtOps.INSTANCE : lookup.createSerializationContext(NbtOps.INSTANCE);
	}

	public static ValueInput of(CompoundTag tag, HolderLookup.Provider lookup) {
		return new ValueInput(tag == null ? new CompoundTag() : tag, lookup);
	}

	public CompoundTag tag() {
		return tag;
	}

	public HolderLookup.Provider lookup() {
		return lookup;
	}

	public <T> Optional<T> read(String key, Codec<T> codec) {
		Tag value = tag.get(key);

		if (value == null)
			return Optional.empty();

		return codec.parse(ops, value).resultOrPartial();
	}

	public <T> Optional<T> read(MapCodec<T> codec) {
		return codec.codec().parse(ops, tag).resultOrPartial();
	}

	public Optional<ValueInput> child(String key) {
		return tag.contains(key, Tag.TAG_COMPOUND) ? Optional.of(new ValueInput(tag.getCompound(key), lookup)) : Optional.empty();
	}

	public ValueInput childOrEmpty(String key) {
		return child(key).orElseGet(() -> new ValueInput(new CompoundTag(), lookup));
	}

	public Optional<ValueInputList> childrenList(String key) {
		if (!tag.contains(key, Tag.TAG_LIST))
			return Optional.empty();

		ListTag list = tag.getList(key, Tag.TAG_COMPOUND);

		return Optional.of(new ValueInputList(list.stream().map(t -> new ValueInput((CompoundTag) t, lookup)).toList()));
	}

	public ValueInputList childrenListOrEmpty(String key) {
		return childrenList(key).orElseGet(() -> new ValueInputList(List.of()));
	}

	public <T> Optional<TypedInputList<T>> list(String key, Codec<T> codec) {
		if (!tag.contains(key, Tag.TAG_LIST))
			return Optional.empty();

		ListTag list = (ListTag) tag.get(key);

		return Optional.of(new TypedInputList<>(list.stream().map(t -> codec.parse(ops, t).resultOrPartial()).flatMap(Optional::stream).toList()));
	}

	public <T> TypedInputList<T> listOrEmpty(String key, Codec<T> codec) {
		return list(key, codec).orElseGet(() -> new TypedInputList<>(List.of()));
	}

	public boolean contains(String key) {
		return tag.contains(key);
	}

	public boolean getBooleanOr(String key, boolean defaultValue) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getBoolean(key) : defaultValue;
	}

	public byte getByteOr(String key, byte defaultValue) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getByte(key) : defaultValue;
	}

	public int getShortOr(String key, short defaultValue) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getShort(key) : defaultValue;
	}

	public Optional<Integer> getInt(String key) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? Optional.of(tag.getInt(key)) : Optional.empty();
	}

	public int getIntOr(String key, int defaultValue) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getInt(key) : defaultValue;
	}

	public Optional<Long> getLong(String key) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? Optional.of(tag.getLong(key)) : Optional.empty();
	}

	public long getLongOr(String key, long defaultValue) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getLong(key) : defaultValue;
	}

	public float getFloatOr(String key, float defaultValue) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getFloat(key) : defaultValue;
	}

	public double getDoubleOr(String key, double defaultValue) {
		return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getDouble(key) : defaultValue;
	}

	public Optional<String> getString(String key) {
		return tag.contains(key, Tag.TAG_STRING) ? Optional.of(tag.getString(key)) : Optional.empty();
	}

	public String getStringOr(String key, String defaultValue) {
		return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : defaultValue;
	}

	public Optional<int[]> getIntArray(String key) {
		return tag.contains(key, Tag.TAG_INT_ARRAY) ? Optional.of(tag.getIntArray(key)) : Optional.empty();
	}

	public Dynamic<Tag> asDynamic() {
		return new Dynamic<>(ops, tag);
	}

	public static final class TypedInputList<T> implements Iterable<T> {
		private final List<T> list;

		private TypedInputList(List<T> list) {
			this.list = list;
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}

		public Stream<T> stream() {
			return list.stream();
		}

		@Override
		public Iterator<T> iterator() {
			return list.iterator();
		}
	}

	public static final class ValueInputList implements Iterable<ValueInput> {
		private final List<ValueInput> list;

		private ValueInputList(List<ValueInput> list) {
			this.list = list;
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}

		public Stream<ValueInput> stream() {
			return list.stream();
		}

		@Override
		public Iterator<ValueInput> iterator() {
			return list.iterator();
		}
	}
}
