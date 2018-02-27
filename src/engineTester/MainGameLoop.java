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
 
    public static void main(String[] args) {
 
        DisplayManager.createDisplay();
        Loader loader = new Loader();
         
        //********** TERRAIN TEXTURE STUFF **********
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        
        //*******************************************
        
        
        ModelData treeData = OBJFileLoader.loadOBJ("tree");
        RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
        TexturedModel staticTree = new TexturedModel(treeModel,new ModelTexture(loader.loadTexture("tree")));
        
        ModelData lowTreeData = OBJFileLoader.loadOBJ("lowPolyTree");
        RawModel lowTreeModel = loader.loadToVAO(lowTreeData.getVertices(), lowTreeData.getTextureCoords(), lowTreeData.getNormals(), lowTreeData.getIndices());
        TexturedModel lowstaticTree = new TexturedModel(lowTreeModel,new ModelTexture(loader.loadTexture("lowPolyTree")));
        
        ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
        RawModel grassModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(), grassData.getIndices());
        TexturedModel staticGrass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
        staticGrass.getTexture().setHasTransparency(true);
        staticGrass.getTexture().setUseFakeLighting(true);
        
        ModelData fernData = OBJFileLoader.loadOBJ("fern");
        RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
        TexturedModel staticFern = new TexturedModel(fernModel,new ModelTexture(loader.loadTexture("fern")));
        staticFern.getTexture().setHasTransparency(true);
        staticFern.getTexture().setUseFakeLighting(true);
        
        List<Entity> trees = new ArrayList<Entity>();
        List<Entity> grass = new ArrayList<Entity>();
        List<Entity> ferns = new ArrayList<Entity>();
        List<Entity> lpTrees = new ArrayList<Entity>();
        Random random = new Random();
        for(int i=0;i<500;i++){
            trees.add(new Entity(staticTree, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
            grass.add(new Entity(staticGrass,new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,1));
            ferns.add(new Entity(staticFern,new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,0.75f));
            lpTrees.add(new Entity(lowstaticTree, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,0.40f));
        }
              
         
        Light light = new Light(new Vector3f(100,100,-20),new Vector3f(1,1,1));
         
        Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap);
        Terrain terrain2 = new Terrain(1,-1,loader,texturePack,blendMap);
         
        Camera camera = new Camera();   
        MasterRenderer renderer = new MasterRenderer();
         
        ModelData playerData = OBJFileLoader.loadOBJ("person");
        RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(), playerData.getNormals(), playerData.getIndices());
        TexturedModel playerTexture = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));
        
        Player player = new Player(playerTexture, new Vector3f(50,0,-40), 0, 0, 0, 0.40f);
        
        while(!Display.isCloseRequested()){
            player.move();
            renderer.processEntity(player);
            
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            for(Entity tree :trees){
                renderer.processEntity(tree); 
            }
            for(Entity gr : grass) {
            	renderer.processEntity(gr);
            }
            for(Entity fern : ferns) {
            	renderer.processEntity(fern);
            }
            for(Entity lpTree : lpTrees) {
            	renderer.processEntity(lpTree);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
 
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }
 
}