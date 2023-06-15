package de.obey.crownmc;
/*

    Author - Obey -> SkySlayer-v4
       08.11.2022 / 18:50

*/

import com.intellectualcrafters.plot.api.PlotAPI;
import de.obey.crownmc.backend.MySQL;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.commands.*;
import de.obey.crownmc.handler.*;
import de.obey.crownmc.listener.AsyncChatListener;
import de.obey.crownmc.listener.AsyncPlayerPreLoginListener;
import de.obey.crownmc.listener.BlockCounterListener;
import de.obey.crownmc.listener.BlockStuffListener;
import de.obey.crownmc.listener.BountyArmorStandInteractListener;
import de.obey.crownmc.listener.DeathListener;
import de.obey.crownmc.listener.EnderchestListener;
import de.obey.crownmc.listener.JoinListener;
import de.obey.crownmc.listener.LoginListener;
import de.obey.crownmc.listener.PickupPotionsListener;
import de.obey.crownmc.listener.PrefixListener;
import de.obey.crownmc.listener.ProtectionListener;
import de.obey.crownmc.listener.PvPSetInteract;
import de.obey.crownmc.listener.QuitListener;
import de.obey.crownmc.listener.RangGutscheinInteractListener;
import de.obey.crownmc.listener.TeleportToPvPWorldListener;
import de.obey.crownmc.listener.TradeListener;
import de.obey.crownmc.listener.VoteRewards;
import de.obey.crownmc.objects.Runnables;
import de.obey.crownmc.tabcomplete.OnlineListTabComplete;
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

    private MySQL mySQL;

    /* Util Instances */
    private MessageUtil messageUtil;

    /* Handler Instances */
    private ChatFilterHandler chatFilterHandler;
    private CombatHandler combatHandler;
    private EloHandler eloHandler;
    private KillFarmHandler killFarmHandler;
    private LocationHandler locationHandler;
    private LoginRewardHandler loginRewardHandler;
    private RangHandler rangHandler;
    private RankingHandler rankingHandler;
    private ScoreboardHandler scoreboardHandler;
    private TradeHandler tradeHandler;
    private UserHandler userHandler;
    private WarpHandler warpHandler;
    private KitHandler kitHandler;
    private BadgeHandler badgeHandler;
    private WorldProtectionHandler worldProtectionHandler;
    private CoinflipHandler coinflipHandler;
    private CrashHandler crashHandler;
    private ShopHandler shopHandler;
    private AutoBroadcastHandler autoBroadcastHandler;
    private BlockEventHandler blockEventHandler;
    private GuessTheNumberCommand guessTheNumberCommand;
    private DailyPotHandler dailyPotHandler;
    private LuckySpinHandler luckySpinHandler;

    private PlotAPI plotAPI;

    /* Is Server restarting ? */
    private boolean restarting = true;

    private Runnables runnables;

    public Initializer(final CrownMain crownMain) {
        this.crownMain = crownMain;
        this.executorService = Executors.newCachedThreadPool();
        this.serverConfig = new ServerConfig(this);
    }

    private void loadCommands() {
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
        crownMain.getCommand("serverstats").setExecutor(new ServerStatsCommand(mySQL));
        crownMain.getCommand("allmoney").setExecutor(new AllMoneyCommand(mySQL, messageUtil));
        crownMain.getCommand("broadcast").setExecutor(new BroadcastCommand(messageUtil));
        crownMain.getCommand("firstjoinitems").setExecutor(new FirstJoinItems(messageUtil, serverConfig));
        crownMain.getCommand("adminitems").setExecutor(new AdminItemsCommand(rangHandler));
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
        crownMain.getCommand("repair").setExecutor(new RepairCommand(messageUtil));
        crownMain.getCommand("money").setExecutor(new MoneyCommand(messageUtil, userHandler));
        crownMain.getCommand("pvpstats").setExecutor(new StatsCommand(messageUtil, userHandler, eloHandler, executorService));
        crownMain.getCommand("loginstreak").setExecutor(new LoginStreakCommand(loginRewardHandler));
        crownMain.getCommand("build").setExecutor(new BuildCommand(messageUtil));
        crownMain.getCommand("elo").setExecutor(new EloCommand(eloHandler, messageUtil));
        crownMain.getCommand("setup").setExecutor(new SetupCommand(messageUtil, crashHandler, blockEventHandler, dailyPotHandler, luckySpinHandler));
        crownMain.getCommand("chatfilter").setExecutor(new ChatFilterCommand(chatFilterHandler));
        crownMain.getCommand("trade").setExecutor(new TradeCommand(messageUtil, tradeHandler));
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
        crownMain.getCommand("vote").setExecutor(new VoteCommand());
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
        crownMain.getCommand("afk").setExecutor(new AfkCommand(messageUtil, scoreboardHandler));
        crownMain.getCommand("say").setExecutor(new SayCommand(messageUtil));
        guessTheNumberCommand = new GuessTheNumberCommand(messageUtil);
        crownMain.getCommand("guessthenumber").setExecutor(guessTheNumberCommand);
        crownMain.getCommand("sudo").setExecutor(new SudoCommand(messageUtil));
        crownMain.getCommand("dailypot").setExecutor(new DailyPotCommand(messageUtil, dailyPotHandler));
        crownMain.getCommand("mute").setExecutor(new MuteCommand(messageUtil, userHandler));
        crownMain.getCommand("unmute").setExecutor(new MuteCommand(messageUtil, userHandler));
        crownMain.getCommand("check").setExecutor(new MuteCommand(messageUtil, userHandler));
        crownMain.getCommand("ignore").setExecutor(new IgnoreCommand(messageUtil, userHandler));
        crownMain.getCommand("ignores").setExecutor(new IgnoresCommand(messageUtil, userHandler));
        crownMain.getCommand("luckyspin").setExecutor(new LuckySpinCommand(messageUtil, userHandler, luckySpinHandler));
    }

    private void loadTabCompleter() {

        crownMain.getCommand("whitelist").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("stop").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("gamemode").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("feed").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("heal").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("trash").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("speed").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("more").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("day").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("night").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("time").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("clear").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("tpa").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("tpahere").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("rang").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("setprefix").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("stats").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("user").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("vanish").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("message").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("respond").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("teamchat").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("stack").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("teleport").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("teleportall").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("fly").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("joinmessage").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("leavemessage").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("chatclear").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("bounty").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("spawn").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("rename").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("pay").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("globalmute").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("location").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("toggle").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("ping").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("invsee").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("serverstats").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("allmoney").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("broadcast").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("firstjoinitems").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("adminitems").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("enderchest").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("skull").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("god").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("domains").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("motd").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("save").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("settings").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("tmote").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("werbung").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("config").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("bodysee").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("privatechatclear").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("packetlogger").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("slowchat").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("support").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("gutschein").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("combattag").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("enchant").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("ranking").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("giveall").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("repair").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("money").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("pvpstats").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("loginstreak").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("build").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("elo").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("setup").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("chatfilter").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("trade").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("prefix").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("warp").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("warps").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("respawnkit").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("freeze").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("kit").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("lore").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("badge").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("protection").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("opme").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("rainbowtab").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("playtime").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("coinflip").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("hat").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("permissionbuch").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("random").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("rand").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("sign").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("crash").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("payall").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("vote").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("discord").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("teamspeak").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("shop").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("labysend").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("setevent").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("workbench").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("bank").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("clearlag").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("blockevent").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("give").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("block").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("sell").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("uuid").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("verlosung").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("afk").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("guessthenumber").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("sudo").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("dailypot").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("mute").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("ignore").setTabCompleter(new OnlineListTabComplete());
        crownMain.getCommand("ignores").setTabCompleter(new OnlineListTabComplete());
    }

    private void loadListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new AsyncPlayerPreLoginListener(this), crownMain);
        pluginManager.registerEvents(new LoginListener(this), crownMain);
        pluginManager.registerEvents(new JoinListener(messageUtil, locationHandler, scoreboardHandler, userHandler, serverConfig, combatHandler), crownMain);
        pluginManager.registerEvents(new QuitListener(this), crownMain);
        pluginManager.registerEvents(new PickupPotionsListener(), crownMain);
        pluginManager.registerEvents(new AsyncChatListener(messageUtil, userHandler, rangHandler, chatFilterHandler, coinflipHandler, plotAPI), crownMain);
        pluginManager.registerEvents(new BlockStuffListener(messageUtil, locationHandler, combatHandler, userHandler, serverConfig, worldProtectionHandler), crownMain);
        pluginManager.registerEvents(new InvseeCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new FirstJoinItems(messageUtil, serverConfig), crownMain);
        pluginManager.registerEvents(new EnderchestListener(userHandler), crownMain);
        pluginManager.registerEvents(new MotdCommand(messageUtil, serverConfig), crownMain);
        pluginManager.registerEvents(new SettingsCommand(this), crownMain);
        pluginManager.registerEvents(new DeathListener(messageUtil, userHandler, killFarmHandler, combatHandler, serverConfig), crownMain);
        pluginManager.registerEvents(new BodySeeCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new GutscheinCommand(messageUtil, userHandler), crownMain);
        pluginManager.registerEvents(new RankingCommand(messageUtil, rankingHandler), crownMain);
        pluginManager.registerEvents(new VoteRewards(this), crownMain);
        pluginManager.registerEvents(new LoginStreakCommand(loginRewardHandler), crownMain);
        pluginManager.registerEvents(new ProtectionListener(messageUtil, combatHandler, worldProtectionHandler), crownMain);
        pluginManager.registerEvents(new BountyArmorStandInteractListener(messageUtil, userHandler), crownMain);
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
        pluginManager.registerEvents(new PvPSetInteract(messageUtil), crownMain);
        pluginManager.registerEvents(new ShopCommand(messageUtil, shopHandler, userHandler), crownMain);
        pluginManager.registerEvents(new BlockCounterListener(serverConfig, userHandler, locationHandler), crownMain);
        pluginManager.registerEvents(new BlockCommand(messageUtil), crownMain);
        pluginManager.registerEvents(new AfkCommand(messageUtil, scoreboardHandler), crownMain);
        pluginManager.registerEvents(guessTheNumberCommand, crownMain);
        pluginManager.registerEvents(new DailyPotCommand(messageUtil, dailyPotHandler), crownMain);
        pluginManager.registerEvents(new WandCommand(messageUtil, userHandler, plotAPI), crownMain);
        pluginManager.registerEvents(new LuckySpinCommand(messageUtil, userHandler, luckySpinHandler), crownMain);
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
                    mySQL = new MySQL(serverConfig);
                }

                if (ticks == 3) {
                    rangHandler = new RangHandler(messageUtil);
                    eloHandler = new EloHandler();
                    combatHandler = new CombatHandler();
                    scoreboardHandler = new ScoreboardHandler();
                    locationHandler = new LocationHandler(messageUtil);
                    userHandler = new UserHandler(serverConfig, messageUtil, scoreboardHandler, locationHandler, mySQL, executorService);
                    loginRewardHandler = new LoginRewardHandler(userHandler, messageUtil);
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

                    if (Bukkit.getPluginManager().getPlugin("PlotSquared") != null)
                        plotAPI = new PlotAPI();

                    coinflipHandler.updateInventory();

                    locationHandler.loadLocations();
                    warpHandler.loadWarps();
                    rangHandler.loadRangs();
                    loginRewardHandler.loadRewards();
                    kitHandler.loadKits();
                    badgeHandler.loadBadges();
                    worldProtectionHandler.loadWorlds();
                    blockEventHandler.setupArmorStands();
                    dailyPotHandler.setupArmorStands();
                    luckySpinHandler.setup();
                }

                if (ticks == 5) {
                    loadCommands();
                    loadTabCompleter();
                    loadListener();

                    runnables = new Runnables(loginRewardHandler, kitHandler, userHandler, scoreboardHandler, autoBroadcastHandler, dailyPotHandler);
                    runnables.start10TickTimerAsync();
                    runnables.start2TickTimerAsync();

                    crashHandler.setupArmorStands();
                    rankingHandler.startUpdater();
                }

                if (ticks == 6) {
                    restarting = false;
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

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers())
            onlinePlayer.kickPlayer("§c§oDer Server startet neu.");

        chatFilterHandler.save();
        rangHandler.save();
        tradeHandler.shutdown();
        loginRewardHandler.save();
        kitHandler.save();
        badgeHandler.save();
        worldProtectionHandler.save();
        shopHandler.save();
        serverConfig.save();
        dailyPotHandler.save();

        userHandler.getUserCache().values().forEach(userHandler::saveData);
    }

}
