package de.zib.gndms.model.dspace;

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

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.zib.gndms.logic.model.config.SetupAction.SetupMode;
import de.zib.gndms.stuff.confuror.ConfigEditor;
import de.zib.gndms.stuff.confuror.ConfigHolder;
import de.zib.gndms.stuff.confuror.ConfigEditor.UpdateRejectedException;

/**
 * A mockup for a subspace configuration.
 * 
 * @author Ulrike Golas
 */

public class MockSubspaceConfiguration extends ConfigHolder {

	public MockSubspaceConfiguration(String path, String gsiftp, boolean visible, long value, SetupMode mode) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory factory = objectMapper.getJsonFactory();
		ConfigEditor.Visitor visitor = new ConfigEditor.DefaultVisitor();
		ConfigEditor editor = newEditor(visitor);
		setObjectMapper(objectMapper);
		
		try {
			JsonNode pn = ConfigHolder.parseSingle(factory, SubspaceConfiguration.createSingleEntry(SubspaceConfiguration.PATH, path));
			JsonNode gn = ConfigHolder.parseSingle(factory, SubspaceConfiguration.createSingleEntry(SubspaceConfiguration.GSIFTPPATH, gsiftp));
			JsonNode vn = ConfigHolder.parseSingle(factory, SubspaceConfiguration.createSingleEntry(SubspaceConfiguration.VISIBLE, visible));
			JsonNode sn = ConfigHolder.parseSingle(factory, SubspaceConfiguration.createSingleEntry(SubspaceConfiguration.SIZE, value));
			JsonNode mn = ConfigHolder.parseSingle(factory, SubspaceConfiguration.createSingleEntry(SubspaceConfiguration.MODE, mode));
			update(editor, pn);
			update(editor, gn);
			update(editor, vn);
			update(editor, sn);
			update(editor, mn);
		} catch (IOException e) {
		} catch (UpdateRejectedException e) {
		}
	}

}
