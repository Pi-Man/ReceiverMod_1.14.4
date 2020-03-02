package piman.recievermod.world.gen.feature.structure;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import io.github.jdiemke.triangulation.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

public class UndergroundStructure extends Structure<NoFeatureConfig> {

    private static final int BLOCK_SIZE = 10;
    private static final int MIN_DISTANCE = 5;

    public UndergroundStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
        System.out.println("UndergroundStructure");
    }

    @Override
    public boolean hasStartAt(@Nonnull ChunkGenerator<?> chunkGen, @Nonnull Random rand, int chunkPosX, int chunkPosZ) {

        int blockX = chunkPosX/BLOCK_SIZE;
        int blockZ = chunkPosZ/BLOCK_SIZE;

        Random blockRand = new Random(chunkGen.getSeed() + blockX << 16 + blockZ);

        int offsetX = blockRand.nextInt(BLOCK_SIZE - MIN_DISTANCE);
        int offsetZ = blockRand.nextInt(BLOCK_SIZE - MIN_DISTANCE);

        return chunkPosX == blockX * BLOCK_SIZE + offsetX && chunkPosZ == blockZ * BLOCK_SIZE + offsetZ;

    }

    @Nonnull
    @Override
    public IStartFactory getStartFactory() {
        System.out.println("getStartFactory");
        return Start::new;
    }

    @Nonnull
    @Override
    public String getStructureName() {
        return "receiver:underground";
    }

    @Override
    public int getSize() {
        return 0;
    }

    public static class Start extends StructureStart {

        private int sizeX, sizeZ;

        private int heightMap[][];
        private List<boolean[][]> heightBitMasks = new ArrayList<>();
        private List<boolean[][]> roomBitMasks = new ArrayList<>();
        private List<boolean[][]> hallwayBitMasks = new ArrayList<>();
        private List<boolean[][]> doorwayBitMasks = new ArrayList<>();

        private List<UndergroundPieces.AbstractPiece> rooms = new ArrayList<>();
        private List<UndergroundPieces.AbstractPiece> corridors = new ArrayList<>();

        private ChunkGenerator<?> generator;
        private TemplateManager templateManager;

        private BlockPos spawnCenter;
        private boolean didSpawn = false;
        private boolean init = false;

        public Start(Structure<?> structureIn, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn, int referenceIn, long seed) {
            super(structureIn, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
            sizeX = 101;
            sizeZ = 101;
            heightMap = new int[sizeX][sizeZ];
            System.out.println("Start");
        }

        @Override
        public void init(@Nonnull ChunkGenerator<?> generator, @Nonnull TemplateManager templateManagerIn, int chunkX, int chunkZ, @Nonnull Biome biomeIn) {

            System.out.println("init_start");

            this.generator = generator;
            this.templateManager = templateManagerIn;
            this.spawnCenter = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);
            spawnCenter = spawnCenter.add(0, generator.func_222531_c(spawnCenter.getX(), spawnCenter.getZ(), Heightmap.Type.OCEAN_FLOOR_WG), 0);

            MutableBoundingBox bbox = MutableBoundingBox.getNewBoundingBox();

            bbox.expandTo(new MutableBoundingBox(spawnCenter.add(-sizeX/2, 0, -sizeZ/2), spawnCenter.add(sizeX/2, 0, sizeZ/2)));

            this.bounds = bbox;

            this.init = true;

            System.out.println("init_end");

        }

        @Override
        public boolean isValid() {
            return init || super.isValid();
        }

        public void generateComponents(IWorld worldIn) {
            System.out.println("gen");

            if (spawnCenter == null) {
                System.out.println("WFT is going on");
                return;
            }

            UndergroundPieces.Elevator elevator = new UndergroundPieces.Elevator(templateManager, rand, spawnCenter.getX(), spawnCenter.getY(), spawnCenter.getZ());

            this.rooms.add(elevator);

            List<List<Vector2D>> layerPoints = new ArrayList<>();

            for (int h = 0; h <= elevator.height; h++) {
                heightBitMasks.add(new boolean[sizeX][sizeZ]);
                roomBitMasks.add(new boolean[sizeX][sizeZ]);
                hallwayBitMasks.add(new boolean[sizeX / 5][sizeZ / 5]);
                doorwayBitMasks.add(new boolean[sizeX][sizeZ]);
                layerPoints.add(new ArrayList<>());
            }

            for (int h = 0; h <= elevator.height; h++) {

                List<UndergroundPieces.AbstractPiece> rooms = new ArrayList<>();

                int area = 0;

                rooms.add(elevator);

                addRoomToMasks(roomBitMasks, elevator, spawnCenter);
                addDoorToMasks(doorwayBitMasks, elevator, spawnCenter);

                System.out.println("start heightMap");
                generateHeightBitMap(worldIn, heightBitMasks.get(h), sizeX / 2, sizeZ / 2, spawnCenter.add(0, -1 - 7 * h, 0));
                System.out.println("end heightMap");

                for (int i = 0; i < sizeX; i++) {
                    for (int j = 0; j < sizeZ; j++) {
                        if (heightBitMasks.get(h)[i][j]) {
                            area++;
//                        if (i % 1 == 0 && j % 1 == 0) {
//                            this.components.add(new UndergroundPieces.TestPiece(templateManagerIn, pos1.getX(), pos1.getY(), pos1.getZ()));
//                        }
                        }
                    }
                }

                List<UndergroundPieces.AbstractPiece.Factory<? extends UndergroundPieces.AbstractPiece>> pieces = Lists.newArrayList(
                        UndergroundPieces.ShootingRange::new,
                        UndergroundPieces.Kitchen::new,
                        UndergroundPieces.Lab::new,
                        UndergroundPieces.Barracks::new,
                        UndergroundPieces.Barracks::new,
                        UndergroundPieces.Barracks::new,
                        UndergroundPieces.Barracks::new,
                        UndergroundPieces.Barracks::new
                );

                if (h < elevator.height) {
                    pieces.add(UndergroundPieces.Stairs::new);
                }

                while (!pieces.isEmpty()) {

                    boolean mask[][] = new boolean[sizeX][sizeZ];

                    for (int i = 0; i < sizeX; i++) {
                        for (int j = 0; j < sizeZ; j++) {
                            mask[i][j] = heightBitMasks.get(h)[i][j] & !roomBitMasks.get(h)[i][j] & !doorwayBitMasks.get(h)[i][j];
                        }
                    }

                    UndergroundPieces.AbstractPiece room = UndergroundPieces.AbstractPiece.fitPiece(templateManager, rand, sizeX, sizeZ, spawnCenter.add(0, -7 - 7 * h, 0), mask, 100, pieces.remove(rand.nextInt(pieces.size())));

                    if (room != null) {
                        addRoomToMasks(roomBitMasks, room, spawnCenter);
                        addDoorToMasks(doorwayBitMasks, room, spawnCenter);
                        rooms.add(room);
                    }

                }

                boolean mask1[][] = new boolean[sizeX][sizeZ];

                for (int i = 0; i < sizeX; i++) {
                    for (int j = 0; j < sizeZ; j++) {
                        mask1[i][j] = heightBitMasks.get(h)[i][j];// & !roomBitMasks.get(h)[i][j] & !doorwayBitMasks.get(h)[i][j];
                        if (!mask1[i][j]) {
                            //this.components.add(new UndergroundPieces.TestPiece(templateManager, i + spawnCenter.getX() - sizeX/2, spawnCenter.getY() - 1 - 7*h, j + spawnCenter.getZ() - sizeZ/2));
                        }
                    }
                }

                Map<Vector2D, UndergroundPieces.AbstractPiece> locationMap = new HashMap<>();

                for (UndergroundPieces.AbstractPiece room : rooms) {
                    int i = room.pos.getX() - spawnCenter.getX() + sizeX / 2;
                    int j = room.pos.getZ() - spawnCenter.getZ() + sizeZ / 2;
                    for (BlockPos pos : room.doorLocations.keySet()) {
                        Vector2D point = new Vector2D(i + pos.getX(), j + pos.getZ());
                        layerPoints.get((room.pos.getY() - spawnCenter.getY() + 1) / -7 - pos.getY()/7).add(point);
                        locationMap.put(point, room);
                    }
                }

                try {
                    DelaunayTriangulator triangulator = new DelaunayTriangulator(layerPoints.get(h));
                    triangulator.triangulate();

                    List<Triangle2D> triangles = triangulator.getTriangles();

                    Set<Edge2D> edges = new HashSet<>();

                    for (Triangle2D triangle : triangles) {
                        Edge2D edge1 = new Edge2D(triangle.a, triangle.b);
                        Edge2D edge2 = new Edge2D(triangle.b, triangle.c);
                        Edge2D edge3 = new Edge2D(triangle.c, triangle.a);
                        edges.add(edge1);
                        edges.add(edge2);
                        edges.add(edge3);
                    }

                    boolean mask[][] = new boolean[sizeX][sizeZ];

                    for (int i = 0; i < sizeX; i++) {
                        for (int j = 0; j < sizeZ; j++) {
                            mask[i][j] = heightBitMasks.get(h)[i][j] & !roomBitMasks.get(h)[i][j];
                        }
                    }

                    Set<Vector2D> connectedPoints = new HashSet<>();

                    for (Edge2D edge : edges) {
                        if(connectDoors(mask, hallwayBitMasks.get(h), edge.a, edge.b)) {
                            connectedPoints.add(edge.a);
                            connectedPoints.add(edge.b);
                        }
                    }

                    locationMap.keySet().retainAll(connectedPoints);
                    rooms.retainAll(locationMap.values());
                }
                catch (NotEnoughPointsException e) {
                    e.printStackTrace();
                    System.out.println("spawnCenter = " + spawnCenter);
                    if (layerPoints.get(h).size() == 2) {
                        boolean mask[][] = new boolean[sizeX][sizeZ];

                        for (int i = 0; i < sizeX; i++) {
                            for (int j = 0; j < sizeZ; j++) {
                                mask[i][j] = heightBitMasks.get(h)[i][j] & !roomBitMasks.get(h)[i][j];
                            }
                        }
                        connectDoors(mask, hallwayBitMasks.get(h), layerPoints.get(h).get(1), layerPoints.get(h).get(0));
                    }
                }

                for (int i = 0; i < sizeX / 5; i++) {
                    for (int j = 0; j < sizeZ / 5; j++) {
                        if (hallwayBitMasks.get(h)[i][j]) {
                            byte wallmask = 0;
                            if (!testMask(hallwayBitMasks.get(h), sizeX / 5, sizeZ / 5, i, j - 1)) {
                                wallmask |= 0b0001;
                            }
                            if (!testMask(hallwayBitMasks.get(h), sizeX / 5, sizeZ / 5, i + 1, j)) {
                                wallmask |= 0b0010;
                            }
                            if (!testMask(hallwayBitMasks.get(h), sizeX / 5, sizeZ / 5, i, j + 1)) {
                                wallmask |= 0b0100;
                            }
                            if (!testMask(hallwayBitMasks.get(h), sizeX / 5, sizeZ / 5, i - 1, j)) {
                                wallmask |= 0b1000;
                            }
                            corridors.add(new UndergroundPieces.CorridorJunction(templateManager, rand, Rotation.NONE, i * 5 + spawnCenter.getX() - sizeX / 2, spawnCenter.getY() - 7 - 7 * h, j * 5 + spawnCenter.getZ() - sizeZ / 2 + 1, wallmask));
                        }
                    }
                }
                this.rooms.addAll(rooms);
            }

//            for (int i = 0; i < sizeX; i++) {
//                for (int j = 0; j < sizeZ; j++) {
//                    this.components.add(new UndergroundPieces.TestPiece(templateManager, i + spawnCenter.getX() - sizeX/2, heightMap[i][j], j + spawnCenter.getZ() - sizeZ/2));
//                }
//            }

            this.components.addAll(corridors);

            this.components.addAll(rooms);

            this.recalculateStructureSize();
        }

        @Override
        public void generateStructure(IWorld worldIn, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {

            if (this.components.isEmpty() && !this.didSpawn) {
                this.generateComponents(worldIn);
                this.didSpawn = true;
            }

            super.generateStructure(worldIn, rand, structurebb, pos);
        }

        public void addRoomToMasks(List<boolean[][]> masks, UndergroundPieces.AbstractPiece room, BlockPos center) {
            for (int w = (room.getBoundingBox().maxY - center.getY() + 1) / -7; w <= (room.getBoundingBox().minY - center.getY() +  1) / -7; w++) {
                if (w >= 0 && w < masks.size()) {
                    for (int u = room.getBoundingBox().minX + 1; u < room.getBoundingBox().maxX; u++) {
                        for (int v = room.getBoundingBox().minZ + 1; v < room.getBoundingBox().maxZ; v++) {
                            int i = u - center.getX() + sizeX / 2;
                            int j = v - center.getZ() + sizeZ / 2;
                            masks.get(w)[i][j] = true;
                            //this.components.add(new UndergroundPieces.TestPiece(templateManager, u, room.pos.getY() - 1, v));
                        }
                    }
                }
            }
        }

        public void addDoorToMasks(List<boolean[][]> masks, UndergroundPieces.AbstractPiece room, BlockPos center) {
            for (BlockPos door : room.doorLocations.keySet()) {
                int w = (room.pos.getY() - center.getY() + 1) / -7 - door.getY()/7;
                if (w >= 0) {
                    int i1 = room.pos.getX() - center.getX() + sizeX / 2;
                    int j1 = room.pos.getZ() - center.getZ() + sizeZ / 2;
                    for (int i = 0; i < 5; i++) {
                        for (int j = -4; j < 1; j++) {
                            masks.get(w)[door.getX() + i + i1][door.getZ() + j + j1] = true;
                        }
                    }
                }
            }
        }

        public boolean connectDoors(boolean[][] mask, boolean[][] hallwayBitMask, Vector2D posA, Vector2D posB) {

            Node end = findPath(mask, hallwayBitMask, (int)posA.x, (int)posA.y - 4, (int)posB.x, (int)posB.y - 4);

            if (end == null) {
                return false;
            }

            for (Node temp = end; temp != null; temp = temp.prev) {
                hallwayBitMask[temp.i/5][temp.j/5] = true;
            }

            return true;

        }

        public void generateHeightBitMap(IWorld iWorld, boolean[][] map, int i, int j, BlockPos center) {
            if (i < 0 || j < 0 || i >= sizeX || j >= sizeZ || map[i][j]) return;
            Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
            queue.add(Pair.of(i, j));
            while(!queue.isEmpty()) {
                Pair<Integer, Integer> p = queue.poll();
                BlockPos pos = center.add( p.getLeft()- sizeX/2, 0, p.getRight() - sizeZ/2);
                if (test(iWorld, map, pos.add(1, 0, 0), p.getLeft() + 1, p.getRight())) {
                    map[p.getLeft() + 1][p.getRight()] = true;
                    queue.add(Pair.of(p.getLeft() + 1, p.getRight()));
                }
                if (test(iWorld, map, pos.add(0, 0, 1), p.getLeft(), p.getRight() + 1)) {
                    map[p.getLeft()][p.getRight() + 1] = true;
                    queue.add(Pair.of(p.getLeft(), p.getRight() + 1));
                }
                if (test(iWorld, map, pos.add(-1, 0, 0), p.getLeft() - 1, p.getRight())) {
                    map[p.getLeft() - 1][p.getRight()] = true;
                    queue.add(Pair.of(p.getLeft() - 1, p.getRight()));
                }
                if (test(iWorld, map, pos.add(0, 0, -1), p.getLeft(), p.getRight() - 1)) {
                    map[p.getLeft()][p.getRight() - 1] = true;
                    queue.add(Pair.of(p.getLeft(), p.getRight() - 1));
                }
            }
        }

        private boolean test(IWorld iWorld, boolean[][] mask, BlockPos pos, int i, int j) {
            if (i < 0 || j < 0 || i >= sizeX || j >= sizeZ || mask[i][j]) return false;
            int height = heightMap[i][j];
            if (height == 0) {
                Heightmap heightmap = this.generateHeightMapChunk(iWorld, pos.getX() >> 4, pos.getZ() >> 4);
                for (int i1 = 0; i1 < 16; i1++) {
                    for (int j1 = 0; j1 < 16; j1++) {
                        int u = i+i1 - (pos.getX() & 15);
                        int v = j+j1 - (pos.getZ() & 15);
                        if (u < 0 || v < 0 || u >= sizeX || v >= sizeZ) continue;
                        heightMap[u][v] = heightmap.getHeight(i1, j1) - 1;
                    }
                }
                height = heightMap[i][j];
            }
            return height > pos.getY();
        }

        private Heightmap generateHeightMapChunk(IWorld world, int x, int z) {

            ChunkPrimer chunk = new ChunkPrimer(new ChunkPos(x, z), new UpgradeData(new CompoundNBT()));

            generator.generateBiomes(chunk);
            generator.makeBase(world, chunk);

            return chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);

        }

        private boolean testMask(boolean[][] mask, int sizeX, int sizeZ, int i, int j) {
            return !(i < 0 || j < 0 || i >= sizeX || j >= sizeZ || !mask[i][j]);
        }

        private boolean testMaskArea(boolean[][] mask, int sizeX, int sizeZ, int i, int j, int u, int v) {
            if (i < 0 || j < 0 || i >= sizeX || j >= sizeZ || i+u < 0 || j+v < 0 || i+u >= sizeX || j+v >= sizeZ) return false;
            for (int i1 = 0; i1 < u; i1++) {
                for (int j1 = 0; j1 < v; j1++) {
                    if (!mask[i+i1][j+j1]) return false;
                }
            }
            return true;
        }

        private Node findPath(boolean[][] mask, boolean[][] hallwayBitMask, int i1, int j1, int i2, int j2) {

            TreeSet<Node> OPEN = new TreeSet<>();
            TreeSet<Node> CLOSED = new TreeSet<>();
            Node END = new Node(i2, j2);
            OPEN.add(new Node(i1, j1, 0, calcHeuristic(hallwayBitMask, null, i1, j1, i2, j2)));

            while(!OPEN.isEmpty()) {
                Node node = OPEN.pollFirst();
                {
                    Node other = new Node(node.i + 5, node.j, calcPathCost(hallwayBitMask, node, 5, 0), calcHeuristic(hallwayBitMask, node, node.i + 5, node.j, i2, j2), node);
                    if (other.equals(END)) return other;
                    if (!CLOSED.contains(other) && testMaskArea(mask, sizeX, sizeZ, other.i, other.j, 5, 5)) {
                        OPEN.add(other);
                    }
                }
                {
                    Node other = new Node(node.i, node.j + 5, calcPathCost(hallwayBitMask, node, 0, 5), calcHeuristic(hallwayBitMask, node, node.i, node.j + 5, i2, j2), node);
                    if (other.equals(END)) return other;
                    if (!CLOSED.contains(other) && testMaskArea(mask, sizeX, sizeZ, other.i, other.j, 5, 5)) {
                        OPEN.add(other);
                    }
                }
                {
                    Node other = new Node(node.i - 5, node.j, calcPathCost(hallwayBitMask, node, -5, 0), calcHeuristic(hallwayBitMask, node, node.i - 5, node.j, i2, j2), node);
                    if (other.equals(END)) return other;
                    if (!CLOSED.contains(other) && testMaskArea(mask, sizeX, sizeZ, other.i, other.j, 5, 5)) {
                        OPEN.add(other);
                    }
                }
                {
                    Node other = new Node(node.i, node.j - 5, calcPathCost(hallwayBitMask, node, 0, -5), calcHeuristic(hallwayBitMask, node, node.i, node.j - 5, i2, j2), node);
                    if (other.equals(END)) return other;
                    if (!CLOSED.contains(other) && testMaskArea(mask, sizeX, sizeZ, other.i, other.j, 5, 5)) {
                        OPEN.add(other);
                    }
                }
                CLOSED.add(node);
                if (CLOSED.size() > sizeX/5 * sizeZ/5) return null;
            }
            return null;
        }

        private float calcHeuristic(boolean[][] hallwayBitMask, Node prev, int i1, int j1, int i2, int j2) {
            int distance = (Math.abs(i2 - i1) + Math.abs(j2 - j1)) * (prev != null && testMask(hallwayBitMask, sizeX/5, sizeZ/5, i1/5, j1/5) ? 0 : 1);
            if (prev == null || prev.prev == null) return distance;
            int dx = i1 - prev.i;
            int dy = j1 - prev.j;
            int du = prev.prev.i - prev.i;
            int dv = prev.prev.j - prev.j;
            int cross = Math.abs(du * dy - dv * dx);
            return distance + 0.001f * cross;
        }

        private float calcPathCost(boolean[][] hallwayBitMask, Node prev, int dx, int dy) {
            float distance = (Math.abs(dx) + Math.abs(dy)) * (prev != null && testMask(hallwayBitMask, sizeX/5, sizeZ/5, (prev.i + dx)/5, (prev.j + dy)/5) ? 0.5f : 1);
            if (prev == null || prev.prev == null) return distance;
            int du = prev.prev.i - prev.i;
            int dv = prev.prev.j - prev.j;
            int cross = Math.abs(du * dy - dv * dx);
            return distance + 0.001f * cross;
        }

        private static class Node implements Comparable<Node> {

            public Node prev;
            public float pathWeight;
            public float heuristic;
            public int i, j;

            public Node(int i, int j) {
                this(i, j, 0, 0);
            }

            public Node(int i, int j, float pathWeight, float heuristic) {
                this.i = i;
                this.j = j;
                this.pathWeight = pathWeight;
                this.heuristic = heuristic;
                this.prev = null;
            }

            public Node(int i, int j, float pathWeight, float heuristic, Node prev) {
                this(i, j, pathWeight, heuristic);
                this.pathWeight += prev.pathWeight;
                this.prev = prev;
            }

            @Override
            public int compareTo(@Nonnull Node other) {
                return other.equals(this) ? 0 : ((other.pathWeight + other.heuristic) > (this.pathWeight + this.heuristic) ? -1 : 1);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Node node = (Node) o;
                return i == node.i && j == node.j;
            }

            @Override
            public int hashCode() {
                return Objects.hash(i, j);
            }
        }
    }
}
