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
package com.avmoga.dpixel.actors.hero;

import com.avmoga.dpixel.Messages.Messages;
import com.watabou.utils.Bundle;

public enum HeroSubClass {

	NONE(null),

	GLADIATOR("gladiator"),
	BERSERKER("berserker"),

	WARLOCK("warlock"),
	BATTLEMAGE("battlemage"),

	ASSASSIN("assassin"),
	FREERUNNER("freerunner"),

	SNIPER("sniper"),
	WARDEN("warden");

	private String title;

	public String title() {
		return Messages.get(this, title);
	}

	public String desc() {
		return Messages.get(this, title + "_desc");
	}

	HeroSubClass(String title) {
		this.title = title;
	}

	private static final String SUBCLASS = "subClass";

	public void storeInBundle(Bundle bundle) {
		bundle.put(SUBCLASS, toString());
	}

	public static HeroSubClass restoreInBundle(Bundle bundle) {
		String value = bundle.getString(SUBCLASS);
			return valueOf(value);
	}

}
