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
import com.avmoga.dpixel.items.RedDewdrop;
import com.avmoga.dpixel.items.VioletDewdrop;
import com.avmoga.dpixel.items.YellowDewdrop;
import com.avmoga.dpixel.items.potions.PotionOfOverHealing;
import com.avmoga.dpixel.levels.Level;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Dewcatcher extends Plant {

	private static final String TXT_DESC = Messages.get(Dewcatcher.class, "desc");
	{
		image = 12;
		plantName = Messages.get(this, "name");
	}

	@Override
	public void activate(Char ch) {
		
		explodeDew(pos);
		if (Random.Int(2)==0){super.activate(ch);}	
		    
		
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed {
		{
			plantName = Messages.get(Dewcatcher.class, "name");

			name = Messages.get(this, "name");
			image = ItemSpriteSheet.SEED_DEWCATCHER;

			plantClass = Dewcatcher.class;
			alchemyClass = PotionOfOverHealing.class;				
		}

		@Override
		public String desc() {
			return TXT_DESC;
		}
		
		
	}
	
public void explodeDew(int cell) {
		
		 for (int n : Level.NEIGHBOURS8) {
			 int c = cell + n;
			 if (c >= 0 && c < Level.getLength() && Level.passable[c]) {
				 
				if (Random.Int(10)==1){Dungeon.level.drop(new VioletDewdrop(), c).sprite.drop();}		
			    else if (Random.Int(5)==1){Dungeon.level.drop(new RedDewdrop(), c).sprite.drop();}
				else if (Random.Int(3)==1){Dungeon.level.drop(new YellowDewdrop(), c).sprite.drop();}
			}
		  }	
		
	}
		
	
}
