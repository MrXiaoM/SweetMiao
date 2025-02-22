package top.mrxiaom.miao.commands;
        
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.miao.func.ChatReplacer;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.miao.func.AbstractModule;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.*;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(SweetMiao plugin) {
        super(plugin);
        registerCommand("sweetmiao", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 3 && "disableCount".equalsIgnoreCase(args[0]) && sender.isOp()) {
            Player player = Util.getOnlinePlayer(args[1]).orElse(null);
            if (player == null) {
                return t(sender, "&e玩家&b " + args[1] + " &e不在线");
            }
            Integer count = Util.parseInt(args[2]).orElse(null);
            if (count == null) {
                return t(sender, "&e请输入一个整数");
            }
            boolean silent = args.length > 3 && args[3].equals("-s");
            ChatReplacer.inst().putDisableCount(player.getUniqueId(), count);
            if (!silent) {
                t(sender, "&e已设置玩家&b " + player.getName() + " &e在之后的&b " + count + " &e次聊天不会触发替换");
            }
            return true;
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            return t(sender, "&a配置文件已重载");
        }
        return true;
    }

    private static final List<String> emptyList = Lists.newArrayList();
    private static final List<String> listOpArg0 = Lists.newArrayList("reload");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return startsWith(sender.isOp() ? listOpArg0 : emptyList, args[0]);
        }
        if (args.length == 2) {
            if ("disableCount".equalsIgnoreCase(args[0]) && sender.isOp()) {
                return null;
            }
        }
        return emptyList;
    }

    public List<String> startsWith(Collection<String> list, String s) {
        return startsWith(null, list, s);
    }
    public List<String> startsWith(String[] addition, Collection<String> list, String s) {
        String s1 = s.toLowerCase();
        List<String> stringList = new ArrayList<>(list);
        if (addition != null) stringList.addAll(0, Lists.newArrayList(addition));
        stringList.removeIf(it -> !it.toLowerCase().startsWith(s1));
        return stringList;
    }
}
