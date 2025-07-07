package com.rubenverg.gtceu_greenhouse;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Vector3f;

import java.util.*;

public class GreenhouseMachineRenderer extends WorkableCasingMachineRenderer {
	private static final Map<BlockState, BakedModel> blockModelCache = new HashMap<>();

	private final Collection<Vector3f> offsets;

	public GreenhouseMachineRenderer(ResourceLocation baseCasing, ResourceLocation workableModel, Collection<Vector3f> offsets) {
		super(baseCasing, workableModel);
		this.offsets = offsets;
	}

	public Collection<Vector3f> getOffsets() {
		return offsets.stream().map(vec -> {
			try {
				return (Vector3f)vec.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}).toList();
	}

	@Override
	public int getViewDistance() {
		return 32;
	}

	@Override
	public boolean isGlobalRenderer(BlockEntity blockEntity) {
		return true;
	}

	@Override
	public boolean hasTESR(BlockEntity blockEntity) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		super.render(blockEntity, partialTicks, stack, buffer, combinedLight, combinedOverlay);

		if (blockEntity instanceof MetaMachineBlockEntity mm) {
			if (mm.metaMachine instanceof WorkableMultiblockMachine multi) {
				if (multi.recipeLogic.isActive()) {
					final var recipe = Objects.requireNonNull(multi.recipeLogic.getLastRecipe());
					var recipeProgress = 1.0;
					if (multi instanceof ISyncedProgress sp) {
						recipeProgress = sp.getProgressPercent();
					}
					final var inputs = recipe.getInputContents(ItemRecipeCapability.CAP);
					if (!inputs.isEmpty()) {
						final var firstInput = inputs.get(0);
						if (((Ingredient) firstInput.content).getItems()[0].getItem() instanceof BlockItem bi) {
							final var mode = growthModeForBlock(bi.getBlock());
							var blockState = bi.getBlock().defaultBlockState();
							if (mode == GrowthMode.AGE_7) {
								blockState = blockState.trySetValue(BlockStateProperties.AGE_7, (int) (recipeProgress * (8 - 1e-25)));
							} else if (mode == GrowthMode.AGE_3) {
								blockState = blockState.trySetValue(BlockStateProperties.AGE_3, (int) (recipeProgress * (4 - 1e-25)));
							} else if (mode == GrowthMode.TALL_FLOWER && recipeProgress < 0.5) {
								blockState = blockState.trySetValue(DoublePlantBlock.HALF, DoublePlantBlock.HALF.getValue("upper").orElseThrow());
							} else if (mode == GrowthMode.GROWING_PLANT) {
								if (recipeProgress < 0.5) {
									blockState = ((GrowingPlantBlock) blockState.getBlock()).getHeadBlock().defaultBlockState();
									blockState = blockState.trySetValue(BlockStateProperties.AGE_25, (int) (recipeProgress * (25 - 1e-25)));
								} else {
									blockState = ((GrowingPlantBlock) blockState.getBlock()).getBodyBlock().defaultBlockState();
								}
								if (recipeProgress % 0.5 >= 0.25 && blockState.getBlock() instanceof CaveVines) {
									blockState = blockState.trySetValue(CaveVines.BERRIES, true);
								}
							}
							final var renderType = RenderTypeHelper.getEntityRenderType(ItemBlockRenderTypes.getRenderType(blockState, false), false);
							final var consumer = buffer.getBuffer(renderType);
							final var rotation = multi.getFrontFacing().getRotation();
							for (final var vec : getOffsets()) {
								final var rotated = vec.rotate(rotation);
								stack.pushPose();
								stack.translate(rotated.x, rotated.y, rotated.z);
								stack.translate(0.0, 1e-25, 0.0);
								if (mode == GrowthMode.TRANSLATE) {
									stack.translate(0.0, recipeProgress - 1, 0.0);
								} else if (mode == GrowthMode.TALL_FLOWER || mode == GrowthMode.DOUBLE_TRANSLATE || mode == GrowthMode.GROWING_PLANT) {
									stack.translate(0.0, (recipeProgress * 2) % (1 + 1e-25) - 1, 0.0);
								} else if (mode == GrowthMode.SCALE) {
									stack.last().pose().scaleAround((float) recipeProgress, 0.5f, 0.0f, 0.5f);
								}
								if (mode == GrowthMode.GROWING_PLANT && blockState.getBlock() instanceof GrowingPlantBlock gp) {
									stack.last().pose().rotateAround(gp.growthDirection.getRotation(), 0.5f, 0.5f, 0.5f);
								}
								renderBlock(renderType, consumer, blockState, stack.last());
								stack.popPose();
							}
							if ((mode == GrowthMode.TALL_FLOWER || mode == GrowthMode.DOUBLE_TRANSLATE || mode == GrowthMode.GROWING_PLANT) && recipeProgress > 0.5) {
								var upperState = blockState;
								if (mode == GrowthMode.TALL_FLOWER) {
									upperState = blockState.trySetValue(DoublePlantBlock.HALF, DoublePlantBlock.HALF.getValue("upper").orElseThrow());
								} else if (mode == GrowthMode.GROWING_PLANT) {
									upperState = ((GrowingPlantBlock) blockState.getBlock()).getHeadBlock().defaultBlockState();
									upperState = upperState.trySetValue(BlockStateProperties.AGE_25, (int) (recipeProgress * (25 - 1e-25)));
									if (blockState.getBlock() instanceof CaveVines) {
										upperState = upperState.trySetValue(CaveVines.BERRIES, true);
									}
								}
								for (final var vec : getOffsets()) {
									final var rotated = vec.rotate(rotation);
									stack.pushPose();
									stack.translate(rotated.x, rotated.y, rotated.z);
									stack.translate(0.0, 1.0, 0.0);
									stack.translate(0.0, 1e-25, 0.0);
									stack.translate(0.0, (recipeProgress * 2) % (1 + 1e-25) - 1, 0.0);
									if (mode == GrowthMode.GROWING_PLANT && blockState.getBlock() instanceof GrowingPlantBlock gp) {
										stack.last().pose().rotateAround(gp.growthDirection.getRotation(), 0.5f, 0.5f, 0.5f);
									}
									renderBlock(renderType, consumer, upperState, stack.last());
									stack.popPose();
								}
							}
						}
					}
				}
			}
		}
	}

