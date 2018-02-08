package Enemy;
import java.util.HashSet;
import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Game;
import bwapi.Unit;

public class EnemyBuildings 
{
	private HashSet<Position> enemyBuildingMemory;
	
	public EnemyBuildings() {
		this.enemyBuildingMemory = new HashSet<Position>();
	}
	
	public void setEnemyBuildings(Game game)
	{
		List<Unit> listofEnemies = game.enemy().getUnits();
		
		//always loop over all currently visible enemy units
		for (Unit enemyUnits : listofEnemies) 
		{
			//if this unit is in fact a building
			if (enemyUnits.getType().isBuilding())
			{
				//check if we have it's position in memory and add it if we don't
				if (!enemyBuildingMemory.contains(enemyUnits.getPosition())) {
					enemyBuildingMemory.add(enemyUnits.getPosition());
				}
			}
		}
		
		// loop over all the positions that we remember
		for (Position p : enemyBuildingMemory)
		{
			// compute the TilePosition corresponding to our remembered Position p
			TilePosition tileCorrespondingToP = new TilePosition(p.getX()/32, p.getY()/32);
			
			// if that tile is currently visible to us
			if (game.isVisible(tileCorrespondingToP))
			{
				// loop over all the visible enemy buildings and find out if at least
				// one of them is still at that remembered position
				boolean buildingStillThere = false;
				for (Unit u : game.enemy().getUnits())
				{
					// The building still exists / is still at that location
					if ((u.getType().isBuilding()) && (u.getPosition().equals(p)))
					{
						buildingStillThere = true;
						break;
					}
				}
				
				// If that building is not at that spot, remove that position from our memory
				if (buildingStillThere == false)
				{
					enemyBuildingMemory.remove(p);
					break;
				}
			}
		}
	}
	public HashSet<Position> getEnemyBuildings() {
		return this.enemyBuildingMemory;
	}
}
