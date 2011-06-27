package de.zib.gndms.logic.dspace;

// TODO documentation, implementation

import java.util.List;

import de.zib.gndms.model.dspace.SliceKind;

public interface SliceKindProvider {
    boolean exists(String sliceKind);
    List<String> listSliceKinds();
    SliceKind getSliceKind(String sliceKind);

}