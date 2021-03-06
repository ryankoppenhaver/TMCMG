package togos.minecraft.mapgen.world.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.Tag;

import togos.minecraft.mapgen.TagMap;

public class ChunkData extends MiniChunkData
{
	public ChunkData( long px, long py, long pz, int w, int h, int d ) {
		super(px,py,pz,w,h,d);
	}
	
	/*
	public final int height = 128; // Y/+up/-down
	public final int depth  =  16; // Z/+west/-east
	public final int width  =  16; // X/+south/-north
	
	            (N, -x)
	               |
	               |
	               |
	               |
	(W, +z)--------0---------(E, -z) 
	               |
	               |
	               |
   	               |
	            (S, +x)
	*/
	
	public byte[] skyLightData   = new byte[(height*depth*width+1)/2];
	public byte[] blockLightData = new byte[(height*depth*width+1)/2];
	public byte[] lightHeightData = new byte[depth*width];
	public boolean terrainPopulated = false;
	
	//// Sky light ////
	
	public void setSkyLight( int x, int y, int z, int value ) {
		putNybble(skyLightData, blockIndex(x,y,z), value);
	}
	
	//// Light height ////
	
	public void setLightHeight( int x, int z, int height ) {
		lightHeightData[z*width+x] = (byte)(height);
	}
	
	public Tag toTag() {
		TagMap levelTags = new TagMap();
		levelTags.add(new ByteArrayTag("Blocks", blockData));
		levelTags.add(new ByteArrayTag("Data", blockExtraBits));
		levelTags.add(new ByteArrayTag("SkyLight", skyLightData));
		levelTags.add(new ByteArrayTag("BlockLight", blockLightData));
		levelTags.add(new ByteArrayTag("HeightMap", lightHeightData));
		levelTags.add(new ListTag("Entities", CompoundTag.class, Collections.EMPTY_LIST));
		
		List tileEntityTags = new ArrayList();
		for( Iterator tidi=tileEntityData.iterator(); tidi.hasNext(); ) {
			tileEntityTags.add( ((TileEntityData)tidi.next()).toTag() );
		}
		levelTags.add(new ListTag("TileEntities", CompoundTag.class, tileEntityTags));
		
		levelTags.add(new LongTag("LastUpdate", 23392));//System.currentTimeMillis()));
		levelTags.add(new IntTag("xPos", (int)(getChunkPositionX()/getChunkWidth())));
		levelTags.add(new IntTag("zPos", (int)(getChunkPositionZ()/getChunkDepth())));
		levelTags.add(new ByteTag("TerrainPopulated", (byte)(terrainPopulated ? 1 : 0)));
		return new CompoundTag("Level", levelTags);
	}
}
