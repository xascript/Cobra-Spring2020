/* 
 * This class is meant to be the the View aspect of the MVC model for our project.
 * 
 * When editing:
 * It should be noted that modifying the appearance can be tricky with JavaFX;
 * typically, combining CSS and JFX graphics libs on an element won't work.
 * 
 * 
 */

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/* the JavaFX environment adaption of MadMansion */
public class MadMansionFX extends Application {
	
	/* the most parent container */
	private static BorderPane window;
	
	/* basically something to hold the title */
	private static FlowPane topFlow;
	
	/* for the list of available commands */
	private static GridPane rightPane;
	
	/* */
	private static TextArea commandArea;
	
	/* where you enter in stuff */
	private static TextArea console;
	
	/* where stuff comes up at */ 
	private static TextArea interactionPane;
	
	private static Player player;
	
	private static Room[] roomTracker;
	
	private static ArrayList<Puzzle> puzzleList;
	
	private static ArrayList<Item> itemsList;
	
	private static ArrayList<Monster> monsterList;
	
	static void createAndShowGUI(Stage primaryStage) {
		window = new BorderPane();
		rightPane = new GridPane();
		Font titleFont = Font.font("Chalkboard", FontWeight.THIN, 32);
		topFlow = new FlowPane();

		Label titleLabel = new Label("MadMansion FX");
		topFlow.getChildren().add(titleLabel);
		
		CommandHandler handler = new CommandHandler();
		
		titleLabel.setFont(titleFont);
		window.setTop(titleLabel);
		commandArea = new TextArea();
		
		console = new TextArea();
		console.setOnKeyPressed(handler);
		window.setBottom(console);
		rightPane.add(commandArea, 0, 0);
		
		commandArea.setEditable(false);
		commandArea.setText("go west: w" + "\n" + "go east: e" );
		window.setRight(rightPane);
		titleLabel.setStyle("-fx-padding: 10; -fx-background-color: #CCFF99;");
		commandArea.setWrapText(true);
		commandArea.setPrefWidth(100);
		commandArea.setStyle("-fx-background-color: #2c2c2e;");
		topFlow.setStyle(titleLabel.getStyle());
		topFlow.setStyle("-fx-background-color: #CCFF99;");
		

		GridPane g = new GridPane();
		GridPane leftPanel = new GridPane();
		leftPanel.setPadding(new Insets(5));
		leftPanel.setVgap(10);

		window.setLeft(leftPanel);
		
		interactionPane = new TextArea();
		interactionPane.setEditable(false);
		
		//interactionPane.setFont(font);
		window.setCenter(g);
		g.setMinSize(200, 200);
		interactionPane.setStyle("-fx-focus-color: transparent;\n" + 
				"-fx-border-style: none;\n" + 
				"-fx-background-radius: 0.0px;\n" + 
				"-fx-border-radius: 0.0px;-fx-font-family: Consolas; -fx-highlight-fill: #00ff00; "
				+ "-fx-highlight-text-fill: #000000; -fx-text-fill: #2c2c2e; "
				+ "-fx-background-color:#000000;");
		
		
		g.add(interactionPane, 0, 1);
		
		Scene scene = new Scene(window, 600, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("MadMansion FX");
		primaryStage.show();
	}
	
	/* this method gets the last user-entered command */
	public static String getCommand() {
		return console.getText();
	}
	
	/* this method defines what happens after the user clicks ENTER */
	public static void relay() {
		
		if (CommandHandler.peekCommand().contains("inspect")) {
			for (Item i : roomTracker[player.getRoom()].getItems()) {
				interactionPane.setText(interactionPane.getText() + "\n" + i.getItemName());
			}
		}
		
		if (CommandHandler.peekCommand().contains("pick up")) {
			for (Item i : roomTracker[player.getRoom()].getItems()) {
				if (CommandHandler.peekCommand().contains(i.getItemName())) {
					player.addItem(i);
					roomTracker[player.getRoom()].removeItem(i);
					break;
				}
			}
		}
		
		if (CommandHandler.peekCommand().contains("drop")) {
			
		}
		
		if (CommandHandler.peekCommand().contains("inventory")) {
			System.out.println("inventory");
			for (int i = 0; i < player.getInventory().length - 1; i++) {
				if (player.getInventory()[i] != null) {
					interactionPane.setText(interactionPane.getText() + "\n" + player.getInventory()[i].getItemName());
					System.out.println("\n" + player.getInventory()[i].getItemName());
				}
			}
		}
		
		
		/* this is the connection between View and Controller */
		interactionPane.setText(interactionPane.getText() + "\n" + "> " + CommandHandler.peekCommand());
		
		/* if the room has a puzzle: */
		if (roomTracker[player.getRoom()].hasPuzzle() && !roomTracker[player.getRoom()].getPuzzle().isSolved()) {
			
			/* if the puzzle is on attempt 1, it is actually over. */
			if (roomTracker[player.getRoom()].getPuzzle().getAttempts() == 1) {
				System.out.println("You lost:(");
			}
			
			// if the user's input contains the correct answer
			else if (CommandHandler.peekCommand().trim().toLowerCase().contains(roomTracker[player.getRoom()].getPuzzle().getAnswer())) {
				roomTracker[player.getRoom()].removePuzzle(roomTracker[player.getRoom()].getPuzzle());
				roomTracker[player.getRoom()].getPuzzle().solve();
				System.out.println("SOLVED");
				interactionPane.setText(interactionPane.getText() + "\n" + "You have solved the puzzle! \n"  + player.getPlayerID() + " entered Room " + player.getRoom() + "!");
				
			}
			else {
				System.out.println(roomTracker[player.getRoom()].getPuzzle().getAnswer());
				System.out.println("Not solved!");
				System.out.println(roomTracker[player.getRoom()].getPuzzle().getAttempts());
				roomTracker[player.getRoom()].getPuzzle().setAttempts(roomTracker[player.getRoom()].getPuzzle().getAttempts() - 1);
			}
		}
		
		else if (roomTracker[player.getRoom()].hasMonster() && roomTracker[player.getRoom()].getMonster().isAlive()) {
			
			if (roomTracker[player.getRoom()].getMonster().getTurn()) {
				interactionPane.setText(interactionPane.getText() + "\n" + roomTracker[player.getRoom()].getMonster().getMonsterName() 
						+ " attacked!");
				player.setHealth(player.getHealth() - roomTracker[player.getRoom()].getMonster().getAttack());

				interactionPane.setText(interactionPane.getText() + "\n" + "Your health: " + player.getHealth());
				roomTracker[player.getRoom()].getMonster().getAttack();
			}
			else {
				roomTracker[player.getRoom()].getMonster().incurDamage(2);
				interactionPane.setText(interactionPane.getText() + "\n" + 
				"You attacked the " + roomTracker[player.getRoom()].getMonster().getMonsterName() + "!");
				interactionPane.setText(interactionPane.getText() + "\n" + 
						"Their health: " + roomTracker[player.getRoom()].getMonster().getHealth() + "!");
				
			}
			if (roomTracker[player.getRoom()].getMonster().getHealth() <= 0) {
				
				interactionPane.setText(interactionPane.getText() + "\n" +
						"You have defeated the " + roomTracker[player.getRoom()].getMonster().getMonsterName() + "!");
				if (roomTracker[player.getRoom()].getMonster().getItems() != null 
						&& roomTracker[player.getRoom()].getMonster().getItems().size() > 0) {
					for (Item monsterDrop : roomTracker[player.getRoom()].getMonster().getItems()) {
						if (!monsterDrop.isDrop()) {
							roomTracker[player.getRoom()].addItem(monsterDrop);
						}
					}
					interactionPane.setText(interactionPane.getText() + "\n" + "I think it dropped something...");
				}
				roomTracker[player.getRoom()].getMonster().setDead();
			}
			
		}
		
		
		
		
		/* no puzzle, so the user navigates the map */
		else 
		{
			if (CommandHandler.peekCommand().contains("north")) {
				player.setRoomNumber(roomTracker[roomTracker[player.getRoom()].getNorth()].getNumber());
				interactionPane.setText(interactionPane.getText() + "\n" + player.getPlayerID() + " entered Room " + player.getRoom() + "!");
				System.out.println(player.getRoom());
			}
			else if (CommandHandler.peekCommand().contains("south")) {
				player.setRoomNumber(roomTracker[roomTracker[player.getRoom()].getSouth()].getNumber());
				interactionPane.setText(interactionPane.getText() + "\n" + player.getPlayerID() + " entered Room " + player.getRoom() + "!");
				System.out.println(player.getRoom());
			}
			else if (CommandHandler.peekCommand().contains("east")) {
				player.setRoomNumber(roomTracker[roomTracker[player.getRoom()].getEast()].getNumber());
				interactionPane.setText(interactionPane.getText() + "\n" + player.getPlayerID() + " entered Room " + player.getRoom() + "!");
				System.out.println(player.getRoom());
			}
			else if (CommandHandler.peekCommand().contains("west")) {
				player.setRoomNumber(roomTracker[roomTracker[player.getRoom()].getWest()].getNumber());
				interactionPane.setText(interactionPane.getText() + "\n" + player.getPlayerID() + " entered Room " + player.getRoom() + "!");
				System.out.println(player.getRoom());
			}
			
			else {
				interactionPane.setText(interactionPane.getText() + "\n" + " Unrecognizable command.");
			}
		}
		
		
		updateView();
		console.clear();
	}
	
	public static void main(String[] args) {
		GameLoader.init();
		GameLoader.run();
		roomTracker = GameLoader.getRooms();
		PlayerLoader.init();
		PlayerLoader.run();
		player = PlayerLoader.getPlayer();
		player.setHealth(100);
		PuzzleLoader.init();
		PuzzleLoader.run();
		puzzleList = PuzzleLoader.getPuzzles();
		ItemLoader.init();
		ItemLoader.run();
		itemsList = ItemLoader.getItems();
		MonsterLoader.init();
		MonsterLoader.run();
		monsterList = MonsterLoader.getMonsters();
		
		
		for (Monster m : monsterList) {
			System.out.println(m.getMonsterName() + " drops: ");
			for (int id : m.getItemIDs()) {
				for (Item item : itemsList) {
					if (id == item.getItemID()) {
						m.addItem(item);
					}
				}
			}
			for (Item i : m.getItemDropPool()) {
				System.out.println(i.getItemName() + "\n");
			}
		}
		
		
		
		System.out.println("----------------");
		System.out.println("FOR TESTING: ");
		for (Puzzle p : puzzleList) {
			roomTracker[p.getRoom()].setPuzzle(p);
			System.out.println("There is a puzzle in Room " + p.getRoom());
		}
		System.out.println("----------------");
		
		System.out.println("----------------");
		System.out.println("FOR TESTING: ");
		for (Monster m : monsterList) {
			roomTracker[m.getRoom()].setMonster(m);
			System.out.println("There is a monster in Room " + m.getRoom());
		}
		System.out.println("----------------");
		
		System.out.println("----------------");
		System.out.println("FOR TESTING: ");
		
		for (int i = 1; i < roomTracker.length; i++) {
			if (roomTracker[i].getItems().size() > 0) {
				System.out.println("e");
				for (Item roomItem : roomTracker[i].getItems()) {
					
					System.out.println("There is a " + roomItem.getItemName() + " in Room " + roomTracker[i].getNumber());
				}
			}
		}
		
		
		
		for (int i = 1; i < roomTracker.length; i++) {
			if (roomTracker[i].hasMonster()) {
				
				for (Item item : roomTracker[i].getMonster().getItems()) {
					Random r = new Random();
					int dropChance = r.nextInt(3);
					if (dropChance == 0) {
						roomTracker[i].addItem(item);
						item.setDropped(true);
						System.out.println("Item drop in " + roomTracker[i].getNumber());
					}
				}
				
				
				
				
			}
			
			
			
			
		}
		
		
		
		
		launch(args);
		
	}
	
	
	public void start(Stage primaryStage) {
		createAndShowGUI(primaryStage);
		updateView();
	}
	
	private static void updateView() {
		
		/* that TextArea on the right */
		commandArea.clear();
		
		/* we're lazy so let's use a sentinel value */
		if (roomTracker[player.getRoom()].getNorth() != 0) {
			commandArea.setText(commandArea.getText() + "[NORTH]:\nRoom " + roomTracker[player.getRoom()].getNorth() + "\n");
		}
		
		if (roomTracker[player.getRoom()].getEast() != 0) {
			commandArea.setText(commandArea.getText() + "[EAST]:\nRoom " + roomTracker[player.getRoom()].getEast() + "\n");
		}
		
		if (roomTracker[player.getRoom()].getSouth() != 0) {
			commandArea.setText(commandArea.getText() + "[SOUTH]:\nRoom " + roomTracker[player.getRoom()].getSouth() + "\n");
		}
		
		if (roomTracker[player.getRoom()].getWest() != 0) {
			commandArea.setText(commandArea.getText() + "[WEST]:\nRoom " + roomTracker[player.getRoom()].getWest() + "\n");
		}
		
		if (roomTracker[player.getRoom()].hasPuzzle() && !(roomTracker[player.getRoom()].getPuzzle().isSolved())) {
			interactionPane.setText(interactionPane.getText() + "\n" + roomTracker[player.getRoom()].getDescription());
			runPuzzle();
		}
		if (roomTracker[player.getRoom()].hasMonster() && roomTracker[player.getRoom()].getMonster().isAlive()) {
			//interactionPane.setText(interactionPane.getText() + "\n" + roomTracker[player.getRoom()].getDescription());
			runBattle();
		}
		else {
			interactionPane.setText(interactionPane.getText() + "\n" + roomTracker[player.getRoom()].getDescription());
		}
		
	}
	
	private static void runPuzzle() {
		interactionPane.setText(interactionPane.getText() 
				+ "\n"
				+ "\n" + "# Hey... champ in the making!" 
				+ "\n" + "# ..." 
				+ "\n" + "# Riddle me this!" 
				+ "\n"
				+ "\n" + roomTracker[player.getRoom()].getPuzzle().getDescription());
	}
	
	private static void runBattle() {
		interactionPane.setText(interactionPane.getText() 
				+ "\n"
				+ "\n" + "The horror... You've encountered a " + roomTracker[player.getRoom()].getMonster().getMonsterName() + "! \n Press enter or return on your keyboard to attack!"
				+ "\n");
	}
	
	
	
}
