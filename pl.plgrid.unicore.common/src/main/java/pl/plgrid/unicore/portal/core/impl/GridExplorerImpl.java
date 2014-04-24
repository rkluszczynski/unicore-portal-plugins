package pl.plgrid.unicore.portal.core.impl;

import eu.unicore.portal.core.GlobalState;
import pl.plgrid.unicore.portal.core.GridExplorer;

/**
 * Created by Rafal on 2014-04-24.
 */
public class GridExplorerImpl implements GridExplorer {

    public GridExplorerImpl() {
        Object uPortal = GlobalState.getCurrent().getSharedObjects().get("uPortal");
    }
}
