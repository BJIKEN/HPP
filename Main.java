package application;
	

import java.io.IOException;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	int timer = 0;
	int timeInterval = 0;
	int tapX = 0;
	int tapY = 0;
	boolean pouring = false;
	int particleType = 0;
	int radius = 5;
	boolean simulating = false;
	boolean reset = false;
	@Override
	public void start(Stage primaryStage) {
		try {	
			BorderPane bp = new BorderPane();
			Group root = new Group();
			Sandbox s = new Sandbox(256,256,root);
			setMouseEvents(primaryStage,s);
			root = s.drawSandbox();
			AnchorPane SP = new AnchorPane();
			VBox hb = new VBox();
			 new AnimationTimer()			// create timer
		    	{
		    		public void handle(long currentNanoTime) {

		    			Group root;
		    			
		    			if(simulating == true) {
		    				s.updateSandbox();
		    			}
		    			if(pouring == true) {
		    				System.out.println("Particle type: "+particleType);
		    				s.setPixel(tapX,tapY,radius,particleType);

		    				
		    				pouring = false;
		    			}
		    			if(reset == true) {
		    				simulating = false;
		    				s.resetSandbox();
		    				reset = false;
		    			}
	    				root = s.drawSandbox();
	    				//hb.getChildren().add(root);
	    				
		    			
		    			
		    		}
		    	}.start();	
			
		    	
		    hb.getChildren().addAll(getButtons(s),root);
			
			Scene scene = new Scene(hb,1024,1100);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void setMouseEvents (Stage stage,Sandbox s) {
	       stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
	    	       new EventHandler<MouseEvent>() {
	    	           @Override
	    	           public void handle(MouseEvent e) {
	    	        	   
	    	        	   float x = (float)e.getX();//get mouse X
	    	        	   float y = (float)e.getY();//get mouse Y
	    	        	   
	    	        	   if((0<x && x<1024)&&(0<y&&y<1024)) {//is within bounds 0<x<512 & 0<y<512
	    	        		   int actualX = (int)(x/4);//convert mouse X to grid coordinates
	    	        		   int actualY = (int)(y/4);//convert mouse Y to grid coordinates
	    	        		   if(e.isPrimaryButtonDown()) {
	    	        			   pouring = true;
	    	        			   System.out.println("Clicked: "+actualX+" , "+actualY);
		    	        		   tapX = actualX;
		    	        		   tapY = actualY;
		    	        	   }
	    	        		   if(e.isSecondaryButtonDown()) {
	    	        			   pouring = true;
	    	        			   tapX = actualX;
	    	        			   tapY = actualY;
	    	        		   }
	    	        		   
	    	        	   }
	    	        	   
	    	           }
	    	       });
	       stage.addEventHandler(MouseEvent.MOUSE_RELEASED, 
	    	       new EventHandler<MouseEvent>() {
	    	           @Override
	    	           public void handle(MouseEvent e) {
	    	        	   
	    	        	   pouring = false;
	    	        	   
	    	           }
	    	       });
	      
	       
	}
	//All the following are used for the buttons at the top of the screen
	private HBox getButtons(Sandbox s) {
		Button[] buttons = new Button[4];
		Button startButton = new Button("startButton");
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				simulating = !simulating;
				System.out.println(s.getParticleCount());
				}
			});
		Button addParticleBtn = new Button("Add Particle");
		addParticleBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Add Particle");
					radius = 5;
					particleType = 0;
				}
			});
		Button resetBtn = new Button("Reset");
		resetBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("resetting");
					reset = true;
				}
			});
		Button addWallBtn = new Button("Add Wall");
		addWallBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Add water");
					radius = 3;
					particleType = 1;
				}
			});
		buttons[0] = startButton;
		buttons[1] = addParticleBtn;
		buttons[2] = resetBtn;
		buttons[3] = addWallBtn;
		return new HBox(buttons);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
