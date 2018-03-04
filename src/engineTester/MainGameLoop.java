package engineTester;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
 
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
 
public class MainGameLoop {
	
	private static Loader loader;
	
    public static void main(String[] args) {
 
        DisplayManager.createDisplay();
        loader = new Loader();
         
        //********** TERRAIN TEXTURE STUFF **********
           
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        
        
        Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap,"heightMap");
        //*******************************************
        
        TexturedModel staticTree = getTexturedModel("tree","tree");
        
        TexturedModel lowPolyStaticTree = getTexturedModel("lowPolyTree","lowPolyTree");
        
        TexturedModel staticGrass = getTexturedModel("grassModel","grassTexture");
        staticGrass.getTexture().setHasTransparency(true);
        staticGrass.getTexture().setUseFakeLighting(true);
        
        TexturedModel staticFern = getTexturedModel("fern","fern");
        staticFern.getTexture().setHasTransparency(true);
        staticFern.getTexture().setUseFakeLighting(true);

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for(int i=0;i<500;i++){
        	float x = random.nextFloat()*800 - 400;
        	float z = random.nextFloat() * -600;
        	float y = terrain.getHeightOfTerrain(x, z);
        	if(i % 1 == 0) {
        		entities.add(new Entity(staticGrass,new Vector3f(x,y,z),0,0,0,1));
        	}
        	if(i % 2 == 0) {
        		entities.add(new Entity(staticFern,new Vector3f(x,y,z),0,0,0,0.75f));
        	}
        	else if(i % 3 == 0) {
        		entities.add(new Entity(lowPolyStaticTree, new Vector3f(x,y,z),0,0,0,0.40f));
        	}
        	else if(i % 5 == 0) {
        		entities.add(new Entity(staticTree, new Vector3f(x,y,z),0,0,0,3));
        	}
        }
              
         
        Light light = new Light(new Vector3f(100,100,-20),new Vector3f(1,1,1));
         
        //Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap,"heightMap");
        //Terrain terrain2 = new Terrain(1,-1,loader,texturePack,blendMap,"heightMap");
         
        MasterRenderer renderer = new MasterRenderer();
         
        ModelData playerData = OBJFileLoader.loadOBJ("person");
        RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        TexturedModel playerTexture = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));
        
        Player player = new Player(playerTexture, new Vector3f(100,0,-100), 0, 0, 0, 0.40f);
        Camera camera = new Camera(player);
        
        while(!Display.isCloseRequested()){
        	camera.move();
            player.move(terrain);
            renderer.processEntity(player);
            
            renderer.processTerrain(terrain);
 
            for(Entity entity : entities) {
            	renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
 
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }
 
    
    private static TexturedModel getTexturedModel(String object, String texture) {
    	ModelData data = OBJFileLoader.loadOBJ(object);
    	RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
    	return new TexturedModel(model,new ModelTexture(loader.loadTexture(texture)));  	
    }
}