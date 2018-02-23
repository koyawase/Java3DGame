package engineTester;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
 
import models.RawModel;
import models.TexturedModel;
 
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
 
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;
 
public class MainGameLoop {
 
    public static void main(String[] args) {
 
        DisplayManager.createDisplay();
        Loader loader = new Loader();
         
         
        RawModel treeModel = OBJLoader.loadObjModel("tree", loader);
        TexturedModel staticTree = new TexturedModel(treeModel,new ModelTexture(loader.loadTexture("lowPolyTree")));
        
        RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
        TexturedModel staticGrass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
        staticGrass.getTexture().setHasTransparency(true);
        staticGrass.getTexture().setUseFakeLighting(true);
        
        List<Entity> trees = new ArrayList<Entity>();
        List<Entity> grass = new ArrayList<Entity>();
        Random random = new Random();
        for(int i=0;i<500;i++){
            trees.add(new Entity(staticTree, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
            grass.add(new Entity(staticGrass,new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,1));
        }
              
         
        Light light = new Light(new Vector3f(100,100,-20),new Vector3f(1,1,1));
         
        Terrain terrain = new Terrain(0,-1,loader,new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(1,-1,loader,new ModelTexture(loader.loadTexture("grass")));
         
        Camera camera = new Camera();   
        MasterRenderer renderer = new MasterRenderer();
         
        while(!Display.isCloseRequested()){
            camera.move();
             
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            for(Entity tree :trees){
                renderer.processEntity(tree); 
            }
            for(Entity gr : grass) {
            	renderer.processEntity(gr);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
 
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
 
    }
 
}