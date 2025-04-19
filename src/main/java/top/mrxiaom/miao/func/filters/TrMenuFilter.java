package top.mrxiaom.miao.func.filters;

import me.arasple.mc.trmenu.taboolib.platform.util.ChatListener;
import org.bukkit.configuration.MemoryConfiguration;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.miao.func.AbstractModule;
import top.mrxiaom.miao.func.ChatReplacer;
import top.mrxiaom.pluginbase.func.AutoRegister;

@AutoRegister(requirePlugins = "TrMenu")
public class TrMenuFilter extends AbstractModule {
    boolean loaded = false;
    public TrMenuFilter(SweetMiao plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        if (loaded) return;
        loaded = true;
        ChatReplacer inst = ChatReplacer.inst();
        inst.registerPlayerFilter(player -> {
            ChatListener listener = ChatListener.INSTANCE;
            return !listener.getInputs().containsKey(player.getName());
        });
    }
}
