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
package com.avmoga.dpixel.items.bags;

import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.wands.Wand;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;

public class WandHolster extends Bag {

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.HOLSTER;

		size = 12;
	}

	@Override
	public boolean grab(Item item) {
		return item instanceof Wand;
	}

	@Override
	public boolean collect(Bag container) {
		if (super.collect(container)) {
			if (owner != null) {
				for (Item item : items) {
					((Wand) item).charge(owner);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDetach() {
		for (Item item : items) {
			((Wand) item).stopCharging();
		}
	}

	@Override
	public int price() {
		return 50;
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}
}
