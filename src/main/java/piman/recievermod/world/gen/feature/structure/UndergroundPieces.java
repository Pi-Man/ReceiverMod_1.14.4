package piman.recievermod.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.*;
import org.lwjgl.system.CallbackI;
import piman.recievermod.util.Reference;
import piman.recievermod.util.handlers.RegistryEventHandler;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;

public class UndergroundPieces {


    public static class TestPiece extends StructurePiece {

        public TestPiece(TemplateManager templateManager, int x, int y, int z) {
            super(RegistryEventHandler.UGTP, new CompoundNBT());
            this.boundingBox = new MutableBoundingBox(x, y, z, x+1, y+1, z+1);
        }

        public TestPiece(TemplateManager templateManager, CompoundNBT nbt) {
            super(RegistryEventHandler.UGTP, nbt);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         *
         * @param tagCompound
         */
        @Override
        protected void readAdditional(@Nonnull CompoundNBT tagCompound) {

        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(@Nonnull IWorld worldIn, @Nonnull Random randomIn, @Nonnull MutableBoundingBox structureBoundingBoxIn, @Nonnull ChunkPos chunkPosIn) {

            this.setBlockState(worldIn, Blocks.GLASS.getDefaultState(), 0, 0, 0, structureBoundingBoxIn);

            return true;
        }
    }

    public abstract static class AbstractPiece extends StructurePiece {

        protected BlockPos pos;
        protected Rotation rotation;

        protected Map<BlockPos, Direction> doorLocations = new LinkedHashMap<>();

        public AbstractPiece(IStructurePieceType type, TemplateManager templateManager, Random rand, int x, int y, int z) {
            super(type, 0);
            pos = new BlockPos(x, y, z);
            rotation = Rotation.NONE;
        }

        public AbstractPiece(IStructurePieceType type, CompoundNBT nbt) {
            super(type, nbt);
            pos = readBlockPos(nbt, "pos");
            rotation = Rotation.valueOf(nbt.getString("rotation"));
            int i1 = nbt.getInt("doorLocationsSize");
            for(int i = 0; i < i1; i++) {
                BlockPos location = readBlockPos(nbt, i + "location");
                Direction direction = Direction.byName(nbt.getString(i + "direction"));
                doorLocations.put(location, direction);
            }
        }

        protected void handleDataMarkers(IWorld worldIn, Random randomIn, Template template, BlockPos pos, PlacementSettings placeSettings){
            for(Template.BlockInfo template$blockinfo : template.func_215381_a(pos, placeSettings, Blocks.STRUCTURE_BLOCK)) {
                if (template$blockinfo.nbt != null) {
                    StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
                    if (structuremode == StructureMode.DATA) {
                        this.handleDataMarker(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, worldIn, randomIn, placeSettings.getBoundingBox());
                    }
                }
            }
        }

        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {

        }

        public static <T extends AbstractPiece> AbstractPiece fitPiece(TemplateManager manager, Random rand, int sizeX, int sizeZ, BlockPos center, boolean[][] mask, int tries, Factory<T> factory) {

            AbstractPiece piece = null;

            while (piece == null && tries-- > 0) {
                int i = rand.nextInt(sizeX) - sizeX/2;
                int j = rand.nextInt(sizeZ) - sizeZ/2;

                i = i / 5 * 5;
                j = j / 5 * 5;

                piece = factory.generate(manager, rand, center.getX() + i, center.getY(), center.getZ() + j);

                Label:
                for (int u = piece.boundingBox.minX; u < piece.boundingBox.maxX; u++) {
                    for (int v = piece.boundingBox.minZ; v < piece.boundingBox.maxZ; v++) {
                        int i1 = u - center.getX() + sizeX/2;
                        int j1 = v - center.getZ() + sizeZ/2;
                        if (i1 < 0 || j1 < 0 || i1 >= sizeX || j1 >= sizeZ || !mask[i1][j1]) {
                            piece = null;
                            break Label;
                        }
                    }
                }

                if (piece != null) {

                    Label:
                    for(BlockPos door : piece.doorLocations.keySet()) {

                        i = piece.pos.getX() + door.getX() - center.getX() + sizeX / 2;
                        j = piece.pos.getZ() + door.getZ() - center.getZ() + sizeZ / 2;

                        for (int u = 0; u < 5; u++) {
                            for (int v = -4; v < 1; v++) {
                                int i1 = u + i;
                                int j1 = v + j;
                                if (i1 < 0 || j1 < 0 || i1 >= sizeX || j1 >= sizeZ || door.getY() < 0 || !mask[i1][j1]) {
                                    piece = null;
                                    break Label;
                                }
                            }
                        }
                    }
                }
            }

            return piece;
        }

        protected BlockPos getDoorLocation(BlockPos pos, Rotation rotation) {
            switch (rotation) {
                case NONE:
                    return pos;
                case CLOCKWISE_90:
                    return new BlockPos(-pos.getZ(), pos.getY(), pos.getX() + 4);
                case CLOCKWISE_180:
                    return new BlockPos(-pos.getX() - 4, pos.getY(), -pos.getZ() + 4);
                case COUNTERCLOCKWISE_90:
                    return new BlockPos(pos.getZ() - 4, pos.getY(), -pos.getX());
            }
            return BlockPos.ZERO;
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         *
         * @param tagCompound
         */
        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            writeBlockPos(tagCompound, "pos", this.pos);
            tagCompound.putString("rotation", rotation.name());
            int i1 = doorLocations.size();
            tagCompound.putInt("doorLocationsSize", doorLocations.size());
            int i = 0;
            for (Map.Entry<BlockPos, Direction> entry : doorLocations.entrySet()) {
                writeBlockPos(tagCompound, i + "location", entry.getKey());
                tagCompound.putString(i + "direction", entry.getValue().getName());
                i++;
            }
        }

        protected static void writeBlockPos(CompoundNBT nbt, String name, BlockPos pos) {
            nbt.putInt(name + "X", pos.getX());
            nbt.putInt(name + "Y", pos.getY());
            nbt.putInt(name + "Z", pos.getZ());
        }

        protected static BlockPos readBlockPos(CompoundNBT nbt, String name) {
            return new BlockPos(nbt.getInt(name + "X"), nbt.getInt(name + "Y"), nbt.getInt(name + "Z"));
        }

        public interface Factory<T extends AbstractPiece> {
            T generate(TemplateManager templateManager, Random rand, int x, int y, int z);
        }
    }

    public static class Elevator extends AbstractPiece {

        Template elevatorTop, elevatorMid, elevatorBot, elevator;
        int height;
        int elevatorLoc;

        public Elevator(TemplateManager templateManager, Random rand, int x, int y, int z) {
            super(RegistryEventHandler.UGEV, templateManager, rand, x, y, z);
            elevatorTop = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator_top"));
            elevatorMid = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator_mid"));
            elevatorBot = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator_bot"));
            elevator = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator"));
            this.doorLocations.put(new BlockPos(0, 0, 0), Direction.NORTH);
            height = rand.nextInt(3);
            elevatorLoc = rand.nextInt(height * 7 + 10);
            for (int i = 0; i < height; i++) {
                this.doorLocations.put(new BlockPos(0, -7 * (i+1), 0), Direction.NORTH);
            }
            boundingBox = elevatorTop.getMutableBoundingBox(new PlacementSettings(), this.pos);
            int i;
            for (i = 0; i < height; i++) {
                boundingBox.expandTo(elevatorMid.getMutableBoundingBox(new PlacementSettings(), this.pos.add(0, -7 * (i + 1), 0)));
            }
            boundingBox.expandTo(elevatorBot.getMutableBoundingBox(new PlacementSettings(), this.pos.add(0, -7 * (i + 1) - 2, 0)));
        }

        public Elevator(TemplateManager templateManager, CompoundNBT compoundNBT) {
            super(RegistryEventHandler.UGEV, compoundNBT);
            elevatorTop = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator_top"));
            elevatorMid = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator_mid"));
            elevatorBot = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator_bot"));
            elevator = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "elevator"));
            height = compoundNBT.getInt("height");
            elevatorLoc = compoundNBT.getInt("elevatorLoc");
            boundingBox = elevatorTop.getMutableBoundingBox(new PlacementSettings(), this.pos);
            int i;
            for (i = 0; i < height; i++) {
                boundingBox.expandTo(elevatorMid.getMutableBoundingBox(new PlacementSettings(), this.pos.add(0, -7 * (i + 1), 0)));
            }
            boundingBox.expandTo(elevatorBot.getMutableBoundingBox(new PlacementSettings(), this.pos.add(0, -7 * (i + 1) - 2, 0)));
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         *
         * @param tagCompound
         */
        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putInt("height", height);
            tagCompound.putInt("elevatorLoc", elevatorLoc);
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {

            elevatorTop.addBlocksToWorld(worldIn, pos, new PlacementSettings().setBoundingBox(structureBoundingBoxIn));

            int i;
            for (i = 0; i < height; i++) {
                elevatorMid.addBlocksToWorld(worldIn, pos.add(0, -7 * (i + 1), 0), new PlacementSettings().setBoundingBox(structureBoundingBoxIn));
            }

            elevatorBot.addBlocksToWorld(worldIn, pos.add(0, -7 * (i + 1) - 2, 0), new PlacementSettings().setBoundingBox(structureBoundingBoxIn));

            elevator.addBlocksToWorld(worldIn, pos.add(0, -7 * (i + 1) - 2 + elevatorLoc, 0), new PlacementSettings().setBoundingBox(structureBoundingBoxIn));

            return true;
        }
    }

