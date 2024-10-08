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
package com.avmoga.dpixel.actors.mobs.npcs;

import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.items.ActiveMrDestructo;
import com.avmoga.dpixel.items.InactiveMrDestructo;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.Mushroom;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.TinkererSprite;
import com.avmoga.dpixel.utils.Utils;
import com.avmoga.dpixel.windows.WndQuest;
import com.avmoga.dpixel.windows.WndTinkerer2;

public class Tinkerer2 extends NPC {

	{
		name = Messages.get(Tinkerer1.class, "name");
		spriteClass = TinkererSprite.class;
	}

	private static final String TXT_DUNGEON = Messages.get(Tinkerer1.class, "dungeon3");


	private static final String TXT_MUSH = Messages.get(Tinkerer1.class, "mush");

	@Override
	protected boolean act() {
		throwItem();
		return super.act();
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public String defenseVerb() {
		return Messages.get(Tinkerer1.class, "def");
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public void add(Buff buff) {
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public void interact() {

		sprite.turnTo(pos, Dungeon.hero.pos);
		Item item = Dungeon.hero.belongings.getItem(Mushroom.class);
		Item inmrd = Dungeon.hero.belongings.getItem(InactiveMrDestructo.class);
		Item acmrd = Dungeon.hero.belongings.getItem(ActiveMrDestructo.class);
			if (item != null && inmrd != null) {
				GameScene.show(new WndTinkerer2(this, item, inmrd));
			} else if (item != null && acmrd != null) {
				GameScene.show(new WndTinkerer2(this, item, acmrd));
			} else if (item != null) {
				GameScene.show(new WndTinkerer2(this, item, null));
			} else {
				tell(TXT_DUNGEON);
			}
		
	}

	private void tell(String format, Object... args) {
		GameScene.show(new WndQuest(this, Utils.format(format, args)));
	}

	@Override
	public String description() {
		return Messages.get(Tinkerer1.class, "desc2");
	}
}
