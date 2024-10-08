package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import exceptions.InvalidTargetException;
import exceptions.MovementException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.characters.Explorer;
import model.characters.Fighter;
import model.characters.Hero;
import model.characters.Medic;
import model.characters.Zombie;
import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import model.world.TrapCell;

public class Game {

	public static Cell[][] map = new Cell[15][15];
	public static ArrayList<Hero> availableHeroes = new ArrayList<Hero>();
	public static ArrayList<Hero> heroes = new ArrayList<Hero>();
	public static ArrayList<Zombie> zombies = new ArrayList<Zombie>();
	public static int row = 15;
	public static int coloumn = 15;

	public static void loadHeroes(String filePath) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Hero hero = null;
			switch (content[1]) {
			case "FIGH":
				hero = new Fighter(content[0], Integer.parseInt(content[2]),
						Integer.parseInt(content[4]),
						Integer.parseInt(content[3]));
				break;
			case "MED":
				hero = new Medic(content[0], Integer.parseInt(content[2]),
						Integer.parseInt(content[4]),
						Integer.parseInt(content[3]));
				break;
			case "EXP":
				hero = new Explorer(content[0], Integer.parseInt(content[2]),
						Integer.parseInt(content[4]),
						Integer.parseInt(content[3]));
				break;
			}
			availableHeroes.add(hero);
			line = br.readLine();

		}
		br.close();

	}
	
	

	public static void startGame(Hero h)  {

		

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new CharacterCell(null);
			}
		}
		availableHeroes.remove(h);
		heroes.add(h);
	
		map[0][0] = new CharacterCell(h);
		h.setLocation(new Point(0, 0));
		
        
		int x=0;
		while(x<10) {
	       
	        int newxint = (int)(Math.random()*15);
			int newyint =(int)(Math.random()*15);
			Point newlocation = new Point(newxint, newyint);
			
				if(Game.map[newlocation.x][newlocation.y] instanceof CharacterCell &&((CharacterCell) Game.map[newlocation.x][newlocation.y]).getCharacter()==null){
					Zombie z = new Zombie();
					z.setLocation(newlocation);
                    Game.map[newlocation.x][newlocation.y] = new CharacterCell(z);
                    zombies.add(z);
                    x++;
				}
			}
			

		
		int i=0;
		while( i< 5) {
			int newxint = (int)(Math.random()*15);
			int newyint =(int)(Math.random()*15);
			Point newlocation = new Point(newxint, newyint);
			if(Game.map[newlocation.x][newlocation.y] instanceof CharacterCell &&((CharacterCell) Game.map[newlocation.x][newlocation.y]).getCharacter()==null){
			  Vaccine v = new Vaccine();
			  Game.map[newlocation.x][newlocation.y] = new CollectibleCell(v);
			  i++;
			}
			
			

		}
		int j=0;
		while(j<5) {
			int newxint = (int)(Math.random()*15);
			int newyint =(int)(Math.random()*15);
			Point newlocation = new Point(newxint, newyint);
			if(Game.map[newlocation.x][newlocation.y] instanceof CharacterCell &&((CharacterCell) Game.map[newlocation.x][newlocation.y]).getCharacter()==null){
			Supply s = new Supply();
			Game.map[newlocation.x][newlocation.y] = new CollectibleCell(s);
			j++;
			}
		}
		int k=0;
        while(k < 5) {
        	int newxint = (int)(Math.random()*15);
			int newyint =(int)(Math.random()*15);
			Point newlocation = new Point(newxint, newyint);
        	if(Game.map[newlocation.x][newlocation.y] instanceof CharacterCell &&((CharacterCell) Game.map[newlocation.x][newlocation.y]).getCharacter()==null){
			Game.map[newlocation.x][newlocation.y] = new TrapCell();
			k++;
        	}
		}
        map[0][0].setVisible(true);
		map[0][1].setVisible(true);
		map[1][0].setVisible(true);
		map[1][1].setVisible(true);

	}

	public static boolean checkWin() {
		// Checks if all vaccines collected
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {

				if (map[i][j] instanceof CollectibleCell && ((CollectibleCell) Game.map[i][j]).getCollectible() instanceof Vaccine) {
					return false;

				}
			}
		}
		// checks if hero has any vaccines unused
		for (int i = 0; i < heroes.size(); i++) {
			if (!heroes.get(i).getVaccineInventory().isEmpty()) {
				return false;
			}
		}
		// checks if there are enough heroes
		if(heroes.size() < 5){
			return false;
		}
		
		return true;
	}

	public static boolean helpercheckhero() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {

				if (map[i][j] instanceof CharacterCell
						&& ((CharacterCell) Game.map[i][j]).getCharacter() instanceof Hero) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean helpercheckGameOver() {
		boolean result = true;
		// Checks if all vaccines collected
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {

				if (map[i][j] instanceof CollectibleCell
						&& ((CollectibleCell) Game.map[i][j]).getCollectible() instanceof Vaccine) {
					result = false;
				}
			}
		}
		// checks if hero has any vaccines unused
		for (int i = 0; i < heroes.size(); i++) {
			if (!heroes.get(i).getVaccineInventory().isEmpty()) {
				result = false;
			}
		}
		if (heroes.isEmpty())
			result = true;
		return result;
	}

	public static boolean checkGameOver() {
		return helpercheckGameOver() || helpercheckhero();
	}



	public static void endTurn() throws InvalidTargetException,
			NotEnoughActionsException, NoAvailableResourcesException,
			MovementException {
		
		
		for (int k = 0; k < 15; k++) {
			for (int j = 0; j < 15; j++) {
				Game.map[k][j].setVisible(false);
			}
		}
	
for (int i=0;i<zombies.size();i++) {
		    
			zombies.get(i).attack();
			zombies.get(i).setTarget(null);
			
			
}
if (zombies.size() < 10) {

	spawnZombie();

}
		
		
for (int j = 0; j < heroes.size(); j++) {
			
			
			heroes.get(j).setActionsAvailable(heroes.get(j).getMaxActions());
			heroes.get(j).setSpecialAction(false);
			heroes.get(j).setTarget(null);
			
			heroes.get(j).helpersetadjacentcells(heroes.get(j).getLocation());
		}


	}

	public static void spawnZombie() {
		Point p = Zombie.createpoint();
		
		Zombie z = new Zombie();
		zombies.add(z);
		z.setLocation(p);
		

		map[z.getLocation().x][z.getLocation().y] = new CharacterCell(z);

	}
	
	

}
