package de.obey.crownmc;
/*

    Author - Obey -> SkySlayer-v4
       08.11.2022 / 18:50

*/

import com.intellectualcrafters.plot.api.PlotAPI;
import de.obey.crownmc.backend.Backend;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.commands.*;
import de.obey.crownmc.handler.*;
import de.obey.crownmc.listener.*;
import de.obey.crownmc.objects.Runnables;
import de.obey.crownmc.tabcomplete.*;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public final class Initializer {

    private final CrownMain crownMain;
    private final ExecutorService executorService;
    private final ServerConfig serverConfig;

    private Backend backend;

    /* Util Instances */
    private MessageUtil messageUtil;

    /* Handler Instances */
    private ChatFilterHandler chatFilterHandler;
    private CombatHandler combatHandler;
    private EloHandler eloHandler;
    private KillFarmHandler killFarmHandler;
    private LocationHandler locationHandler;
    private RangHandler rangHandler;
    private RankingHandler rankingHandler;
    private ScoreboardHandler scoreboardHandler;
    private TradeHandler tradeHandler;
    private UserHandler userHandler;
    private ClanHandler clanHandler;
    private WarpHandler warpHandler;
    private KitHandler kitHandler;
    private BadgeHandler badgeHandler;
    private WorldProtectionHandler worldProtectionHandler;
    private CoinflipHandler coinflipHandler;
    private CrashHandler crashHandler;
    private ShopHandler shopHandler;
    private AutoBroadcastHandler autoBroadcastHandler;
    private BlockEventHandler blockEventHandler;
    private DailyPotHandler dailyPotHandler;
    private LuckySpinHandler luckySpinHandler;
    private VotePartyHandler votePartyHandler;
    private JackPotHandler jackPotHandler;
    private RouletteHandler rouletteHandler;
    private PvPAltarHandler pvPAltarHandler;
    private BanHandler banHandler;
    private MuteHandler muteHandler;
    private NpcHandler npcHandler;
    private WordScrambleHandler wordScrumbleHandler;
    private StatTrackHandler statTrackHandler;
    private PlaytestHandler playtestHandler;
    private GoalHandler goalHandler;
    private CoinbombHandler coinbombHandler;

    private PlotAPI plotAPI;

    /* Is Server restarting ? */
    private boolean restarting = true;

    private Runnables runnables;

    //private CrownBot crownBot;

    public Initializer(final CrownMain crownMain) {
        this.crownMain = crownMain;
        this.executorService = Executors.newCachedThreadPool();
        this.serverConfig = new ServerConfig(this);
    }

    private BanCommand banCommand;
    private BlockStuffListener blockStuffListener;
    private void loadCommandsAndListener() {
        final ShieldCommand shieldCommand = new ShieldCommand(messageUtil);
        banCommand = new BanCommand(messageUtil, banHandler);
        blockStuffListener = new BlockStuffListener(messageUtil, locationHandler, combatHandler, userHandler, serverConfig, worldProtectionHandler);
        final PvPAltarListener pvPAltarListener = new PvPAltarListener(messageUtil, pvPAltarHandler);
        final WarpAugeListener warpAugeListener = new WarpAugeListener(messageUtil, combatHandler, locationHandler);
        final VoteKickCommand voteKickCommand = new VoteKickCommand(messageUtil);
        final GuessTheNumberCommand guessTheNumberCommand = new GuessTheNumberCommand(messageUtil);
        final NpcCommand npcCommand = new NpcCommand(messageUtil, npcHandler);
        final ClanCommand clanCommand = new ClanCommand(messageUtil, clanHandler, userHandler);
        final StatTrackCommand statTrackCommand = new StatTrackCommand(messageUtil, statTrackHandler);

        crownMain.getCommand("whitelist").setExecutor(new WhitelistCommand(serverConfig, messageUtil));
        crownMain.getCommand("stop").setExecutor(new StopCommand(this));
        crownMain.getCommand("gamemode").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("feed").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("heal").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("trash").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("speed").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("more").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("day").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("night").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("time").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("clear").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("tpa").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("tpahere").setExecutor(new EssentialCommands(this));
        crownMain.getCommand("rang").setExecutor(new RangCommand(messageUtil, rangHandler, scoreboardHandler));
        crownMain.getCommand("setprefix").setExecutor(new SetPrefixCommand(this));
        crownMain.getCommand("stats").setExecutor(new StatsCommand(messageUtil, userHandler, eloHandler, executorService));
        crownMain.getCommand("user").setExecutor(new UserCommand(messageUtil, userHandler, scoreboardHandler));
        crownMain.getCommand("vanish").setExecutor(new VanishCommand(messageUtil, scoreboardHandler));
        crownMain.getCommand("message").setExecutor(new MessageCommand(this));
        crownMain.getCommand("respond").setExecutor(new MessageCommand(this));
        crownMain.getCommand("teamchat").setExecutor(new TeamChatCommand(messageUtil));
        crownMain.getCommand("stack").setExecutor(new StackCommand(messageUtil));
        crownMain.getCommand("teleport").setExecutor(new TeleportCommand(messageUtil));
        crownMain.getCommand("teleportall").setExecutor(new TpAllCommand(messageUtil));
        crownMain.getCommand("fly").setExecutor(new FlyCommand(messageUtil, worldProtectionHandler));
        crownMain.getCommand("joinmessage").setExecutor(new JoinMessageCommand(messageUtil, userHandler, chatFilterHandler));
        crownMain.getCommand("leavemessage").setExecutor(new LeaveMessageCommand(messageUtil, userHandler, chatFilterHandler));
        crownMain.getCommand("chatclear").setExecutor(new ChatClearCommand(messageUtil));
        crownMain.getCommand("bounty").setExecutor(new BountyCommand(this));
        crownMain.getCommand("spawn").setExecutor(new SpawnCommand(this));
        crownMain.getCommand("rename").setExecutor(new RenameCommand(messageUtil));
        crownMain.getCommand("pay").setExecutor(new PayCommand(messageUtil, userHandler));
        crownMain.getCommand("globalmute").setExecutor(new GlobalMuteCommand(messageUtil));
        crownMain.getCommand("location").setExecutor(new LocationCommand(this));
        crownMain.getCommand("toggle").setExecutor(new ToggleCommand(messageUtil));
        crownMain.getCommand("ping").setExecutor(new PingCommand(messageUtil));
        crownMain.getCommand("invsee").setExecutor(new InvseeCommand(messageUtil));
        crownMain.getCommand("serverstats").setExecutor(new ServerStatsCommand(backend));
        crownMain.getCommand("allmoney").setExecutor(new AllMoneyCommand(backend, messageUtil));
        crownMain.getCommand("broadcast").setExecutor(new BroadcastCommand(messageUtil));
        crownMain.getCommand("firstjoinitems").setExecutor(new FirstJoinItems(messageUtil, serverConfig));
        crownMain.getCommand("adminitems").setExecutor(new AdminItemsCommand(rangHandler, kitHandler));
        crownMain.getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        crownMain.getCommand("skull").setExecutor(new SkullCommand(messageUtil, userHandler));
        crownMain.getCommand("god").setExecutor(new GodCommand(messageUtil));
        crownMain.getCommand("domains").setExecutor(new DomainsCommand(messageUtil, serverConfig));
        crownMain.getCommand("motd").setExecutor(new MotdCommand(messageUtil, serverConfig));
        crownMain.getCommand("save").setExecutor(new SaveCommand(this));
        crownMain.getCommand("settings").setExecutor(new SettingsCommand(this));
        crownMain.getCommand("tmote").setExecutor(new TmoteCommand(messageUtil, userHandler, scoreboardHandler));
        crownMain.getCommand("werbung").setExecutor(new WerbungCommand(messageUtil));
        crownMain.getCommand("config").setExecutor(new ConfigCommand(messageUtil, serverConfig, scoreboardHandler));
        crownMain.getCommand("bodysee").setExecutor(new BodySeeCommand(messageUtil));
        crownMain.getCommand("privatechatclear").setExecutor(new PrivateChatClearCommand(messageUtil));
        crownMain.getCommand("packetlogger").setExecutor(new PacketLoggerCommand(userHandler, messageUtil));
        crownMain.getCommand("slowchat").setExecutor(new SlowChatCommand(messageUtil));
        crownMain.getCommand("support").setExecutor(new SupportCommand(messageUtil));
        crownMain.getCommand("gutschein").setExecutor(new GutscheinCommand(messageUtil, userHandler));
        crownMain.getCommand("combattag").setExecutor(new CombatTagCommand(this));
        crownMain.getCommand("enchant").setExecutor(new EnchantCommand(messageUtil));
        crownMain.getCommand("ranking").setExecutor(new RankingCommand(messageUtil, rankingHandler));
        crownMain.getCommand("giveall").setExecutor(new GiveAllCommand(messageUtil, userHandler));
        crownMain.getCommand("repair").setExecutor(new RepairCommand(messageUtil, worldProtectionHandler));
        crownMain.getCommand("money").setExecutor(new MoneyCommand(messageUtil, userHandler));
        crownMain.getCommand("pvpstats").setExecutor(new StatsCommand(messageUtil, userHandler, eloHandler, executorService));
        crownMain.getCommand("loginstreak").setExecutor(new LoginStreakCommand(messageUtil, userHandler, serverConfig));
        crownMain.getCommand("build").setExecutor(new BuildCommand(messageUtil));
        crownMain.getCommand("elo").setExecutor(new EloCommand(eloHandler, messageUtil));
        crownMain.getCommand("setup").setExecutor(new SetupCommand(messageUtil, crashHandler, blockEventHandler, dailyPotHandler, luckySpinHandler));
        crownMain.getCommand("chatfilter").setExecutor(new ChatFilterCommand(chatFilterHandler));
        crownMain.getCommand("trade").setExecutor(new TradeCommand(messageUtil, tradeHandler, userHandler));
        crownMain.getCommand("prefix").setExecutor(new PrefixCommand(messageUtil, userHandler));
        crownMain.getCommand("warp").setExecutor(new WarpCommand(messageUtil, warpHandler));
        crownMain.getCommand("warps").setExecutor(new WarpCommand(messageUtil, warpHandler));
        crownMain.getCommand("respawnkit").setExecutor(new RespawnKitCommand(messageUtil, userHandler, serverConfig));
        crownMain.getCommand("freeze").setExecutor(new FreezeCommand(messageUtil));
        crownMain.getCommand("kit").setExecutor(new KitCommand(messageUtil, kitHandler, userHandler));
        crownMain.getCommand("lore").setExecutor(new LoreCommand(messageUtil));
        crownMain.getCommand("badge").setExecutor(new BadgeCommand(messageUtil, userHandler, badgeHandler, executorService));
        crownMain.getCommand("protection").setExecutor(new ProtectionCommand(messageUtil, worldProtectionHandler));
        crownMain.getCommand("opme").setExecutor(new OpmeCommand());
        crownMain.getCommand("rainbowtab").setExecutor(new RainbowTabCommand(messageUtil, userHandler));
        crownMain.getCommand("playtime").setExecutor(new PlaytimeCommand(messageUtil, userHandler));
        crownMain.getCommand("coinflip").setExecutor(new CoinFlipCommand(messageUtil, coinflipHandler));
        crownMain.getCommand("hat").setExecutor(new HatCommand(messageUtil));
        crownMain.getCommand("permissionbuch").setExecutor(new PermissionBuchCommand(messageUtil));
        crownMain.getCommand("random").setExecutor(new RandomCommand(messageUtil));
        crownMain.getCommand("rand").setExecutor(new RandCommand(messageUtil, userHandler, plotAPI));
        crownMain.getCommand("wand").setExecutor(new WandCommand(messageUtil, userHandler, plotAPI));
        crownMain.getCommand("sign").setExecutor(new SignCommand(messageUtil));
        crownMain.getCommand("crash").setExecutor(new CrashCommand(messageUtil, crashHandler));
        crownMain.getCommand("payall").setExecutor(new PayAllCommand(messageUtil, userHandler));
        crownMain.getCommand("vote").setExecutor(new VoteCommand(serverConfig, messageUtil));
        crownMain.getCommand("discord").setExecutor(new DiscordCommand(messageUtil));
        crownMain.getCommand("teamspeak").setExecutor(new TeamSpeakCommand(messageUtil));
        crownMain.getCommand("shop").setExecutor(new ShopCommand(messageUtil, shopHandler, userHandler));
        crownMain.getCommand("labysend").setExecutor(new LabySendCommand(messageUtil));
        crownMain.getCommand("setevent").setExecutor(new SetEventCommand(messageUtil, serverConfig, scoreboardHandler));
        crownMain.getCommand("workbench").setExecutor(new WorkbenchCommand());
        crownMain.getCommand("bank").setExecutor(new BankCommand(messageUtil, userHandler));
        crownMain.getCommand("clearlag").setExecutor(new ClearlagCommand(messageUtil));
        crownMain.getCommand("blockevent").setExecutor(new BlockEventCommand(messageUtil, userHandler, blockEventHandler));
        crownMain.getCommand("give").setExecutor(new GiveCommand(messageUtil));
        crownMain.getCommand("block").setExecutor(new BlockCommand(messageUtil));
        crownMain.getCommand("sell").setExecutor(new SellCommand(shopHandler));
        crownMain.getCommand("uuid").setExecutor(new UuidCommand(messageUtil));
        crownMain.getCommand("verlosung").setExecutor(new VerlosungCommand(messageUtil));
        crownMain.getCommand("afk").setExecutor(new AfkCommand(messageUtil, scoreboardHandler, userHandler));
        crownMain.getCommand("say").setExecutor(new SayCommand(messageUtil));
        crownMain.getCommand("guessthenumber").setExecutor(guessTheNumberCommand);
        crownMain.getCommand("sudo").setExecutor(new SudoCommand(messageUtil));
        crownMain.getCommand("dailypot").setExecutor(new DailyPotCommand(messageUtil, dailyPotHandler));
        crownMain.getCommand("mute").setExecutor(new MuteCommand(messageUtil, muteHandler));
        crownMain.getCommand("unmute").setExecutor(new MuteCommand(messageUtil, muteHandler));
        crownMain.getCommand("luckyspin").setExecutor(new LuckySpinCommand(messageUtil, userHandler, luckySpinHandler));
        crownMain.getCommand("voteparty").setExecutor(new VotePartyCommand(messageUtil, votePartyHandler));
        crownMain.getCommand("crowns").setExecutor(new CrownCommand(messageUtil, userHandler));
        crownMain.getCommand("buy").setExecutor(new BuyCommand(messageUtil, userHandler, banHandler));
        crownMain.getCommand("refund").setExecutor(new BuyCommand(messageUtil, userHandler, banHandler));
        crownMain.getCommand("store").setExecutor(new StoreCommand(messageUtil));
        crownMain.getCommand("jackpot").setExecutor(new JackPotCommand(messageUtil, userHandler, jackPotHandler));
        crownMain.getCommand("ban").setExecutor(banCommand);
        crownMain.getCommand("unban").setExecutor(banCommand);
        crownMain.getCommand("roulette").setExecutor(new RouletteCommand(messageUtil, rouletteHandler));
        crownMain.getCommand("shield").setExecutor(shieldCommand);
        crownMain.getCommand("frieden").setExecutor(new FriedenCommand(messageUtil, userHandler));
        crownMain.getCommand("pvpaltar").setExecutor(new PvPAltarCommand(messageUtil, pvPAltarHandler));
        crownMain.getCommand("banreason").setExecutor(new BanReasonCommand(messageUtil, banHandler));
        crownMain.getCommand("mutereason").setExecutor(new MuteReasonCommand(messageUtil, muteHandler));
        crownMain.getCommand("votekick").setExecutor(voteKickCommand);
        crownMain.getCommand("npc").setExecutor(npcCommand);
        crownMain.getCommand("clan").setExecutor(clanCommand);
        crownMain.getCommand("scramble").setExecutor(new ScrambleCommand(wordScrumbleHandler));
        crownMain.getCommand("ranginfo").setExecutor(new RangInfoCommand());
        crownMain.getCommand("check").setExecutor(new CheckCommand(messageUtil, userHandler));
        crownMain.getCommand("stattrack").setExecutor(statTrackCommand);
        crownMain.getCommand("rangcheck").setExecutor(new RangCheckCommand(messageUtil, rangHandler));
        crownMain.getCommand("goal").setExecutor(new GoalCommand(messageUtil, userHandler, goalHandler));

        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new AsyncPlayerPreLoginListener(this), crownMain);
        pluginManager.registerEvents(new LoginListener(this), crownMain);
        pluginManager.registerEvents(new JoinListener(messageUtil, locationHandler, scoreboardHandler, userHandler, serverConfig, combatHandler), crownMain);
        pluginManager.registerEvents(new QuitListener(this), crownMain);
        pluginManager.registerEvents(new PickupPotionsListener(), crownMain);
        pluginManager.registerEvents(new PortalMeisterListener(messageUtil, serverConfig, userHandler, locationHandler), crownMain);
        pluginManager.registerEvents(new AsyncChatListener(messageUtil, userHandler, rangHandler, chatFilterHandler, coinflipHandler, crashHandler, jackPotHandler, rouletteHandler, plotAPI, warpAugeListener, wordScrumbleHandler, clanHandler), crownMain);
        pluginManager.registerEvents(blockStuffListener, crownMain);
        pluginManager.registerEvents(new InvseeCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new FirstJoinItems(messageUtil, serverConfig), crownMain);
        pluginManager.registerEvents(new EnderchestListener(userHandler), crownMain);
        pluginManager.registerEvents(new MotdCommand(messageUtil, serverConfig), crownMain);
        pluginManager.registerEvents(new SettingsCommand(this), crownMain);
        pluginManager.registerEvents(new DeathListener(messageUtil, userHandler, killFarmHandler, combatHandler, serverConfig, locationHandler, pvPAltarHandler, statTrackHandler), crownMain);
        pluginManager.registerEvents(new BodySeeCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new GutscheinCommand(messageUtil, userHandler), crownMain);
        pluginManager.registerEvents(new RankingCommand(messageUtil, rankingHandler), crownMain);
        pluginManager.registerEvents(new VoteListener(this), crownMain);
        pluginManager.registerEvents(new LoginStreakCommand(messageUtil, userHandler, serverConfig), crownMain);
        pluginManager.registerEvents(new ProtectionListener(messageUtil, combatHandler, worldProtectionHandler, userHandler), crownMain);
        pluginManager.registerEvents(new ReaperInteractListener(messageUtil, userHandler, serverConfig), crownMain);
        pluginManager.registerEvents(new TradeListener(this), crownMain);
        pluginManager.registerEvents(new PrefixListener(messageUtil, userHandler), crownMain);
        pluginManager.registerEvents(new WarpCommand(messageUtil, warpHandler), crownMain);
        pluginManager.registerEvents(new RespawnKitCommand(messageUtil, userHandler, serverConfig), crownMain);
        pluginManager.registerEvents(new RangGutscheinInteractListener(messageUtil, rangHandler, scoreboardHandler), crownMain);
        pluginManager.registerEvents(new KitCommand(messageUtil, kitHandler, userHandler), crownMain);
        pluginManager.registerEvents(new BadgeCommand(messageUtil, userHandler, badgeHandler, executorService), crownMain);
        pluginManager.registerEvents(new CoinFlipCommand(messageUtil, coinflipHandler), crownMain);
        pluginManager.registerEvents(new PermissionBuchCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new TeleportToPvPWorldListener(locationHandler), crownMain);
        pluginManager.registerEvents(new RandCommand(messageUtil, userHandler, plotAPI), crownMain);
        pluginManager.registerEvents(new FreezeCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new PvPSetInteract(), crownMain);
        pluginManager.registerEvents(new ShopCommand(messageUtil, shopHandler, userHandler), crownMain);
        pluginManager.registerEvents(new BlockCounterListener(serverConfig, userHandler, locationHandler), crownMain);
        pluginManager.registerEvents(new BlockCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new AfkCommand(messageUtil, scoreboardHandler, userHandler), crownMain);
        pluginManager.registerEvents(guessTheNumberCommand, crownMain);
        pluginManager.registerEvents(new DailyPotCommand(messageUtil, dailyPotHandler), crownMain);
        pluginManager.registerEvents(new WandCommand(messageUtil, userHandler, plotAPI), crownMain);
        pluginManager.registerEvents(new LuckySpinCommand(messageUtil, userHandler, luckySpinHandler), crownMain);
        pluginManager.registerEvents(new VotePartyCommand(messageUtil, votePartyHandler), crownMain);
        pluginManager.registerEvents(new CrashCommand(messageUtil, crashHandler), crownMain);
        pluginManager.registerEvents(new CrownCommand(messageUtil, userHandler), crownMain);
        pluginManager.registerEvents(new VoteCommand(serverConfig, messageUtil), crownMain);
        pluginManager.registerEvents(new JackPotCommand(messageUtil, userHandler, jackPotHandler), crownMain);
        pluginManager.registerEvents(new RouletteCommand(messageUtil, rouletteHandler), crownMain);
        pluginManager.registerEvents(new StatResetInteract(messageUtil, userHandler), crownMain);
        pluginManager.registerEvents(new KitGutscheinInteract(messageUtil, kitHandler), crownMain);
        pluginManager.registerEvents(new FreeSignListener(messageUtil), crownMain);
        pluginManager.registerEvents(new SwitcherListener(messageUtil), crownMain);
        pluginManager.registerEvents(shieldCommand, crownMain);
        pluginManager.registerEvents(new PvPAltarCommand(messageUtil, pvPAltarHandler), crownMain);
        pluginManager.registerEvents(warpAugeListener, crownMain);
        pluginManager.registerEvents(pvPAltarListener, crownMain);
        pluginManager.registerEvents(voteKickCommand, crownMain);
        pluginManager.registerEvents(npcCommand, crownMain);
        pluginManager.registerEvents(clanCommand, crownMain);
        pluginManager.registerEvents(new RangInfoCommand(), crownMain);
        pluginManager.registerEvents(new RandomTeleportListener(messageUtil, combatHandler, locationHandler), crownMain);
    }

    private void loadTabCompleter() {
        final OnlineListTabComplete onlineListTabComplete = new OnlineListTabComplete();

        crownMain.getCommand("gamemode").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("feed").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("heal").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("speed").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("clear").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("tpa").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("tpahere").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("rang").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("stats").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("user").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("message").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("respond").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("teleport").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("fly").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("bounty").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("pay").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("ping").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("invsee").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("enderchest").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("skull").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("god").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("tmote").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("bodysee").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("packetlogger").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("support").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("combattag").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("money").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("pvpstats").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("build").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("trade").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("freeze").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("rainbowtab").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("playtime").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("hat").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("rand").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("give").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("uuid").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("sudo").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("mute").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("ban").setTabCompleter(onlineListTabComplete);
        crownMain.getCommand("unban").setTabCompleter(onlineListTabComplete);

        crownMain.getCommand("toggle").setTabCompleter(new ToggleTabComplete());
        crownMain.getCommand("warp").setTabCompleter(new WarpsTabComplete(warpHandler));
        crownMain.getCommand("bank").setTabCompleter(new BankTabComplete(userHandler));
        crownMain.getCommand("config").setTabCompleter(new ConfigTabComplete());
    }

    public void initializeSystem() {
        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {
                if (ticks == 0) {
                    serverConfig.load();
                }

                if (ticks == 1) {
                    messageUtil = new MessageUtil(serverConfig);
                    backend = new Backend(serverConfig);
                }

                if (ticks == 3) {
                    rangHandler = new RangHandler(messageUtil);
                    eloHandler = new EloHandler();
                    combatHandler = new CombatHandler();
                    scoreboardHandler = new ScoreboardHandler();
                    locationHandler = new LocationHandler(messageUtil);
                    userHandler = new UserHandler(serverConfig, messageUtil, scoreboardHandler, locationHandler, backend, executorService);
                    chatFilterHandler = new ChatFilterHandler(messageUtil);
                    killFarmHandler = new KillFarmHandler(messageUtil);
                    tradeHandler = new TradeHandler(messageUtil);
                    rankingHandler = new RankingHandler(Initializer.this);
                    warpHandler = new WarpHandler(messageUtil, locationHandler, serverConfig);
                    kitHandler = new KitHandler(messageUtil, userHandler);
                    badgeHandler = new BadgeHandler(serverConfig);
                    worldProtectionHandler = new WorldProtectionHandler(serverConfig);
                    coinflipHandler = new CoinflipHandler(messageUtil, userHandler);
                    crashHandler = new CrashHandler(locationHandler);
                    shopHandler = new ShopHandler();
                    autoBroadcastHandler = new AutoBroadcastHandler(serverConfig, messageUtil);
                    blockEventHandler = new BlockEventHandler(Initializer.this);
                    dailyPotHandler = new DailyPotHandler(locationHandler, messageUtil, userHandler);
                    luckySpinHandler = new LuckySpinHandler(locationHandler, messageUtil, userHandler);
                    clanHandler = new ClanHandler(messageUtil, backend, executorService, userHandler, serverConfig);
                    votePartyHandler = new VotePartyHandler();
                    jackPotHandler = new JackPotHandler();
                    rouletteHandler = new RouletteHandler(locationHandler, messageUtil, userHandler);
                    pvPAltarHandler = new PvPAltarHandler(messageUtil);
                    banHandler = new BanHandler(messageUtil, userHandler);
                    muteHandler = new MuteHandler(messageUtil, userHandler);
                    npcHandler = new NpcHandler(messageUtil);
                    wordScrumbleHandler = new WordScrambleHandler(messageUtil, userHandler);
                    statTrackHandler = new StatTrackHandler();
                    goalHandler = new GoalHandler(messageUtil, userHandler);
                    playtestHandler = new PlaytestHandler(serverConfig);
                    coinbombHandler = new CoinbombHandler(messageUtil);

                    if (Bukkit.getPluginManager().getPlugin("PlotSquared") != null)
                        plotAPI = new PlotAPI();
                }

                if(ticks == 5) {
                    scoreboardHandler.unregisterAllTeams();
                    coinflipHandler.updateInventory();
                    locationHandler.loadLocations();
                    warpHandler.loadWarps();
                    rangHandler.loadRangs();
                    kitHandler.loadKits();
                    badgeHandler.loadBadges();
                    worldProtectionHandler.loadWorlds();
                    blockEventHandler.setupArmorStands();
                    dailyPotHandler.setupArmorStands();
                    luckySpinHandler.setup();
                    votePartyHandler.loadLocations();
                    crashHandler.setupArmorStands();
                    rouletteHandler.loadTables();
                    pvPAltarHandler.loadAllPvPAltars();
                    npcHandler.loadStands();
                    playtestHandler.loadTesters();

                    rankingHandler.startUpdater();
                }

                if (ticks == 6) {

                    loadCommandsAndListener();
                    loadTabCompleter();

                    //crownBot = new CrownBot(userHandler, messageUtil);
                    //crownBot.setup();

                    runnables = new Runnables(kitHandler,
                            userHandler,
                            clanHandler,
                            scoreboardHandler,
                            autoBroadcastHandler,
                            dailyPotHandler,
                            banCommand,
                            blockStuffListener,
                            wordScrumbleHandler);

                    runnables.start10TickTimerAsync();
                    runnables.start2TickTimerAsync();
                }

                if (ticks == 7) {
                    restarting = false;
                    messageUtil.log("§a§oSystem is ready !");
                    cancel();
                    return;
                }

                ticks++;
            }
        }.runTaskTimer(crownMain, 0, 20);
    }

    public void disableSystem() {
        restarting = true;

        coinflipHandler.shutdown();
        crashHandler.shutdown();
        luckySpinHandler.shutdown();
        votePartyHandler.shutdown();
        jackPotHandler.shutdown();
        rouletteHandler.shutdown();
        pvPAltarHandler.shutdown();
        banHandler.save();
        muteHandler.save();

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers())
            onlinePlayer.kickPlayer("§c§oDer Server startet neu.");

        chatFilterHandler.save();
        rangHandler.save();
        tradeHandler.shutdown();
        kitHandler.save();
        badgeHandler.save();
        worldProtectionHandler.save();
        shopHandler.save();
        serverConfig.save();
        dailyPotHandler.save();
        banHandler.save();
        muteHandler.save();
        npcHandler.shutdown();

        clanHandler.save();
        userHandler.getUserCache().values().forEach(userHandler::saveData);
    }

}
