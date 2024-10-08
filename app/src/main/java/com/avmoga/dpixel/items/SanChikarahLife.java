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
package com.avmoga.dpixel.items;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;

public class SanChikarahLife extends Item {

	{
		name = "三相之力生命碎片";
		image = ItemSpriteSheet.SANCHIKARAH;

		stackable = false;
		unique = true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}


	@Override
	public boolean doPickUp(Hero hero) {
		if (super.doPickUp(hero)) {

			if (Dungeon.level != null && Dungeon.depth==32) {
				Dungeon.sanchikarahlife= true;							
			}

			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int price() {
		return 10 * quantity;
	}

	@Override
	public String info() {
		return "一个强力碎片。三个碎片需要被拼在一起来发挥作用。";
	}
}
