package application;

//LatSite is a data structure used to represent each of the pixels visible
// They comprise of an array of integers representing particles departing in each of the 4 directions, up down left right.
//The fifth particle direction is used as a flag to signify that the location is actually a wall (and will therefore be used for bouncing particles off of)

public class LatSite {
	int[] particleDirs = new int[5];
	public LatSite(int a,int b, int c, int d,int isWall) { // initialise the LatSite
		particleDirs[0] = a;
		particleDirs[1] = b;
		particleDirs[2] = c;
		particleDirs[3] = d;
		particleDirs[4] = isWall;
	}
	public void setDirs(int[] directions) {//allows for direct setting of the array of outbound particles
		particleDirs = directions;
	}
	public int[] getDirs() {//returns the outbound particles (but notably not the flag at index 4)
		int[] toReturn = new int[6];
		for(int i = 0;i<4;i++) {
			toReturn[i] = particleDirs[i];
		}
		return toReturn;
	}
	public boolean isWall() {//returns if the flag at index 4 is true (representing if it is a wall or not)
		if(particleDirs[4] != 0) {
			return true;
		}
		return false;
	}
	public int getDataTotal() {//returns the total count of particles (1,2,3,4) which is used to display the colour intensity of the pixel
		int count = 0;
		for(int i =0;i<4;i++) {
			count += particleDirs[i];
		}
		return count;
	}
	public void setIndi(int index, int val) {//used for directly changing an outbound direction at index "index" to value "val", also can be used to set a LatSite to being a wall by changing the flag
		particleDirs[index] = val;
	}
	public int[] getRawData() {//returns the raw array as opposed to just the first 4 elements that represent the outbound particles
		return particleDirs;
	}
	public void printState() {//prints the current state of the LatSite in regards to current particles (a bit pointless, old from debugging)
		System.out.print("States are: ");
		for(int i = 0;i<4;i++) {
			System.out.print(particleDirs[i]+", ");
		}
		System.out.println(" ");
	}
}
