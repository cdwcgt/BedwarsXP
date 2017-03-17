package ldcr.BedwarsXP.XPShop.bedwarsrel;

import io.github.bedwarsrel.BedwarsRel.Game.Game;
import io.github.bedwarsrel.BedwarsRel.Game.RessourceSpawner;
import io.github.bedwarsrel.BedwarsRel.Shop.NewItemShop;
import io.github.bedwarsrel.BedwarsRel.Villager.MerchantCategory;
import io.github.bedwarsrel.BedwarsRel.Villager.MerchantCategoryComparator;
import io.github.bedwarsrel.BedwarsRel.Villager.VillagerTrade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.Utils.ListUtils;
import ldcr.BedwarsXP.Utils.ReflectionUtils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NewShopReplacer implements Runnable {
	Game game;
	CommandSender s;

	public NewShopReplacer(String e, CommandSender sender) {
		s = sender;
		game = io.github.bedwarsrel.BedwarsRel.Main.getInstance()
				.getGameManager().getGame(e);
	}

	@Override
	public void run() {
		HashMap<Material, MerchantCategory> map = game.getItemShopCategories();
		if (Config.Full_XP_Bedwars) {
			Iterator<Entry<Material, MerchantCategory>> i1 = map.entrySet()
					.iterator();
			for (; i1.hasNext();) {
				Entry<Material, MerchantCategory> en = i1.next();
				MerchantCategory m = en.getValue();
				ArrayList<VillagerTrade> t = m.getOffers();
				ArrayList<XPVillagerTrade> n = new ArrayList<XPVillagerTrade>();
				for (int i = 0; i < t.size(); i++) {
					n.add(new XPVillagerTrade(t.get(i)));
				}
				try {
					ReflectionUtils.setPrivateValue(m, "offers", n);
				} catch (Exception e1) {
					s.sendMessage("��6��l[BedwarsXP] ��cΪ��ͼ " + game.getName()
							+ " �滻ԭʼ�̵�Ϊ�����̵�ʧ��");
					e1.printStackTrace();
				}
				map.put(en.getKey(), m);
			}
		}
		if (Config.Add_Res_Shop) {
			ArrayList<VillagerTrade> trades = new ArrayList<VillagerTrade>();
			trades.add(new XPVillagerTrade(
					RessourceSpawner
							.createSpawnerStackByConfig(io.github.bedwarsrel.BedwarsRel.Main
									.getInstance().getConfig()
									.get("ressource.bronze"))));
			trades.add(new XPVillagerTrade(
					RessourceSpawner
							.createSpawnerStackByConfig(io.github.bedwarsrel.BedwarsRel.Main
									.getInstance().getConfig()
									.get("ressource.iron"))));
			trades.add(new XPVillagerTrade(
					RessourceSpawner
							.createSpawnerStackByConfig(io.github.bedwarsrel.BedwarsRel.Main
									.getInstance().getConfig()
									.get("ressource.gold"))));
			MerchantCategory mc = new MerchantCategory("��6��l����һ���Դ",
					Material.EXP_BOTTLE, trades,
					ListUtils.newList("��a����ľ���һ�����Ʒ"), 3, "bw.base");
			map.put(Material.EXP_BOTTLE, mc);
		}
		try {
			Field itemshops = ReflectionUtils.getField(game, "newItemShops");
			itemshops.setAccessible(true);
			HashMap<Player, NewItemShop> shops = new HashMap<Player, NewItemShop>();
			List<MerchantCategory> order = new ArrayList<MerchantCategory>(
					map.values());
			Collections.sort(order, new MerchantCategoryComparator());
			for (Player pl : game.getPlayers()) {
				XPItemShop Shop = new XPItemShop(order, game);
				shops.put(pl, Shop);
			}
			ReflectionUtils.setPrivateValue(game, "newItemShops", shops);
			s.sendMessage("��6��l[BedwarsXP] ��bΪ��ͼ " + game.getName()
					+ " �滻�����̵�ɹ�!");
		} catch (Exception e1) {
			s.sendMessage("��6��l[BedwarsXP] ��cΪ��ͼ " + game.getName()
					+ " ��ʼ�������̵�ʱ����");
			e1.printStackTrace();
		}
	}
}