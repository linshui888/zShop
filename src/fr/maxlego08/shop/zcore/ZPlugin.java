package fr.maxlego08.shop.zcore;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.maxlego08.shop.listener.ListenerAdapter;
import fr.maxlego08.shop.zcore.logger.Logger;
import fr.maxlego08.shop.zcore.logger.Logger.LogType;
import fr.maxlego08.shop.zcore.utils.storage.Persist;
import fr.maxlego08.shop.zcore.utils.storage.Saveable;
import net.milkbowl.vault.economy.Economy;

public abstract class ZPlugin extends JavaPlugin {

	private final Logger log = new Logger(this.getDescription().getFullName());
	private Gson gson;
	private Persist persist;
	private static ZPlugin plugin;
	private long enableTime;
	private List<Saveable> savers = new ArrayList<>();
	private List<ListenerAdapter> listenerAdapters = new ArrayList<>();
	private Economy economy = null;

	private PlayerPoints playerPoints;
	private PlayerPointsAPI playerPointsAPI;

	public ZPlugin() {
		plugin = this;
	}

	protected boolean preEnable() {

		enableTime = System.currentTimeMillis();

		log.log("=== ENABLE START ===");
		log.log("Plugin Version V<&>c" + getDescription().getVersion(), LogType.INFO);

		getDataFolder().mkdirs();

		
		gson = getGsonBuilder().create();
		persist = new Persist(this);

		if (!new File(getDataFolder() + "/items.yml").exists())
			saveResource("items.yml", false);
		saveResource("items-example-fr.yml", true);
		saveResource("items-example-en.yml", true);

		setupEconomy();
		hookPlayerPoints();

		return true;

	}

	protected void postEnable() {

		log.log("=== ENABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

	}

	protected void preDisable() {

		enableTime = System.currentTimeMillis();
		log.log("=== DISABLE START ===");

	}

	protected void postDisable() {

		log.log("=== DISABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

	}

	public GsonBuilder getGsonBuilder() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}

	public void addListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}

	public void addListener(ListenerAdapter adapter) {
		if (adapter instanceof Saveable) {
			addSave((Saveable) adapter);
		}
		listenerAdapters.add(adapter);
	}

	public void addSave(Saveable saver) {
		this.savers.add(saver);
	}

	public Logger getLog() {
		return this.log;
	}

	public Gson getGson() {
		return gson;
	}

	public Persist getPersist() {
		return persist;
	}

	public List<Saveable> getSavers() {
		return savers;
	}

	public static ZPlugin z() {
		return plugin;
	}

	/**
	 * @return boolean ture if economy is setup
	 */
	protected boolean setupEconomy() {
		try {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
					.getRegistration(Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
		} catch (NoClassDefFoundError e) {
		}
		return (economy != null);
	}

	public Economy getEconomy() {
		if (!setupEconomy()){
			
			this.getServer().getPluginManager().disablePlugin(this);
		}
		return economy;
	}

	public List<ListenerAdapter> getListenerAdapters() {
		return listenerAdapters;
	}

	/**
	 * @return boolean
	 */
	protected void hookPlayerPoints() {
		try {
			final Plugin plugin = (Plugin) this.getServer().getPluginManager().getPlugin("PlayerPoints");
			if (plugin == null)
				return;
			playerPoints = PlayerPoints.class.cast(plugin);
			if (playerPoints != null) {
				playerPointsAPI = playerPoints.getAPI();
				log.log("PlayerPoint plugin detection performed successfully", LogType.SUCCESS);
			} else
				log.log("Impossible de charger player point !", LogType.SUCCESS);
		} catch (Exception e) {
		}
	}

	/**
	 * @return the playerPoints
	 */
	public PlayerPoints getPlayerPoints() {
		return playerPoints;
	}

	/**
	 * @return the playerPointsAPI
	 */
	public PlayerPointsAPI getPlayerPointsAPI() {

		if (playerPointsAPI == null) {

			hookPlayerPoints();

		}

		return playerPointsAPI;
	}

}
