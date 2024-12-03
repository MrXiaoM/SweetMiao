package top.mrxiaom.miao.func;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.pluginbase.func.AutoRegister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoRegister
public class ChatReplacer extends AbstractModule implements Listener {
    public ChatReplacer(SweetMiao plugin) {
        super(plugin);
        registerEvents();
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        super.reloadConfig(config);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("sweet.miao.chat")
                && !player.hasPermission("sweet.miao.bypass")) {
            e.setMessage(processChat(e.getMessage()));
        }
    }
    static Pattern patternEnd = Pattern.compile("[。！？；.!?;]+$");
    public static String processChat(String s) {
        String[] message = splitPunctuation(s.trim());
        if (endsWithMood(message[0])) {
            if (endsWithQuestionMood(message[0]) && !hasQuestionMark(message[1])) {
                message[0] = removeLast(message[0]);
                message[1] = "？" + message[1];
            } else if (!specialQuestionWords(message[0])) {
                message[0] = removeLast(message[0]);
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
