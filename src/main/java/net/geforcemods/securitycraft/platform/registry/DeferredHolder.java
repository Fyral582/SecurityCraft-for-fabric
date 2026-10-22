package net.geforcemods.securitycraft.platform.registry;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

/**
 * Fabric replacement for NeoForge's DeferredHolder. Wraps a registry entry that is registered by a {@link DeferredRegister} and
 * resolves lazily against the vanilla registry.
 */
public class DeferredHolder<R, T extends R> implements Holder<R>, Supplier<T> {
	protected final ResourceKey<R> key;
	private Holder<R> holder;
	private T directValue;

	protected DeferredHolder(ResourceKey<R> key) {
		this.key = Objects.requireNonNull(key);
	}

	public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
		return create(ResourceKey.create(registryKey, valueName));
	}

	public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<R> key) {
		return new DeferredHolder<>(key);
	}

	/**
	 * Used for registries that are not backed by a vanilla {@link Registry} (e.g. entity data serializers)
	 */
	void bindDirect(T value) {
		directValue = value;
	}

	@SuppressWarnings("unchecked")
	void bind(Holder<R> holder) {
		this.holder = holder;
		directValue = (T) holder.value();
	}

	@SuppressWarnings("unchecked")
	private Holder<R> getDelegateOrThrow() {
		if (holder == null) {
			Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.getValue(key.registry());

			if (registry != null)
				holder = registry.get(key).orElse(null);

			if (holder == null)
				throw new IllegalStateException("Trying to access unbound value: " + key);
		}

		return holder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T value() {
		if (directValue != null)
			return directValue;

		return (T) getDelegateOrThrow().value();
	}

	@Override
	public T get() {
		return value();
	}

	public Optional<T> asOptional() {
		return isBound() ? Optional.of(get()) : Optional.empty();
	}

	public ResourceLocation getId() {
		return key.location();
	}

	public ResourceKey<R> getKey() {
		return key;
	}

	public Holder<R> getDelegate() {
		return getDelegateOrThrow();
	}

	@Override
	public boolean isBound() {
		return directValue != null || (holder != null && holder.isBound());
	}

	@Override
	public boolean is(ResourceLocation id) {
		return id.equals(key.location());
	}

	@Override
	public boolean is(ResourceKey<R> key) {
		return key == this.key || this.key.equals(key);
	}

	@Override
	public boolean is(Predicate<ResourceKey<R>> filter) {
		return filter.test(key);
	}

	@Override
	public boolean is(TagKey<R> tag) {
		return getDelegateOrThrow().is(tag);
	}

	@Override
	public boolean is(Holder<R> holder) {
		return holder.is(key);
	}

	@Override
	public Stream<TagKey<R>> tags() {
		return getDelegateOrThrow().tags();
	}

	@Override
	public Either<ResourceKey<R>, R> unwrap() {
		return Either.left(key);
	}

	@Override
	public Optional<ResourceKey<R>> unwrapKey() {
		return Optional.of(key);
	}

	@Override
	public Kind kind() {
		return Kind.REFERENCE;
	}

	@Override
	public boolean canSerializeIn(HolderOwner<R> owner) {
		return getDelegateOrThrow().canSerializeIn(owner);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof Holder<?> h && h.kind() == Kind.REFERENCE && h.unwrapKey().map(key::equals).orElse(false));
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public String toString() {
		return "DeferredHolder{" + key + "}";
	}
}
