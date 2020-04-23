function getInput() {
    if (fiblib.inState != null)
        return fiblib.inState;
    else
        return fiblib.getDefaultBlockstate("minecraft:chest");
}

function getOutput() {
    return fiblib.isLookingAt(6) ? getInput() : fiblib.getDefaultBlockstate("minecraft:red_wool");
}