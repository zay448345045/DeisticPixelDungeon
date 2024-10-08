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
package com.avmoga.dpixel.items.weapon.missiles;

import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Dart extends MissileWeapon {

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.DART;
		rapperValue = 1;

		MIN = 1;
		MAX = 4;

		bones = false; // Finding them in bones would be semi-frequent and
						// disappointing.
	}

	public Dart() {
		this(1);
	}

	public Dart(int number) {
		super();
		quantity = number;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public Item random() {
		quantity = Random.Int(5, 15);
		return this;
	}

	@Override
	public int price() {
		return quantity * 2;
	}
}
