package de.zib.gndms.gritserv.typecon.types;

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



import types.SliceStageInORQT;
import de.zib.gndms.model.gorfx.types.io.SliceStageInORQWriter;
import de.zib.gndms.model.gorfx.types.io.DataDescriptorWriter;
import de.zib.gndms.model.gorfx.types.io.SliceStageInORQConverter;
import de.zib.gndms.model.gorfx.types.SliceStageInORQ;
import de.zib.gndms.gritserv.typecon.GORFXClientTools;

import javax.xml.soap.SOAPException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

import org.apache.axis.description.FieldDesc;
import org.apache.axis.message.MessageElement;

/**
 * @author  try ma ik jo rr a zib
 * @version  $Id$
 * <p/>
 * User: mjorra, Date: 16.10.2008, Time: 08:51:08
 */
public class SliceStageInORQXSDTypeWriter extends AbstractXSDTypeWriter<SliceStageInORQT>
    implements SliceStageInORQWriter {

    private DataDescriptorXSDTypeWriter dataWriter;


    public void writeGridSiteName( String gsn ) {
        addElement( 0, "gridSite", gsn );
    }


    public void writeDataFileName( String dfn ) {
        addElement( 2, "dataFile", dfn );
    }


    public void writeMetaDataFileName( String mfn ) {
        try {
            String fn = "MetadataFile";
            MessageElement me = findElement( fn );
            me.setObjectValue( mfn );
        } catch ( SOAPException e ) {
            boxException( e );
        }
    }


    public DataDescriptorWriter getDataDescriptorWriter() {

        if( dataWriter == null )
            dataWriter = new DataDescriptorXSDTypeWriter();
        return dataWriter;
    }


    public void beginWritingDataDescriptor() {
        if( dataWriter == null )
            throw new IllegalStateException( "no data writer present" );
    }


    public void doneWritingDataDescriptor() {
        if( dataWriter == null )
            throw new IllegalStateException( "no data writer present" );
        try {
            String fn = "StagedData";
            MessageElement me = findElement( fn );
            me.setObjectValue( dataWriter.getProduct( ) );
        } catch ( SOAPException e ) {
            boxException( e );
        }
    }


    public void writeJustEstimate( boolean je ) {
        // not required in XSD context.
    }


    public void writeContext( HashMap<String, String> ctx ) {
        // not required in XSD context.
    }


    public void begin() {
        try {
            setProduct( GORFXClientTools.createEmptySliceStageInORQT( )  );
        } catch ( SOAPException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        }
    }


    public void done() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    protected void boxException( Exception e ) {
        throw new RuntimeException( e.getMessage(), e );
    }


    public void writeId( String id ) {
        // Not required here
    }


    /**
     * On shot write resp. convert operation.
     *
     * @param orq Source provider stage in orq.
     * @return XSD version of the source orq.
     */
    public static SliceStageInORQT write( SliceStageInORQ orq ) {

        SliceStageInORQXSDTypeWriter writer = new SliceStageInORQXSDTypeWriter();
        SliceStageInORQConverter conv = new SliceStageInORQConverter( writer, orq );
        conv.convert();

        return writer.getProduct();
    }


    protected void addElement( int idx, String nam, Object elm ) {

        FieldDesc fd = SliceStageInORQT.getTypeDesc().getFieldByName( nam );
        LinkedList<MessageElement> ll = new LinkedList<MessageElement>( Arrays.asList( getProduct().get_any() ) );

        try {
            ll.add( idx,
                GORFXClientTools.createElementForField( fd, elm ) );
        } catch ( SOAPException e ) {
            boxException( e );
        }
        getProduct().set_any( ll.toArray( new MessageElement[ll.size()] ) );
    }


    protected MessageElement findElement( String nam ) {

        MessageElement[] mes = getProduct().get_any();

        if( mes == null )
            return null;

        for ( MessageElement me : mes ) {
            if ( nam.equals( me.getName() ) )
                return me;
        }

        return null;
    }
}
