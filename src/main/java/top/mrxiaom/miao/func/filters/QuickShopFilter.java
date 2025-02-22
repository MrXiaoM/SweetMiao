package top.mrxiaom.miao.func.filters;

import org.bukkit.configuration.MemoryConfiguration;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.api.shop.ShopManager;
import top.mrxiaom.miao.SweetMiao;
import top.mrxiaom.miao.func.AbstractModule;
import top.mrxiaom.miao.func.ChatReplacer;
import top.mrxiaom.pluginbase.func.AutoRegister;

@AutoRegister(requirePlugins = "QuickShop")
public class QuickShopFilter extends AbstractModule {
    boolean loaded = false;
    public QuickShopFilter(SweetMiao plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        if (loaded) return;
        loaded = true;
        ChatReplacer inst = ChatReplacer.inst();
        inst.registerPlayerFilter(player -> {
            QuickShop plugin = QuickShop.getInstance();
            ShopManager shopManager = plugin.getShopManager();
            return !shopManager.getActions().containsKey(player.getUniqueId());
        });
    }
}
