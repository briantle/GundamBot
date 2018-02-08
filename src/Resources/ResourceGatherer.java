package Resources;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.Game;
import bwapi.Player;

public class ResourceGatherer
{
	public static Unit findClosestMineral(Unit myUnit, Game game) {
		Unit closestMineral = null;
		// find closest mineral
		for (Unit neutralUnit : game.neutral().getUnits())
		{
			if (neutralUnit.getType().isMineralField()) {
				if (closestMineral == null || myUnit.getDistance(neutralUnit) < myUnit.getDistance(closestMineral)) {
					closestMineral = neutralUnit;
				}
			}
		}		
		return closestMineral;
	}
	public static void gatherMinerals(Unit myUnit, Game game)
	{
		Unit closestMineral = findClosestMineral(myUnit, game);
		if (closestMineral != null)
			myUnit.gather(closestMineral, false);
	}
	public static void gatherGas(Unit myUnit, Game game, Player self)
	{
		Unit closestGas = null;
		for (Unit units : self.getUnits()) {
			if (units.getType() == UnitType.Terran_Refinery) {
				if (closestGas == null || myUnit.getDistance(units) < myUnit.getDistance(closestGas)) {
					closestGas = units;
				}
			}
		}
		myUnit.gather(closestGas, false);
	}
	public static boolean availableGas(Game game)
	{
		for (Unit neutralUnit : game.neutral().getUnits()) 
		{
			if (neutralUnit.getType() == UnitType.Resource_Vespene_Geyser)
			{
				return true;
			}
		}
		return false;
	}
	public static int gasCarriers(Player self) {
		int carriers = 0;
		for (Unit myUnit : self.getUnits()) {
			if (myUnit.isGatheringGas()) {
				carriers++;
			}
		}
		return carriers;
	}
}
