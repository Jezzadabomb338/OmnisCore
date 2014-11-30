package me.jezza.oc.common.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import static me.jezza.oc.common.utils.MathHelper.RoundingMethod;

public class CoordSet {

    // final
    private int x, y, z;

    public CoordSet() {
        this(0, 0, 0);
    }

    public CoordSet(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CoordSet(int[] array) throws ArrayIndexOutOfBoundsException {
        this(array[0], array[1], array[2]);
    }

    public CoordSet(String x, String y, String z) throws NumberFormatException {
        this(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
    }

    public CoordSet(TileEntity tile) {
        this(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    /**
     * Floors the player's position.
     */
    public CoordSet(EntityPlayer player) {
        this(player, RoundingMethod.FLOOR);
    }

    public CoordSet(EntityPlayer player, RoundingMethod roundingMethod) {
        switch (roundingMethod) {
            case CEILING:
                x = (int) Math.ceil(player.posX);
                y = (int) Math.ceil(player.posY);
                z = (int) Math.ceil(player.posZ);
                break;
            case ROUND:
                x = (int) Math.round(player.posX);
                y = (int) Math.round(player.posY);
                z = (int) Math.round(player.posZ);
                break;
            case FLOOR:
            default:
                x = (int) Math.floor(player.posX);
                y = (int) Math.floor(player.posY);
                z = (int) Math.floor(player.posZ);
        }
    }

    public void setX(int x) {
//        return new CoordSet(x, y, z);
        this.x = x;
    }

    public void setY(int y) {
//        return new CoordSet(x, y, z);
        this.y = y;
    }

    public void setZ(int z) {
//        return new CoordSet(x, y, z);
        this.z = z;
    }

    public void addX(int x) {
        this.x += x;
    }

    public void addY(int y) {
        this.y += y;
    }

    public void addZ(int z) {
        this.z += z;
    }

    /**
     * If x is final, this will be removed and x made public.
     */
    public int getX() {
        return x;
    }

    /**
     * If y is final, this will be removed and y made public.
     */
    public int getY() {
        return y;
    }

    /**
     * If z is final, this will be removed and z made public.
     */
    public int getZ() {
        return z;
    }

    public boolean withinRange(CoordSet tempSet, int range) {
        return getDistanceSq(tempSet) <= (range * range);
    }

    public double getDistanceSq(CoordSet tempSet) {
        double x2 = x - tempSet.x;
        double y2 = y - tempSet.y;
        double z2 = z - tempSet.z;
        return x2 * x2 + y2 * y2 + z2 * z2;
    }

    public float getDistance(CoordSet tempSet) {
        return net.minecraft.util.MathHelper.sqrt_double(getDistanceSq(tempSet));
    }

    public CoordSet addForgeDirection(ForgeDirection direction) {
        x += direction.offsetX;
        y += direction.offsetY;
        z += direction.offsetZ;
        return this;
    }

    public boolean isAdjacent(CoordSet coordSet) {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            CoordSet tempSet = coordSet.copy().addForgeDirection(direction);
            if (tempSet.equals(x, y, z))
                return true;
        }
        return false;
    }

    public TileEntity getTileEntity(IBlockAccess world) {
        return world.getTileEntity(x, y, z);
    }

    public TileEntity getTileFromDirection(IBlockAccess world, ForgeDirection direction) {
        return copy().addForgeDirection(direction).getTileEntity(world);
    }

    public boolean hasTileEntity(IBlockAccess world) {
        return getTileEntity(world) != null;
    }

    public Block getBlock(IBlockAccess world) {
        return world.getBlock(x, y, z);
    }

    public boolean setBlockToAir(World world) {
        return world.setBlockToAir(x, y, z);
    }

    public boolean isAirBlock(World world) {
        return world.isAirBlock(x, y, z);
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setIntArray("coordSet", asArray());
    }

    public static CoordSet readFromNBT(NBTTagCompound tag) {
        return new CoordSet(tag.getIntArray("coordSet"));
    }

    public static CoordSet createFromMinecraftTag(NBTTagCompound tag) {
        int x = tag.getInteger("x");
        int y = tag.getInteger("y");
        int z = tag.getInteger("z");
        return new CoordSet(x, y, z);
    }

    public static CoordSet readFromString(String loc) {
        if (!loc.matches("-?\\d*:-?\\d*:-?\\d*"))
            return null;
        String[] coords = loc.split(":");

        int x = 0;
        int y = 0;
        int z = 0;
        try {
            x = Integer.parseInt(coords[0]);
            y = Integer.parseInt(coords[1]);
            z = Integer.parseInt(coords[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return new CoordSet(x, y, z);
    }

    public void writeBytes(ByteBuf bytes) {
        bytes.writeInt(x);
        bytes.writeInt(y);
        bytes.writeInt(z);
    }

    public static CoordSet readBytes(ByteBuf bytes) {
        int x = bytes.readInt();
        int y = bytes.readInt();
        int z = bytes.readInt();

        return new CoordSet(x, y, z);
    }

    public int[] asArray() {
        return new int[]{x, y, z};
    }

    public boolean equals(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }

    @Override
    public int hashCode() {
        int hash = this.x;
        hash *= 31 + this.y;
        hash *= 31 + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof CoordSet))
            return false;
        return ((CoordSet) other).equals(x, y, z);
    }

    public String toPacketString() {
        return x + ":" + y + ":" + z;
    }

    @Override
    public String toString() {
        return '[' + toPacketString() + ']';
    }

    /**
     * If x,y, and z are final, this method will be removed.
     * Clone is faster, however if it fails, which it won't, but if it does, a copy will be made the normal way.
     */
    public CoordSet copy() {
        try {
            return (CoordSet) this.clone();
        } catch (CloneNotSupportedException e) {
        }
        return new CoordSet(x, y, z);
    }

    public CoordSet asChunkCoords() {
        x = x >> 4;
        y = y >> 4;
        z = z >> 4;

        return this;
    }

    public CoordSet toChunkCoords() {
        return copy().asChunkCoords();
    }

    public CoordSet asBlockCoords() {
        x *= 16;
        y *= 16;
        z *= 16;

        return this;
    }

    public CoordSet toBlockCoords() {
        return copy().asBlockCoords();
    }

    public CoordSet asSpawnPoint() {
        x = x % 16;
        y = y % 16;
        z = z % 16;

        return this;
    }

    public CoordSet toSpawnPoint() {
        return copy().asSpawnPoint();
    }

    public static CoordSet fromChunkCoordinates(ChunkCoordinates coordinates) {
        return new CoordSet(coordinates.posX, coordinates.posY, coordinates.posZ);
    }

}
