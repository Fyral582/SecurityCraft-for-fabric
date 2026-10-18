package net.geforcemods.securitycraft.platform.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Fabric replacement for NeoForge's DeferredRegister. Entries are collected when the static fields are initialized and registered
 * to the vanilla registry when {@link #register()} is called during mod initialization.
 */
public class DeferredRegister<T> {
	/**
	 * Entity data serializers don't have a vanilla registry, so Fabric's tracked data registry is used for them
	 */
	public static final ResourceKey<Registry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("securitycraft", "entity_data_serializers"));
	private final ResourceKey<? extends Registry<T>> registryKey;
	private final String namespace;
	private final Map<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
	private final Collection<DeferredHolder<T, ? extends T>> entriesView = Collections.unmodifiableSet(entries.keySet());
	private boolean registered = false;

	protected DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		this.registryKey = registryKey;
		this.namespace = namespace;
	}

	public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> key, String namespace) {
		return new DeferredRegister<>(key, namespace);
	}

	public static Items createItems(String modid) {
		return new Items(modid);
	}

	public static Blocks createBlocks(String modid) {
		return new Blocks(modid);
	}

	public static DataComponents createDataComponents(ResourceKey<Registry<DataComponentType<?>>> registryKey, String modid) {
		return new DataComponents(registryKey, modid);
	}

	public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> sup) {
		return register(name, key -> sup.get());
	}

	@SuppressWarnings("unchecked")
	public <I extends T> DeferredHolder<T, I> register(String name, Function<Identifier, ? extends I> func) {
		if (registered)
			throw new IllegalStateException("Cannot register new entries to DeferredRegister after it has been registered");

		Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
		DeferredHolder<T, I> holder = createHolder(registryKey, id);

		if (entries.putIfAbsent(holder, () -> func.apply(id)) != null)
			throw new IllegalArgumentException("Duplicate registration " + name);

		return holder;
	}

	@SuppressWarnings("unchecked")
	protected <I extends T> DeferredHolder<T, I> createHolder(ResourceKey<? extends Registry<T>> registryKey, Identifier id) {
		return DeferredHolder.create(ResourceKey.create((ResourceKey<? extends Registry<T>>) registryKey, id));
	}

	public TagKey<T> createTagKey(String path) {
		return TagKey.create(registryKey, Identifier.fromNamespaceAndPath(namespace, path));
	}

	/**
	 * Registers all entries of this DeferredRegister to the respective registry, in the order they were added
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void register() {
		if (registered)
			return;

		registered = true;

		if (registryKey == ENTITY_DATA_SERIALIZERS) {
			for (Map.Entry<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entry : entries.entrySet()) {
				T value = entry.getValue().get();

				FabricTrackedDataRegistry.register(entry.getKey().getId(), (EntityDataSerializer<?>) value);
				((DeferredHolder) entry.getKey()).bindDirect(value);
			}

			return;
		}

		Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());

		if (registry == null)
			throw new IllegalStateException("Unknown registry " + registryKey);

		for (Map.Entry<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entry : entries.entrySet()) {
			DeferredHolder<T, ? extends T> holder = entry.getKey();
			T value = entry.getValue().get();
			Holder<T> registered;

			//the value may have already been registered by a Fabric API helper (e.g. ArgumentTypeRegistry)
			if (registry.getResourceKey(value).isPresent())
				registered = registry.wrapAsHolder(value);
			else
				registered = Registry.registerForHolder(registry, holder.getKey(), value);

			((DeferredHolder) holder).bind(registered);
		}
	}

	/**
	 * Kept for source compatibility with code that passed the NeoForge mod event bus
	 */
	public void register(Object ignored) {
		register();
	}

	public Collection<DeferredHolder<T, ? extends T>> getEntries() {
		return entriesView;
	}

	public ResourceKey<? extends Registry<T>> getRegistryKey() {
		return registryKey;
	}

	public String getNamespace() {
		return namespace;
	}

	public static class Blocks extends DeferredRegister<Block> {
		protected Blocks(String namespace) {
			super(Registries.BLOCK, namespace);
		}

		@Override
		public <B extends Block> DeferredBlock<B> register(String name, Function<Identifier, ? extends B> func) {
			return (DeferredBlock<B>) super.register(name, func);
		}

		@Override
		public <B extends Block> DeferredBlock<B> register(String name, Supplier<? extends B> sup) {
			return register(name, key -> sup.get());
		}

		public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
			return register(name, key -> func.apply(props.setId(ResourceKey.create(Registries.BLOCK, key))));
		}

		public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> props) {
			return register(name, key -> func.apply(props.get().setId(ResourceKey.create(Registries.BLOCK, key))));
		}

		public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, UnaryOperator<BlockBehaviour.Properties> props) {
			return register(name, key -> func.apply(props.apply(BlockBehaviour.Properties.of()).setId(ResourceKey.create(Registries.BLOCK, key))));
		}

		public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func) {
			return registerBlock(name, func, BlockBehaviour.Properties.of());
		}

		public DeferredBlock<Block> registerSimpleBlock(String name, BlockBehaviour.Properties props) {
			return registerBlock(name, Block::new, props);
		}

		public DeferredBlock<Block> registerSimpleBlock(String name, Supplier<BlockBehaviour.Properties> props) {
			return registerBlock(name, Block::new, props);
		}

		public DeferredBlock<Block> registerSimpleBlock(String name, UnaryOperator<BlockBehaviour.Properties> props) {
			return registerBlock(name, Block::new, props);
		}

		public DeferredBlock<Block> registerSimpleBlock(String name) {
			return registerSimpleBlock(name, BlockBehaviour.Properties.of());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected <I extends Block> DeferredHolder<Block, I> createHolder(ResourceKey<? extends Registry<Block>> registryKey, Identifier id) {
			return (DeferredHolder<Block, I>) DeferredBlock.createBlock(ResourceKey.create(Registries.BLOCK, id));
		}
	}

	public static class Items extends DeferredRegister<Item> {
		protected Items(String namespace) {
			super(Registries.ITEM, namespace);
		}

		@Override
		public <I extends Item> DeferredItem<I> register(String name, Function<Identifier, ? extends I> func) {
			return (DeferredItem<I>) super.register(name, func);
		}

		@Override
		public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> sup) {
			return register(name, key -> sup.get());
		}

		public DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, Item.Properties properties) {
			return register(name, key -> new BlockItem(block.get(), properties.setId(ResourceKey.create(Registries.ITEM, key)).useBlockDescriptionPrefix()));
		}

		public DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, UnaryOperator<Item.Properties> properties) {
			return register(name, key -> new BlockItem(block.get(), properties.apply(new Item.Properties()).setId(ResourceKey.create(Registries.ITEM, key)).useBlockDescriptionPrefix()));
		}

		public DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block) {
			return registerSimpleBlockItem(name, block, new Item.Properties());
		}

		public DeferredItem<BlockItem> registerSimpleBlockItem(DeferredHolder<Block, ?> block, Item.Properties properties) {
			return registerSimpleBlockItem(block.getId().getPath(), block, properties);
		}

		public DeferredItem<BlockItem> registerSimpleBlockItem(DeferredHolder<Block, ?> block, UnaryOperator<Item.Properties> properties) {
			return registerSimpleBlockItem(block.getId().getPath(), block, properties);
		}

		public DeferredItem<BlockItem> registerSimpleBlockItem(DeferredHolder<Block, ?> block) {
			return registerSimpleBlockItem(block, new Item.Properties());
		}

		public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Item.Properties props) {
			return register(name, key -> func.apply(props.setId(ResourceKey.create(Registries.ITEM, key))));
		}

		public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> props) {
			return register(name, key -> func.apply(props.get().setId(ResourceKey.create(Registries.ITEM, key))));
		}

		public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, UnaryOperator<Item.Properties> props) {
			return register(name, key -> func.apply(props.apply(new Item.Properties()).setId(ResourceKey.create(Registries.ITEM, key))));
		}

		public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func) {
			return registerItem(name, func, new Item.Properties());
		}

		public DeferredItem<Item> registerSimpleItem(String name, Item.Properties props) {
			return registerItem(name, Item::new, props);
		}

		public DeferredItem<Item> registerSimpleItem(String name, UnaryOperator<Item.Properties> props) {
			return registerItem(name, Item::new, props);
		}

		public DeferredItem<Item> registerSimpleItem(String name) {
			return registerItem(name, Item::new);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected <I extends Item> DeferredHolder<Item, I> createHolder(ResourceKey<? extends Registry<Item>> registryKey, Identifier id) {
			return (DeferredHolder<Item, I>) DeferredItem.createItem(ResourceKey.create(Registries.ITEM, id));
		}
	}

	public static class DataComponents extends DeferredRegister<DataComponentType<?>> {
		protected DataComponents(ResourceKey<Registry<DataComponentType<?>>> registryKey, String namespace) {
			super(registryKey, namespace);
		}

		public <D> DeferredHolder<DataComponentType<?>, DataComponentType<D>> registerComponentType(String name, UnaryOperator<DataComponentType.Builder<D>> builder) {
			return register(name, () -> builder.apply(DataComponentType.builder()).build());
		}
	}
}
