package com.stal111.valhelsia_structures.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stal111.valhelsia_structures.core.init.ModRecipes;
import com.stal111.valhelsia_structures.utils.ModTags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Axe Crafting Recipe <br>
 * Valhelsia Structures - com.stal111.valhelsia_structures.common.recipe.AxeCraftingRecipe
 * <p>
 * A crafting recipe that can use any axe (that extends {@link AxeItem}) and a number of other ingredients in a shapeless
 * form. The axe loses one durability per craft but is returned.
 *
 * @author Valhelsia Team
 * @since 2020-06-01
 */
public class AxeCraftingRecipe extends CustomRecipe {

    private final Ingredient input;
    private final ItemStack output;
    private final int count;

    public AxeCraftingRecipe(Ingredient input, ItemStack output, CraftingBookCategory category) {
        super(category);
        this.input = input;
        this.output = output;
        this.count = output.getCount();
    }

    @Override
    public boolean matches(CraftingContainer inv, @Nonnull Level level) {
        int axeSlot = -1;
        ItemStack stack = null;

        for (int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack item = inv.getItem(slot);

            if (item.getItem() instanceof AxeItem && !item.is(ModTags.Items.AXE_CRAFTING_BLACKLISTED)) {
                axeSlot = slot;
                break;
            }
        }

        if (axeSlot == -1) {
            return false;
        }

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack1 = inv.getItem(i);
            if (i != axeSlot && !stack1.isEmpty()) {
                if (input.test(stack1) && (stack == null || ItemStack.isSameItem(stack, stack1))) {
                    stack = stack1;
                } else {
                    return false;
                }
            }
        }

        return stack != null;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv, @NotNull RegistryAccess registryAccess) {
        int logCount = 0;

        for(int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (input.test(stack)) {
                logCount++;
            }
        }

        return new ItemStack(output.getItem(), logCount * count);
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        int logCount = 0;

        for(int i = 0; i < itemStacks.size(); i++) {
            if (input.test(inv.getItem(i))) {
                logCount++;
            }
        }


        for(int i = 0; i < itemStacks.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof AxeItem) {
                ItemStack stack1 = stack.copy();
                stack1.setDamageValue(stack1.getDamageValue() + logCount);

                if (!(stack1.getDamageValue() >= stack1.getMaxDamage())) {
                    itemStacks.set(i, stack1);
                }
            }
        }

        return itemStacks;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.AXE_CRAFTING_SERIALIZER.get();
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public static class Serializer implements RecipeSerializer<AxeCraftingRecipe> {

        private static final Codec<AxeCraftingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> {
                    return recipe.input;
                }),
                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> {
                    return recipe.output;
                }),
                CraftingBookCategory.CODEC.fieldOf("category").forGetter(CustomRecipe::category)
        ).apply(instance, AxeCraftingRecipe::new));

        @Override
        public @NotNull Codec<AxeCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @org.jetbrains.annotations.Nullable AxeCraftingRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);

            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            return new AxeCraftingRecipe(input, output, category);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull AxeCraftingRecipe recipe) {
            buffer.writeEnum(recipe.category());
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }
    }
}
