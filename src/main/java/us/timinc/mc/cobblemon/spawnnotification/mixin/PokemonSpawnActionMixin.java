package us.timinc.mc.cobblemon.spawnnotification.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification;

import java.util.List;

@Mixin(value = PokemonSpawnAction.class, remap = false)
public abstract class PokemonSpawnActionMixin {
    @Shadow
    private PokemonProperties props;

    @Inject(method = "createEntity", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void possiblyBroadcastSpawn(CallbackInfoReturnable<Entity> cir, List list, ItemStack itemStack, PokemonEntity entity) {
        SpawningContext ctx = ((PokemonSpawnAction) (Object) this).getCtx();
        Level level = ctx.getWorld();

        if (level.isClientSide) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        SpawnNotification.INSTANCE.possiblyBroadcastSpawn(entity, serverLevel, ctx.getPosition());
    }
}
