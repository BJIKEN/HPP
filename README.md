# HPP Model demo
This repo contains the code for a simple demonstration of the HPP gas automaton simulation. It has been programmed in Java and used the Javafx library to handle things like rendering and buttons. 
The HPP (Hardy–Pomeau–Pazzis) is a lattice gas automaton used for simulating liquids. This is probably the simplest kind of simulation and is quite limited in its capabilities. 
As the concept dates from papers published in the 1970s, it is quite outdated and has been replaced by other concepts like the lattice Boltzmann method. One of the main issues with this method for simulating anything is the fact that particles are trapped in a square grid pattern, they can only travel in four directions: up, down, left and right. Later gas automata used 6 directions and even 8 direction grids, replacing the squares with hexagons.

## How does it work.
The HPP model involves a square grid, typically represented by an array of pixels. Each pixel represents a grid location that particles can travel through.

A grid location can be imagined as having inbound particles coming from above, below, from the left and from the right. On each refresh the grid location takes in the particles from these four directions and based on what directions the particles are coming in from and if collisions have occurred, sends out the particles in their given directions awaiting the next refresh where the appropriate neighbours will take in the particles emitted towards them and repeat the process. At any grid location there is a maximum of 4 particles.

The rules for handling collisions at grid locations are as follows:
- If one, three, or four particles entering a grid location: Allow the particles to continue on in their desired direction.
- If two particles:
  - If the two particles are perpendicular, allow them to continue in their desired direction
  - If the two particles are heading directly towards eachother, output both through the directions perpendicular to their direction. EG: If the particles are approaching from the left and the right, output them, one going up and one going down.

Unlike in methods found in more complex LGA, collusions of three and four particles even if head on, do not result in any change to the particles directions. This is because it is not possible to have more than one particle exiting a grid location in the same direction. And in the case of 4 particles entering, the result would be 4 particles leaving anyway regardless so there is no point calculating anything, just let the particles go through.


The following GIF taken from the HPP wikipedia article (available at https://en.wikipedia.org/wiki/HPP_model) may help at explaining this concept. (if it decides to work! otherwise available at: https://upload.wikimedia.org/wikipedia/commons/4/45/HPP_small.gif)
![](https://upload.wikimedia.org/wikipedia/commons/4/45/HPP_small.gif)

## My implementation
For my own implementation I use three classes: LatSite, Sandbox and Main.  
### LatSite
This is made up of an array 5 integers, one integer per outgoing direction from a location: up, down, left & right, And a fifth integer to be a flag to indicate whether or not the location is a wall (where a particle should reflect back on itself). In practise all these integers are either 0s or 1s. This class also features appropriate methods that are useful for things such as changing the value of the int array at a certain index, getting the number of particles outgoing at the given site (calculated by adding the value of the first 4 elements of the array), and quickly determining if a LatSite is a wall.

### Sandbox
This class is used for holding together the simulation. It is used to store the array of LatSites as well as handling the conversion from LatSites to actual pixels on the screen. This class is where the main method for refreshing the simulation is. This class is instantiated as an object in main. 
Some of the methods this class contains include:
- setPixel: Used by the mouse drawing tool to set all the pixels within a certain radius of the passed location (x,y) to a certain type of LatSite, that being either becoming a wall, or a full LatSite (One where it is emitting particles in all four directions)
- updateSandbox: used to update the core sandbox, applying the rules of the cellular automata.
- drawSandbox: Called from other classes to convert the simulation from an array of latsites to pixels
- convertToScreen2: The updated version of convertToScreen. Who'd guessed that drawing 19000 individual squares would be very slow! This function instead converts the latsites into an image where the pixels represent the latsites. A pixel can be one of six colours: Red for walls, White for empty spaces and for the remaining four following the simple formula: 255 - (255*((Number of particles at given site)/4)) to give us four shades of grey, the lighter the less dense the LatSite. I am quite pleased with the effect of this optimisation as it turned the simulation of something like 19000 particles from running at something like 4fps, back to a smooth framerate. The only downside is the image isn't the clearest however I'm sure there is a fix for this.

### Main
Used to manage the buttons and displaying of the simulation to the screen. Here the sandbox class is instantiated and controlled. Nothing much else notable.

## Limitations
This method of gas automaton is severely limited for several reasons.
- Everything follows the grid structure of up, down, left & right.
- No energy is lost anywhere in the process therefore can run on infinitely.
- Every particle travels at a fixed speed, moving one location site per refresh.


## Why this?
At the time of programming this I had gained quite an interest in Cellular Automata like that seen in Conways game of life, and upon seeing that it was possible to do things like liquid/gas simulation by applying simple rules to a grid of pixels I had to give it a go. While my implementation may not be the cleanest implementation, it does work and serves more as a cool demo to play around with and watch the particles form all kinds of weird shapes as they deform and reflect off of different shaped walls.

## Future of this mini project
I am unlikely to polish this project up and add additional features though if I were to, the following things need to be addressed:
- Custom "Sandbox" sizes that actually work, Despite the constructor for sandbox allowing the passing of a height and width variable, these don't matter much as the display side of things has been hard programmed for 256*256 pixels
- Sort the low quality of the image displayed, perhaps it is anti-aliasing. I bet it is a simple fix.
- Sort out the buttons and simulation in regards to their scaling. Currently the mousedrag which allows users to place particles into the simulation is slightly offset.
- Add more functionality, the ability to change the radius of the particle draw on tool via a button, remove particles and walls etc.
- Add a torus geometry canvas (where if a particle goes off the top it comes back in from the bottom and likewise if leaves to the left, enters through the right.


  
