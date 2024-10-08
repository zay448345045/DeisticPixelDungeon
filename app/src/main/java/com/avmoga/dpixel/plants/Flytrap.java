/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.avmoga.dpixel.plants;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.blobs.Blob;
import com.avmoga.dpixel.actors.blobs.WaterOfUpgradeEating;
import com.avmoga.dpixel.actors.blobs.WellWater;
import com.avmoga.dpixel.items.potions.PotionOfOverHealing;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;

public class Flytrap extends Plant {

	private static final String TXT_DESC = Messages.get(Flytrap.class, "desc");

	{
		image = 15;
		plantName = Messages.get(this, "name");
	}

	@Override
	public void activate(Char ch) {
		if (ch==null){
		 if (WellWater.affectCellPlant(pos)){
			super.activate(null);	
		    }
		}
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed {
		{
			plantName = Messages.get(Flytrap.class, "name");

			name = Messages.get(this, "name");
			image = ItemSpriteSheet.SEED_FLYTRAP;

			plantClass = Flytrap.class;
			alchemyClass = PotionOfOverHealing.class;
		}

		@Override
		public String desc() {
			return Messages.get(Plant.class, "seeddesc", plantName);
		}

		@Override
		public Plant couch(int pos) {
			GameScene.add(Blob.seed(pos, 1, WaterOfUpgradeEating.class));
			return super.couch(pos);
		}
	}
		
	public static boolean checkWater(){
		
	WellWater water = (WellWater) Dungeon.level.blobs.get(WaterOfUpgradeEating.class);
	  if (water == null) {
		return false;
		} else if (water != null && water.volume==0) {
	    return false;
		} else {
		return true;
		}
	 } 
}
