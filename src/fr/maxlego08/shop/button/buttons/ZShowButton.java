package fr.maxlego08.shop.button.buttons;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.shop.api.button.buttons.ItemButton;
import fr.maxlego08.shop.api.button.buttons.ShowButton;
import fr.maxlego08.shop.api.enums.ButtonType;
import fr.maxlego08.shop.api.enums.InventoryType;

public class ZShowButton extends ZPermissibleButton implements ShowButton {

	private final List<String> lore;

	/**
	 * @param type
	 * @param itemStack
	 * @param slot
	 * @param permission
	 * @param message
	 * @param elseButton
	 * @param lore
	 */
	public ZShowButton(ButtonType type, ItemStack itemStack, int slot, List<String> lore) {
		super(type, itemStack, slot);
		this.lore = lore;
	}

	@Override
	public List<String> getLore() {
		return lore;
	}

	@Override
	public List<String> getLore(ItemButton button, int amount, InventoryType type) {
		return lore.stream().map(line -> {
			
			line = line.replace("%sellPrice%",
					String.valueOf(button.getSellPrice() * (type == InventoryType.SELL ? amount : 1)));
			line = line.replace("%buyPrice%",
					String.valueOf(button.getBuyPrice() * (type == InventoryType.BUY ? amount : 1)));
			
			line = line.replace("%currency%", button.getEconomy().getCurrenry());
			line = line.replace("&", "�");
			return line;
		}).collect(Collectors.toList());
	}

	@Override
	public ItemStack applyLore(ItemButton button, int amount, InventoryType type) {
		ItemStack itemStack = button.getItemStack().clone();
		itemStack.setAmount(amount);
		List<String> lore = new ArrayList<>();
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta.hasLore())
			lore.addAll(itemMeta.getLore());
		lore.addAll(getLore(button, amount, type));
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

}