    public static class ShootingRange extends AbstractPiece {

        Template shootingRange;

        public ShootingRange(TemplateManager templateManager, Random rand, int x, int y, int z) {
            super(RegistryEventHandler.UGSR, templateManager, rand, x, y, z);
            this.rotation = Rotation.randomRotation(rand);
            this.doorLocations.put(this.getDoorLocation(new BlockPos(2, 0, 0), this.rotation), Direction.NORTH);
            BlockPos pos = this.doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.shootingRange = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "shooting_range"));
            this.boundingBox = shootingRange.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
        }

        public ShootingRange(TemplateManager templateManager, CompoundNBT nbt) {
            super(RegistryEventHandler.UGSR, nbt);
            this.shootingRange = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "shooting_range"));
            this.doorLocations.put(this.getDoorLocation(new BlockPos(2, 0, 0), this.rotation), Direction.NORTH);
            BlockPos pos = this.doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.boundingBox = shootingRange.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
        }

        /**
             * (abstract) Helper method to read subclass data from NBT
             *
             * @param tagCompound
             */
        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {

            PlacementSettings settings = new PlacementSettings().setRotation(this.rotation).setBoundingBox(structureBoundingBoxIn);

            shootingRange.addBlocksToWorld(worldIn, pos, settings);

            this.handleDataMarkers(worldIn, randomIn, shootingRange, pos, settings);

            return true;
        }

        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
            if (function.equals("chest")) {
                this.generateChest(worldIn, sbb, rand, pos, new ResourceLocation(Reference.MOD_ID, "shooting_range"), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, rotation.rotate(Direction.SOUTH)));
            }
        }

    }

    public static class Barracks extends  AbstractPiece {

        Template template;

        public Barracks(TemplateManager templateManager, Random rand, int x, int y, int z) {
            super(RegistryEventHandler.UGBR, templateManager, rand, x, y, z);
            rotation = Rotation.randomRotation(rand);
            this.template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "barracks"));
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 0, 0), rotation), Direction.NORTH);
            BlockPos pos = this.doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(rotation), this.pos);
        }

        public Barracks(TemplateManager templateManager, CompoundNBT nbt) {
            super(RegistryEventHandler.UGBR, nbt);
            this.template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "barracks"));
            this.rotation = Rotation.valueOf(nbt.getString("rotation"));
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 0, 0), rotation), Direction.NORTH);
            BlockPos pos = this.doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(rotation), this.pos);
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("rotation", rotation.name());
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {

            PlacementSettings settings = new PlacementSettings().setRotation(rotation).setBoundingBox(structureBoundingBoxIn);

            template.addBlocksToWorld(worldIn, this.pos, settings);

            this.handleDataMarkers(worldIn, randomIn, template, pos, settings);

            return true;
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
            if (function.equals("chest_left")) {
                this.generateChest(worldIn, sbb, rand, pos, new ResourceLocation(Reference.MOD_ID, "barracks"), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, rotation.rotate(Direction.WEST)));
            }
            if (function.equals("chest_right")) {
                this.generateChest(worldIn, sbb, rand, pos, new ResourceLocation(Reference.MOD_ID, "barracks"), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, rotation.rotate(Direction.EAST)));
            }
        }
    }

    public static class Kitchen extends AbstractPiece {

        Template template;

        public Kitchen(TemplateManager templateManager, Random rand, int x, int y, int z) {
            super(RegistryEventHandler.UGKT, templateManager, rand, x, y, z);
            this.rotation = Rotation.randomRotation(rand);
            this.doorLocations.put(getDoorLocation(new BlockPos(1, 0, 0), this.rotation), Direction.NORTH);
            this.doorLocations.put(getDoorLocation(new BlockPos(-4, 0, 10), this.rotation), Direction.WEST);
            this.doorLocations.put(getDoorLocation(new BlockPos(-4, 0, 20), this.rotation), Direction.WEST);
            this.doorLocations.put(getDoorLocation(new BlockPos(1, 0, 30), this.rotation), Direction.SOUTH);
            BlockPos pos = this.doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "kitchen"));
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(rotation), this.pos);
        }

        public Kitchen(TemplateManager templateManager, CompoundNBT nbt) {
            super(RegistryEventHandler.UGKT, nbt);
            this.doorLocations.put(getDoorLocation(new BlockPos(1, 0, 0), this.rotation), Direction.NORTH);
            this.doorLocations.put(getDoorLocation(new BlockPos(-4, 0, 10), this.rotation), Direction.WEST);
            this.doorLocations.put(getDoorLocation(new BlockPos(-4, 0, 20), this.rotation), Direction.WEST);
            this.doorLocations.put(getDoorLocation(new BlockPos(1, 0, 30), this.rotation), Direction.SOUTH);
            BlockPos pos = this.doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "kitchen"));
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(rotation), this.pos);
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {

            PlacementSettings settings = new PlacementSettings().setRotation(this.rotation).setBoundingBox(structureBoundingBoxIn);

            this.template.addBlocksToWorld(worldIn, this.pos, settings);

            this.handleDataMarkers(worldIn, randomIn, template, pos, settings);

            return true;
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
            if (function.equals("barrel")) {
                this.generateChest(worldIn, sbb, rand, pos, new ResourceLocation(Reference.MOD_ID, "kitchen"), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, rotation.rotate(Direction.WEST)));
            }
        }
    }

    public static class Lab extends AbstractPiece {

        Template template;

        public Lab(TemplateManager templateManager, Random rand, int x, int y, int z) {
            super(RegistryEventHandler.UGLB, templateManager, rand, x, y, z);
            this.rotation = Rotation.randomRotation(rand);
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 0, 0), this.rotation), Direction.NORTH);
            this.pos = this.pos.subtract(this.doorLocations.keySet().iterator().next());
            template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "laboratory"));
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
        }

        public Lab(TemplateManager templateManager, CompoundNBT nbt) {
            super(RegistryEventHandler.UGLB, nbt);
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 0, 0), this.rotation), Direction.NORTH);
            this.pos = this.pos.subtract(this.doorLocations.keySet().iterator().next());
            template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "laboratory"));
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {

            template.addBlocksToWorld(worldIn, this.pos, new PlacementSettings().setRotation(this.rotation).setBoundingBox(structureBoundingBoxIn));

            return true;
        }
    }

    public static class CorridorJunction extends AbstractPiece {

        Template template;
        Rotation rotation;
        byte wallmask;

        public CorridorJunction(TemplateManager templateManager, Random rand, Rotation rotation, int x, int y, int z, byte wallmask) {
            super(RegistryEventHandler.UGCR, templateManager, rand, x, y, z);
            this.template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "hallway_junction"));
            this.rotation = rotation;
            this.wallmask = wallmask;
            this.boundingBox = this.template.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
            this.setCoordBaseMode(Direction.SOUTH);
        }

        public CorridorJunction(TemplateManager templateManager, CompoundNBT nbt) {
            super(RegistryEventHandler.UGCR, nbt);
            this.template = templateManager.getTemplate(new ResourceLocation(Reference.MOD_ID, "hallway_junction"));
            this.rotation = Rotation.valueOf(nbt.getString("rotation"));
            this.wallmask = nbt.getByte("wallmask");
            this.boundingBox = this.template.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
            this.setCoordBaseMode(Direction.SOUTH);
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("rotation", this.rotation.name());
            tagCompound.putByte("wallmask", this.wallmask);
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {

            this.template.addBlocksToWorld(worldIn, this.pos, new PlacementSettings().setBoundingBox(structureBoundingBoxIn).setRotation(this.rotation));

            if ((wallmask & 0b0001) != 0) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        this.setBlockState(worldIn, Blocks.WHITE_CONCRETE.getDefaultState(), 1+i, j+1, 0, structureBoundingBoxIn);
                    }
                }
            }
            if ((wallmask & 0b0010) != 0) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        this.setBlockState(worldIn, Blocks.WHITE_CONCRETE.getDefaultState(), 4, j+1, i+1, structureBoundingBoxIn);
                    }
                }
            }
            if ((wallmask & 0b0100) != 0) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        this.setBlockState(worldIn, Blocks.WHITE_CONCRETE.getDefaultState(), 1+i, j+1, 4, structureBoundingBoxIn);
                    }
                }
            }
            if ((wallmask & 0b1000) != 0) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        this.setBlockState(worldIn, Blocks.WHITE_CONCRETE.getDefaultState(), 0, j+1, i+1, structureBoundingBoxIn);
                    }
                }
            }

            for (int i = 0; i < 50; i++) {
                if (randomIn.nextInt(50) == 0) {
                    ZombieEntity zombie = new ZombieEntity(worldIn.getWorld());
                    zombie.setPosition(this.getXWithOffset(2, 2), this.getYWithOffset(1), this.getZWithOffset(2, 2));
                    zombie.enablePersistence();
                    worldIn.addEntity(zombie);
                }
            }

            return true;
        }
    }

    public static class Stairs extends AbstractPiece {

        Template template;

        public Stairs(TemplateManager templateManager, Random rand, int x, int y, int z) {
            super(RegistryEventHandler.UGSC, templateManager, rand, x, y, z);
            this.rotation = Rotation.randomRotation(rand);
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 7, 15), this.rotation), Direction.SOUTH);
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 0, 0), this.rotation), Direction.NORTH);
            BlockPos pos = doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "stairs"));
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
        }

        public Stairs(TemplateManager templateManager, CompoundNBT nbt) {
            super(RegistryEventHandler.UGSC, nbt);
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 7, 15), this.rotation), Direction.SOUTH);
            this.doorLocations.put(getDoorLocation(new BlockPos(0, 0, 0), this.rotation), Direction.NORTH);
            BlockPos pos = doorLocations.keySet().iterator().next();
            this.pos = this.pos.subtract(pos);
            this.template = templateManager.getTemplateDefaulted(new ResourceLocation(Reference.MOD_ID, "stairs"));
            this.boundingBox = template.getMutableBoundingBox(new PlacementSettings().setRotation(this.rotation), this.pos);
        }

        /**
         * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at the
         * end, it adds Fences...
         *
         * @param worldIn
         * @param randomIn
         * @param structureBoundingBoxIn
         * @param chunkPosIn
         */
        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {

            this.template.addBlocksToWorld(worldIn, this.pos, new PlacementSettings().setRotation(this.rotation).setBoundingBox(structureBoundingBoxIn));

            return true;
        }
    }
}
