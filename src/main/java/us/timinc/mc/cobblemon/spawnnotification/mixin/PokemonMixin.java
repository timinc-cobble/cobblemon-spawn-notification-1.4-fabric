package us.timinc.mc.cobblemon.spawnnotification.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(value = Pokemon.class, remap = false)
public abstract class PokemonMixin {
    @Inject(method = "sendOutWithAnimation", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void possiblyPlayShinySound(LivingEntity source, ServerLevel level, Vec3 position, UUID battleId, Function1<? super PokemonEntity, Unit> mutation, CallbackInfoReturnable<CompletableFuture<PokemonEntity>> cir, CompletableFuture<PokemonEntity> future) {
        future.thenAccept(pokemonEntity -> SpawnNotification.INSTANCE.possiblyPlayShinySound(pokemonEntity, level, new BlockPos(position)));
    }
}
