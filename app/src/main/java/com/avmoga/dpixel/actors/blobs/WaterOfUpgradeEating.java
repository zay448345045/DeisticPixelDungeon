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
package com.avmoga.dpixel.actors.blobs;

import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.effects.BlobEmitter;
import com.avmoga.dpixel.effects.Speck;
import com.avmoga.dpixel.items.Generator;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.Stylus;
import com.avmoga.dpixel.items.UpgradeBlobRed;
import com.avmoga.dpixel.items.UpgradeBlobViolet;
import com.avmoga.dpixel.items.UpgradeBlobYellow;
import com.avmoga.dpixel.items.Generator.Category;
import com.avmoga.dpixel.items.potions.Potion;
import com.avmoga.dpixel.items.scrolls.Scroll;
import com.watabou.utils.Random;

public class WaterOfUpgradeEating extends WellWater {

	@Override
	protected Item affectItem(Item item) {

		if (item.isUpgradable()) {
			item = eatUpgradable(item);
		} else if (item instanceof Scroll
				    || item instanceof Potion
				    || item instanceof Stylus) {
			item = eatStandard(item);
		} else {
			item = null;
		}
		
		return item;

	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0);
	}

	private Item eatUpgradable(Item w) {

		int ups = w.level;
		
		Item n = null;

		if (Random.Float()<(ups/10)){
			
			n = new UpgradeBlobViolet();
			
		} else if (Random.Float()<(ups/5)) {
			
			n =  new UpgradeBlobRed();
			
        } else if (Random.Float()<(ups/3)) {
			
			n =  new UpgradeBlobYellow();
		
		} else {
			
			n = Generator.random(Category.SEEDRICH);
		}
		
		return n;
	}
	
	private Item eatStandard(Item w) {

		Item n = null;
        
		if (Random.Float()<0.1f){
			n = new UpgradeBlobYellow();
		} else {
			n = Generator.random(Category.SEEDRICH);
		}
		
		return n;
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
