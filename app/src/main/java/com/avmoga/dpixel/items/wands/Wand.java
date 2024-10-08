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
package com.avmoga.dpixel.items.wands;

import com.avmoga.dpixel.Assets;
import com.avmoga.dpixel.Badges;
import com.avmoga.dpixel.Dungeon;
import com.avmoga.dpixel.Messages.Messages;
import com.avmoga.dpixel.actors.Actor;
import com.avmoga.dpixel.actors.Char;
import com.avmoga.dpixel.actors.buffs.Buff;
import com.avmoga.dpixel.actors.buffs.Invisibility;
import com.avmoga.dpixel.actors.hero.Hero;
import com.avmoga.dpixel.actors.hero.HeroClass;
import com.avmoga.dpixel.effects.MagicMissile;
import com.avmoga.dpixel.items.Item;
import com.avmoga.dpixel.items.ItemStatusHandler;
import com.avmoga.dpixel.items.KindOfWeapon;
import com.avmoga.dpixel.items.bags.Bag;
import com.avmoga.dpixel.items.rings.RingOfMagic.Magic;
import com.avmoga.dpixel.mechanics.Ballistica;
import com.avmoga.dpixel.scenes.CellSelector;
import com.avmoga.dpixel.scenes.GameScene;
import com.avmoga.dpixel.sprites.ItemSpriteSheet;
import com.avmoga.dpixel.ui.QuickSlotButton;
import com.avmoga.dpixel.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Wand extends KindOfWeapon {

	private static final int USAGES_TO_KNOW = 40;

	public static HashSet<Class<? extends Wand>> getKnown() {
		return handler.known();
	}

	public static HashSet<Class<? extends Wand>> getUnknown() {
		return handler.unknown();
	}
	private static final String TXT_SILENCED = "法杖因沉默诅咒无法释放力量！";
	public static final String AC_ZAP = Messages.get(Wand.class, "ac_zap");

	private static final String TXT_WOOD = Messages.get(Wand.class, "wood");
	private static final String TXT_DAMAGE = Messages.get(Wand.class, "damage");
	private static final String TXT_WEAPON = Messages.get(Wand.class, "weapon");

	private static final String TXT_FIZZLES = Messages.get(Wand.class, "fizzles");
	private static final String TXT_SELF_TARGET = Messages.get(Wand.class, "self_target");

	private static final String TXT_IDENTIFY = Messages.get(Wand.class, "identify");

	private static final String TXT_REINFORCED = Messages.get(Wand.class, "re");

	private static final float TIME_TO_ZAP = 1f;

	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;

	protected Charger charger;

	private boolean curChargeKnown = false;

	private int usagesToKnow = USAGES_TO_KNOW;

	protected boolean hitChars = true;

	private static final Class<?>[] wands = { WandOfTeleportation.class,
			WandOfSlowness.class, WandOfFirebolt.class, WandOfPoison.class,
			WandOfRegrowth.class, WandOfBlink.class, WandOfLightning.class,
			WandOfAmok.class, WandOfTelekinesis.class, WandOfFlock.class,
			WandOfDisintegration.class, WandOfAvalanche.class };
	private static final String[] woods = {"冬青","紫杉","乌木","樱桃",
			"柚木","罗文","柳树","桃心","竹子","紫心",
			"橡木","桦木"  };
	private static final Integer[] images = { ItemSpriteSheet.WAND_HOLLY,
			ItemSpriteSheet.WAND_YEW, ItemSpriteSheet.WAND_EBONY,
			ItemSpriteSheet.WAND_CHERRY, ItemSpriteSheet.WAND_TEAK,
			ItemSpriteSheet.WAND_ROWAN, ItemSpriteSheet.WAND_WILLOW,
			ItemSpriteSheet.WAND_MAHOGANY, ItemSpriteSheet.WAND_BAMBOO,
			ItemSpriteSheet.WAND_PURPLEHEART, ItemSpriteSheet.WAND_OAK,
			ItemSpriteSheet.WAND_BIRCH };

	private static ItemStatusHandler<Wand> handler;

	private String wood;

	{
		defaultAction = AC_ZAP;
	}

	@SuppressWarnings("unchecked")
	public static void initWoods() {
		handler = new ItemStatusHandler<Wand>((Class<? extends Wand>[]) wands,
				woods, images);
	}

	public static void save(Bundle bundle) {
		handler.save(bundle);
	}

	@SuppressWarnings("unchecked")
	public static void restore(Bundle bundle) {
		handler = new ItemStatusHandler<Wand>((Class<? extends Wand>[]) wands,
				woods, images, bundle);
	}

	public Wand() {
		super();

		calculateDamage();

		try {
			syncVisuals();
		} catch (Exception e) {
			// Wand of Magic Missile
		}
	}

	@Override
	public void syncVisuals() {
		image = handler.image(this);
		wood = handler.label(this);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (curCharges > 0 || !curChargeKnown) {
			actions.add(AC_ZAP);
		}
		if (hero.heroClass != HeroClass.MAGE) {
			actions.remove(AC_EQUIP);
			actions.remove(AC_UNEQUIP);
		}
		return actions;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		onDetach();
		return super.doUnequip(hero, collect, single);
	}

	@Override
	public void activate(Hero hero) {
		charge(hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_ZAP)) {

			curUser = hero;
			curItem = this;
			if(!curUser.isSilenced()){
				GameScene.selectCell(zapper);
			} else{
				GLog.h(TXT_SILENCED);
			}

		} else {

			super.execute(hero, action);

		}
	}

	protected abstract void onZap(int cell);

	@Override
	public boolean collect(Bag container) {
		if (super.collect(container)) {
			if (container.owner != null) {
				charge(container.owner);
			}
			return true;
		} else {
			return false;
		}
	};

	public void charge(Char owner) {
		if (charger == null)
			(charger = new Charger()).attachTo(owner);
	}

	@Override
	public void onDetach() {
		stopCharging();
	}

	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}

	public int level() {
		
		int magicLevel = 0;
		if (charger != null) {
			Magic magic = charger.target.buff(Magic.class);
			if  (magic != null ){
			    magicLevel = magic.level;
			}
			return magic == null ? level : Math.max(level + magicLevel, 0);
		} else {
			return level;
		}
	}

	protected boolean isKnown() {
		return handler.isKnown(this);
	}

	public void setKnown() {
		if (!isKnown()) {
			handler.know(this);
		}

		Badges.validateAllWandsIdentified();
	}

	@Override
	public Item identify() {

		setKnown();
		curChargeKnown = true;
		super.identify();

		updateQuickslot();

		return this;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(super.toString());

		String status = status();
		if (status != null) {
			sb.append(" (" + status + ")");
		}

		return sb.toString();
	}

	@Override
	public String name() {
		return isKnown() ? name : wood + " 法杖";
	}

	@Override
	public String info() {
		StringBuilder info = new StringBuilder(desc());
		if (Dungeon.hero.heroClass == HeroClass.MAGE) {
			info.append("\n\n");
			if (levelKnown) {
				info.append(String.format(TXT_DAMAGE, MIN, MAX));
			} else {
				info.append(String.format(TXT_WEAPON));
			}

		}
		if (reinforced) {
			info.append(String.format(TXT_REINFORCED));
		}
		return info.toString();
	}

	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown() && curChargeKnown;
	}

	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}

	@Override
	public Item upgrade() {

		super.upgrade();

		updateLevel();
		curCharges = Math.min(curCharges + 1, maxCharges);
		updateQuickslot();

		return this;
	}

	@Override
	public Item degrade() {
		super.degrade();

		updateLevel();
		updateQuickslot();

		return this;
	}

	public void updateLevel() {
		maxCharges = Math.min(initialCharges() + level, 14);
		curCharges = Math.min(curCharges, maxCharges);

		calculateDamage();
	}

	protected int initialCharges() {
		return 2;
	}

	private void calculateDamage() {
		int tier = 1 + level / 3;
		MIN = tier;
		MAX = (tier * tier - tier + 10) / 2 + level;
	}

	protected void fx(int cell, Callback callback) {
		MagicMissile.blueLight(curUser.sprite.parent, curUser.pos, cell,
				callback);
		Sample.INSTANCE.play(Assets.SND_ZAP);
	}

	protected void wandUsed() {
		curCharges--;
		if (!isIdentified() && --usagesToKnow <= 0) {
			identify();
			GLog.w(TXT_IDENTIFY, name());
		} else {
			updateQuickslot();
		}

		curUser.spendAndNext(TIME_TO_ZAP);
	}
	
	protected void wandEmpty() {
		curCharges=0;
		updateQuickslot();
	}

	@Override
	public Item random() {
		if (Random.Float() < 0.5f) {
			upgrade();
			if (Random.Float() < 0.15f) {
				upgrade();
			}
		}

		return this;
	}

	public static boolean allKnown() {
		return handler.known().size() == wands.length;
	}

	@Override
	public int price() {
		int price = 75;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level > 0) {
				price *= (level + 1);
			} else if (level < 0) {
				price /= (1 - level);
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

		
	private static final String UNFAMILIRIARITY = "unfamiliarity";
	private static final String MAX_CHARGES = "maxCharges";
	private static final String CUR_CHARGES = "curCharges";
	private static final String CUR_CHARGE_KNOWN = "curChargeKnown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(UNFAMILIRIARITY, usagesToKnow);
		bundle.put(MAX_CHARGES, maxCharges);
		bundle.put(CUR_CHARGES, curCharges);
		bundle.put(CUR_CHARGE_KNOWN, curChargeKnown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if ((usagesToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
			usagesToKnow = USAGES_TO_KNOW;
		}
		maxCharges = bundle.getInt(MAX_CHARGES);
		curCharges = bundle.getInt(CUR_CHARGES);
		curChargeKnown = bundle.getBoolean(CUR_CHARGE_KNOWN);
	}

	protected static CellSelector.Listener zapper = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer target) {

			if (target != null) {
				
				final Wand curWand = (Wand) Item.curItem;

				curWand.setKnown();

				final int cell = Ballistica.cast(curUser.pos, target, true,	curWand.hitChars);
				
				if (target == curUser.pos || cell == curUser.pos) {
					GLog.i(TXT_SELF_TARGET);
					return;
				}
				
				curUser.sprite.zap(cell);

				QuickSlotButton.target(Actor.findChar(cell));

				if (curWand.curCharges > 0) {

					curUser.busy();

					curWand.fx(cell, new Callback() {
						@Override
						public void call() {
							curWand.onZap(cell);
							curWand.wandUsed();
						}
					});

					Invisibility.dispel();

				} else {

					GLog.w(TXT_FIZZLES);
					curWand.levelKnown = true;

					curWand.updateQuickslot();
				}

			}
		}

		@Override
		public String prompt() {
			return "选择施法位置";
		}
	};

	protected class Charger extends Buff {

		private static final float TIME_TO_CHARGE = 40f;

		@Override
		public boolean attachTo(Char target) {
			super.attachTo(target);
			delay();

			return true;
		}

		@Override
		public boolean act() {

			if (curCharges < maxCharges) {
				curCharges++;
				updateQuickslot();
			}

			delay();

			return true;
		}

		protected void delay() {
			float time2charge = ((Hero) target).heroClass == HeroClass.MAGE ? TIME_TO_CHARGE
					/ (float) Math.sqrt(1 + level)
					: TIME_TO_CHARGE;
			spend(time2charge);
		}
	}
}
