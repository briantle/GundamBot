package Train_Build_Research;
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class TrainingOrders
{
	public static void trainTroops(Game game, Player self, Unit myUnit, boolean cut)
	{
		if (myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50 && cut == false) 
		{
			// Keep creating scvs until the supply reaches 9 and we have not built a supply depot yet
			if ((self.supplyUsed() / 2) < 9 && !self.hasUnitTypeRequirement(UnitType.Terran_Supply_Depot))
			{
				myUnit.train(UnitType.Terran_SCV);
			}
			// Create an additional scv, then we build a barracks
			else if ((self.supplyUsed() / 2) < 11 && !self.hasUnitTypeRequirement(UnitType.Terran_Supply_Depot)) {
				myUnit.train(UnitType.Terran_SCV);
			}
			else if (self.hasUnitTypeRequirement(UnitType.Terran_Barracks)) {
				myUnit.train(UnitType.Terran_SCV);
			}
		}
		// If enough minerals and we have a barracks built, create marines
		if ((self.supplyUsed() / 2) >= 11 && myUnit.getType() == UnitType.Terran_Barracks && self.minerals() >= 100) {
			myUnit.train(UnitType.Terran_Marine);
		}
		if (myUnit.getType() == UnitType.Terran_Factory)
		{
			// train vultures if we have enough minerals and our factory doesn't have machine shop addon
			if (self.minerals() >= 75 && myUnit.canBuildAddon() == true) {
				myUnit.train(UnitType.Terran_Vulture);
			}
			// train tanks if we have enough minerals and gas and our factory has machine shop addon
			if (self.minerals() >= 150 && self.gas() >= 100 && myUnit.canBuildAddon(UnitType.Terran_Machine_Shop) == false)
			{
				if (self.allUnitCount(UnitType.Terran_Factory) >= 2)
				{
					myUnit.train(UnitType.Terran_Siege_Tank_Tank_Mode);
				}
			}
		}		
	}
}