	private void renderBlock(RenderType renderType, VertexConsumer consumer, BlockState state, PoseStack.Pose pose) {
		final BakedModel model = blockModelCache.computeIfAbsent(state, theBlockState -> {
			final var location = BlockModelShaper.stateToModelLocation(theBlockState);
			final var unbaked = ModelFactory.getUnBakedModel(location);
			return Objects.requireNonNull(unbaked.bake(ModelFactory.getModeBaker(), Material::sprite, ModelFactory.getRotation(Direction.NORTH), location));
		});
		{
			final var quads = Objects.requireNonNull(model.getQuads(state, null, RandomSource.create(42), ModelData.EMPTY, renderType));
			for (final var quad : quads) {
				consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
			}
		}
		for (final var face : Direction.stream().toList()) {
			final var quads = Objects.requireNonNull(model.getQuads(state, face, RandomSource.create(42), ModelData.EMPTY, renderType));
			for (final var quad : quads) {
				consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
			}
		}
	}

	protected GrowthMode growthModeForBlock(Block block) {
		if (block instanceof CropBlock || block instanceof StemBlock) return GrowthMode.AGE_7;
		if (block instanceof DoublePlantBlock) return GrowthMode.TALL_FLOWER;
		if (block instanceof FlowerBlock || block instanceof MangrovePropaguleBlock) return GrowthMode.TRANSLATE;
		if (block instanceof SaplingBlock) return GrowthMode.SCALE;
		if (block instanceof SugarCaneBlock || block instanceof CactusBlock) return GrowthMode.DOUBLE_TRANSLATE;
		if (block instanceof SweetBerryBushBlock) return GrowthMode.AGE_3;
		if (block instanceof GrowingPlantBlock) return GrowthMode.GROWING_PLANT;
		return GrowthMode.NONE;
	}
}
