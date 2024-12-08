package top.mrxiaom.miao.func;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoRegister
public class ChatReplacer extends AbstractModule implements Listener {
    boolean enable = false;
    EventPriority priority;
    EventExecutor onChat = (ignored, event) -> {
        if (!enable) return;
        AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        if (player.hasPermission("sweet.miao.chat")
                && !player.hasPermission("sweet.miao.bypass")) {
            e.setMessage(processChat(e.getMessage()));
        }
    };
    public ChatReplacer(SweetMiao plugin) {
        super(plugin);
        registerEvents();
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        super.reloadConfig(config);
        this.enable = config.getBoolean("enable", false);
        this.priority = Util.valueOr(EventPriority.class, config.getString("priority"), EventPriority.LOW);
        if (priority == EventPriority.MONITOR) {
            priority = EventPriority.LOW;
        }

        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, priority, onChat, plugin);
    }

    static Pattern patternEnd = Pattern.compile("[。！？；.!?;]+$");
    public static String processChat(String s) {
        String[] message = splitPunctuation(s.trim());
        if (message[0].endsWith("喵")) return s;
        if (endsWithMood(message[0])) {
            if (endsWithQuestionMood(message[0]) && !hasQuestionMark(message[1])) {
                message[0] = removeLast(message[0]);
                message[1] = "？" + message[1];
            } else {
                message[0] = removeLast(message[0]);
            }
        }
        if (specialQuestionWords(message[0])) {
            message[0] = removeLast(message[0]);
            if (!hasQuestionMark(message[1])) {
                message[1] = "？" + message[1];
            }
        }
        return message[0] + "喵" + message[1];
    }
    public static String removeLast(String s) {
        return s.substring(0, s.length() - 1);
    }
    public static boolean endsWithMood(String s) {
        return s.endsWith("啊") || s.endsWith("嘛") || endsWithQuestionMood(s);
    }
    public static boolean endsWithQuestionMood(String s) {
        return s.endsWith("吗");
    }
    public static boolean specialQuestionWords(String s) {
        return s.endsWith("什么");
    }
    public static boolean hasQuestionMark(String s) {
        return s.contains("?") || s.contains("？");
    }
    public static String[] splitPunctuation(String str) {
        Matcher m = patternEnd.matcher(str);
        if (!m.find()) return new String[] { str, "" };
        String group = m.group();
        return new String[] { str.substring(0, str.length() - group.length()), group};
    }
}
