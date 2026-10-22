package net.geforcemods.securitycraft.platform.registry;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

/**
 * A direct holder set whose contents are resolved the first time they are needed. This allows referencing SecurityCraft's
 * registry entries in vanilla code that runs before SecurityCraft's content has been registered.
 */
public class LazyHolderSet<T> extends HolderSet.ListBacked<T> {
	private final Supplier<List<Holder<T>>> contents;

	public LazyHolderSet(Supplier<List<Holder<T>>> contents) {
		this.contents = Suppliers.memoize(contents::get);
	}

	@Override
	protected List<Holder<T>> contents() {
		return contents.get();
	}

	@Override
	public boolean isBound() {
		return true;
	}

	@Override
	public Either<TagKey<T>, List<Holder<T>>> unwrap() {
		return Either.right(contents());
	}

	@Override
	public Optional<TagKey<T>> unwrapKey() {
		return Optional.empty();
	}

	@Override
	public boolean contains(Holder<T> holder) {
		return contents().contains(holder);
	}

	@Override
	public String toString() {
		return "LazyDirectSet[" + contents() + "]";
	}
}
