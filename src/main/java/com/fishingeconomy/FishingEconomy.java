package com.fishingeconomy;

import com.fishingeconomy.commands.*;
import com.fishingeconomy.market.StockMarketGui;
import com.fishingeconomy.commands.StockMarketCommand;
import com.fishingeconomy.bounties.BountyManager;
import com.fishingeconomy.bounties.BountyListener;
import com.fishingeconomy.commands.BountyCommand;
import com.fishingeconomy.gambling.GamblingManager;
import com.fishingeconomy.gambling.CoinflipGui;
import com.fishingeconomy.commands.CoinflipCommand;
import com.fishingeconomy.trading.TradeManager;
import com.fishingeconomy.trading.TradeListener;
import com.fishingeconomy.commands.TradeCommand;
import com.fishingeconomy.shops.ShopListener;
import com.fishingeconomy.auction.AuctionHouseManager;
import com.fishingeconomy.auction.AuctionGui;
import com.fishingeconomy.commands.AuctionCommand;
import com.fishingeconomy.farming.FarmingGui;
import com.fishingeconomy.commands.FarmingCommand;
import com.fishingeconomy.jobs.JobsListener;
import com.fishingeconomy.gambling.SlotsGui;
import com.fishingeconomy.commands.SlotsCommand;
import com.fishingeconomy.fishing.FishManager;
import com.fishingeconomy.gui.GuiManager;
import com.fishingeconomy.mining.OresManager;
import com.fishingeconomy.config.ConfigManager;
import com.fishingeconomy.economy.EconomyManager;
import com.fishingeconomy.farming.CropsManager;
import com.fishingeconomy.hunting.MobsManager;
import com.fishingeconomy.jobs.JobsManager;
import com.fishingeconomy.jobs.JobsGui;
import com.fishingeconomy.banking.BankManager;
import com.fishingeconomy.banking.BankGui;
import com.fishingeconomy.market.MarketManager;
import com.fishingeconomy.commands.SellAllCommand;
import com.fishingeconomy.commands.JobsCommand;
import com.fishingeconomy.commands.BankCommand;
import com.fishingeconomy.fishing.FishingListener;
import com.fishingeconomy.vault.VaultHook;
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
    private com.fishingeconomy.fishing.ReelingManager reelingManager;
    private com.fishingeconomy.fishing.BaitManager baitManager;
    private com.fishingeconomy.fishing.AquariumManager aquariumManager;
    private com.fishingeconomy.corporations.CorporationManager corporationManager;
    private com.fishingeconomy.fishing.ExpeditionManager expeditionManager;
    private com.fishingeconomy.banking.BondsManager bondsManager;
    private com.fishingeconomy.realestate.RealEstateManager realEstateManager;
    private com.fishingeconomy.fishing.RodCraftingManager rodCraftingManager;
    private com.fishingeconomy.fishing.TournamentManager tournamentManager;
    private com.fishingeconomy.fishing.BreedingTankManager breedingTankManager;
    private com.fishingeconomy.fishing.SeaMerchantManager seaMerchantManager;
    private com.fishingeconomy.fishing.FishTrophyManager fishTrophyManager;
    private com.fishingeconomy.fishing.SubmarineManager submarineManager;
    private com.fishingeconomy.market.MarketSeasonManager marketSeasonManager;
    private com.fishingeconomy.gui.CustomRodGui customRodGui;
    private com.fishingeconomy.fishing.FishCodexManager fishCodexManager;
    private com.fishingeconomy.fishing.OceanicBossRaid oceanicBossRaid;
    private com.fishingeconomy.fishing.AquacultureRigManager aquacultureRigManager;
    private com.fishingeconomy.fishing.BaitCraftingStation baitCraftingStation;
    private com.fishingeconomy.fishing.DeepSeaTreasureSalvage deepSeaTreasureSalvage;
    private com.fishingeconomy.fishing.OceanWeatherAndTides oceanWeatherAndTides;
    private com.fishingeconomy.updater.UpdateManager updateManager;
    private com.fishingeconomy.gear.DivingSuitManager divingSuitManager;
    private com.fishingeconomy.fishing.RodEnchantManager rodEnchantManager;
    private com.fishingeconomy.gui.FishTestLabGUI fishTestLabGUI;
    private com.fishingeconomy.gui.FishingMasteryTreeGUI fishingMasteryTreeGUI;
    private com.fishingeconomy.multiblock.FishMultiblockManager fishMultiblockManager;
    private com.fishingeconomy.fishing.ExoticFishingEventManager exoticFishingEventManager;
    private com.fishingeconomy.multiblock.FilletBenchGUI filletBenchGUI;
    private com.fishingeconomy.listeners.GuiSecurityListener guiSecurityListener;
    private com.fishingeconomy.effects.SpecialCatchAnimationManager specialCatchAnimationManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load Configs
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfigs();

        // Initialize Managers
        this.economyManager = new EconomyManager(this);
        this.fishManager = new FishManager(this);
        this.fishCodexManager = new com.fishingeconomy.fishing.FishCodexManager(this);
        this.oceanicBossRaid = new com.fishingeconomy.fishing.OceanicBossRaid(this);
        this.aquacultureRigManager = new com.fishingeconomy.fishing.AquacultureRigManager(this);
        this.baitCraftingStation = new com.fishingeconomy.fishing.BaitCraftingStation(this);
        this.deepSeaTreasureSalvage = new com.fishingeconomy.fishing.DeepSeaTreasureSalvage(this);
        this.oceanWeatherAndTides = new com.fishingeconomy.fishing.OceanWeatherAndTides(this);
        this.divingSuitManager = new com.fishingeconomy.gear.DivingSuitManager(this);
        this.rodEnchantManager = new com.fishingeconomy.fishing.RodEnchantManager(this);
        this.globalTournamentEngine = new com.fishingeconomy.fishing.GlobalFishingTournamentEngine(this);
        this.shipwreckManager = new com.fishingeconomy.fishing.ShipwreckExpeditionManager(this);
        this.fishMutationLab = new com.fishingeconomy.fishing.FishMutationLab(this);
        this.fishTaxidermyMuseum = new com.fishingeconomy.fishing.FishTaxidermyMuseum(this);
        this.fishTestLabGUI = new com.fishingeconomy.gui.FishTestLabGUI(this);
        this.updateManager = new com.fishingeconomy.updater.UpdateManager(this);
        this.updateManager.startAsyncCheck();
        this.reelingManager = new com.fishingeconomy.fishing.ReelingManager(this);
        this.baitManager = new com.fishingeconomy.fishing.BaitManager(this);
        this.aquariumManager = new com.fishingeconomy.fishing.AquariumManager(this);
        this.corporationManager = new com.fishingeconomy.corporations.CorporationManager(this);
        this.expeditionManager = new com.fishingeconomy.fishing.ExpeditionManager(this);
        this.bondsManager = new com.fishingeconomy.banking.BondsManager(this);
        this.realEstateManager = new com.fishingeconomy.realestate.RealEstateManager(this);
        this.rodCraftingManager = new com.fishingeconomy.fishing.RodCraftingManager(this);
        this.tournamentManager = new com.fishingeconomy.fishing.TournamentManager(this);
        this.breedingTankManager = new com.fishingeconomy.fishing.BreedingTankManager(this);
        this.seaMerchantManager = new com.fishingeconomy.fishing.SeaMerchantManager(this);
        this.fishTrophyManager = new com.fishingeconomy.fishing.FishTrophyManager(this);
        this.submarineManager = new com.fishingeconomy.fishing.SubmarineManager(this);
        this.marketSeasonManager = new com.fishingeconomy.market.MarketSeasonManager(this);
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
        this.customRodGui = new com.fishingeconomy.gui.CustomRodGui(this);
        this.customRodStatsGui = new com.fishingeconomy.gui.CustomRodStatsGui(this);
        this.fishingMasteryTreeGUI = new com.fishingeconomy.gui.FishingMasteryTreeGUI(this);
        this.fishMultiblockManager = new com.fishingeconomy.multiblock.FishMultiblockManager(this);
        this.exoticFishingEventManager = new com.fishingeconomy.fishing.ExoticFishingEventManager(this);
        this.filletBenchGUI = new com.fishingeconomy.multiblock.FilletBenchGUI(this);
        this.guiSecurityListener = new com.fishingeconomy.listeners.GuiSecurityListener(this);
        this.specialCatchAnimationManager = new com.fishingeconomy.effects.SpecialCatchAnimationManager(this);

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

        getCommand("corp").setExecutor(new com.fishingeconomy.commands.CorporationCommand(this));
        getCommand("corp").setTabCompleter(ecoTab);

        getCommand("bonds").setExecutor(new com.fishingeconomy.commands.BondsCommand(this));
        getCommand("bonds").setTabCompleter(ecoTab);

        getCommand("property").setExecutor(new com.fishingeconomy.commands.RealEstateCommand(this));
        getCommand("property").setTabCompleter(ecoTab);

        getCommand("expedition").setExecutor(new com.fishingeconomy.commands.ExpeditionCommand(this));
        getCommand("expedition").setTabCompleter(ecoTab);

        CustomRodsCommand rodCmd = new com.fishingeconomy.commands.CustomRodsCommand(this);
        getCommand("customrod").setExecutor(rodCmd);
        getCommand("customrod").setTabCompleter(rodCmd);

        FishGuideCommand fishGuideCmd = new com.fishingeconomy.commands.FishGuideCommand(this);
        getCommand("fish").setExecutor(fishGuideCmd);
        getCommand("fish").setTabCompleter(fishGuideCmd);

        getCommand("ftournament").setExecutor(new com.fishingeconomy.commands.TournamentCommand(this));
        getCommand("ftournament").setTabCompleter(ecoTab);

        getCommand("breedfish").setExecutor(new com.fishingeconomy.commands.BreedingCommand(this));

        getCommand("merchant").setExecutor(new com.fishingeconomy.commands.SeaMerchantCommand(this));
        getCommand("merchant").setTabCompleter(ecoTab);

        getCommand("mapchart").setExecutor(new com.fishingeconomy.commands.MapChartCommand(this));
        getCommand("trophy").setExecutor(new com.fishingeconomy.commands.TrophyCommand(this));
        getCommand("submarine").setExecutor(new com.fishingeconomy.commands.SubmarineCommand(this));

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
        getServer().getPluginManager().registerEvents(divingSuitManager, this);
        getServer().getPluginManager().registerEvents(rodEnchantManager, this);
        getServer().getPluginManager().registerEvents(shipwreckManager, this);
        getServer().getPluginManager().registerEvents(fishMutationLab, this);
        getServer().getPluginManager().registerEvents(fishTaxidermyMuseum, this);
        getServer().getPluginManager().registerEvents(fishTestLabGUI, this);
        getServer().getPluginManager().registerEvents(customRodStatsGui, this);
        getServer().getPluginManager().registerEvents(fishingMasteryTreeGUI, this);
        getServer().getPluginManager().registerEvents(updateManager, this);
        getServer().getPluginManager().registerEvents(aquariumManager, this);
        getServer().getPluginManager().registerEvents(new com.fishingeconomy.multiblock.FishMultiblockListener(this, fishMultiblockManager), this);
        getServer().getPluginManager().registerEvents(filletBenchGUI, this);
        getServer().getPluginManager().registerEvents(guiSecurityListener, this);
        getServer().getPluginManager().registerEvents(new com.fishingeconomy.gui.FishContentGuideGUI(this), this);

        // Register Vault Economy
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            VaultHook.hook(this);
        }

        getLogger().info("FishingEconomy has been enabled!");
    }

    public void onDisable() {
        if (fishMultiblockManager != null) {
            fishMultiblockManager.saveStructures();
        }
        if (exoticFishingEventManager != null) {
            exoticFishingEventManager.stopAllEvents();
        }
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

    public com.fishingeconomy.fishing.ReelingManager getReelingManager() {
        return reelingManager;
    }

    public com.fishingeconomy.fishing.BaitManager getBaitManager() {
        return baitManager;
    }

    public com.fishingeconomy.fishing.AquariumManager getAquariumManager() {
        return aquariumManager;
    }

    public com.fishingeconomy.corporations.CorporationManager getCorporationManager() {
        return corporationManager;
    }

    public com.fishingeconomy.fishing.ExpeditionManager getExpeditionManager() {
        return expeditionManager;
    }

    public com.fishingeconomy.banking.BondsManager getBondsManager() {
        return bondsManager;
    }

    public com.fishingeconomy.realestate.RealEstateManager getRealEstateManager() {
        return realEstateManager;
    }

    public com.fishingeconomy.fishing.RodCraftingManager getRodCraftingManager() {
        return rodCraftingManager;
    }

    public com.fishingeconomy.fishing.TournamentManager getTournamentManager() {
        return tournamentManager;
    }

    public com.fishingeconomy.fishing.BreedingTankManager getBreedingTankManager() {
        return breedingTankManager;
    }

    public com.fishingeconomy.fishing.SeaMerchantManager getSeaMerchantManager() {
        return seaMerchantManager;
    }

    public com.fishingeconomy.fishing.FishTrophyManager getFishTrophyManager() {
        return fishTrophyManager;
    }

    public com.fishingeconomy.fishing.SubmarineManager getSubmarineManager() {
        return submarineManager;
    }

    public com.fishingeconomy.market.MarketSeasonManager getMarketSeasonManager() {
        return marketSeasonManager;
    }

    public com.fishingeconomy.gui.CustomRodGui getCustomRodGui() {
        return customRodGui;
    }

    public com.fishingeconomy.fishing.FishCodexManager getFishCodexManager() {
        return fishCodexManager;
    }

    public com.fishingeconomy.fishing.OceanicBossRaid getOceanicBossRaid() {
        return oceanicBossRaid;
    }

    public com.fishingeconomy.fishing.AquacultureRigManager getAquacultureRigManager() {
        return aquacultureRigManager;
    }

    public com.fishingeconomy.fishing.BaitCraftingStation getBaitCraftingStation() {
        return baitCraftingStation;
    }

    public com.fishingeconomy.fishing.DeepSeaTreasureSalvage getDeepSeaTreasureSalvage() {
        return deepSeaTreasureSalvage;
    }

    public com.fishingeconomy.fishing.OceanWeatherAndTides getOceanWeatherAndTides() {
        return oceanWeatherAndTides;
    }

    public com.fishingeconomy.updater.UpdateManager getUpdateManager() {
        return updateManager;
    }

    public com.fishingeconomy.gear.DivingSuitManager getDivingSuitManager() {
        return divingSuitManager;
    }

    public com.fishingeconomy.fishing.RodEnchantManager getRodEnchantManager() {
        return rodEnchantManager;
    }

    private com.fishingeconomy.fishing.GlobalFishingTournamentEngine globalTournamentEngine;
    private com.fishingeconomy.fishing.ShipwreckExpeditionManager shipwreckManager;
    private com.fishingeconomy.fishing.FishMutationLab fishMutationLab;
    private com.fishingeconomy.fishing.FishTaxidermyMuseum fishTaxidermyMuseum;

    public com.fishingeconomy.fishing.GlobalFishingTournamentEngine getGlobalTournamentEngine() {
        return globalTournamentEngine;
    }

    public com.fishingeconomy.fishing.ShipwreckExpeditionManager getShipwreckManager() {
        return shipwreckManager;
    }

    public com.fishingeconomy.fishing.FishMutationLab getFishMutationLab() {
        return fishMutationLab;
    }

    public com.fishingeconomy.fishing.FishTaxidermyMuseum getFishTaxidermyMuseum() {
        return fishTaxidermyMuseum;
    }

    private com.fishingeconomy.gui.CustomRodStatsGui customRodStatsGui;

    public com.fishingeconomy.gui.CustomRodStatsGui getCustomRodStatsGui() {
        return customRodStatsGui;
    }

    public com.fishingeconomy.gui.FishTestLabGUI getFishTestLabGUI() {
        return fishTestLabGUI;
    }

    public com.fishingeconomy.gui.FishingMasteryTreeGUI getFishingMasteryTreeGUI() {
        return fishingMasteryTreeGUI;
    }

    public com.fishingeconomy.multiblock.FishMultiblockManager getFishMultiblockManager() {
        return fishMultiblockManager;
    }

    public com.fishingeconomy.fishing.ExoticFishingEventManager getExoticFishingEventManager() {
        return exoticFishingEventManager;
    }

    public com.fishingeconomy.effects.SpecialCatchAnimationManager getSpecialCatchAnimationManager() {
        return specialCatchAnimationManager;
    }
}
