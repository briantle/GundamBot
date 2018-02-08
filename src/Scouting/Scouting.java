package Scouting;
import bwapi.Unit;
import bwta.BaseLocation;

import java.util.List;

import Enemy.EnemyBuildings;
import Resources.ResourceGatherer;
import bwapi.Game;

public class Scouting
{
	private static BaseLocation baseToScout = null;
	
	public static void scouting(BaseLocation enemyBase, boolean isScoutingIdle, Unit scout, Unit myBase, Game game, EnemyBuildings enemyBuildings, List<BaseLocation> possibleEnemyBaseLocations)
	{
		isScoutDead(scout);
		
		if (scout == null) {
			return;
		}
		
		// Retreat if we are under attack
		if (scout.isUnderAttack()) 
		{
			enemyBase = baseToScout;
			backToBase(game, myBase, scout);
		}
        // add isScoutingIdle because scouting drone can be idle for more
        // than one frame and this behavior causes that drone can't scout
        // last base when map has four starting locations
		if (scout.isIdle() && !isScoutingIdle)
		{
			isScoutingIdle = true;
			
			if (enemyBuildings.getEnemyBuildings().isEmpty()) 
			{
				possibleEnemyBaseLocations.remove(baseToScout);
				 
				 baseToScout = getBase(scout, possibleEnemyBaseLocations);
				 scout.move(baseToScout.getPosition());
				 
				 if (possibleEnemyBaseLocations.size() == 1){
				 	enemyBase = baseToScout;
				 } 
			}
			else
			{
				Unit harassWorker = null;
				for (Unit enemyUnit : game.enemy().getUnits())
				{
					if (enemyUnit.getType().isWorker() && enemyUnit.isGatheringMinerals() || enemyUnit.isCarryingMinerals())
					{
						harassWorker = enemyUnit;
						break;
					}
				}	
				scout.attack(harassWorker);
				enemyBase = baseToScout;
			}
		}
		 else{
		 	isScoutingIdle = false;
		 }
	}
	public static void backToBase(Game game, Unit myBase, Unit myScout)
	{
		Unit baseMineral = ResourceGatherer.findClosestMineral(myBase, game);
		// If the main base still has mineral fields, send the scout back to mine
		if (baseMineral != null)
			myScout.gather(baseMineral);
		// Otherwise, goodbye my good friend
	}
    private static BaseLocation getBase(Unit scout, List<BaseLocation> possibleEnemyBaseLocations)
    {
        BaseLocation nearestBaseLocation = null;
        int nearestDistance = Integer.MAX_VALUE;

        for (BaseLocation baseLocation : possibleEnemyBaseLocations) {
            int distance = scout.getDistance(baseLocation.getPosition());

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestBaseLocation = baseLocation;
            }
        }
        return nearestBaseLocation;
    }
    public static void isScoutDead(Unit myScout)
    {
    	if (myScout != null && !myScout.exists())
    		myScout = null;
    }
}
