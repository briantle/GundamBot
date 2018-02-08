import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import Building.BuildingUtilities;
import Enemy.EnemyBuildings;
import Resources.ResourceGatherer;
import Scouting.Scouting;
import Train_Build_Research.BuildOrders;
import Train_Build_Research.ResearchOrders;
import Train_Build_Research.TrainingOrders;
import bwapi.*;
import bwta.*;

/**
 * 
 * GundamBot is a Terran bot that replicates the Gundam Rush strategy
 * 
 * @author Brian
 *
 */
public class GundamBot extends DefaultBWListener
{
	private Mirror mirror = new Mirror();
	private Game game;
	private Player self;
	
	private boolean cut = false;
	private boolean isEnemyBaseDestroyed = false;
	private static boolean isScoutingIdle = false;
	private Unit scoutUnit = null;
	private Unit myBase = null;
	public static BaseLocation enemyBase = null;
	private static EnemyBuildings enemyBuildings;
    private static List<BaseLocation> possibleEnemyBaseLocations;
	
	ArrayList<Unit> listofBuilders = new ArrayList<Unit>();
	
	public void run()
	{
		mirror.getModule().setEventListener(this);
		mirror.startGame();
	}
	
	@Override
	public void onUnitCreate(Unit unit) {
		System.out.println("New unit discovered " + unit.getType() );
	}
	
	@Override
	public void onStart() 
	{
		game = mirror.getGame();
		self = game.self();
		
		enemyBuildings = new EnemyBuildings();
		
		// Use BWTA to analyze map
		// This may take a few minutes if the map is processed first time!
		System.out.println("Analyzing map...");
		BWTA.readMap();
		BWTA.analyze();
		System.out.println("Map data ready!");
		
        possibleEnemyBaseLocations = BWTA.getStartLocations();
		possibleEnemyBaseLocations.remove(BWTA.getStartLocation(self));
		
		for (Unit myUnit : self.getUnits())
		{
			if (myBase == null && myUnit.getType() == UnitType.Terran_Command_Center)
				myBase = myUnit;
		}
	}
	@Override
	public void onFrame() 
	{
		
		// Update enemy building positions
		enemyBuildings.setEnemyBuildings(game);
		
		// Iterate through my units
		for (Unit myUnit : self.getUnits()) 
		{				
			// For debugging
			game.drawTextMap(myUnit.getPosition().getX(), myUnit.getPosition().getY(), myUnit.getOrder().toString());
			
			// If list is not empty, checked if any of our builders were killed
			for (int i = 0; i < listofBuilders.size(); i++) 
			{
				Unit unit = listofBuilders.get(i);
				
				// Remove the worker from the builder list if it was killed
				if (unit.exists() == false)
					listofBuilders.remove(i);
			}			
			if (myUnit.getType().isWorker() && myUnit != scoutUnit)
			{
				// If we don't have 3 builders in the list
				if (listofBuilders.size() < 3 && self.allUnitCount(UnitType.Terran_SCV) >= 3) 
				{
					// If an empty list, add a builder to it
					if (listofBuilders.size() == 0 && myUnit != scoutUnit)
						listofBuilders.add(myUnit);
					// Store builder in here
					for (int i = 0; i < listofBuilders.size(); i++) 
					{
						if (myUnit != listofBuilders.get(i) && listofBuilders.size() < 3 && myUnit != scoutUnit)
						{
							listofBuilders.add(myUnit);
						}
					}	
				}
			}
			
			// research available tech
			if (myUnit.getType().isBuilding())
				ResearchOrders.canResearch(game, self, myUnit);
			
			// At 70% build time for machine shop, cut off scv production
			if (myUnit.getType() == UnitType.Terran_Machine_Shop && myUnit.getRemainingBuildTime() == 150)
				cut = true;
				
			// If we don't have 20 workers, stop cutoff of scv production
			if (self.allUnitCount(UnitType.Terran_SCV) < 20)
				cut = false;
			
			// Gather resources if we have an idle worker
			if (myUnit.getType().isWorker() && myUnit.isIdle()) 
			{
				// If we have a refinery and there are less than 3 workers gathering gas
				if (self.hasUnitTypeRequirement(UnitType.Terran_Refinery) && ResourceGatherer.gasCarriers(self) < 3){
					ResourceGatherer.gatherGas(myUnit, game, self);
				}
				// Otherwise, gather minerals
				else
					ResourceGatherer.gatherMinerals(myUnit, game);
			}
			
			// Scout positions
			Scouting.scouting(enemyBase, isScoutingIdle, scoutUnit, myBase, game, enemyBuildings, possibleEnemyBaseLocations);
			
			// Train troops as long we haven't maxed out the supply capacity
			if ((self.supplyUsed() / 2) < 200)
			TrainingOrders.trainTroops(game, self, myUnit, cut);
			
            if (!myUnit.getType().isWorker() && !myUnit.getType().isBuilding() && myUnit.isIdle()) {
                attackEnemy(myUnit);
            }
		}
		
		// Find out what we can build
		BuildOrders.canBuild(game, self, listofBuilders);
		
	}
	private void attackEnemy(Unit mySoldier)
	{
		HashSet<Position> enemyBuildingPositions = enemyBuildings.getEnemyBuildings();
		
		if (!enemyBuildingPositions.isEmpty())
		{
			Position enemyBposition = enemyBuildingPositions.iterator().next();
			mySoldier.attack(enemyBposition);
		}
		else
		{
			if (enemyBase != null)
			{
				if (!isEnemyBaseDestroyed) {
					mySoldier.attack(enemyBase.getPosition());
				}
				else {
					ScoutandAttack(mySoldier);
				}
			}
			else {
				ScoutandAttack(mySoldier);
			}
		}
	}
	private void ScoutandAttack(Unit mySoldier) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		Position randomPosition = new Position(random.nextInt(game.mapWidth() * 32), random.nextInt(game.mapHeight() * 32));
		
		if (mySoldier.canAttack(randomPosition)) {
			mySoldier.attack(randomPosition);
		}
									
	}

	@Override
	public void onUnitComplete(Unit unit)
	{
		if (scoutUnit == null && unit.getType().isWorker() && self.allUnitCount(UnitType.Terran_SCV) >= 9){
			scoutUnit = unit;
		}
	}
    @Override
    public void onUnitDestroy(Unit unit) {
        UnitType unitType = unit.getType();

        if (!Objects.equals(unit.getPlayer().getName(), self.getName())) {
            if (BuildingUtilities.isBase(unitType)) {
                isEnemyBaseDestroyed = true;
            }
        }
    }
	public static void main(String[] args) {
		new GundamBot().run();
	}
}
