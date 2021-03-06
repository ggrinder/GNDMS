package de.zib.gndms.logic.action;

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



import de.zib.gndms.model.common.ModelUUIDGen;
import de.zib.gndms.model.dspace.SliceKind;
import de.zib.gndms.model.dspace.Subspace;
import de.zib.gndms.model.dspace.Slice;
import de.zib.gndms.logic.model.dspace.CreateSliceAction;
import static org.testng.AssertJUnit.*;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.List;
import java.util.GregorianCalendar;
import java.io.File;

/**
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * 
 * User: mjorra, Date: 14.08.2008, Time: 14:03:31
 */
public class SliceCreationValidator {

    ModelUUIDGen uuidgen;
    String gId;
    Calendar terminationTime;
    Subspace subspace;
    SliceKind Kind;
    long storageSize;
    String uid;
    CreateSliceAction Action;

    public SliceCreationValidator( ) {

    }

    /**
     * Delivers a newly created action, w/o entity manager.
     */
    CreateSliceAction createCreateSliceAction( ) {

        Action = new CreateSliceAction( gId, uid, terminationTime, uuidgen, Kind, storageSize );
        Action.setModel( subspace );
        return Action;
    }


    void validate( Slice sl ) {

        assertEquals( gId, sl.getId( ) );
        assertEquals( terminationTime, sl.getTerminationTime() );
        assertEquals( uid, sl.getOwner() );
        assertSame( subspace, sl.getSubspace( ) );
        assertSame( Kind , sl.getKind( ) );
        File f = new File( subspace.getPathForSlice( sl ) );
        try{
            System.out.println( f.getCanonicalPath() );
        } catch( Exception e ) {
            e.printStackTrace(  );
        }
        assertTrue( "Check if path exists and its a directory", f.exists( ) && f.isDirectory( ) );
    }

    
    void validateFromDB( Slice sl, EntityManager em ) {

        Query q = em.createQuery( "SELECT x FROM Slices x WHERE x.id = :idParam" );
        q.setParameter( "idParam", sl.getId() );
        List<Slice> rs = ( List<Slice> ) q.getResultList();
        assertTrue( "Check length of list == 1", rs.size( ) == 1 );
        System.out.println( "checking slice form db" );
        validate( rs.get( 0 ) );

        q = em.createQuery( "SELECT x.directoryId FROM Slices x WHERE x.id = :idParam" );
        q.setParameter( "idParam", sl.getId() );
        String did = (String) q.getSingleResult();
        assertEquals( sl.getDirectoryId(), did );

        q = em.createQuery( "SELECT x.terminationTime FROM Slices x WHERE x.id = :idParam" );
        q.setParameter( "idParam", sl.getId() );
        Calendar tt = ( GregorianCalendar ) q.getSingleResult();
        assertEquals( sl.getTerminationTime( ), tt );

        q = em.createNativeQuery( "SELECT SUBSPACE_ID FROM dspace.slices WHERE ID = ?1" );
        q.setParameter( 1, sl.getId() );
        String sid = (String) q.getSingleResult();
        assertEquals( sl.getSubspace().getId( ), sid );

        q = em.createNativeQuery( "SELECT KIND_URI FROM dspace.slices WHERE ID = ?1" );
        q.setParameter( 1, sl.getId() );
        String skid = (String) q.getSingleResult();
        assertEquals( sl.getKind().getURI( ), skid );
    }

    public ModelUUIDGen getUuidgen() {
        return uuidgen;
    }

    public void setUuidgen( ModelUUIDGen uuidgen ) {
        this.uuidgen = uuidgen;
    }

    public String getGId() {
        return gId;
    }

    public void setGId( String gId ) {
        this.gId = gId;
    }

    public Calendar getTerminationTime() {
        return terminationTime;
    }

    public void setTerminationTime( Calendar terminationTime ) {
        this.terminationTime = terminationTime;
    }

    public Subspace getSubspace() {
        return subspace;
    }

    public void setSubspace( Subspace subspace ) {
        this.subspace = subspace;
    }

    public CreateSliceAction getAction() {
        return Action;
    }

    public void setAction( CreateSliceAction action ) {
        Action = action;
    }

    public SliceKind getKind() {
        return Kind;
    }

    public void setKind( SliceKind kind ) {
        Kind = kind;
    }

}
