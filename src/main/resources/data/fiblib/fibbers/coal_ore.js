function getInput() {
    if (fiblib.inState != null)
        return fiblib.inState;
    else
        return fiblib.getDefaultBlockstate("minecraft:coal_ore");
}

function getOutput() {
    if (fiblib.player == null || fiblib.player.isCreative())
        return getInput();
    else {

        return fiblib.getDefaultBlockstate("minecraft:glowstone");
    }
}