package de.zib.gndms.logic.model.gorfx;

/*
 * Copyright 2008-2011 Zuse Institute Berlin (ZIB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import de.zib.gndms.kit.network.PersistentMarkerListener;
import de.zib.gndms.kit.util.WidAux;
import de.zib.gndms.model.gorfx.Task;
import de.zib.gndms.model.gorfx.AbstractTask;
import de.zib.gndms.model.gorfx.types.AbstractORQ;
import org.globus.ftp.Marker;

/**
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * <p/>
 * User: mjorra, Date: 11.10.2008, Time: 18:47:42
 *
 * A persistent marker listener which updates the progress of a task.
 *
 * The maximum progress is the number of files to transfer hence one progress step is the completed transfer
 * of a single file.
 */
public class TaskPersistentMarkerListener extends PersistentMarkerListener {

    private AbstractTask task;
    private String gorfxid;


    @Override
    public void markerArrived( final Marker marker ) {
        try{
            WidAux.initWid( task.getWid() );
            WidAux.initGORFXid( task.getWid() );
            super.markerArrived( marker );
        } finally{
            WidAux.removeGORFXid( );
            WidAux.removeWid( );
        }
    }


    public void setCurrentFile( String currentFile ) {
        super.setCurrentFile( currentFile );
        task.setProgress( task.getProgress( ) + 1 );
    }


    public AbstractTask getTask() {
        return task;
    }


    public void setTask( AbstractTask task ) {
        this.task = task;
        gorfxid = (( AbstractORQ ) task.getOrq()).getActId();
    }
}
