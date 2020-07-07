package com.stal111.valhelsia_structures.world.structures;

import com.mojang.serialization.Codec;
import com.stal111.valhelsia_structures.config.StructureGenConfig;
import com.stal111.valhelsia_structures.utils.StructureUtils;
import com.stal111.valhelsia_structures.world.structures.pieces.ForgePieces;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.MarginedStructureStart;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.TemplateManager;

import javax.annotation.Nonnull;

public class ForgeStructure extends AbstractValhelsiaStructure {
    public static final String SHORT_NAME = "forge";

    public ForgeStructure(Codec<NoFeatureConfig> noFeatureConfigCodec) {
        super(noFeatureConfigCodec, SHORT_NAME, 2);
    }

    @Override
    public int getSeparation() {
        return StructureGenConfig.FORGE_SEPARATION.get();
    }

    @Override
    public int getDistance() {
        return StructureGenConfig.FORGE_DISTANCE.get();
    }

    @Override
    public int getSeedModifier() {
        return 12857691;
    }

    @Override
    public double getSpawnChance() {
        return StructureGenConfig.FORGE_SPAWN_CHANCE.get();
    }

    @Override
    @Nonnull
    public IStartFactory getStartFactory() {
        return Start::new;
    }

    public static class Start extends MarginedStructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox bounds, int reference, long seed) {
            super(structure, chunkX, chunkZ, bounds, reference, seed);
        }

        @Override
        public void func_230364_a_(ChunkGenerator generator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig noFeatureConfig) {
            Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
            BlockPos position = StructureUtils.getSurfaceStructurePosition(generator, 2, rotation, chunkX, chunkZ);
            ForgePieces.generate(generator, templateManager, position, this.components, this.rand);
            this.recalculateStructureSize();
        }
    }
}
