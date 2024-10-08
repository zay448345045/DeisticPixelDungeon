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
package com.avmoga.dpixel.items.scrolls;

import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.effects.Identification;
import com.avmoga.dpixel.items.Heap;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.utils.GLog;
import com.avmoga.dpixel.windows.WndBag;

public class ScrollOfIdentify extends InventoryScroll {

	{
		name = Messages.get(this, "name");
		inventoryTitle = Messages.get(this, "inv_title");
		mode = WndBag.Mode.UNIDENTIFED;
		consumedValue = 10;

		bones = true;
	}
	@Override
	public void detonateIn(Hero hero){
		Item item = hero.belongings.randomUnequipped();
		onItemSelected(item);
	}
	@Override
	public void detonate(Heap heap){
		if(heap.size() > 1){
			for(Item item : heap.items){
				if (!item.isIdentified()){
					item.identify();
					break;
				}
			}
		}
	}

	@Override
	protected void onItemSelected(Item item) {

		curUser.sprite.parent.add(new Identification(curUser.sprite.center()
				.offset(0, -16)));

		item.identify();
		GLog.i(Messages.get(this, "it_is", item));

		Badges.validateItemLevelAquired(item);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public int price() {
		return isKnown() ? 30 * quantity : super.price();
	}
}
