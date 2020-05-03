package dev.hephaestus.fiblib.blocks;

import it.unimi.dsi.fastutil.longs.LongSet;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Set;

public interface TrackerComponent extends Component {
    Set<Integer> trackedStates();
    HashMap<Integer, LongSet> tracked();
    LongSet tracked(BlockState state);
    void track(BlockState state, BlockPos pos);
    void remove(BlockState state, BlockPos pos);
    int getVersion();
    void update();
}
