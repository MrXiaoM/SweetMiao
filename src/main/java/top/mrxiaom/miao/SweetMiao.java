package top.mrxiaom.miao;
        
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.EconomyHolder;

public class SweetMiao extends BukkitPlugin {
    public static SweetMiao getInstance() {
        return (SweetMiao) BukkitPlugin.getInstance();
    }

    public SweetMiao() {
        super(options()
                .bungee(false)
                .adventure(false)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .vaultEconomy(false)
                .scanIgnore("top.mrxiaom.sweetmiao.libs")
        );
    }


    @Override
    protected void afterEnable() {
        getLogger().info("SweetMiao 加载完毕");
    }
}
