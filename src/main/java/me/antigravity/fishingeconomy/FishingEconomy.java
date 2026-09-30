package me.antigravity.fishingeconomy;

import me.antigravity.fishingeconomy.commands.*;
import me.antigravity.fishingeconomy.market.StockMarketGui;
import me.antigravity.fishingeconomy.commands.StockMarketCommand;
import me.antigravity.fishingeconomy.bounties.BountyManager;
import me.antigravity.fishingeconomy.bounties.BountyListener;
import me.antigravity.fishingeconomy.commands.BountyCommand;
import me.antigravity.fishingeconomy.gambling.GamblingManager;
import me.antigravity.fishingeconomy.gambling.CoinflipGui;
import me.antigravity.fishingeconomy.commands.CoinflipCommand;
import me.antigravity.fishingeconomy.trading.TradeManager;
import me.antigravity.fishingeconomy.trading.TradeListener;
import me.antigravity.fishingeconomy.commands.TradeCommand;
import me.antigravity.fishingeconomy.shops.ShopListener;
import me.antigravity.fishingeconomy.auction.AuctionHouseManager;
import me.antigravity.fishingeconomy.auction.AuctionGui;
import me.antigravity.fishingeconomy.commands.AuctionCommand;
import me.antigravity.fishingeconomy.farming.FarmingGui;
import me.antigravity.fishingeconomy.commands.FarmingCommand;
import me.antigravity.fishingeconomy.jobs.JobsListener;
import me.antigravity.fishingeconomy.gambling.SlotsGui;
import me.antigravity.fishingeconomy.commands.SlotsCommand;
import me.antigravity.fishingeconomy.fishing.FishManager;
import me.antigravity.fishingeconomy.gui.GuiManager;
import me.antigravity.fishingeconomy.mining.OresManager;
import me.antigravity.fishingeconomy.config.ConfigManager;
import me.antigravity.fishingeconomy.economy.EconomyManager;
import me.antigravity.fishingeconomy.farming.CropsManager;
import me.antigravity.fishingeconomy.hunting.MobsManager;
import me.antigravity.fishingeconomy.jobs.JobsManager;
import me.antigravity.fishingeconomy.jobs.JobsGui;
import me.antigravity.fishingeconomy.banking.BankManager;
import me.antigravity.fishingeconomy.banking.BankGui;
import me.antigravity.fishingeconomy.market.MarketManager;
import me.antigravity.fishingeconomy.commands.SellAllCommand;
import me.antigravity.fishingeconomy.commands.JobsCommand;
import me.antigravity.fishingeconomy.commands.BankCommand;
import me.antigravity.fishingeconomy.fishing.FishingListener;
import me.antigravity.fishingeconomy.vault.VaultHook;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingEconomy extends JavaPlugin {

    private static FishingEconomy instance;
    private ConfigManager configManager;
    private EconomyManager economyManager;
    private FishManager fishManager;
    private GuiManager guiManager;
    private OresManager oresManager;
    private CropsManager cropsManager;
    private MobsManager mobsManager;
    private JobsManager jobsManager;
    private JobsGui jobsGui;
    private BankManager bankManager;
    private BankGui bankGui;
    private MarketManager marketManager;
    private StockMarketGui stockMarketGui;
    private BountyManager bountyManager;
    private GamblingManager gamblingManager;
    private CoinflipGui coinflipGui;
    private TradeManager tradeManager;
    private AuctionHouseManager auctionHouseManager;
    private AuctionGui auctionGui;
    private FarmingGui farmingGui;
    private SlotsGui slotsGui;
    private me.antigravity.fishingeconomy.fishing.ReelingManager reelingManager;
    private me.antigravity.fishingeconomy.fishing.BaitManager baitManager;
    private me.antigravity.fishingeconomy.fishing.AquariumManager aquariumManager;
    private me.antigravity.fishingeconomy.corporations.CorporationManager corporationManager;
    private me.antigravity.fishingeconomy.fishing.ExpeditionManager expeditionManager;
    private me.antigravity.fishingeconomy.banking.BondsManager bondsManager;
    private me.antigravity.fishingeconomy.realestate.RealEstateManager realEstateManager;
    private me.antigravity.fishingeconomy.fishing.RodCraftingManager rodCraftingManager;
    private me.antigravity.fishingeconomy.fishing.TournamentManager tournamentManager;
    private me.antigravity.fishingeconomy.fishing.BreedingTankManager breedingTankManager;
    private me.antigravity.fishingeconomy.fishing.SeaMerchantManager seaMerchantManager;
    private me.antigravity.fishingeconomy.fishing.FishTrophyManager fishTrophyManager;
    private me.antigravity.fishingeconomy.fishing.SubmarineManager submarineManager;
    private me.antigravity.fishingeconomy.market.MarketSeasonManager marketSeasonManager;
    private me.antigravity.fishingeconomy.gui.CustomRodGui customRodGui;
    private me.antigravity.fishingeconomy.fishing.FishCodexManager fishCodexManager;
    private me.antigravity.fishingeconomy.fishing.OceanicBossRaid oceanicBossRaid;
    private me.antigravity.fishingeconomy.fishing.AquacultureRigManager aquacultureRigManager;
    private me.antigravity.fishingeconomy.fishing.BaitCraftingStation baitCraftingStation;
    private me.antigravity.fishingeconomy.fishing.DeepSeaTreasureSalvage deepSeaTreasureSalvage;
    private me.antigravity.fishingeconomy.fishing.OceanWeatherAndTides oceanWeatherAndTides;
    private me.antigravity.fishingeconomy.updater.UpdateManager updateManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load Configs
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfigs();

        // Initialize Managers
        this.economyManager = new EconomyManager(this);
        this.fishManager = new FishManager(this);
        this.fishCodexManager = new me.antigravity.fishingeconomy.fishing.FishCodexManager(this);
        this.oceanicBossRaid = new me.antigravity.fishingeconomy.fishing.OceanicBossRaid(this);
        this.aquacultureRigManager = new me.antigravity.fishingeconomy.fishing.AquacultureRigManager(this);
        this.baitCraftingStation = new me.antigravity.fishingeconomy.fishing.BaitCraftingStation(this);
        this.deepSeaTreasureSalvage = new me.antigravity.fishingeconomy.fishing.DeepSeaTreasureSalvage(this);
        this.oceanWeatherAndTides = new me.antigravity.fishingeconomy.fishing.OceanWeatherAndTides(this);
        this.updateManager = new me.antigravity.fishingeconomy.updater.UpdateManager(this);
        this.updateManager.startAsyncCheck();
        this.reelingManager = new me.antigravity.fishingeconomy.fishing.ReelingManager(this);
        this.baitManager = new me.antigravity.fishingeconomy.fishing.BaitManager(this);
        this.aquariumManager = new me.antigravity.fishingeconomy.fishing.AquariumManager(this);
        this.corporationManager = new me.antigravity.fishingeconomy.corporations.CorporationManager(this);
        this.expeditionManager = new me.antigravity.fishingeconomy.fishing.ExpeditionManager(this);
        this.bondsManager = new me.antigravity.fishingeconomy.banking.BondsManager(this);
        this.realEstateManager = new me.antigravity.fishingeconomy.realestate.RealEstateManager(this);
        this.rodCraftingManager = new me.antigravity.fishingeconomy.fishing.RodCraftingManager(this);
        this.tournamentManager = new me.antigravity.fishingeconomy.fishing.TournamentManager(this);
        this.breedingTankManager = new me.antigravity.fishingeconomy.fishing.BreedingTankManager(this);
        this.seaMerchantManager = new me.antigravity.fishingeconomy.fishing.SeaMerchantManager(this);
        this.fishTrophyManager = new me.antigravity.fishingeconomy.fishing.FishTrophyManager(this);
        this.submarineManager = new me.antigravity.fishingeconomy.fishing.SubmarineManager(this);
        this.marketSeasonManager = new me.antigravity.fishingeconomy.market.MarketSeasonManager(this);
        this.guiManager = new GuiManager(this);
        this.oresManager = new OresManager(this);
        this.cropsManager = new CropsManager(this);
        this.mobsManager = new MobsManager(this);
        this.jobsManager = new JobsManager(this);
        this.jobsGui = new JobsGui(this);
        this.bankManager = new BankManager(this);
        this.bankGui = new BankGui(this);
        this.marketManager = new MarketManager(this);
        this.stockMarketGui = new StockMarketGui(this);
        this.bountyManager = new BountyManager(this);
        this.gamblingManager = new GamblingManager(this);
        this.coinflipGui = new CoinflipGui(this);
        this.tradeManager = new TradeManager(this);
        this.auctionHouseManager = new AuctionHouseManager(this);
        this.auctionGui = new AuctionGui(this);
        this.farmingGui = new FarmingGui(this);
        this.slotsGui = new SlotsGui(this);
        this.customRodGui = new me.antigravity.fishingeconomy.gui.CustomRodGui(this);

        // Register Commands & TabCompleters
        EconomyCommand ecoCmd = new EconomyCommand(this);
        getCommand("balance").setExecutor(ecoCmd);
        getCommand("pay").setExecutor(ecoCmd);
        getCommand("baltop").setExecutor(ecoCmd);

        AdminCommand adminCmd = new AdminCommand(this);
        EconomyTabCompleter ecoTab = new EconomyTabCompleter(this);
        getCommand("eco").setExecutor(adminCmd);
        getCommand("eco").setTabCompleter(ecoTab);

        getCommand("fishshop").setExecutor(new FishShopCommand(this));
        getCommand("sellall").setExecutor(new SellAllCommand(this));
        getCommand("jobs").setExecutor(new JobsCommand(this));
        getCommand("bank").setExecutor(new BankCommand(this));
        getCommand("stockmarket").setExecutor(new StockMarketCommand(this));

        getCommand("bounty").setExecutor(new BountyCommand(this));
        getCommand("bounty").setTabCompleter(ecoTab);

        getCommand("coinflip").setExecutor(new CoinflipCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(this));
        getCommand("ah").setExecutor(new AuctionCommand(this));
        getCommand("farming").setExecutor(new FarmingCommand(this));
        getCommand("slots").setExecutor(new SlotsCommand(this));

        getCommand("corp").setExecutor(new me.antigravity.fishingeconomy.commands.CorporationCommand(this));
        getCommand("corp").setTabCompleter(ecoTab);

        getCommand("bonds").setExecutor(new me.antigravity.fishingeconomy.commands.BondsCommand(this));
        getCommand("bonds").setTabCompleter(ecoTab);

        getCommand("property").setExecutor(new me.antigravity.fishingeconomy.commands.RealEstateCommand(this));
        getCommand("property").setTabCompleter(ecoTab);

        getCommand("expedition").setExecutor(new me.antigravity.fishingeconomy.commands.ExpeditionCommand(this));
        getCommand("expedition").setTabCompleter(ecoTab);

        CustomRodsCommand rodCmd = new me.antigravity.fishingeconomy.commands.CustomRodsCommand(this);
        getCommand("customrod").setExecutor(rodCmd);
        getCommand("customrod").setTabCompleter(rodCmd);

        FishGuideCommand fishGuideCmd = new me.antigravity.fishingeconomy.commands.FishGuideCommand(this);
        getCommand("fish").setExecutor(fishGuideCmd);
        getCommand("fish").setTabCompleter(fishGuideCmd);

        getCommand("ftournament").setExecutor(new me.antigravity.fishingeconomy.commands.TournamentCommand(this));
        getCommand("ftournament").setTabCompleter(ecoTab);

        getCommand("breedfish").setExecutor(new me.antigravity.fishingeconomy.commands.BreedingCommand(this));

        getCommand("merchant").setExecutor(new me.antigravity.fishingeconomy.commands.SeaMerchantCommand(this));
        getCommand("merchant").setTabCompleter(ecoTab);

        getCommand("mapchart").setExecutor(new me.antigravity.fishingeconomy.commands.MapChartCommand(this));
        getCommand("trophy").setExecutor(new me.antigravity.fishingeconomy.commands.TrophyCommand(this));
        getCommand("submarine").setExecutor(new me.antigravity.fishingeconomy.commands.SubmarineCommand(this));

        // Register Listeners
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(guiManager, this);
        getServer().getPluginManager().registerEvents(cropsManager, this);
        getServer().getPluginManager().registerEvents(mobsManager, this);
        getServer().getPluginManager().registerEvents(jobsGui, this);
        getServer().getPluginManager().registerEvents(bankGui, this);
        getServer().getPluginManager().registerEvents(stockMarketGui, this);
        getServer().getPluginManager().registerEvents(new BountyListener(this), this);
        getServer().getPluginManager().registerEvents(coinflipGui, this);
        getServer().getPluginManager().registerEvents(new TradeListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
        getServer().getPluginManager().registerEvents(auctionGui, this);
        getServer().getPluginManager().registerEvents(farmingGui, this);
        getServer().getPluginManager().registerEvents(new JobsListener(this), this);
        getServer().getPluginManager().registerEvents(slotsGui, this);
        getServer().getPluginManager().registerEvents(customRodGui, this);
        getServer().getPluginManager().registerEvents(fishCodexManager, this);
        getServer().getPluginManager().registerEvents(oceanicBossRaid, this);
        getServer().getPluginManager().registerEvents(aquacultureRigManager, this);
        getServer().getPluginManager().registerEvents(baitCraftingStation, this);
        getServer().getPluginManager().registerEvents(deepSeaTreasureSalvage, this);
        getServer().getPluginManager().registerEvents(updateManager, this);

        // Register Vault Economy
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            VaultHook.hook(this);
        }

        getLogger().info("FishingEconomy has been enabled!");
    }

    public void onDisable() {
        if (economyManager != null) {
            economyManager.saveAll();
        }
        getLogger().info("FishingEconomy has been disabled!");
    }

    public static FishingEconomy getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public FishManager getFishManager() {
        return fishManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public OresManager getOresManager() {
        return oresManager;
    }

    public CropsManager getCropsManager() {
        return cropsManager;
    }

    public JobsManager getJobsManager() {
        return jobsManager;
    }

    public JobsGui getJobsGui() {
        return jobsGui;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public BankGui getBankGui() {
        return bankGui;
    }

    public MarketManager getMarketManager() {
        return marketManager;
    }

    public StockMarketGui getStockMarketGui() {
        return stockMarketGui;
    }

    public BountyManager getBountyManager() {
        return bountyManager;
    }

    public GamblingManager getGamblingManager() {
        return gamblingManager;
    }

    public CoinflipGui getCoinflipGui() {
        return coinflipGui;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public AuctionHouseManager getAuctionHouseManager() {
        return auctionHouseManager;
    }

    public AuctionGui getAuctionGui() {
        return auctionGui;
    }

    public FarmingGui getFarmingGui() {
        return farmingGui;
    }

    public SlotsGui getSlotsGui() {
        return slotsGui;
    }

    public me.antigravity.fishingeconomy.fishing.ReelingManager getReelingManager() {
        return reelingManager;
    }

    public me.antigravity.fishingeconomy.fishing.BaitManager getBaitManager() {
        return baitManager;
    }

    public me.antigravity.fishingeconomy.fishing.AquariumManager getAquariumManager() {
        return aquariumManager;
    }

    public me.antigravity.fishingeconomy.corporations.CorporationManager getCorporationManager() {
        return corporationManager;
    }

    public me.antigravity.fishingeconomy.fishing.ExpeditionManager getExpeditionManager() {
        return expeditionManager;
    }

    public me.antigravity.fishingeconomy.banking.BondsManager getBondsManager() {
        return bondsManager;
    }

    public me.antigravity.fishingeconomy.realestate.RealEstateManager getRealEstateManager() {
        return realEstateManager;
    }

    public me.antigravity.fishingeconomy.fishing.RodCraftingManager getRodCraftingManager() {
        return rodCraftingManager;
    }

    public me.antigravity.fishingeconomy.fishing.TournamentManager getTournamentManager() {
        return tournamentManager;
    }

    public me.antigravity.fishingeconomy.fishing.BreedingTankManager getBreedingTankManager() {
        return breedingTankManager;
    }

    public me.antigravity.fishingeconomy.fishing.SeaMerchantManager getSeaMerchantManager() {
        return seaMerchantManager;
    }

    public me.antigravity.fishingeconomy.fishing.FishTrophyManager getFishTrophyManager() {
        return fishTrophyManager;
    }

    public me.antigravity.fishingeconomy.fishing.SubmarineManager getSubmarineManager() {
        return submarineManager;
    }

    public me.antigravity.fishingeconomy.market.MarketSeasonManager getMarketSeasonManager() {
        return marketSeasonManager;
    }

    public me.antigravity.fishingeconomy.gui.CustomRodGui getCustomRodGui() {
        return customRodGui;
    }

    public me.antigravity.fishingeconomy.fishing.FishCodexManager getFishCodexManager() {
        return fishCodexManager;
    }

    public me.antigravity.fishingeconomy.fishing.OceanicBossRaid getOceanicBossRaid() {
        return oceanicBossRaid;
    }

    public me.antigravity.fishingeconomy.fishing.AquacultureRigManager getAquacultureRigManager() {
        return aquacultureRigManager;
    }

    public me.antigravity.fishingeconomy.fishing.BaitCraftingStation getBaitCraftingStation() {
        return baitCraftingStation;
    }

    public me.antigravity.fishingeconomy.fishing.DeepSeaTreasureSalvage getDeepSeaTreasureSalvage() {
        return deepSeaTreasureSalvage;
    }

    public me.antigravity.fishingeconomy.fishing.OceanWeatherAndTides getOceanWeatherAndTides() {
        return oceanWeatherAndTides;
    }

    public me.antigravity.fishingeconomy.updater.UpdateManager getUpdateManager() {
        return updateManager;
    }
}
