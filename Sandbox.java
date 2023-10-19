package application;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Sandbox {
	int mapRes =4;
	public int rows = 32;
	public int columns = 32;
	LatSite[] box = new LatSite[1024];

	Group root;
	int xPOffset = 0;

	
	int[] moves = new int[12];
	int[] incomingDirections = new int[4];
	int[] oppDir = new int[4];
	
	int[] emptyPrefab = {0,0,0,0,0};
	int[] wallPrefab  = {0,0,0,0,1};
	
	public Sandbox(int width, int height, Group r) {
		rows = height;
		columns = width;
		root = r;
		box = new LatSite[height*width];
		initSandbox();
		
		moves[0] = -columns; // move 0
		moves[1] = 1;
		moves[2] = columns;
		moves[3] = -1;
		
		incomingDirections[0] = 2;
		incomingDirections[1] = 3;
		incomingDirections[2] = 0;
		incomingDirections[3] = 1;
		
		
	}
	public void setPixel(int x,int y,int rad,int type) {//not optimised, sets pixels at x,y and around x,y in radius r, setting type of pixel to int type
		int asIndex = x+(y*rows);
		
		if(type == 0) {
			box[asIndex].setDirs(new int[] {1,1,1,1,0});
			for(int i = 0;i<(rows*columns);i++) {
				int tx = i%columns;
				int ty = i/columns;
				float distance = (float)Math.sqrt(((x-tx)*(x-tx))+((y-ty)*(y-ty)));
				if(distance<rad) {
					box[i].setDirs(new int[] {1,1,1,1,0});
				}
			}
		}
		else if(type == 1) {
			box[asIndex].setDirs(new int[] {0,0,0,0,1});
			for(int i = 0;i<(rows*columns);i++) {
				int tx = i%columns;
				int ty = i/columns;
				float distance = (float)Math.sqrt(((x-tx)*(x-tx))+((y-ty)*(y-ty)));
				if(distance<rad) {
					box[i].setDirs(new int[] {0,0,0,0,1});
				}
			}
		}
	}
	public void resetSandbox() {
		initSandbox();
	}
	void initSandbox() {//initialises sandbox space, sets walls
		for(int i = 0;i<(rows*columns);i++) {//initialise values
			box[i] = new LatSite(0,0,0,0,0);
		}
		
		int lastRowIndex = (columns * (rows-1));//references first element of last row
		for(int i=0;i<columns;i++) {
			box[i].setDirs(wallPrefab);
		}
		for(int i=lastRowIndex;i<(rows*columns);i++) {
			box[i].setDirs(wallPrefab);;
		}
		for(int i = 1;i<rows;i++) {
			box[i*columns].setDirs(wallPrefab);
			box[(i*columns)-1].setDirs(wallPrefab);
		}
	}
	void updateSandbox() {
		//create buffer to hold t+1 results before committing them where t is current iteration.
		LatSite[] boxBuffer = new LatSite[rows*columns];
		for(int i = 0;i<(rows*columns);i++) {
			boxBuffer[i] = new LatSite(0,0,0,0,0);
			boxBuffer[i].setDirs(box[i].getRawData());
		}
		
		for(int i = 0;i<(rows*columns);i++) {
			
			//for finding the particles that are entering the point
			int[] incomingParticles = new int[4];//maximum of 4 (1 for each direction)
			int incomingTotal = 0;//number of particles entering
			for(int v = 0;v<4;v++) {//for each neighbour of current LatSite, find which particles are coming to us.
				int nextIndex = i+moves[v];
				if(nextIndex>-1 && nextIndex<rows*columns){//make sure the point we are checking is actually within the "play area"
					int receivedParticle = box[nextIndex].getDirs()[incomingDirections[v]];//get boxes neighbours outbound particles, find the one that is coming to current particle using the incomingDirections array
					if(receivedParticle == 1) {//we have received a particle from direction v
						incomingParticles[v] = 1;
						incomingTotal++;
					}
				}
				
				
			}
			int[] outgoingParticles = new int[5];
			boolean isWall = box[i].isWall();
			if(!isWall) {//not a wall
				
				if(incomingTotal>0 && incomingTotal!=2) {//if there is at least one particle coming in (but not two!)
					for(int p = 0;p<4;p++) {
						if(incomingParticles[p] == 1) {
							outgoingParticles[incomingDirections[p]] = 1;//set the particle to its outgoing direction (just keep going straight)
						}
					}
				}
				if(incomingTotal == 2) {
					for(int p = 0;p<4;p++) {
						if(incomingParticles[p]==1) {
							if(incomingParticles[incomingDirections[p]]==1) {
								//two headed straight towards eachother! 
								// if p is 0, we know other opposite is 2, therefore next position will be p+1 (1) and incomingDirection[p+1] (3), likewise with 1 is 2 and 0
								//we dont have to worry about overflows because we only need to check positions 0 and 1 to see if there are opposing particles as particle in position 3 would cause overflow but we know it is opposite to 1 so it is already dealt with)
								outgoingParticles[p+1] = 1;
								outgoingParticles[incomingDirections[p+1]] = 1;
								p = 4; //acts as a "Break" to abort for loop as we have 
							}
							else {
								outgoingParticles[incomingDirections[p]] = 1;//set the particle to its outgoing direction (just keep going straight)
							}
						}
					}
				}
				
			}
			else {//is a wall, just send back how it came
				for(int p = 0;p<4;p++) {
					if(incomingParticles[p] == 1) {//if particle coming in from this direction, bounce it back using incomingDirections to find bounce direction
						//System.out.println("Incoming from: "+p+" outgoing: "+incomingDirections[p]);
						outgoingParticles[p] = 1;
					}
				}
				outgoingParticles[4] = 1;//remember to set the wall flag to 1 (by default its 0)
			}
			boxBuffer[i].setDirs(outgoingParticles);
		}
		for(int i = 0;i<(rows*columns);i++) {
			box[i].setDirs(boxBuffer[i].getRawData());
		}
	}
	public Group drawSandbox() {
		root.getChildren().clear();
		convertToScreen2();
		return root;
	}
	void convertToScreen() {//draws squares to screen 
		for(int drX = 0;drX<columns;drX++) {//for each x tile
			for(int drY = 0;drY<rows;drY++) {//for each y tile
				int dataTot = box[drX+(drY*rows)].getDataTotal();
				
				if(dataTot>0 || box[drX+(drY*rows)].isWall()) {//if the tile is not empty
					if(dataTot>0) {
						//System.out.println("data total: "+dataTot);
					}
					
					  Rectangle r = new Rectangle(mapRes,mapRes);
					  if(box[drX+(drY*rows)].isWall()) {
						  r.setFill(Color.RED);
					  }
					  else {
						 
						  int RGBval =  255-((255/4)*dataTot);
						  r.setFill(Color.rgb(RGBval, RGBval, RGBval));
					  }
					
				      r.setX((mapRes*drX));
				      r.setY(mapRes*drY);

				      
					root.getChildren().add(r);//add the image to the group "root"
				}
				
			}
		}
	}
	//The original version drew each individual pixel as a separate "rectangle", unsuprisingly this was very intensive and ran really poorly as the number of particles increased.
	//instead I opted to draw the data to a "WritableImage" and display the result as an image. This is much more efficient and runs far far better with basically minimal slowdown.
	void convertToScreen2() {
		WritableImage img = new WritableImage(1024,1100);
		PixelWriter pw = img.getPixelWriter();
		
		for(int drX = 0;drX<columns;drX++) {//for each x tile
			for(int drY = 0;drY<rows;drY++) {//for each y tile
				int dataTot = box[drX+(drY*rows)].getDataTotal();//get data total to determine the darkness of the pixel
				
				if(dataTot>0 || box[drX+(drY*rows)].isWall()) {//if the tile is not empty or is a wall
					Color r = Color.WHEAT; //just initialise the colour
					if(box[drX+(drY*rows)].isWall()) {//if the latsite is a wall, set the colour to red
						r =Color.RED;
					}
					else {//if it is not a wall, find what colour the pixel should be. we do this by dividing 255 by 4 (as there are 4 possible states for a latsite to be in regards to its data total (1,2,3,4))
						int RGBval =  255-((255/4)*dataTot);
						r= Color.rgb(RGBval, RGBval, RGBval);
					}
					//once the pixels correct colour has been determined, we can now set the pixel in the pixelwriter image to that colour. This is much cheaper than the old method of actually instantiating a rectangle.
					pw.setColor(drX, drY, r);
				}
				
			}
		}
		ImageView iv = new ImageView(img);
		iv.setScaleX(mapRes);
		iv.setScaleY(mapRes);
		iv.setSmooth(false);
		root.getChildren().add(iv);
	}
	
	public int getParticleCount() {
		int count = 0;
		for(int i = 0;i<(rows*columns);i++) {
			count+=box[i].getDataTotal();
			
		}
		return count;
	}

}

