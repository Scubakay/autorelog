plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.10-SNAPSHOT" apply false
    //id("dev.kikugie.j52j") version "1.0.2" apply false // Enables asset processing by writing json5 files
    id("me.modmuss50.mod-publish-plugin") version "0.7.+" apply false // Publishes builds to hosting websites
}
stonecutter active "dev" /* [SC] DO NOT EDIT */

stonecutter.tasks {
    // Order publishing: https://stonecutter.kikugie.dev/blog/changes/0.7.html#_0-7-alpha-21
    order(named("publishMods"))
}

stonecutter.parameters {
    replacements {
        // ConnectScreen was moved in 1.20.5
        string { id = "reconnect_import"; direction = eval(metadata.version, "<1.20.5"); phase = "LAST"; replace("import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;", "import net.minecraft.client.gui.screen.ConnectScreen;") }

        // Before 1.20 we need to get ServerInfo somewhere else
        string { id = "reconnect_serverinfo"; direction = eval(metadata.version, "<1.20"); phase = "LAST"; replace("handler.getServerInfo();", "MinecraftClient.getInstance().getCurrentServerEntry();") }

        // The disconnect method was introduced in GameMenuScreen in 1.20
        string { id = "gamemenuscreenmixin_import"; direction = eval(metadata.version, "<1.20"); phase = "LAST"; replace("import com.scubakay.autorelog.util.Reconnect;", "import com.scubakay.autorelog.util.Reconnect;\nimport net.minecraft.client.gui.widget.ButtonWidget;") }
        string { id = "gamemenuscreenmixin_inject"; direction = eval(metadata.version, "<1.20"); phase = "LAST"; replace("@Inject(method = \"disconnect\", at = @At(\"HEAD\"))", "@Inject(method = \"method_19836\", at = @At(\"HEAD\"))") }
        string { id = "gamemenuscreenmixin_signature"; direction = eval(metadata.version, "<1.20"); phase = "LAST"; replace("injectDisconnect(CallbackInfo ci)", "injectDisconnect(ButtonWidget button, CallbackInfo ci)") }

        // DrawContext was introduced in 1.20
        string { id = "screenmixin_import"; direction = eval(metadata.version, "<1.20"); phase = "LAST"; replace("import net.minecraft.client.gui.DrawContext;", "import net.minecraft.client.util.math.MatrixStack;\nimport net.minecraft.client.gui.DrawableHelper;") }
        string { id = "screenmixin_signature";  direction = eval(metadata.version, "<1.20"); phase = "LAST"; replace("injectCountdown(DrawContext context", "injectCountdown(MatrixStack matrices") }
        string { id = "screenmixin_drawwithshadow"; direction = eval(metadata.version, "<1.20"); phase = "LAST"; replace("context.drawTextWithShadow(this.textRenderer", "DrawableHelper.drawTextWithShadow(matrices, this.textRenderer") }
    }
}
