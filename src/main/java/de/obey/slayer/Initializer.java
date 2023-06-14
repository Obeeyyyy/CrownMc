package de.obey.slayer;
/*

    Author - Obey -> SkySlayer-v4
       08.11.2022 / 18:50

*/

import com.intellectualcrafters.plot.api.PlotAPI;
import de.obey.slayer.backend.MySQL;
import de.obey.slayer.backend.ServerConfig;
import de.obey.slayer.commands.*;
import de.obey.slayer.handler.*;
import de.obey.slayer.listener.AsyncChatListener;
import de.obey.slayer.listener.AsyncPlayerPreLoginListener;
import de.obey.slayer.listener.BlockCounterListener;
import de.obey.slayer.listener.BlockStuffListener;
import de.obey.slayer.listener.BountyArmorStandInteractListener;
import de.obey.slayer.listener.DeathListener;
import de.obey.slayer.listener.EnderchestListener;
import de.obey.slayer.listener.JoinListener;
import de.obey.slayer.listener.LoginListener;
import de.obey.slayer.listener.PickupPotionsListener;
import de.obey.slayer.listener.PrefixListener;
import de.obey.slayer.listener.ProtectionListener;
import de.obey.slayer.listener.PvPSetInteract;
import de.obey.slayer.listener.QuitListener;
import de.obey.slayer.listener.RangGutscheinInteractListener;
import de.obey.slayer.listener.TeleportToPvPWorldListener;
import de.obey.slayer.listener.TradeListener;
import de.obey.slayer.listener.VoteRewards;
import de.obey.slayer.objects.Runnables;
import de.obey.slayer.tabcomplete.OnlineListTabComplete;
import de.obey.slayer.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public final class Initializer {

    private final SlayerMain slayerMain;
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

    private PlotAPI plotAPI;

    /* Is Server restarting ? */
    private boolean restarting = true;

    private Runnables runnables;

    public Initializer(final SlayerMain slayerMain) {
        this.slayerMain = slayerMain;
        this.executorService = Executors.newCachedThreadPool();
        this.serverConfig = new ServerConfig(this);
    }

    private void loadCommands() {
        slayerMain.getCommand("whitelist").setExecutor(new WhitelistCommand(serverConfig, messageUtil));
        slayerMain.getCommand("stop").setExecutor(new StopCommand(this));
        slayerMain.getCommand("gamemode").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("feed").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("heal").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("trash").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("speed").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("more").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("day").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("night").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("time").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("clear").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("tpa").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("tpahere").setExecutor(new EssentialCommands(this));
        slayerMain.getCommand("rang").setExecutor(new RangCommand(messageUtil, rangHandler, scoreboardHandler));
        slayerMain.getCommand("setprefix").setExecutor(new SetPrefixCommand(this));
        slayerMain.getCommand("stats").setExecutor(new StatsCommand(messageUtil, userHandler, eloHandler, executorService));
        slayerMain.getCommand("user").setExecutor(new UserCommand(messageUtil, userHandler, scoreboardHandler));
        slayerMain.getCommand("vanish").setExecutor(new VanishCommand(messageUtil, scoreboardHandler));
        slayerMain.getCommand("message").setExecutor(new MessageCommand(this));
        slayerMain.getCommand("respond").setExecutor(new MessageCommand(this));
        slayerMain.getCommand("teamchat").setExecutor(new TeamChatCommand(messageUtil));
        slayerMain.getCommand("stack").setExecutor(new StackCommand(messageUtil));
        slayerMain.getCommand("teleport").setExecutor(new TeleportCommand(messageUtil));
        slayerMain.getCommand("teleportall").setExecutor(new TpAllCommand(messageUtil));
        slayerMain.getCommand("fly").setExecutor(new FlyCommand(messageUtil, worldProtectionHandler));
        slayerMain.getCommand("joinmessage").setExecutor(new JoinMessageCommand(messageUtil, userHandler, chatFilterHandler));
        slayerMain.getCommand("leavemessage").setExecutor(new LeaveMessageCommand(messageUtil, userHandler, chatFilterHandler));
        slayerMain.getCommand("chatclear").setExecutor(new ChatClearCommand(messageUtil));
        slayerMain.getCommand("bounty").setExecutor(new BountyCommand(this));
        slayerMain.getCommand("spawn").setExecutor(new SpawnCommand(this));
        slayerMain.getCommand("rename").setExecutor(new RenameCommand(messageUtil));
        slayerMain.getCommand("pay").setExecutor(new PayCommand(messageUtil, userHandler));
        slayerMain.getCommand("globalmute").setExecutor(new GlobalMuteCommand(messageUtil));
        slayerMain.getCommand("location").setExecutor(new LocationCommand(this));
        slayerMain.getCommand("toggle").setExecutor(new ToggleCommand(messageUtil));
        slayerMain.getCommand("ping").setExecutor(new PingCommand(messageUtil));
        slayerMain.getCommand("invsee").setExecutor(new InvseeCommand(messageUtil));
        slayerMain.getCommand("serverstats").setExecutor(new ServerStatsCommand(mySQL));
        slayerMain.getCommand("allmoney").setExecutor(new AllMoneyCommand(mySQL, messageUtil));
        slayerMain.getCommand("broadcast").setExecutor(new BroadcastCommand(messageUtil));
        slayerMain.getCommand("firstjoinitems").setExecutor(new FirstJoinItems(messageUtil, serverConfig));
        slayerMain.getCommand("adminitems").setExecutor(new AdminItemsCommand(rangHandler));
        slayerMain.getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        slayerMain.getCommand("skull").setExecutor(new SkullCommand(messageUtil, userHandler));
        slayerMain.getCommand("god").setExecutor(new GodCommand(messageUtil));
        slayerMain.getCommand("domains").setExecutor(new DomainsCommand(messageUtil, serverConfig));
        slayerMain.getCommand("motd").setExecutor(new MotdCommand(messageUtil, serverConfig));
        slayerMain.getCommand("save").setExecutor(new SaveCommand(this));
        slayerMain.getCommand("settings").setExecutor(new SettingsCommand(this));
        slayerMain.getCommand("tmote").setExecutor(new TmoteCommand(messageUtil, userHandler, scoreboardHandler));
        slayerMain.getCommand("werbung").setExecutor(new WerbungCommand(messageUtil));
        slayerMain.getCommand("config").setExecutor(new ConfigCommand(messageUtil, serverConfig, scoreboardHandler));
        slayerMain.getCommand("bodysee").setExecutor(new BodySeeCommand(messageUtil));
        slayerMain.getCommand("privatechatclear").setExecutor(new PrivateChatClearCommand(messageUtil));
        slayerMain.getCommand("packetlogger").setExecutor(new PacketLoggerCommand(userHandler, messageUtil));
        slayerMain.getCommand("slowchat").setExecutor(new SlowChatCommand(messageUtil));
        slayerMain.getCommand("support").setExecutor(new SupportCommand(messageUtil));
        slayerMain.getCommand("gutschein").setExecutor(new GutscheinCommand(messageUtil, userHandler));
        slayerMain.getCommand("combattag").setExecutor(new CombatTagCommand(this));
        slayerMain.getCommand("enchant").setExecutor(new EnchantCommand(messageUtil));
        slayerMain.getCommand("ranking").setExecutor(new RankingCommand(messageUtil, rankingHandler));
        slayerMain.getCommand("giveall").setExecutor(new GiveAllCommand(messageUtil, userHandler));
        slayerMain.getCommand("repair").setExecutor(new RepairCommand(messageUtil));
        slayerMain.getCommand("money").setExecutor(new MoneyCommand(messageUtil, userHandler));
        slayerMain.getCommand("pvpstats").setExecutor(new StatsCommand(messageUtil, userHandler, eloHandler, executorService));
        slayerMain.getCommand("loginstreak").setExecutor(new LoginStreakCommand(loginRewardHandler));
        slayerMain.getCommand("build").setExecutor(new BuildCommand(messageUtil));
        slayerMain.getCommand("elo").setExecutor(new EloCommand(eloHandler, messageUtil));
        slayerMain.getCommand("setup").setExecutor(new SetupCommand(messageUtil, crashHandler, blockEventHandler, dailyPotHandler));
        slayerMain.getCommand("chatfilter").setExecutor(new ChatFilterCommand(chatFilterHandler));
        slayerMain.getCommand("trade").setExecutor(new TradeCommand(messageUtil, tradeHandler));
        slayerMain.getCommand("prefix").setExecutor(new PrefixCommand(messageUtil, userHandler));
        slayerMain.getCommand("warp").setExecutor(new WarpCommand(messageUtil, warpHandler));
        slayerMain.getCommand("warps").setExecutor(new WarpCommand(messageUtil, warpHandler));
        slayerMain.getCommand("respawnkit").setExecutor(new RespawnKitCommand(messageUtil, userHandler, serverConfig));
        slayerMain.getCommand("freeze").setExecutor(new FreezeCommand(messageUtil));
        slayerMain.getCommand("kit").setExecutor(new KitCommand(messageUtil, kitHandler, userHandler));
        slayerMain.getCommand("lore").setExecutor(new LoreCommand(messageUtil));
        slayerMain.getCommand("badge").setExecutor(new BadgeCommand(messageUtil, userHandler, badgeHandler, executorService));
        slayerMain.getCommand("protection").setExecutor(new ProtectionCommand(messageUtil, worldProtectionHandler));
        slayerMain.getCommand("opme").setExecutor(new OpmeCommand());
        slayerMain.getCommand("rainbowtab").setExecutor(new RainbowTabCommand(messageUtil, userHandler));
        slayerMain.getCommand("playtime").setExecutor(new PlaytimeCommand(messageUtil, userHandler));
        slayerMain.getCommand("coinflip").setExecutor(new CoinFlipCommand(messageUtil, coinflipHandler));
        slayerMain.getCommand("hat").setExecutor(new HatCommand(messageUtil));
        slayerMain.getCommand("permissionbuch").setExecutor(new PermissionBuchCommand(messageUtil));
        slayerMain.getCommand("random").setExecutor(new RandomCommand(messageUtil));
        slayerMain.getCommand("rand").setExecutor(new RandCommand(messageUtil, userHandler, plotAPI));
        slayerMain.getCommand("wand").setExecutor(new WandCommand(messageUtil, userHandler, plotAPI));
        slayerMain.getCommand("sign").setExecutor(new SignCommand(messageUtil));
        slayerMain.getCommand("crash").setExecutor(new CrashCommand(messageUtil, crashHandler));
        slayerMain.getCommand("payall").setExecutor(new PayAllCommand(messageUtil, userHandler));
        slayerMain.getCommand("vote").setExecutor(new VoteCommand());
        slayerMain.getCommand("discord").setExecutor(new DiscordCommand(messageUtil));
        slayerMain.getCommand("teamspeak").setExecutor(new TeamSpeakCommand(messageUtil));
        slayerMain.getCommand("shop").setExecutor(new ShopCommand(messageUtil, shopHandler, userHandler));
        slayerMain.getCommand("labysend").setExecutor(new LabySendCommand(messageUtil));
        slayerMain.getCommand("setevent").setExecutor(new SetEventCommand(messageUtil, serverConfig, scoreboardHandler));
        slayerMain.getCommand("workbench").setExecutor(new WorkbenchCommand());
        slayerMain.getCommand("bank").setExecutor(new BankCommand(messageUtil, userHandler));
        slayerMain.getCommand("clearlag").setExecutor(new ClearlagCommand(messageUtil));
        slayerMain.getCommand("blockevent").setExecutor(new BlockEventCommand(messageUtil, userHandler, blockEventHandler));
        slayerMain.getCommand("give").setExecutor(new GiveCommand(messageUtil));
        slayerMain.getCommand("block").setExecutor(new BlockCommand(messageUtil));
        slayerMain.getCommand("sell").setExecutor(new SellCommand(shopHandler));
        slayerMain.getCommand("uuid").setExecutor(new UuidCommand(messageUtil));
        slayerMain.getCommand("verlosung").setExecutor(new VerlosungCommand(messageUtil));
        slayerMain.getCommand("afk").setExecutor(new AfkCommand(messageUtil, scoreboardHandler));
        slayerMain.getCommand("say").setExecutor(new SayCommand(messageUtil));
        guessTheNumberCommand = new GuessTheNumberCommand(messageUtil);
        slayerMain.getCommand("guessthenumber").setExecutor(guessTheNumberCommand);
        slayerMain.getCommand("sudo").setExecutor(new SudoCommand(messageUtil));
        slayerMain.getCommand("dailypot").setExecutor(new DailyPotCommand(messageUtil, dailyPotHandler));
        slayerMain.getCommand("mute").setExecutor(new MuteCommand(messageUtil, userHandler));
        slayerMain.getCommand("unmute").setExecutor(new MuteCommand(messageUtil, userHandler));
        slayerMain.getCommand("check").setExecutor(new MuteCommand(messageUtil, userHandler));
        slayerMain.getCommand("ignore").setExecutor(new IgnoreCommand(messageUtil, userHandler));
        slayerMain.getCommand("ignores").setExecutor(new IgnoresCommand(messageUtil, userHandler));
    }

    private void loadTabCompleter() {

        slayerMain.getCommand("whitelist").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("stop").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("gamemode").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("feed").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("heal").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("trash").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("speed").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("more").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("day").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("night").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("time").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("clear").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("tpa").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("tpahere").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("rang").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("setprefix").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("stats").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("user").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("vanish").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("message").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("respond").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("teamchat").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("stack").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("teleport").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("teleportall").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("fly").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("joinmessage").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("leavemessage").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("chatclear").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("bounty").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("spawn").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("rename").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("pay").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("globalmute").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("location").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("toggle").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("ping").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("invsee").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("serverstats").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("allmoney").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("broadcast").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("firstjoinitems").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("adminitems").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("enderchest").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("skull").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("god").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("domains").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("motd").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("save").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("settings").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("tmote").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("werbung").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("config").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("bodysee").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("privatechatclear").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("packetlogger").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("slowchat").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("support").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("gutschein").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("combattag").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("enchant").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("ranking").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("giveall").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("repair").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("money").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("pvpstats").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("loginstreak").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("build").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("elo").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("setup").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("chatfilter").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("trade").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("prefix").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("warp").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("warps").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("respawnkit").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("freeze").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("kit").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("lore").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("badge").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("protection").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("opme").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("rainbowtab").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("playtime").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("coinflip").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("hat").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("permissionbuch").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("random").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("rand").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("sign").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("crash").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("payall").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("vote").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("discord").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("teamspeak").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("shop").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("labysend").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("setevent").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("workbench").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("bank").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("clearlag").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("blockevent").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("give").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("block").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("sell").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("uuid").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("verlosung").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("afk").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("guessthenumber").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("sudo").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("dailypot").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("mute").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("ignore").setTabCompleter(new OnlineListTabComplete());
        slayerMain.getCommand("ignores").setTabCompleter(new OnlineListTabComplete());
    }

    private void loadListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new AsyncPlayerPreLoginListener(this), slayerMain);
        pluginManager.registerEvents(new LoginListener(this), slayerMain);
        pluginManager.registerEvents(new JoinListener(messageUtil, locationHandler, scoreboardHandler, userHandler, serverConfig, combatHandler), slayerMain);
        pluginManager.registerEvents(new QuitListener(this), slayerMain);
        pluginManager.registerEvents(new PickupPotionsListener(), slayerMain);
        pluginManager.registerEvents(new AsyncChatListener(messageUtil, userHandler, rangHandler, chatFilterHandler, coinflipHandler, plotAPI), slayerMain);
        pluginManager.registerEvents(new BlockStuffListener(messageUtil, locationHandler, combatHandler, userHandler, serverConfig, worldProtectionHandler), slayerMain);
        pluginManager.registerEvents(new InvseeCommand(messageUtil), slayerMain);
        pluginManager.registerEvents(new FirstJoinItems(messageUtil, serverConfig), slayerMain);
        pluginManager.registerEvents(new EnderchestListener(userHandler), slayerMain);
        pluginManager.registerEvents(new MotdCommand(messageUtil, serverConfig), slayerMain);
        pluginManager.registerEvents(new SettingsCommand(this), slayerMain);
        pluginManager.registerEvents(new DeathListener(messageUtil, userHandler, killFarmHandler, combatHandler, serverConfig), slayerMain);
        pluginManager.registerEvents(new BodySeeCommand(messageUtil), slayerMain);
        pluginManager.registerEvents(new GutscheinCommand(messageUtil, userHandler), slayerMain);
        pluginManager.registerEvents(new RankingCommand(messageUtil, rankingHandler), slayerMain);
        pluginManager.registerEvents(new VoteRewards(this), slayerMain);
        pluginManager.registerEvents(new LoginStreakCommand(loginRewardHandler), slayerMain);
        pluginManager.registerEvents(new ProtectionListener(messageUtil, combatHandler, worldProtectionHandler), slayerMain);
        pluginManager.registerEvents(new BountyArmorStandInteractListener(messageUtil, userHandler), slayerMain);
        pluginManager.registerEvents(new TradeListener(this), slayerMain);
        pluginManager.registerEvents(new PrefixListener(messageUtil, userHandler), slayerMain);
        pluginManager.registerEvents(new WarpCommand(messageUtil, warpHandler), slayerMain);
        pluginManager.registerEvents(new RespawnKitCommand(messageUtil, userHandler, serverConfig), slayerMain);
        pluginManager.registerEvents(new RangGutscheinInteractListener(messageUtil, rangHandler, scoreboardHandler), slayerMain);
        pluginManager.registerEvents(new KitCommand(messageUtil, kitHandler, userHandler), slayerMain);
        pluginManager.registerEvents(new BadgeCommand(messageUtil, userHandler, badgeHandler, executorService), slayerMain);
        pluginManager.registerEvents(new CoinFlipCommand(messageUtil, coinflipHandler), slayerMain);
        pluginManager.registerEvents(new PermissionBuchCommand(messageUtil), slayerMain);
        pluginManager.registerEvents(new TeleportToPvPWorldListener(locationHandler), slayerMain);
        pluginManager.registerEvents(new RandCommand(messageUtil, userHandler, plotAPI), slayerMain);
        pluginManager.registerEvents(new FreezeCommand(messageUtil), slayerMain);
        pluginManager.registerEvents(new PvPSetInteract(messageUtil), slayerMain);
        pluginManager.registerEvents(new ShopCommand(messageUtil, shopHandler, userHandler), slayerMain);
        pluginManager.registerEvents(new BlockCounterListener(serverConfig, userHandler, locationHandler), slayerMain);
        pluginManager.registerEvents(new BlockCommand(messageUtil), slayerMain);
        pluginManager.registerEvents(new AfkCommand(messageUtil, scoreboardHandler), slayerMain);
        pluginManager.registerEvents(guessTheNumberCommand, slayerMain);
        pluginManager.registerEvents(new DailyPotCommand(messageUtil, dailyPotHandler), slayerMain);
        pluginManager.registerEvents(new WandCommand(messageUtil, userHandler, plotAPI), slayerMain);
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
        }.runTaskTimer(slayerMain, 0, 20);
    }

    public void disableSystem() {
        restarting = true;

        coinflipHandler.shutdown();
        crashHandler.shutdown();

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
