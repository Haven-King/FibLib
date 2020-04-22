var inState = fiblib.getDefaultBlockstate("minecraft:coal_ore");
var outState = fiblib.getDefaultBlockstate("minecraft:coal_ore");

if (!player.isCreative())
    outState = fiblib.getDefaultBlockstate("minecraft:glowstone");