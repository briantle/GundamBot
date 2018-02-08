package Train_Build_Research;
import java.util.ArrayList;

import Building.BuildingUtilities;
import Resources.ResourceGatherer;
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.TilePosition;
import bwapi.UnitType;

public class BuildOrders
{
	public static void canBuild(Game game, Player self, ArrayList<Unit> listofBuilders) 
	{
		for (int i = 0; i < listofBuilders.size(); i++) 
		{
			Unit unit = listofBuilders.get(i);
			if (!unit.isCarryingMinerals() && !unit.isCarryingGas() && !unit.isConstructing() && unit.exists())
			{
				workerCmd(game, self, unit);
				break;
			}
		}
		for (Unit myUnit : self.getUnits())
		{
			if (buildingCmd(game, self, myUnit) == true)
				break;			
		}
	}
	public static boolean isBeingBuild(Player self, UnitType building) 
	{
		for (Unit myUnit : self.getUnits()) {
			if (myUnit.getType() == building && myUnit.isBeingConstructed()) {
				return true;
			}
		}
		return false;
	}
	public static void buildBuildings(Game game, Player self, Unit worker, UnitType building) 
	{
		TilePosition tp = BuildingUtilities.getBuildTile(game, worker, building, self.getStartLocation());
		// If there is space to build something
		if (tp != null)
		{
			worker.build(building, tp);
		}
	}
	public static void workerCmd(Game game, Player self, Unit myWorker)
	{
		// if we are running out of supplies and have enough minerals, build more supply depots
		if ((self.supplyTotal() - self.supplyUsed() <= 2) && self.minerals() >= 100 && (self.supplyUsed() / 2) < 200)
		{
			if (isBeingBuild(self, UnitType.Terran_Supply_Depot) == false)
				buildBuildings(game, self, myWorker, UnitType.Terran_Supply_Depot);
		}
		// Create barracks at 11 supply used 
		if ((self.supplyUsed() / 2) >= 11 && self.minerals() >= 150 && self.allUnitCount(UnitType.Terran_Barracks) < 1)
		{
			if (isBeingBuild(self, UnitType.Terran_Barracks) == false)
				buildBuildings(game, self, myWorker, UnitType.Terran_Barracks);
		}
		// Create refinery
		if (self.minerals() >= 100 && ResourceGatherer.availableGas(game) == true && self.allUnitCount(UnitType.Terran_Refinery) < 1)
		{
			buildBuildings(game, self, myWorker, UnitType.Terran_Refinery);
		}
		// Create factory
		if (self.minerals() >= 200 && self.gas() >= 100 && self.allUnitCount(UnitType.Terran_Factory) < 2)
		{
			if (isBeingBuild(self, UnitType.Terran_Factory) == false)
				buildBuildings(game, self, myWorker, UnitType.Terran_Factory);
		}			
	}
	public static boolean buildingCmd(Game game, Player self, Unit myBuilding)
	{
		if (myBuilding.getType() == UnitType.Terran_Factory)
		{
			// Create a machine shop
			if (self.minerals() >= 50 && self.gas() >= 50 && myBuilding.canBuildAddon(UnitType.Terran_Machine_Shop, true) 
					&& self.allUnitCount(UnitType.Terran_Machine_Shop) < 1)
			{
				myBuilding.buildAddon(UnitType.Terran_Machine_Shop);
				return true;
			}
		}
		return false;
	}
}
