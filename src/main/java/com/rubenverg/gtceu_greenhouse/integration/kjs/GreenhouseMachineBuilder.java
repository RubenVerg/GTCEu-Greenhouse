package com.rubenverg.gtceu_greenhouse.integration.kjs;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.rubenverg.gtceu_greenhouse.GreenhouseMachineRenderer;
import com.rubenverg.gtceu_greenhouse.SyncedProgressElectricMultiblockMachine;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class GreenhouseMachineBuilder extends MultiblockMachineBuilder {
	private Collection<Vector3f> offsets;
	private String baseModel;
	private String overlayModel;

	protected GreenhouseMachineBuilder(Registrate registrate, String name, BiFunction<BlockBehaviour.Properties, MultiblockMachineDefinition, IMachineBlock> blockFactory, BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory, TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory) {
		super(registrate, name, SyncedProgressElectricMultiblockMachine::new, blockFactory, itemFactory, blockEntityFactory);
	}

	public static GreenhouseMachineBuilder createGreenhouse(Registrate registrate, String name, BiFunction<BlockBehaviour.Properties, MultiblockMachineDefinition, IMachineBlock> blockFactory, BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory, TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory) {
		return new GreenhouseMachineBuilder(registrate, name, blockFactory, itemFactory, blockEntityFactory)
			.hasTESR(true);
	}

	@Override
	public GreenhouseMachineBuilder shapeInfo(Function<MultiblockMachineDefinition, MultiblockShapeInfo> shape) {
		return (GreenhouseMachineBuilder) super.shapeInfo(shape);
	}

	@Override
	public GreenhouseMachineBuilder shapeInfos(Function<MultiblockMachineDefinition, List<MultiblockShapeInfo>> shapes) {
		return (GreenhouseMachineBuilder) super.shapeInfos(shapes);
	}

	@Override
	public GreenhouseMachineBuilder recoveryItems(Supplier<ItemLike[]> items) {
		return (GreenhouseMachineBuilder) super.recoveryItems(items);
	}

	@Override
	public GreenhouseMachineBuilder recoveryStacks(Supplier<ItemStack[]> stacks) {
		return (GreenhouseMachineBuilder) super.recoveryStacks(stacks);
	}

	@Override
	public GreenhouseMachineBuilder shape(VoxelShape shape) {
		return (GreenhouseMachineBuilder) super.shape(shape);
	}

	@Override
	public GreenhouseMachineBuilder multiblockPreviewRenderer(boolean multiBlockWorldPreview, boolean multiBlockXEIPreview) {
		return (GreenhouseMachineBuilder) super.multiblockPreviewRenderer(multiBlockWorldPreview, multiBlockXEIPreview);
	}

	@Override
	public GreenhouseMachineBuilder rotationState(RotationState rotationState) {
		return (GreenhouseMachineBuilder) super.rotationState(rotationState);
	}

	@Override
	public GreenhouseMachineBuilder hasTESR(boolean hasTESR) {
		return (GreenhouseMachineBuilder) super.hasTESR(hasTESR);
	}

	@Override
	public GreenhouseMachineBuilder blockProp(NonNullUnaryOperator<BlockBehaviour.Properties> blockProp) {
		return (GreenhouseMachineBuilder) super.blockProp(blockProp);
	}

	@Override
	public GreenhouseMachineBuilder itemProp(NonNullUnaryOperator<Item.Properties> itemProp) {
		return (GreenhouseMachineBuilder) super.itemProp(itemProp);
	}

	@Override
	public GreenhouseMachineBuilder blockBuilder(Consumer<BlockBuilder<? extends Block, ?>> blockBuilder) {
		return (GreenhouseMachineBuilder) super.blockBuilder(blockBuilder);
	}

	@Override
	public GreenhouseMachineBuilder itemBuilder(Consumer<ItemBuilder<? extends MetaMachineItem, ?>> itemBuilder) {
		return (GreenhouseMachineBuilder) super.itemBuilder(itemBuilder);
	}

	@Override
	public GreenhouseMachineBuilder recipeTypes(GTRecipeType... recipeTypes) {
		return (GreenhouseMachineBuilder) super.recipeTypes(recipeTypes);
	}

	@Override
	public GreenhouseMachineBuilder recipeType(GTRecipeType recipeTypes) {
		return (GreenhouseMachineBuilder) super.recipeType(recipeTypes);
	}

	@Override
	public GreenhouseMachineBuilder tier(int tier) {
		return (GreenhouseMachineBuilder) super.tier(tier);
	}

	@Override
	public GreenhouseMachineBuilder recipeOutputLimits(Object2IntMap<RecipeCapability<?>> map) {
		return (GreenhouseMachineBuilder) super.recipeOutputLimits(map);
	}

	@Override
	public GreenhouseMachineBuilder addOutputLimit(RecipeCapability<?> capability, int limit) {
		return (GreenhouseMachineBuilder) super.addOutputLimit(capability, limit);
	}

	@Override
	public GreenhouseMachineBuilder itemColor(BiFunction<ItemStack, Integer, Integer> itemColor) {
		return (GreenhouseMachineBuilder) super.itemColor(itemColor);
	}

	@Override
	public GreenhouseMachineBuilder modelRenderer(Supplier<ResourceLocation> model) {
		return (GreenhouseMachineBuilder) super.modelRenderer(model);
	}

	@Override
	public GreenhouseMachineBuilder defaultModelRenderer() {
		return (GreenhouseMachineBuilder) super.defaultModelRenderer();
	}

	@Override
	public GreenhouseMachineBuilder tooltipBuilder(BiConsumer<ItemStack, List<Component>> tooltipBuilder) {
		return (GreenhouseMachineBuilder) super.tooltipBuilder(tooltipBuilder);
	}

	@Override
	public GreenhouseMachineBuilder appearance(Supplier<BlockState> state) {
		return (GreenhouseMachineBuilder) super.appearance(state);
	}

	@Override
	public GreenhouseMachineBuilder appearanceBlock(Supplier<? extends Block> block) {
		return (GreenhouseMachineBuilder) super.appearanceBlock(block);
	}

	@Override
	public GreenhouseMachineBuilder langValue(String langValue) {
		return (GreenhouseMachineBuilder) super.langValue(langValue);
	}

	@Override
	public GreenhouseMachineBuilder tooltips(Component... components) {
		return (GreenhouseMachineBuilder) super.tooltips(components);
	}

	@Override
	public GreenhouseMachineBuilder conditionalTooltip(Component component, boolean condition) {
		return (GreenhouseMachineBuilder) super.conditionalTooltip(component, condition);
	}

	@Override
	public GreenhouseMachineBuilder conditionalTooltip(Component component, Supplier<Boolean> condition) {
		return (GreenhouseMachineBuilder) super.conditionalTooltip(component, condition);
	}

	@Override
	public GreenhouseMachineBuilder abilities(PartAbility... abilities) {
		return (GreenhouseMachineBuilder) super.abilities(abilities);
	}

	@Override
	public GreenhouseMachineBuilder paintingColor(int paintingColor) {
		return (GreenhouseMachineBuilder) super.paintingColor(paintingColor);
	}

	@Override
	public GreenhouseMachineBuilder recipeModifier(RecipeModifier recipeModifier) {
		return (GreenhouseMachineBuilder) super.recipeModifier(recipeModifier);
	}

	@Override
	public GreenhouseMachineBuilder recipeModifier(RecipeModifier recipeModifier, boolean alwaysTryModifyRecipe) {
		return (GreenhouseMachineBuilder) super.recipeModifier(recipeModifier, alwaysTryModifyRecipe);
	}

	@Override
	public GreenhouseMachineBuilder recipeModifiers(RecipeModifier... recipeModifiers) {
		return (GreenhouseMachineBuilder) super.recipeModifiers(recipeModifiers);
	}

	@Override
	public GreenhouseMachineBuilder recipeModifiers(boolean alwaysTryModifyRecipe, RecipeModifier... recipeModifiers) {
		return (GreenhouseMachineBuilder) super.recipeModifiers(alwaysTryModifyRecipe, recipeModifiers);
	}

	@Override
	public GreenhouseMachineBuilder noRecipeModifier() {
		return (GreenhouseMachineBuilder) super.noRecipeModifier();
	}

	@Override
	public GreenhouseMachineBuilder alwaysTryModifyRecipe(boolean alwaysTryModifyRecipe) {
		return (GreenhouseMachineBuilder) super.alwaysTryModifyRecipe(alwaysTryModifyRecipe);
	}

	@Override
	public GreenhouseMachineBuilder beforeWorking(BiPredicate<IRecipeLogicMachine, GTRecipe> beforeWorking) {
		return (GreenhouseMachineBuilder) super.beforeWorking(beforeWorking);
	}

	@Override
	public GreenhouseMachineBuilder onWorking(Predicate<IRecipeLogicMachine> onWorking) {
		return (GreenhouseMachineBuilder) super.onWorking(onWorking);
	}

	@Override
	public GreenhouseMachineBuilder onWaiting(Consumer<IRecipeLogicMachine> onWaiting) {
		return (GreenhouseMachineBuilder) super.onWaiting(onWaiting);
	}

	@Override
	public GreenhouseMachineBuilder afterWorking(Consumer<IRecipeLogicMachine> afterWorking) {
		return (GreenhouseMachineBuilder) super.afterWorking(afterWorking);
	}

	@Override
	public GreenhouseMachineBuilder regressWhenWaiting(boolean dampingWhenWaiting) {
		return (GreenhouseMachineBuilder) super.regressWhenWaiting(dampingWhenWaiting);
	}

	@Override
	public GreenhouseMachineBuilder editableUI(@Nullable EditableMachineUI editableUI) {
		return (GreenhouseMachineBuilder) super.editableUI(editableUI);
	}

	@Override
	public GreenhouseMachineBuilder onBlockEntityRegister(NonNullConsumer<BlockEntityType<BlockEntity>> onBlockEntityRegister) {
		return (GreenhouseMachineBuilder) super.onBlockEntityRegister(onBlockEntityRegister);
	}

	@Override
	public GreenhouseMachineBuilder generator(boolean generator) {
		return (GreenhouseMachineBuilder) super.generator(generator);
	}

	@Override
	public GreenhouseMachineBuilder pattern(Function<MultiblockMachineDefinition, BlockPattern> pattern) {
		return (GreenhouseMachineBuilder) super.pattern(pattern);
	}

	@Override
	public GreenhouseMachineBuilder allowExtendedFacing(boolean allowExtendedFacing) {
		return (GreenhouseMachineBuilder) super.allowExtendedFacing(allowExtendedFacing);
	}

	@Override
	public GreenhouseMachineBuilder allowFlip(boolean allowFlip) {
		return (GreenhouseMachineBuilder) super.allowFlip(allowFlip);
	}

	@Override
	public GreenhouseMachineBuilder partSorter(Comparator<IMultiPart> partSorter) {
		return (GreenhouseMachineBuilder) super.partSorter(partSorter);
	}

	@Override
	public GreenhouseMachineBuilder partAppearance(TriFunction<IMultiController, IMultiPart, Direction, BlockState> partAppearance) {
		return (GreenhouseMachineBuilder) super.partAppearance(partAppearance);
	}

	@Override
	public GreenhouseMachineBuilder additionalDisplay(BiConsumer<IMultiController, List<Component>> additionalDisplay) {
		return (GreenhouseMachineBuilder) super.additionalDisplay(additionalDisplay);
	}

	public GreenhouseMachineBuilder textures(String base, String overlay) {
		this.baseModel = base;
		this.overlayModel = overlay;
		return this;
	}

	public GreenhouseMachineBuilder offsets(float[]... ofs) {
		this.offsets = Stream.of(ofs).map(Vector3f::new).toList();
		return this;
	}

	@Override
	@HideFromJS
	public MultiblockMachineDefinition register() {
		if (Objects.isNull(offsets)) {
			throw new RuntimeException("Greenhouse definition must specify offsets!");
		}
		if (Objects.isNull(baseModel) || Objects.isNull(overlayModel)) {
			throw new RuntimeException("Greenhouse definition must specify textures!");
		}
		this.renderer(() -> new GreenhouseMachineRenderer(ResourceLocation.parse(this.baseModel), ResourceLocation.parse(this.overlayModel), this.offsets));
		return super.register();
	}
}
